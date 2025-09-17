import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * This class is responsible for connecting to the OpenWeatherMap API and retrieving air pollution data.
 *
 * @author Anton Davidouski
 */


public class ApiConnection {

    private static final String apiKey = "42912c00d47c0fb3ae19bcb050db29c5";

    public ApiConnection() {}

    /**
     * Retrieves the top 5 locations that match the search query.
     * @param location The user's search query.
     * @return A HashMap containing the top 5 locations and their latitude and longitude.
     */
    public HashMap<String, double[]> getTopLocationsForSearch(String location) {
        String locationJson = makeApiCallToGeolocatorAPI(location);
        if (locationJson == null) {
            System.out.println("Failed to retrieve location data.");
            return null;
        }
        return extractTopMatches(locationJson);
    }

    /**
     * Retrieves air pollution data for a given latitude and longitude.
     * @param lat Latitude
     * @param lon Longitude
     * @return An array of doubles containing the air pollution data.
     */
    public double[] getDataForLatLon(double lat, double lon) {
        String airPollutionJson = makeApiCallToLocation(lat, lon);
        if (airPollutionJson == null) {
            System.out.println("Failed to retrieve air pollution data.");
            return null;
        }
        return processDataJsonString(airPollutionJson);
    }

    /**
     * Makes an API call to the OpenWeatherMap API to retrieve air pollution data for a given latitude and longitude.
     * @param lat
     * @param lon
     * @return
     */
    private String makeApiCallToLocation(double lat, double lon) {
        String urlString = "https://api.openweathermap.org/data/2.5/air_pollution?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
        return makeHttpRequest(urlString);
    }

    /**
     * Makes an API call to the OpenWeatherMap GeoLocator API to retrieve the top 5 locations that match the search query.
     * @param location
     * @return
     */
    private String makeApiCallToGeolocatorAPI(String location) {
        location = location.replace(" ", "%20");
        String urlString = "https://api.openweathermap.org/geo/1.0/direct?q=" + location + "&limit=5&appid=" + apiKey;
        return makeHttpRequest(urlString);
    }

    /**
     * Makes an HTTP request to the given URL and returns the response as a string.
     * @param urlString The URL to make the request to.
     * @return The response from the server as a string.
     */
    private String makeHttpRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
                in.close();
                return content.toString();
            } else {
                System.out.println("API request failed. Response Code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            System.out.println("API call failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Processes the JSON string returned by the OpenWeatherMap API and extracts the air pollution data.
     * @param jsonString The JSON string returned by the API.
     * @return An array of doubles containing the air pollution data.
     */
    private double[] processDataJsonString(String jsonString) {
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONObject listItem = root.getJSONArray("list").getJSONObject(0);
            JSONObject main = listItem.getJSONObject("main");
            JSONObject components = listItem.getJSONObject("components");

            double aqi = main.getDouble("aqi");
            double co = components.getDouble("co");
            double no2 = components.getDouble("no2");
            double o3 = components.getDouble("o3");
            double so2 = components.getDouble("so2");
            double pm2_5 = components.getDouble("pm2_5");
            double pm10 = components.getDouble("pm10");
            double nh3 = components.getDouble("nh3");

            return new double[]{aqi, co, no2, o3, so2, pm2_5, pm10, nh3};
        } catch (Exception e) {
            System.out.println("Error parsing air pollution JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the top 5 locations that match the search query from the JSON string returned by the OpenWeatherMap GeoLocator API.
     * @param jsonString The JSON string returned by the API.
     * @return A HashMap containing the top 5 locations and their latitude and longitude.
     */
    private HashMap<String, double[]> extractTopMatches(String jsonString) {
        HashMap<String, double[]> matches = new HashMap<>();
        try {
            JSONArray locations = new JSONArray(jsonString);
            for (int i = 0; i < locations.length(); i++) {
                JSONObject loc = locations.getJSONObject(i);
                String name = loc.getString("name");
                String country = loc.getString("country");
                String state = loc.optString("state", "");
                String matchName = name + (state.isEmpty() ? "" : ", " + state) + ", " + country;
                double lat = loc.getDouble("lat");
                double lon = loc.getDouble("lon");
                matches.put(matchName, new double[]{lat, lon});
            }
            return matches;
        } catch (Exception e) {
            System.out.println("Error parsing geolocation JSON: " + e.getMessage());
            return null;   
        }
    }
}
