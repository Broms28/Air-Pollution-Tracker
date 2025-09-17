import javafx.scene.chart.*;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is responsible for comparing the air pollution data of different cities.
 *
 * @author Anton Davidouski
 */

public class DataComparison {

    private HashMap<String, CityData> cityData = new HashMap<>();
    // store healthy values for each pollutant - this will be used to scale the data and find by how
    // much each city's data exceeds the healthy value
    private double[] healthyValue = {2, 4400, 40, 60, 20, 10, 20, 40};

    public DataComparison() {}

    /**
     * Add a city's data to the comparison.
     * @param name The name of the city.
     * @param data The city's air pollution data.
     * @return True if the city's data was successfully added, false otherwise.
     */
    public boolean addCityData(String name, double[] data) {
        if (cityData.containsKey(name)) {
            return false;
        }
        else {
            CityData city = new CityData(name, data);
            cityData.put(name, city);
            return true;
        }
    }

    /**
     * Remove a city's data from the comparison.
     * @param name The name of the city.
     * @return True if the city's data was successfully removed, false otherwise.
     */
    public boolean removeCityData(String name) {
        if (!cityData.containsKey(name)) {
            return false;
        } else {
            cityData.remove(name);
            return true;
        }
    }

    /**
     * Originally intended as a true min max scaling method, but that made the data look visually uninformative,
     * since it was just all flt at the top end as the max value always got scaled to 1.
     * I have switched since to setting min to always be 0 and max to be the healthy value for each pollutant, so the readout from this function
     * will show the user how much the pollutant level exceeds the healthy value if above 1
     * @param value The value to scale.
     * @param min The minimum value in the dataset.
     * @param max The maximum value in the dataset.
     * @return The scaled value.
     */
    private double minMaxScale(double value, double min, double max) {
        return (value - min) / (max - min);
    }


    /**
     * Create a bar chart comparing the air pollution data of different cities.
     * @return A border pane containing the bar chart.
     */
    public BorderPane createChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Pollutant");
        xAxis.getStyleClass().add("xAxisAPI");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Pollutant Level (Scaled)");
        yAxis.getStyleClass().add("yAxisAPI");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Pollutant Levels by City - Above 1 indicates an unhealthy pollutant level");
        barChart.setCategoryGap(30);
        barChart.setBarGap(5);
        barChart.getStyleClass().add("barChartAPI");

        String[] pollutantNames = {"AQI", "CO", "NO2", "O3", "SO2", "PM2.5", "PM10", "NH3"};

        xAxis.getCategories().addAll(pollutantNames);

        for (String cityName : cityData.keySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(cityName);

            double[] data = cityData.get(cityName).getData();
            double[] scaledData = new double[data.length]; // store in the scaled data in a new array, as to not modify the original data
            for (int i = 0; i < data.length; i++) {
                double scaledValue = minMaxScale(data[i], 0, healthyValue[i]);
                if (scaledValue == 0){
                    scaledValue = 0.01; // Prevents the bar from being invisible if the value is 0
                }
                scaledData[i] = scaledValue;
            }
            for (int i = 0; i < data.length; i++) {
                series.getData().add(new XYChart.Data<>(pollutantNames[i], scaledData[i]));
            }

            barChart.getData().add(series);
        }

        BorderPane pane = new BorderPane();
        pane.setCenter(barChart);
        return pane;
    }
}
