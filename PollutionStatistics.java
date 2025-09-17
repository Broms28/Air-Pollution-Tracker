import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * PollutionStatistics class handles the display of pollution data on various charts
 * and provides a sidebar for user controls including pollutant selection, year range,
 * and chart type selection.
 *
 * @author Nicolás Alcalá Olea and Yaal Luka Edrey Gatignol
 */

public class PollutionStatistics {

    private BorderPane borderPane;
    private VBox chartBox;
    private Chart chart;

    private String fromYearSelected;
    private String toYearSelected;

    private DataAggregator dataAggregator;

    private GridPane rightBar;
    private Set<String> selectedPollutants = new HashSet<>();

    private Label titleLabel;

    private Label pollutantLabel;
    private Label fromYearLabel;
    private Label toYearLabel;
    private ComboBox<String> fromYearComboBox;
    private ComboBox<String> toYearComboBox;
    private HBox toggleButtons;
    private Label maxLabel;
    private Label maxGridCode;
    private Label minLabel;
    private Label minGridCode;
    

    private Label chartTypeLabel;
    private ComboBox<String> chartTypeComboBox;

    /**
     * Constructor for PollutionStatistics.
     * Initializes UI components, sets up event handlers for user input, and prepares the layout.
     *
     * @param dataAggregator the data source for pollution data
     */

    public PollutionStatistics(DataAggregator dataAggregator) {
        this.dataAggregator = dataAggregator;
        System.out.println(dataAggregator);
        borderPane = new BorderPane();

        chartBox = new VBox();
        chart = new Chart();
        chartBox.getChildren().add(chart.getChartBox());
        
        VBox.setVgrow(chart.getChartBox(), Priority.ALWAYS);
        chartBox.setFillWidth(true);
        
        borderPane.setCenter(chartBox);

        rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setMinWidth(375);

        titleLabel = new Label("Statistics Panel");
        titleLabel.getStyleClass().add("titleLabel");

        pollutantLabel = new Label("Select Pollutant(s):");
        pollutantLabel.getStyleClass().add("pollutantLabelSts");

        ToggleButton pm25Toggle = new ToggleButton("PM2.5");
        pm25Toggle.getStyleClass().add("pm25Toggle");
        ToggleButton no2Toggle = new ToggleButton("NO2");
        no2Toggle.getStyleClass().add("no2Toggle");
        ToggleButton pm10Toggle = new ToggleButton("PM10");
        pm10Toggle.getStyleClass().add("pm10Toggle");

        pm25Toggle.setOnAction(e -> togglePollutant("pm2.5", pm25Toggle));
        no2Toggle.setOnAction(e -> togglePollutant("no2", no2Toggle));
        pm10Toggle.setOnAction(e -> togglePollutant("pm10", pm10Toggle));

        toggleButtons = new HBox(10, pm25Toggle, no2Toggle, pm10Toggle);

        fromYearLabel = new Label("From Year:");
        fromYearLabel.getStyleClass().add("fromYearLabelSts");

        fromYearComboBox = new ComboBox<>();
        fromYearComboBox.setPromptText("Select Start Year");
        fromYearComboBox.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");
        fromYearComboBox.getStyleClass().add("fromYearComboBoxSts");

        toYearLabel = new Label("To Year:");
        toYearLabel.getStyleClass().add("toYearLabelSts");

        toYearComboBox = new ComboBox<>();
        toYearComboBox.setPromptText("Select End Year");
        toYearComboBox.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");
        toYearComboBox.getStyleClass().add("toYearComboBoxSts");
        
        chartTypeLabel = new Label("Chart Type:");
        chartTypeLabel.getStyleClass().add("chartTypeLabelSts");

        chartTypeComboBox = new ComboBox<>();
        chartTypeComboBox.setPromptText("Select Chart Type");
        chartTypeComboBox.getItems().addAll("Line Chart", "Bar Chart", "Pie Chart");
        chartTypeComboBox.getSelectionModel().select("Line Chart"); // Default selection
        chartTypeComboBox.getStyleClass().add("chartTypeComboBoxSts");

        maxLabel = new Label("Highest pollution level: 0.0 µg/m³");
        maxGridCode = new Label("Gridcode: N/A");
        maxLabel.getStyleClass().add("maxLabelSts");
        maxGridCode.getStyleClass().add("maxGridCodeSts");

        minLabel = new Label("Lowest pollution level: 0.0 µg/m³");
        minGridCode = new Label("Gridcode: N/A");
        minLabel.getStyleClass().add("minLabelSts");
        minGridCode.getStyleClass().add("minGridCodeSts");

        fromYearComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    fromYearSelected = newVal;
                    validateYearSelection(fromYearSelected, toYearSelected);
                    updateChart();
        });

        toYearComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    toYearSelected = newVal;
                    validateYearSelection(fromYearSelected, toYearSelected);
                    updateChart();
        });
        
        chartTypeComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    updateChartType(newVal);
                    updateChart();
        });

        updateSideBar();  
        borderPane.setRight(rightBar);
    }
    
    /**
     * Updates the sidebar for the pie chart to remove the toYearComboBox
     * and to reset the selections.
     */
    private void updateSideBar() {  
        rightBar.getChildren().clear();
        if (chart.getCurrentChartType() == Chart.ChartType.PIE) {
            // Reset selections and takes off toYear when switching to pie chart
            toYearComboBox.setValue(null);

            rightBar.add(titleLabel, 0, 0);
            rightBar.add(new Label(" "), 0, 1);
            rightBar.add(pollutantLabel, 0, 2);
            rightBar.add(toggleButtons, 0, 3);
            rightBar.add(new Label(" "), 0, 4);
            rightBar.add(chartTypeLabel, 0, 5);
            rightBar.add(chartTypeComboBox, 0, 6);
            rightBar.add(new Label(" "), 0, 7);
            rightBar.add(new Label("Select Year:"), 0, 8); 
            rightBar.add(fromYearComboBox, 0, 9);
            rightBar.add(new Label(" "), 0, 10);
            rightBar.add(new Label(" "), 0, 11);
            rightBar.add(new Label(" "), 0, 12);
            rightBar.add(new Label(" "), 0, 13);
            rightBar.add(new Label(" "), 0, 14); 
            rightBar.add(maxLabel, 0, 15);
            rightBar.add(maxGridCode, 0, 16);
            rightBar.add(new Label(" "), 0, 17);
            rightBar.add(minLabel, 0, 18);
            rightBar.add(minGridCode, 0, 19);
        } else {
            rightBar.add(titleLabel, 0, 0);
            rightBar.add(new Label(" "), 0, 1);
            rightBar.add(pollutantLabel, 0, 2);
            rightBar.add(toggleButtons, 0, 3);
            rightBar.add(new Label(" "), 0, 4);
            rightBar.add(chartTypeLabel, 0, 5);
            rightBar.add(chartTypeComboBox, 0, 6);
            rightBar.add(new Label(" "), 0, 7);
            rightBar.add(fromYearLabel, 0, 8);
            rightBar.add(fromYearComboBox, 0, 9);
            rightBar.add(new Label(" "), 0, 10);
            rightBar.add(toYearLabel, 0, 11);
            rightBar.add(toYearComboBox, 0, 12);
            rightBar.add(new Label(" "), 0, 13);
            rightBar.add(new Label(" "), 0, 14); 
            rightBar.add(maxLabel, 0, 15);
            rightBar.add(maxGridCode, 0, 16);
            rightBar.add(new Label(" "), 0, 17);
            rightBar.add(minLabel, 0, 18);
            rightBar.add(minGridCode, 0, 19);
        }
    }

    private void updateChart() {
        // For pie chart, only the fromYearComboBox is used
        if (chart.getCurrentChartType() == Chart.ChartType.PIE) {
            if (fromYearSelected == null || selectedPollutants.isEmpty()) {
                // Clear chart data
                HashMap<String, DataSet> emptyData = new HashMap<>();
                chart.updateChart(emptyData);
                return;
            }

            int year = Integer.parseInt(fromYearSelected);
            HashMap<String, DataSet> data = new HashMap<>();
            double maxPolTempValue = Double.MIN_VALUE;
            double minPolTempValue = Double.MAX_VALUE;
            String maxLocTempValue = "N/A";
            String minLocTempValue = "N/A";

            // Iterate through selected pollutants and get their data for the selected year
            for (String pollutant : selectedPollutants) {
                DataSet pollutantData = dataAggregator.getCityDataSet("London", Integer.toString(year), pollutant);
                data.put(year + "-" + pollutant, pollutantData);

                for (DataPoint dataPoint : pollutantData.getData()) {
                    double value = dataPoint.value();
                    int gridCode = dataPoint.gridCode();

                    if (value > maxPolTempValue) {
                        maxPolTempValue = value;
                        maxLocTempValue = ((Integer) gridCode).toString();
                    }
                    if (value < minPolTempValue) {
                        minPolTempValue = value;
                        minLocTempValue = ((Integer) gridCode).toString();
                    }
                }
            }

            // Display the results
            chart.updateChart(data);
            maxLabel.setText("Highest pollution level: " + maxPolTempValue + " µg/m³");
            minLabel.setText("Lowest pollution level: " + minPolTempValue + " µg/m³");
            maxGridCode.setText("Grid Code: " + maxLocTempValue);
            minGridCode.setText("Grid Code: " + minLocTempValue);
        } else {
            // For line and bar charts, both fromYearComboBox and toYearComboBox are used

            // Check if both years are selected and if any pollutants are selected
            if (fromYearSelected == null || toYearSelected == null || selectedPollutants.isEmpty()) {
                // Clear chart data
                HashMap<String, DataSet> emptyData = new HashMap<>();
                chart.updateChart(emptyData);
                return;
            }

            if (!validateYearSelection(fromYearSelected, toYearSelected)) {
                return;
            }

            int startYear = Integer.parseInt(fromYearSelected);
            int endYear = Integer.parseInt(toYearSelected);
            HashMap<String, DataSet> data = new HashMap<>();
            double maxPolTempValue = Double.MIN_VALUE;
            double minPolTempValue = Double.MAX_VALUE;
            String maxLocTempValue = "N/A";
            String minLocTempValue = "N/A";

            // Iterate through selected pollutants and get their data for the selected year range
            for (String pollutant : selectedPollutants) {
                HashMap<String, DataSet> pollutantData = dataSetRange(pollutant, startYear, endYear);
                data.putAll(pollutantData);

                for (DataSet dataSet : pollutantData.values()) {
                    for (DataPoint dataPoint : dataSet.getData()) {
                        double value = dataPoint.value();
                        int gridCode = dataPoint.gridCode();

                        if (value > maxPolTempValue) {
                            maxPolTempValue = value;
                            maxLocTempValue = ((Integer) gridCode).toString();
                        }
                        if (value < minPolTempValue) {
                            minPolTempValue = value;
                            minLocTempValue = ((Integer) gridCode).toString();
                        }
                    }
                }
            }

            // Display the results
            chart.updateChart(data);
            maxLabel.setText("Highest pollution level: " + maxPolTempValue + " µg/m³");
            minLabel.setText("Lowest pollution level: " + minPolTempValue + " µg/m³");
            maxGridCode.setText("Grid Code: " + maxLocTempValue);
            minGridCode.setText("Grid Code: " + minLocTempValue);
        }
    }

    /**
     * Validates the year selection to ensure that the 'To Year' is not earlier than the 'From Year'.
     *
     * @param fromYear the selected 'From Year'
     * @param toYear   the selected 'To Year'
     * @return true if the selection is valid, false otherwise
     */
    private boolean validateYearSelection(String fromYear, String toYear) {
        if (fromYear != null && toYear != null) {
            int from = Integer.parseInt(fromYear);
            int to = Integer.parseInt(toYear);

            if (to < from) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Year Selection");
                alert.setHeaderText("Invalid Range");
                alert.setContentText("The 'To Year' cannot be earlier than the 'From Year'!");
                alert.showAndWait();
    
                // Reset the 'To Year' selection
                toYearSelected = null;
                javafx.application.Platform.runLater(() -> {
                    toYearComboBox.valueProperty().set(null);
                });
                
                return false;
            }
        }
        return true;
    }

    /**
     * Toggles the selection of a pollutant based on the state of the corresponding ToggleButton.
     *
     * @param pollutant the name of the pollutant
     * @param button    the ToggleButton associated with the pollutant
     */
    private void togglePollutant(String pollutant, ToggleButton button) {
        if (button.isSelected()) {
            selectedPollutants.add(pollutant);
        } else {
            selectedPollutants.remove(pollutant);
        }
        updateChart();
    }

    /**
     * Updates the chart type based on the selected option from the ComboBox.
     *
     * @param chartTypeName the name of the selected chart type
     */
    private void updateChartType(String chartTypeName) {
        Chart.ChartType chartType;
        switch (chartTypeName) {
            case "Bar Chart":
                chartType = Chart.ChartType.BAR;
                break;
            case "Pie Chart":
                chartType = Chart.ChartType.PIE;
                break;
            default:
                chartType = Chart.ChartType.LINE;
                break;
        }
        chart.setChartType(chartType);
        updateSideBar();
    }

    /**
     * Returns the selected pollutants.
     *
     * @return a set of selected pollutants
     */
    private HashMap<String, DataSet> dataSetRange(String pollutant, int startYear, int endYear){
        HashMap<String, DataSet> dataRange = new HashMap<>();
        for(int i = startYear; i <= endYear; i++){
            dataRange.put(i + "-" + pollutant, dataAggregator.getCityDataSet("London", Integer.toString(i), pollutant));
        }
        return dataRange;
    }

    /**
     * Returns the BorderPane containing the chart and sidebar.
     *
     * @return the BorderPane
     */
    public BorderPane getBorderPane() {
        return borderPane;
    }
}
