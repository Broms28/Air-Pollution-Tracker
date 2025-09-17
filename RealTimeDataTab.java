import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.HashMap;


/**
 * The real time data tab
 *
 * @author Anton Davidouski
 */


public class RealTimeDataTab {

    // colours taken from a chart i found online using a colour picker
    public static final String green = "#5bc63b";
    public static final String yellow = "#f6c142";
    public static final String orange = "#ed6f2d";
    public static final String red = "#ea3223";
    public static final String purple = "#8c43f6";

    private BorderPane searchBorderPane;
    private ApiConnection apiConnection;
    private double lat;
    private double lon;
    private double[] data;
    private String lastLocation;
    private GridPane dataGrid;
    private DataComparison dataComparison;
    private int numberOfCitiesCompared = 0;
    private Label numberOfCitiesComparedLabel;
    private VBox container;

    public RealTimeDataTab() {
        apiConnection = new ApiConnection();
        dataComparison = new DataComparison();
        create();
    }

    /**
     * Creates the real time data tab.
     */
    public void create() {
        searchBorderPane = new BorderPane();

        TextField searchField = new TextField();
        searchField.setPromptText("Enter a city name...");
        searchField.setPrefWidth(300);
        searchField.getStyleClass().add("search-field");

        //searchField.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 5px; -fx-border-width: 2px; -fx-font-size: 14px");

        HBox searchBox = new HBox(10);

        searchBox.setPadding(new Insets(20));
        searchBox.getChildren().addAll(searchField);
        searchBox.setAlignment(Pos.CENTER);

        // needs to be in a container so it can be centered
        container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);
        container.getChildren().add(searchBox);

        dataGrid = new GridPane();
        dataGrid.setHgap(20);
        dataGrid.setVgap(20);
        dataGrid.setPadding(new Insets(20));
        dataGrid.setAlignment(Pos.CENTER); // gridpane to hold the pollution blocks

        numberOfCitiesComparedLabel = new Label("Cities in comparison: " + numberOfCitiesCompared);
        numberOfCitiesComparedLabel.setVisible(false);
        numberOfCitiesComparedLabel.getStyleClass().add("cities-compared-label");

        Button compareAddButton = new Button("Add to comparison");
        compareAddButton.setVisible(false);
        compareAddButton.setOnMousePressed(event -> addCityToCompare());
        compareAddButton.getStyleClass().add("compare-add-button");

        Button compareRemoveButton = new Button("Remove from comparison");
        compareRemoveButton.setVisible(false);
        compareRemoveButton.setOnMousePressed(event -> removeCityFromCompare());
        compareRemoveButton.getStyleClass().add("compare-remove-button");

        Button chartButton = new Button("Chart");
        chartButton.setVisible(false);
        chartButton.setOnMousePressed(event -> displayChart());
        chartButton.getStyleClass().add("chart-button");

        container.getChildren().addAll(dataGrid, numberOfCitiesComparedLabel, compareAddButton, compareRemoveButton, chartButton);

        ProgressIndicator spinner = new ProgressIndicator(); // loading spinner
        spinner.setMaxSize(50, 50);
        spinner.setVisible(false);

        searchBorderPane.setCenter(container);
        searchBorderPane.setTop(spinner);

        ContextMenu suggestionsPopup = new ContextMenu(); // for search suggestions

        PauseTransition pause = new PauseTransition(Duration.millis(500));
        // pause will run when user stops typing - only then make
        // api call to get suggestions so to not hit rate limit
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.stop(); // restart the pause
            lat = 0; // reset the last accessed lat and lon
            lon = 0;

            if (newValue.trim().isEmpty()) {
                suggestionsPopup.hide();
                return;
            }

            pause.setOnFinished(event -> {
                spinner.setVisible(true);

                Task<HashMap<String, double[]>> apiCall = new Task<>() { // api call in separate thread so loading spinner can show
                    @Override
                    protected HashMap<String, double[]> call() {
                        return apiConnection.getTopLocationsForSearch(newValue.trim());
                    }
                };

                apiCall.setOnSucceeded(event1 -> {
                    HashMap<String, double[]> results = apiCall.getValue();
                    spinner.setVisible(false);
                    suggestionsPopup.getItems().clear();

                    if (results != null && results.size() > 0) {
                        for (String result : results.keySet()) {
                            MenuItem item = new MenuItem(result);
                            item.setOnAction(actionEvent -> {
                                compareAddButton.setVisible(true);
                                compareRemoveButton.setVisible(true); // make all buttons visible if this is the search the user has made
                                chartButton.setVisible(true);
                                numberOfCitiesComparedLabel.setVisible(true);

                                searchField.setText(result);
                                searchField.getParent().requestFocus(); // remove focus from search field

                                suggestionsPopup.hide();

                                lat = results.get(result)[0]; // set the lat and lon for the location clicked
                                lon = results.get(result)[1];

                                lastLocation = result; // store formatted location name
                                displayData(spinner); // display the data for the location, pass the spinner in as another call will be made there too
                            });
                            suggestionsPopup.getItems().add(item); // add all suggestions to the context menu
                        }
                    } else {
                        MenuItem item = new MenuItem("No results found");
                        suggestionsPopup.getItems().add(item);
                    }

                    if (!suggestionsPopup.isShowing()) {
                        suggestionsPopup.show(searchField, Side.BOTTOM, 0, 0); // display the context menu under the search field
                    }
                    spinner.setVisible(false);
                });

                new Thread(apiCall).start(); // start the api call
            });
            pause.playFromStart(); // start the pause
        });

        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // hide the context menu if the search field loses focus
                pause.stop();
                suggestionsPopup.hide();
            }
        });

    }

    /**
     * Displays the data for a given location.
     * @param spinner The loading spinner.
     */
    public void displayData(ProgressIndicator spinner) {
        spinner.setVisible(true);
        Task<Void> apiCall = new Task<>() {
            @Override
            protected Void call() {
                data = apiConnection.getDataForLatLon(lat, lon);
                return null;
            }
        };

        apiCall.setOnSucceeded(event -> {
            spinner.setVisible(false);
            renderData(data, dataGrid);
        });
        new Thread(apiCall).start();
    }

    /**
     * Gets the main border pane to return to the main application.
     * @return The pane.
     */
    public BorderPane getPane() {
        return searchBorderPane;
    }


    /**
     * Renders the data for a given location.
     * @param data The data to render.
     * @param dataGrid The grid to render the data in.
     */
    private void renderData(double[] data, GridPane dataGrid) {
        dataGrid.getChildren().clear(); // empty the grid

        if (data == null) { // means the api call failed somehow, so let the user know
            dataGrid.add(new Text("Failed to retrieve data."), 0, 0);
            return;
        }

        String[] labels = {"AQI", "CO", "NO2", "O3", "SO2", "PM2.5", "PM10", "NH3"};

        for (int i = 0; i < data.length; i++) {
            // for each data point, create a rectangle with the colour based on the value
            double value = data[i];
            String label = labels[i];

            Rectangle rect = new Rectangle(100, 100);
            rect.setArcWidth(25);
            rect.setArcHeight(25);
            rect.setFill(getColor(value, label));

            Text labelText = new Text(label);
            labelText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            labelText.setFill(Color.WHITE);

            Text valueText = new Text(String.format("%.1f", value)); // round to 1 dp
            valueText.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            valueText.setFill(Color.WHITE);

            VBox boxContent = new VBox(5, labelText, valueText);
            boxContent.setAlignment(Pos.CENTER);

            StackPane box = new StackPane(rect, boxContent);

            int row = i / 4;
            int col = i % 4;
            dataGrid.add(box, col, row);
        }
    }

    /**
     * Adds a city to the comparison chart
     */
    private void addCityToCompare() {
        if (dataComparison.addCityData(lastLocation, data)) {
            numberOfCitiesCompared++;
            numberOfCitiesComparedLabel.setText("Cities in comparison: " + numberOfCitiesCompared);
        }
    }

    /**
     * Removes a city from the comparison chart
     */
    private void removeCityFromCompare() {
        if (dataComparison.removeCityData(lastLocation)) {
            numberOfCitiesCompared--;
            numberOfCitiesComparedLabel.setText("Cities in comparison: " + numberOfCitiesCompared);
        }
    }

    /**
     * Displays the comparison chart
     */
    private void displayChart() {
        if (numberOfCitiesCompared < 2) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Not enough data");
            alert.setContentText("Please add at least two cities to compare.");
            alert.show();
            return;
        }
        BorderPane chart = dataComparison.createChart();
        Button back = new Button("Back");
        back.getStyleClass().add("backButton");
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(back);
        back.setOnMousePressed(event -> hideChart());
        chart.setTop(buttonBox);
        searchBorderPane.setCenter(chart);
    }

    /**
     * Hides the comparison chart
     */
    private void hideChart() {
        searchBorderPane.setCenter(container);
    }

    /**
     * Gets the colour for a given value and pollutant.
     * @param value The value.
     * @param pollutant The pollutant.
     * @return The colour.
     */
    private Color getColor(double value, String pollutant) {
        // ranges taken from API source's own documentation:
        // https://openweathermap.org/api/air-pollution
        switch (pollutant) {
            case "AQI":
                return switch ((int) value) {
                    case 1 -> Color.web(green);
                    case 2 -> Color.web(yellow);
                    case 3 -> Color.web(orange);
                    case 4 -> Color.web(red);
                    case 5 -> Color.web(purple);
                    default -> Color.BLACK;
                };
            case "SO2":
                if (value < 20) return Color.web(green);
                else if (value < 80) return Color.web(yellow);
                else if (value < 250) return Color.web(orange);
                else if (value < 350) return Color.web(red);
                else return Color.web(purple);
            case "NO2":
                if (value < 40) return Color.web(green);
                else if (value < 70) return Color.web(yellow);
                else if (value < 150) return Color.web(orange);
                else if (value < 200) return Color.web(red);
                else return Color.web(purple);
            case "PM10":
                if (value < 20) return Color.web(green);
                else if (value < 50) return Color.web(yellow);
                else if (value < 100) return Color.web(orange);
                else if (value < 200) return Color.web(red);
                else return Color.web(purple);
            case "PM2.5":
                if (value < 10) return Color.web(green);
                else if (value < 25) return Color.web(yellow);
                else if (value < 50) return Color.web(orange);
                else if (value < 75) return Color.web(red);
                else return Color.web(purple);
            case "O3":
                if (value < 60) return Color.web(green);
                else if (value < 100) return Color.web(yellow);
                else if (value < 140) return Color.web(orange);
                else if (value < 180) return Color.web(red);
                else return Color.web(purple);
            case "CO":
                if (value < 4400) return Color.web(green);
                else if (value < 9400) return Color.web(yellow);
                else if (value < 12400) return Color.web(orange);
                else if (value < 15400) return Color.web(red);
                else return Color.web(purple);
            case "NH3":
                if (value < 40) return Color.web(green);
                else if (value < 80) return Color.web(yellow);
                else if (value < 120) return Color.web(orange);
                else if (value < 160) return Color.web(red);
                else return Color.web(purple);
            case "NO":
                if (value < 20) return Color.web(green);
                else if (value < 40) return Color.web(yellow);
                else if (value < 60) return Color.web(orange);
                else if (value < 80) return Color.web(red);
                else return Color.web(purple);
            default:
                return Color.BLACK;
        }
    }
}
