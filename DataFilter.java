import java.util.HashMap;

/**
 * This class is responsible for filtering air pollution data by city based on predefined boundaries.
 * It organizes the data into separate datasets for each city.
 *
 * @author Rom Steinberg
 */

public class DataFilter {

    private static final HashMap<String, int[]> CITY_BOUNDARIES = City.getCitiesBoundaries();

    public static HashMap<String, DataSet> filterCityData(DataSet dataSet) {
        HashMap<String, DataSet> cityDataSets = new HashMap<>();

        // Create empty datasets for each city
        for (String city : CITY_BOUNDARIES.keySet()) {
            cityDataSets.put(city, new DataSet(dataSet.getPollutant(), dataSet.getYear(), dataSet.getMetric(), dataSet.getUnits()));
        }
        // Filter data points into correct city dataset
        for (DataPoint dp : dataSet.getData()) {
            for (String city : CITY_BOUNDARIES.keySet()) {
                int[] bounds = CITY_BOUNDARIES.get(city);
                if (dp.x() >= bounds[0] && dp.x() <= bounds[1] && dp.y() >= bounds[2] && dp.y() <= bounds[3]) {
                    cityDataSets.get(city).addData(dataPointArray(dp));
                }
            }
        }
        return cityDataSets;
    }

    private static String[] dataPointArray(DataPoint dp){
        return new String[]{dp.gridCode()+"",dp.x()+"",dp.y()+"",dp.value()+""};
    }
}
