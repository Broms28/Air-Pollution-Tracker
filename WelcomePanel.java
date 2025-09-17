import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * The WelcomePanel class is responsible for creating and displaying
 * the welcome screen of the London Air Pollution Viewer application.
 * It sets up the layout using a BorderPane and populates the top, center,
 * and bottom sections with appropriate UI elements and styles.
 */

public class WelcomePanel {

    private BorderPane root;

    /**
     * Creates the welcome panel with all its attributes and configures the
     * primary Stage to display it.
     *
     * @param firstStage The primary Stage on which the welcome panel will be set and displayed.
     */

    public void createWelcomePanel(Stage firstStage) {
        root = new BorderPane();
        root.getStyleClass().add("welcomePanel");

        Scene scene = new Scene(root, 800, 450);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        firstStage.setScene(scene);
        firstStage.setResizable(false);
        firstStage.show();

        Label welcomeLabel = new Label("Welcome!");
        GridPane topBar = new GridPane();
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(75,0,0,0));
        topBar.getChildren().add(welcomeLabel);
        welcomeLabel.getStyleClass().add("welcomeLabel");
        root.setTop(topBar);

        Label informationLabel = new Label("Welcome to the London Air Pollution Viewer – an interactive tool for" +
                " exploring air quality in London. Select pollutants and years to view dynamic heat maps, detailed " +
                "charts, and graphs. Compare London’s data with other UK cities and get predictive insights based " +
                "on six years of historical data.");

        GridPane center = new GridPane();
        informationLabel.setWrapText(true);
        informationLabel.setMaxWidth(650);
        informationLabel.setTextAlignment(TextAlignment.CENTER);
        center.setPadding(new Insets(0,0,20,0));
        center.getChildren().add(informationLabel);
        informationLabel.getStyleClass().add("informationLabel");
        root.setCenter(informationLabel);

        Button continueButton = new Button("Continue");
        GridPane bottomBar = new GridPane();
        bottomBar.setPadding(new Insets(0,0,20,0));
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.getChildren().add(continueButton);
        continueButton.getStyleClass().add("continueButton");
        root.setBottom(bottomBar);

        continueButton.setOnAction(event -> {
            Stage stage = new Stage();
            try {
            new AppWindow().createInitialPanel(stage);
            } catch(Exception e){
                e.printStackTrace();
            }
            firstStage.close();
        });
    }
}
