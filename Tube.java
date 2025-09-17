import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Manages the Tube map and user interactions for journey planning.
 * This class provides a graphical interface for users to enter starting and ending stations,
 * calculate the journey, and display air pollution data related to the journey.
 *
 * @author Rom Steinberg
 * @version 28.7
 */

public class Tube {
    private DataAggregator dataAggregator;
    private MapImage map;
    private Image mapImage;
    private ImageView mapView;
    private AnchorPane anchorPane;
    private BorderPane borderPane;
    private String endStn;
    private String startStn;
    private TubeSystem tubeSystem;
    private ErrorHandler errorHandler;
    private VBox journeyInfoContainer;
    private Label journeyLabel;
    private TextArea journeyDetailsText;
    private Button viewDetailsButton;
    

    public Tube(DataAggregator dataAggregator, ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.dataAggregator = dataAggregator;
        tubeSystem = new TubeSystem();
        create();
    }

    public void create() { 
        map = new MapImage("N/A", "resources/TubeMap.png");
        mapImage = map.getImage();
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(true);
        mapView.setFitWidth(500);

        // Create an AnchorPane to contain the map and allow resizing
        anchorPane = new AnchorPane();
        anchorPane.getChildren().add(mapView);
        anchorPane.setMinWidth(500);
        anchorPane.setMinHeight(300);
        mapView.fitWidthProperty().bind(anchorPane.widthProperty());
        mapView.fitHeightProperty().bind(anchorPane.heightProperty());

        // Create a BorderPane layout and place the map in the center
        borderPane = new BorderPane();
        borderPane.setCenter(anchorPane);

        // Create a right-hand control panel using a GridPane
        GridPane rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setPrefWidth(250);
        rightBar.setMinWidth(150);
        rightBar.setMaxWidth(300);

        // Create labels and text fields for user input
        Label startStnLabel = new Label("Enter Starting Station:");
        TextField startStnText = new TextField();
        startStnText.setMaxWidth(150);
        startStnText.setPromptText("Type Station Name");
        startStnLabel.getStyleClass().add("startStnLabel");
        startStnText.getStyleClass().add("startStnText");

        Label endStnLabel = new Label("Enter End Station:");
        TextField endStnText = new TextField();
        endStnText.setMaxWidth(150);
        endStnText.setPromptText("Type Station Name");
        endStnLabel.getStyleClass().add("endStnLabel");
        endStnText.getStyleClass().add("endStnText");

        // Journey submission button
        Button button = new Button("Check Journey");
        button.getStyleClass().add("checkButton");

        // Handle Enter key press event to trigger journey submission
        borderPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                submitAction(startStnText, endStnText);
            }
        });

        // Handle button click event to trigger journey submission
        button.setOnAction(e -> {
            submitAction(startStnText, endStnText);
        });
        
        // Create a button for viewing journey details (initially hidden)
        viewDetailsButton = new Button("View Journey Details");
        viewDetailsButton.setVisible(false);
        viewDetailsButton.setOnAction(e -> showJourneyDetailsPopup());
        viewDetailsButton.getStyleClass().add("viewDetailsButton");

        // Create a container for journey information display
        journeyInfoContainer = new VBox(10);
        journeyInfoContainer.setPadding(new Insets(10));
        journeyInfoContainer.setVisible(false);

        // Create a label for journey information
        journeyLabel = new Label();
        journeyLabel.setVisible(false);
        journeyLabel.setWrapText(true);
        journeyLabel.setPadding(new Insets(10));

        // Create a text area for detailed journey information
        journeyDetailsText = new TextArea();
        journeyDetailsText.setEditable(false);
        journeyDetailsText.setWrapText(true);
        journeyDetailsText.setPrefHeight(300);
        journeyDetailsText.setPrefWidth(300);

        // Add journey label to the information container
        journeyInfoContainer.getChildren().addAll(journeyLabel);

        // Add UI elements to the right panel (GridPane) + spacing and layout
        rightBar.add(startStnLabel, 0, 0);
        rightBar.add(startStnText, 0, 1);
        rightBar.add(endStnLabel, 0, 2);
        rightBar.add(endStnText, 0, 3);
        rightBar.add(button, 0, 4);
        rightBar.add(journeyLabel, 0, 6);
        rightBar.add(viewDetailsButton, 0, 7);
        GridPane.setMargin(viewDetailsButton, new Insets(10, 0, 0, 0));
        GridPane.setMargin(startStnLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(startStnText, new Insets(0, 0, 10, 0));
        GridPane.setMargin(endStnLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(endStnText, new Insets(0, 0, 10, 0));
        GridPane.setMargin(button, new Insets(10, 0, 0, 0));
        GridPane.setMargin(journeyInfoContainer, new Insets(10, 0, 0, 0));

        borderPane.setRight(rightBar);
    }

    /**
     * Handles the submission of the journey details entered by the user.
     * It validates the input station names, processes them, and enables the journey details button.
     *
     * @param startStnText TextField for the starting station input
     * @param endStnText TextField for the destination station input
     */
    private void submitAction(TextField startStnText, TextField endStnText) {
        startStn = startStnText.getText().toLowerCase(); 
        endStn = endStnText.getText().toLowerCase();
        startStn = startStn.replaceAll("and", "&").replaceAll("'", "");
        endStn = endStn.replaceAll("and", "&").replaceAll("'", "");
        
        // Validate the input, might return an error msg
        if (startStn.isEmpty() || endStn.isEmpty() || getTubeDataSet().findStationData(startStn.trim()) == null || getTubeDataSet().findStationData(endStn.trim()) == null) {
            errorHandler.showError("Please enter a valid station name inside zone 1.");
            return;
        }

        calculateJourney();
        viewDetailsButton.setVisible(true);
    }

    /**
    * @return The tube dataset
    */
    private TubeDataSet getTubeDataSet() {
        return dataAggregator.getTubeDataSet();
    }

    /**
     * Displays a popup window containing the journey details.
     */
    private void showJourneyDetailsPopup() {
        Stage popupStage = new Stage();
        popupStage.setTitle("Journey Details");

        VBox popupLayout = new VBox(10);
        popupLayout.setPadding(new Insets(10));
        popupLayout.getStyleClass().add("popupLayout");

        // Create a text area to display journey details
        TextArea journeyTextArea = new TextArea(journeyDetailsText.getText());
        journeyTextArea.setEditable(false);
        journeyTextArea.setWrapText(true);
        journeyTextArea.setPrefSize(400, 300); // Set the preferred size of the text area
        journeyTextArea.getStyleClass().add("journeyTextArea");

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());
        closeButton.getStyleClass().add("closeButton");

        popupLayout.getChildren().addAll(journeyTextArea, closeButton);

        Scene popupScene = new Scene(popupLayout, 420, 350);
        popupStage.setScene(popupScene);
        popupStage.show(); // Display the popup window
        popupScene.getStylesheets().add("style.css");
    }


    /**
     * Converts the user's journey into data points related to air pollution levels.
     * It calculates total and average PM2.5 pollution levels for underground and street level,
     * formats the journey details, and updates the UI components accordingly.
     */
    private void calculateJourney(){
        List<String> journey = tubeSystem.calculateJourney(startStn, endStn);
        double totalBG = 0; // Total underground pollution level
        double totalOG = 0; // Total street-level pollution level

        String journeyDetails = "Your journey goes through*: \n";
        journeyDetails += "------------------------------\n";

        // Iterate through each station in the journey
        for (String station : journey) {
            TubeDataPoint tdp = getTubeDataSet().findStationData(station);
            totalBG += tdp.tubeData();
            totalOG += tdp.streetData();

            journeyDetails += station + " (" + tdp.tubeData() +"ug m^3 PM2.5)" + "\n";
        }

        // Calculate and add average pollution levels
        journeyDetails += "------------------------------\n";
        journeyDetails += "Average pollution below ground: " + ((int)(totalBG * 100)/100)/journey.size() + "\n";
        journeyDetails += "Average pollution on street level: " + (Math.round(totalOG * 100)/100)/journey.size();

        journeyDetails += "\n\n**Note: The data is an estimate taken from a research made in 2020 for PM2.5 pollution levels in selected stations around London.";
        journeyDetails += "\nThe research can be found in this link: https://content.tfl.gov.uk/dust-monitoring-lu-stations.pdf";

        journeyLabel.setText("By taking the Tube you will be exposed to " +Math.round(totalBG/(Math.round(totalOG * 100)/100)) + " times more PM2.5 compared to street level." + "\n");

        journeyDetailsText.setText(journeyDetails);
        journeyLabel.setVisible(true);
        journeyInfoContainer.setVisible(true);
    }


    /**
     * @return The BorderPane containing the map and controls
     */
    public BorderPane getPane() {
        return borderPane;
    }
    
}
