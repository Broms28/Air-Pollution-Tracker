import javafx.scene.chart.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;

import javafx.application.Platform;


/**
 * The Chart class is responsible for creating and managing three types of charts
 * (LineChart, BarChart, and PieChart) using JavaFX. It also updates the charts based on
 * provided data and allows switching between different chart types.
 * 
 * @author Yaal Luka Edrey Gatignol
 * @version 1.0
 */
public class Chart {
    private VBox chartContainer;
    private LineChart<Number, Number> lineChart;
    private BarChart<String, Number> barChart;
    private PieChart pieChart;

    private Map<String, XYChart.Series<Number, Number>> seriesMap;
    private ChartType currentChartType;

    private HashMap<String, String> pollutantColors = new HashMap<>();

    /**
     * Enumeration representing the different types of charts supported.
     */
    public enum ChartType {
        LINE, BAR, PIE
    }

    /**
     * Constructor for the Chart class.
     * 
     * Initializes the chart container, sets up the different chart types (line, bar, and pie),
     * defines color mappings for pollutants, and sets the default chart type to LINE.
     * 
     */
    public Chart() {
        chartContainer = new VBox();
        // Make the chart container expandable
        chartContainer.setFillWidth(true);
        VBox.setVgrow(chartContainer, Priority.ALWAYS);
        
        seriesMap = new HashMap<>();

        pollutantColors.put("PM10", "#3498db"); // blue
        pollutantColors.put("PM2.5", "#e74c3c"); // red
        pollutantColors.put("NO2", "#2ecc71"); // green
        
        // Initialize all chart types
        lineChart();
        barChart();
        pieChart();
        
        // Set default chart type
        setChartType(ChartType.LINE);
    }
    
    /**
     * Initializes the LineChart.
     * Sets up the X and Y axes with proper labels, bounds, and formatting. Configures
     * the chart appearance, disables symbols, and binds its size to the container.
     */
    private void lineChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.getStyleClass().add("xAxisLC");
        yAxis.getStyleClass().add("yAxisLC");

        xAxis.setLabel("Time (in years)");
        yAxis.setLabel("Average Pollution Level (in µg/m³)");

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(2018);
        xAxis.setUpperBound(2023);
        xAxis.setTickUnit(1);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(30);

        // Format the x axis
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number value) {
                return String.valueOf(value.intValue());
            }
        });

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setPrefSize(750, 575);

        // Allow the chart to resize with the window
        lineChart.prefWidthProperty().bind(chartContainer.widthProperty());
        lineChart.prefHeightProperty().bind(chartContainer.heightProperty());
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.setTitle("Pollution Levels Over Time");
        lineChart.getStyleClass().add("lineChart");
    }
    
    /**
     * Initializes the BarChart.
     * Configures category (X) and numeric (Y) axes with labels and fixed bounds. Binds the 
     * chart's size to the container, sets the chart properties, and applies styling.
     */
    private void barChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.getStyleClass().add("xAxisBC");
        yAxis.getStyleClass().add("yAxisBC");
        
        xAxis.setLabel("Year");
        yAxis.setLabel("Average Pollution Level (in µg/m³)");
        
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(30);
        
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setPrefSize(750, 575);

        // Allow the chart to resize with the window
        barChart.prefWidthProperty().bind(chartContainer.widthProperty());
        barChart.prefHeightProperty().bind(chartContainer.heightProperty());
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        barChart.setTitle("Pollution Levels by Year");
        barChart.getStyleClass().add("barChart");
    }
    
    /**
     * Initializes the PieChart.
     * Sets up the PieChart with preferred size, binds it to the container, hides the legend,
     * and enables animations.
     */
    private void pieChart() {
        pieChart = new PieChart();
        pieChart.setPrefSize(750, 575);

        // Allow the chart to resize with the window
        pieChart.prefWidthProperty().bind(chartContainer.widthProperty());
        pieChart.prefHeightProperty().bind(chartContainer.heightProperty());
        pieChart.setLegendVisible(false);
        pieChart.setAnimated(true);
        pieChart.setTitle("Pollution Distribution");
    }
    
    /**
     * Sets the current chart type and updates the displayed chart accordingly.
     * Clears the chart container and adds the chart corresponding to the provided type.
     *
     * @param type The new ChartType (LINE, BAR, or PIE).
     */
    public void setChartType(ChartType type) {
        this.currentChartType = type;
        
        // Clear the container
        chartContainer.getChildren().clear();
        
        // Add the appropriate chart based on type
        switch (type) {
            case LINE:
                chartContainer.getChildren().add(lineChart);
                break;
            case BAR:
                chartContainer.getChildren().add(barChart);
                break;
            case PIE:
                chartContainer.getChildren().add(pieChart);
                break;
        }
    }

    /**
     * Updates the current chart with new data.
     * Clears all existing data from every chart type, resets the series map, and calls the specific
     * update method for the currently active chart.
     *
     * @param dataRange A HashMap containing data sets keyed by a combination of year and pollutant.
     */
    public void updateChart(HashMap<String, DataSet> dataRange) {
        // Clear existing data from all charts
        lineChart.getData().clear();
        barChart.getData().clear();
        pieChart.getData().clear();
        seriesMap.clear();
        
        // Update the appropriate chart based on current type
        switch (currentChartType) {
            case LINE:
                updateLineChart(dataRange);
                break;
            case BAR:
                updateBarChart(dataRange);
                break;
            case PIE:
                updatePieChart(dataRange);
                break;
        }
    }
    
    /**
     * Processes the provided data and updates the LineChart.
     * Iterates over the data, creates or updates series for each pollutant, calculates the average
     * value for each data point, and applies the pollutant-specific colors after rendering.
     *
     * @param dataRange A HashMap containing data sets keyed by a combination of year and pollutant.
     */
    private void updateLineChart(HashMap<String, DataSet> dataRange) {
        // Calculate the average value for each pollutant
        for (Map.Entry<String, DataSet> entry : dataRange.entrySet()) {
            // Key's format being: "year-pollutant"
            String[] keyParts = entry.getKey().split("-");
            int year = Integer.parseInt(keyParts[0]);
            String pollutant = keyParts[1].toUpperCase();

            // Create a new series for each pollutant if it doesn't exist
            seriesMap.putIfAbsent(pollutant, new XYChart.Series<>());
            XYChart.Series<Number, Number> series = seriesMap.get(pollutant);
    
            double avgValue = entry.getValue().getData().stream()
                    .mapToDouble(DataPoint::value)
                    .average()
                    .orElse(0);
    

            series.setName(pollutant);
            series.getData().add(new XYChart.Data<>(year, avgValue));
        }
    
        lineChart.getData().addAll(seriesMap.values());
        
        // Apply consistent colouring to each line
        Platform.runLater(() -> {
            for (XYChart.Series<Number, Number> series : lineChart.getData()) {
                String pollutant = series.getName();
                // Get the color for the pollutant in the map.
                String color = pollutantColors.getOrDefault(pollutant, "black");
                Node node = series.getNode();
                if (node != null) {
                    String style = String.format("-fx-stroke: %s;", color);
                    node.setStyle(style);
                }
            }
        });
    }
    
    /**
     * Processes the provided data and updates the BarChart.
     * Organizes data into series based on pollutant type, calculates the average values per year,
     * and applies the corresponding color styles to each bar.
     *
     * @param dataRange A HashMap containing data sets keyed by a combination of year and pollutant.
     */
    private void updateBarChart(HashMap<String, DataSet> dataRange) {
        Map<String, XYChart.Series<String, Number>> barSeriesMap = new HashMap<>();
        
        // Calculate the average value for each pollutant
        for (Map.Entry<String, DataSet> entry : dataRange.entrySet()) {
            // Key's format being: "year-pollutant"
            String[] keyParts = entry.getKey().split("-");
            String year = keyParts[0];
            String pollutant = keyParts[1].toUpperCase();
            
            // Create a new series for each pollutant if it doesn't exist
            barSeriesMap.putIfAbsent(pollutant, new XYChart.Series<>());
            XYChart.Series<String, Number> series = barSeriesMap.get(pollutant);
            
            double avgValue = entry.getValue().getData().stream()
                    .mapToDouble(DataPoint::value)
                    .average()
                    .orElse(0);
            

            series.setName(pollutant);        
            series.getData().add(new XYChart.Data<>(year, avgValue));
        }
        
        barChart.getData().addAll(barSeriesMap.values());

        // Apply consistent colouring to each bar
        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                String pollutant = series.getName();
                // Get the color for the pollutant in the map.
                String color = pollutantColors.getOrDefault(pollutant, "black");
                for (XYChart.Data<String, Number> data : series.getData()) {
                    Node barNode = data.getNode();
                    if (barNode != null) {
                        barNode.setStyle(String.format("-fx-bar-fill: %s;", color));
                    }
                }
            }
        });
    }
    
    /**
     * Processes the provided data and updates the PieChart.
     * Calculates the average pollution level for each pollutant, creates pie chart data items,
     * adds them to the PieChart, and applies the appropriate color to each slice.
     *
     * @param dataRange A HashMap containing data sets keyed by a combination of year and pollutant.
     */
    private void updatePieChart(HashMap<String, DataSet> dataRange) {
        Map<String, Double> pollutantAverages = new HashMap<>();
        
        // Calculate the average value for each pollutant
        for (Map.Entry<String, DataSet> entry : dataRange.entrySet()) {
            // Key's format being: "year-pollutant"
            String[] keyParts = entry.getKey().split("-");
            String pollutant = keyParts[1].toLowerCase();
            
            double avgValue = entry.getValue().getData().stream()
                    .mapToDouble(DataPoint::value)
                    .average()
                    .orElse(0);
            
            pollutantAverages.put(pollutant, avgValue);
        }
        
        // Add data (the average values) to the pie chart
        for (Map.Entry<String, Double> entry : pollutantAverages.entrySet()) {
            String pollutant = entry.getKey();
            double value = entry.getValue();
            
            PieChart.Data data = new PieChart.Data(pollutant.toUpperCase(), value);
            pieChart.getData().add(data);
        }
        
        // Apply consistent colouring to each slice
        Platform.runLater(() -> {
            for (PieChart.Data data : pieChart.getData()) {
                String pollutant = data.getName().toLowerCase();
                // Get the color for the pollutant in the map.
                String color = pollutantColors.getOrDefault(pollutant.toUpperCase(), "black");
                data.getNode().setStyle(String.format("-fx-pie-color: %s;", color));
            }
        });
    }

    /**
     * @return The currently active ChartType.
     */
    public ChartType getCurrentChartType() {
        return currentChartType;
    }

    /**
     * @return A VBox that contains the chart.
     */
    public Pane getChartBox() {
        return chartContainer;
    }
}