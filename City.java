import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The City class encapsulates the user interface and data handling for displaying
 * city maps with pollution data overlays. It provides UI components for selecting pollutants,
 * years, and switching between cities. The class manages mouse event tracking
 * and updates the map overlay based on environmental data retrieved from a data aggregator.
 * 
 * @author Rom Steinberg, Anton Davidouski, Nicolás Alcalá Olea
 * @version 28.7
 */

public class City {
    private DataAggregator dataAggregator;
    private DataSet selectedDataSet;
    private MapImage map;
    private Image mapImage;
    private ImageView mapView;
    private AnchorPane anchorPane;
    private GridPane rightBar;
    private double mapImageAspectRatio;
    private Label dataPointValue;
    private Label gridCodeValue;
    private Label xValue;
    private Label yValue;
    private String pollutantSelected;
    private ComboBox<String> cityComboBox;
    private ComboBox<String> yearComboBox;
    private ComboBox<String> pollutantComboBox;
    private String yearSelected;
    private BorderPane borderPane;
    protected String name;
    private Button predictionButton;
    private Label lowLabel;
    private Label highLabel;
    private static final HashMap<String, int[]> CITY_BOUNDARIES = new HashMap<>();

    static {
        // The boundaries for different cities
        CITY_BOUNDARIES.put("London", new int[]{510394, 554000, 168000, 194000, 1});
        CITY_BOUNDARIES.put("Manchester", new int[]{376000, 390901, 393400, 401667, 3});
        CITY_BOUNDARIES.put("Birmingham", new int[]{401000, 415930, 282200, 290530, 3});
        CITY_BOUNDARIES.put("Leeds", new int[]{421070, 436570, 430350, 438580, 3});
        CITY_BOUNDARIES.put("Bristol", new int[]{354400, 369550, 169150, 177650, 3});
    }

    private int mouseX;
    private int mouseY;

    /**
     * Constructs a new City instance with the specified city name, bounds, and a
     * data aggregator for retrieving a data set of the cities pollution.
     *
     * @param cityName       the name of the city
     * @param dataAggregator the data aggregator instance used to retrieve a data set of the cities pollution
     */
    public City(String cityName, DataAggregator dataAggregator) {
        this.dataAggregator = dataAggregator;
        this.name = cityName;
        create(name);
        trackMouseLocation();
    }

    /**
     * Initializes and creates the city tab view, including the map image, control panels,
     * and UI components for pollutant and year selection.
     *
     * @param name the name of the city to be displayed
     */
    public void create(String name) {

        map = new MapImage(name,"resources/" + name + ".png");
        mapImage = map.getImage();
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(true);
        mapView.setFitWidth(500);
        mapImageAspectRatio = mapImage.getWidth() / mapImage.getHeight();
        mapView.getStyleClass().add("mapImage");


        anchorPane = new AnchorPane();
        anchorPane.getChildren().add(mapView);
        anchorPane.setMinWidth(500);
        anchorPane.setMinHeight(300);
        mapView.fitWidthProperty().bind(anchorPane.widthProperty());
        mapView.fitHeightProperty().bind(anchorPane.heightProperty());
        anchorPane.getStyleClass().add("anchorPane");

        borderPane = new BorderPane();
        borderPane.setCenter(anchorPane);
        BorderPane.setMargin(anchorPane, new Insets(5,0,5,5));
        borderPane.getStyleClass().add("borderPane");

        rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setPrefWidth(250);
        rightBar.setMinWidth(150);
        rightBar.setMaxWidth(300);
        rightBar.getStyleClass().add("rightBar");

        // Create the city selector if the current city is not London.
        if(!name.equals("London")){
            createCitySelector();
        }

        Label pollutantLabel = new Label("Choose a pollutant:");
        pollutantLabel.getStyleClass().add("pollutantLabel");
        pollutantComboBox = new ComboBox<>();
        pollutantComboBox = new ComboBox<>();
        pollutantComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    pollutantSelected = newValue;
                    pollutantComboBox.setPromptText(pollutantSelected);
                    updateColourMap();
                });
        pollutantComboBox.setPromptText("Pollutant");
        pollutantComboBox.getItems().addAll("pm2.5", "no2", "pm10");
        pollutantComboBox.getStyleClass().add("pollutantComboBox");

        Label yearLabel = new Label("Choose a year:");
        yearLabel.getStyleClass().add("yearLabel");
        yearComboBox = new ComboBox<>();
        yearComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    yearSelected = newValue;
                    yearComboBox.setPromptText(yearSelected);
                    updateColourMap();
                });
        yearComboBox.setPromptText("Year");
        yearComboBox.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");
        yearComboBox.getStyleClass().add("yearComboBox");

        // Prediction button for generating pollution predictions for future years.
        predictionButton = new Button("Predict");
        predictionButton.setOnAction(event -> {
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            Label instructionLabel = new Label("Please enter a year (2024 - 2030):");
            TextField yearInputField = new TextField();
            yearInputField.setPromptText("Year");
            instructionLabel.getStyleClass().add("instructionLabel");
            yearInputField.getStyleClass().add("inputField");

            Button okButton = new Button("OK");
            Button cancelButton = new Button("Cancel");
            okButton.getStyleClass().add("okButton");
            cancelButton.getStyleClass().add("cancelButton");

            HBox buttons = new HBox(10, okButton, cancelButton);
            buttons.setAlignment(Pos.CENTER);
            buttons.getStyleClass().add("buttons");

            VBox vbox = new VBox(10, instructionLabel, yearInputField, buttons);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(10));
            vbox.getStyleClass().add("vbox");

            Scene dialogScene = new Scene(vbox, 300, 150);
            dialogStage.setScene(dialogScene);
            dialogStage.getScene().getStylesheets().add("style.css");

            okButton.setOnAction(e -> {
                String inputYear = yearInputField.getText().trim();
                try {
                    int year = Integer.parseInt(inputYear);
                    if (year < 2024 || year > 2030) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Year must be between 2024 and 2030.");
                        DialogPane dialogPane = alert.getDialogPane();
                        alert.showAndWait();
                    } else {
                        if (!yearComboBox.getItems().contains(inputYear)) {
                            yearComboBox.getItems().add(inputYear);
                        }
                        dialogStage.close();
                        new Prediction(dataAggregator, inputYear);
                    }
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input. Please enter a valid year.");
                    alert.showAndWait();
                }
            });

            cancelButton.setOnAction(e -> dialogStage.close());

            dialogStage.showAndWait();
        });
        predictionButton.getStyleClass().add("predictButton");


        Label dataPointLabel = new Label("Value: ");
        dataPointValue = new Label("Select a year and pollutant");
        dataPointLabel.getStyleClass().add("dataPointLabel");
        dataPointValue.getStyleClass().add("dataPointValue");

        Label gridCodeLabel = new Label("Grid Code: ");
        gridCodeValue = new Label("Select a year and pollutant");
        gridCodeLabel.getStyleClass().add("gridCodeLabel");
        gridCodeValue.getStyleClass().add("gridCodeValue");

        xValue = new Label();
        xValue.getStyleClass().add("xValue");

        yValue = new Label();
        yValue.getStyleClass().add("yValue");

        xValue.setVisible(false);
        yValue.setVisible(false);

        mapView.setOnMouseMoved(event -> {
            xValue.setVisible(true);
            yValue.setVisible(true);
            xValue.setText("X: " + (int) event.getX());
            yValue.setText("Y: " + (int) event.getY());
            mouseX = (int) event.getX();
            mouseY = (int) event.getY();
            updateStats();
        });

        rightBar.add(pollutantLabel, 0, 2);
        rightBar.add(pollutantComboBox, 0, 3);
        rightBar.add(yearLabel, 0, 4);
        rightBar.add(yearComboBox, 0, 5);
        rightBar.add(dataPointLabel, 0, 8);
        rightBar.add(dataPointValue, 0, 9);
        rightBar.add(gridCodeLabel, 0, 10);
        rightBar.add(gridCodeValue, 0, 11);
        rightBar.add(xValue, 0, 12);
        rightBar.add(yValue, 0, 13);

        GridPane.setMargin(yearLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(yearComboBox, new Insets(0, 0, 10, 0));
        GridPane.setMargin(dataPointLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(dataPointValue, new Insets(0, 0, 10, 0));
        GridPane.setMargin(gridCodeLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(gridCodeValue, new Insets(0, 0, 10, 0));
        GridPane.setMargin(xValue, new Insets(20, 0, 10, 0));
        GridPane.setMargin(yValue, new Insets(5, 0, 10, 0));

        if("London".equals(name)){
            rightBar.add(predictionButton, 0, 6);
            GridPane.setMargin(predictionButton, new Insets(10, 0, 10, 0));
        }
        borderPane.setRight(rightBar);

        HBox aqiBarContainer = new HBox(10);
        aqiBarContainer.setAlignment(Pos.CENTER);
        aqiBarContainer.setPrefHeight(50);
        aqiBarContainer.setMinHeight(30);
        aqiBarContainer.setMaxHeight(80);
        aqiBarContainer.getStyleClass().add("aqiBarContainer");

        lowLabel = new Label("GOOD");
        lowLabel.getStyleClass().add("lowLabel");

        highLabel = new Label("POOR");
        highLabel.getStyleClass().add("highLabel");

        lowLabel.setMinWidth(Region.USE_PREF_SIZE);
        highLabel.setMinWidth(Region.USE_PREF_SIZE);

        Region aqiBar = new Region();
        aqiBar.setPrefHeight(20);
        aqiBar.setMaxWidth(Double.MAX_VALUE);
        aqiBar.getStyleClass().add("aqiBar");

        StackPane aqiStack = new StackPane();
        aqiStack.setAlignment(Pos.CENTER);
        HBox.setHgrow(aqiStack, Priority.ALWAYS);
        aqiBar.prefWidthProperty().bind(aqiStack.widthProperty());
        aqiStack.getChildren().addAll(aqiBar);

        aqiBarContainer.getChildren().addAll(lowLabel, aqiStack, highLabel);
        borderPane.setBottom(aqiBarContainer);
    }

    /**
     * @return the GridPane representing the right control panel
     */
    public GridPane getRightBar() {
        return rightBar;
    }

    /**
     * Updates the displayed statistics based on the current mouse position on the map.
     */

    private void updateStats(){
        if (selectedDataSet == null) {
            return;
        }
        int[] bounds = CITY_BOUNDARIES.get(name);
        int[] imageDimensions = convertMapViewDimensionsToImageDimensions((int) mapView.getFitWidth(), (int) mapView.getFitHeight());
        int imageWidth = imageDimensions[0];
        int imageHeight = imageDimensions[1];
        int x = (int) ((mouseX / (double) imageWidth) * (bounds[1] - bounds[0]) + bounds[0]);
        int y = (int) (bounds[3] - ((mouseY / (double) imageHeight) * (bounds[3] - bounds[2])));
        DataPoint nearestDataPoint = selectedDataSet.findNearestDataPoint(x, y);
        dataPointValue.setText(nearestDataPoint.value() + "  µg/m³");
        gridCodeValue.setText(String.valueOf(nearestDataPoint.gridCode()));
    }

    /**
     * @return the BorderPane representing the city's main view
     */

    public BorderPane getPane() {
        return borderPane;
    }

    /**
     * Updates the color overlay on the city map based on the selected pollutant and year.
     * It retrieves the corresponding dataset from the data aggregator, processes each data point
     * to apply a color mapping, and then updates the map view with a blurred overlay.
     */

    public void updateColourMap(){
        if (pollutantSelected == null || yearSelected == null) {
            return;
        }
        selectedDataSet = dataAggregator.getCityDataSet(name,yearSelected, pollutantSelected);
        lowLabel.setText(String.format("%.1f", selectedDataSet.getMin())+" MIN");
        highLabel.setText(String.format("%.1f", selectedDataSet.getMax())+" MAX");

        map.resetOverlay();
        for (DataPoint dataPoint : selectedDataSet.getData()) {
            if (dataPoint.value() > 0) {
                map.processDataPoint(dataPoint, selectedDataSet.getMin(), selectedDataSet.getMax(),CITY_BOUNDARIES.get(name)[4]);
            }
        }
        map.applyBlur(60);
        Image mapImage = map.getCombined();
        mapView.setImage(mapImage);
    }

    public static HashMap<String, int[]> getCitiesBoundaries() {
        return CITY_BOUNDARIES;
    }

    /**
     * Converts the given dimensions of the map view to the corresponding dimensions of the underlying map image.
     *
     * @param x the width of the map view
     * @param y the height of the map view
     * @return an array of two integers where index 0 is the calculated image width and index 1 is the image height
     */
    public int[] convertMapViewDimensionsToImageDimensions(int x, int y) {
        double providedAspectRatio = (double) x / y;
        int[] dimensions = new int[2];
        if (providedAspectRatio > mapImageAspectRatio) {
            dimensions[0] = (int) (y * mapImageAspectRatio);
            dimensions[1] = y;
        } else {
            dimensions[0] = x;
            dimensions[1] = (int) (x / mapImageAspectRatio);
        }
        return dimensions;
    }

    /**
     * Sets up mouse tracking on the map view. When a mouse click is detected,
     * it calculates the corresponding coordinates in the dataset and displays detailed
     * information about the nearest data point.
     */
    private void trackMouseLocation() {
        mapView.setOnMouseClicked(event -> {
            if (selectedDataSet != null) {
                int[] bounds = CITY_BOUNDARIES.get(name);
                int[] imageDimensions = convertMapViewDimensionsToImageDimensions((int) mapView.getFitWidth(), (int) mapView.getFitHeight());
                int imageWidth = imageDimensions[0];
                int imageHeight = imageDimensions[1];
                int x = (int) ((mouseX / (double) imageWidth) * (bounds[1] - bounds[0]) + bounds[0]);
                int y = (int) (bounds[3] - ((mouseY / (double) imageHeight) * (bounds[3] - bounds[2])));
                DataPoint nearestDataPoint = selectedDataSet.findNearestDataPoint(x, y);
                showDataPointInfo(nearestDataPoint);
            }
        });
    }

    /**
     * Creates and configures the city selection UI component.
     * When a new city is selected, the view is updated accordingly.
     */
    public void createCitySelector() {
        Label cityLabel = new Label("Choose a city:");
        cityComboBox = new ComboBox<>();
        cityComboBox.setPromptText(name);
        cityLabel.getStyleClass().add("cityLabel");

        Set<String> cities = new HashSet<>(CITY_BOUNDARIES.keySet());
        cities.remove("London"); // Remove London from the list
        cities.remove(name); // Remove the current city from the list
        cityComboBox.getItems().addAll(cities);
        cityComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    updateCity(newValue);
                    updateColourMap();
                    if(yearSelected != null)
                        yearComboBox.setPromptText(yearSelected);
                    if(pollutantSelected != null)
                        pollutantComboBox.setPromptText(pollutantSelected);
                });
        GridPane.setMargin(cityComboBox, new Insets(0, 0, 10, 0));
        getRightBar().add(cityLabel, 0, 0);
        getRightBar().add(cityComboBox, 0, 1);

        cityComboBox.getStyleClass().add("cityComboBox");
    }

    /**
     * Updates the current city to the specified city name and refreshes the UI accordingly.
     *
     * @param cityName the name of the city to switch to
     */
    private void updateCity(String cityName) {
        // Update city name based on provide value
        switch (cityName) {
            case "Manchester":
                this.name = "Manchester";
                break;
            case "Birmingham":
                this.name = "Birmingham";
                break;
            case "Leeds":
                this.name = "Leeds";
                break;
            case "Bristol":
                this.name = "Bristol";
                break;

            default:
                break;
        }
        create(name);
        trackMouseLocation();
        AppWindow.setUKCities(this);
    }

    /**
     * Displays an informational alert dialog with details about the specified data point.
     *
     * @param dataPoint the data point whose information is to be displayed
     */
    private void showDataPointInfo(DataPoint dataPoint) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Data Point Information");
        alert.setHeaderText(null);
        alert.setContentText("Grid Code: " + dataPoint.gridCode() + "\nX: " + dataPoint.x() + "\nY: " + dataPoint.y() + "\nValue: " + dataPoint.value());
        alert.showAndWait();
    }

    /**
     * @return the data aggregator used for accessing environmental data
     */
    public DataAggregator getDataAggregator() {
        return dataAggregator;
    }
}
