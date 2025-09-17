import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * AppWindow serves as the main application window and manages the display
 * of different tabs such as the London map, pollution statistics, UK cities,
 * tube journey, and real time data. It also handles data loading.
 *
 * @author Nicolás Alcalá Olea, Rom Steinberg, Anton Davidouski and Yaal Luka Edrey Gatignol
 */

public class AppWindow extends Application {

    private BorderPane root;
    private TabPane tabPane;
    private WelcomePanel welcomePanel;
    private Tab londonTab;
    private Tab statsTab;
    private static Tab UKTab;
    private Tab tubeTab;
    private Tab realTimeDataTab;

    private DataAggregator dataAggregator;
    private DataAggregator tubeDataAggregator;

    /**
     * This method is automatically called when the application starts.
     * It creates the welcome panel.
     *
     * @param stage the primary stage for this application.
     */

    @Override
    public void start(Stage stage) {
        createWelcomePanel(stage);
    }

    /**
     * Creates and configures the TabPane along with all its associated tabs.
     */

    private void createTabPane() {
        tabPane = new TabPane();

        londonTab = new Tab("London Map");
        londonTab.setClosable(false);
        londonTab.getStyleClass().add("londonTab");

        statsTab = new Tab("Pollution Statistics");
        statsTab.setClosable(false);
        statsTab.getStyleClass().add("statsTab");

        UKTab = new Tab("UK Cities");
        UKTab.setClosable(false);
        UKTab.getStyleClass().add("UKTab");

        tubeTab = new Tab("Tube Journey");
        tubeTab.setClosable(false);
        tubeTab.getStyleClass().add("tubeTab");

        realTimeDataTab = new Tab("Real Time Data");
        realTimeDataTab.setClosable(false);
        realTimeDataTab.getStyleClass().add("realTimeDataTab");

        tabPane.getTabs().addAll(londonTab, statsTab, UKTab, tubeTab, realTimeDataTab);

        City londonTabAnchor = new City("London", dataAggregator);
        londonTab.setContent(londonTabAnchor.getPane());

        PollutionStatistics statsContent = new PollutionStatistics(dataAggregator);
        statsTab.setContent(statsContent.getBorderPane());

        City UKcities = new City("Manchester", dataAggregator);
        UKTab.setContent(UKcities.getPane());

        Tube tube = new Tube(tubeDataAggregator, message ->
                new Alert(Alert.AlertType.ERROR, message).showAndWait());
        tubeTab.setContent(tube.getPane());

        RealTimeDataTab realTimeDataTabAnchor = new RealTimeDataTab();
        realTimeDataTab.setContent(realTimeDataTabAnchor.getPane());

    }

    /**
     * Creates and displays the welcome panel when the application starts.
     * The welcome panel provides an initial user interface before the main panel is loaded.
     *
     * @param stage the primary stage for displaying the welcome panel.
     */

    public void createWelcomePanel(Stage stage) {
        welcomePanel = new WelcomePanel();
        welcomePanel.createWelcomePanel(new Stage());
        //welcome pane initialisation
    }

    /**
     * Creates and configures the main application panel, it also processes
     * the data for all the years and pollutants.
     *
     * @param stage the primary stage for the main application window.
     */

    public void createInitialPanel(Stage stage) {
        root = new BorderPane();
        root.getStyleClass().add("initialPanel");
        dataAggregator = new DataAggregator();
        tubeDataAggregator = new DataAggregator();
        createTabPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1250, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("London Air Pollution Data Viewer");
        stage.setScene(scene);
        stage.show();

        Alert startAlert = new Alert(Alert.AlertType.INFORMATION);
        startAlert.setTitle("Loading Data");
        startAlert.setHeaderText("Please wait...");
        startAlert.setContentText("Data is being loaded. " + "This popup will close when the data is loaded.");
        startAlert.initOwner(stage);

        // Configure dialog pane
        DialogPane dialogPane = startAlert.getDialogPane();
        dialogPane.getScene().getWindow().setOnCloseRequest(event -> event.consume());
        
        // Disable the OK button, don't remove it
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setDisable(true);
        }

        ProgressIndicator progressIndicator = new ProgressIndicator();
        startAlert.setGraphic(progressIndicator);

        startAlert.show();

        Task<Void> dataLoadingTask = new Task<>() {
            @Override
            protected Void call() {
                dataAggregator.processDirectory("UKAirPollutionData/NO2/");
                dataAggregator.processDirectory("UKAirPollutionData/pm10/");
                dataAggregator.processDirectory("UKAirPollutionData/pm2.5/");
                tubeDataAggregator.processDirectory("UKAirPollutionData/Tube/");
                return null;
            }

            protected void succeeded(){
                startAlert.close();
            }
        };

        new Thread(dataLoadingTask).start();
    }

    /**
     * Updates the content of the UK Cities tab with a new City object.
     *
     * @param ukCities the City instance containing the updated content for the UK Cities tab.
     */

    public static void setUKCities(City ukCities) {
        UKTab.setContent(ukCities.getPane());
    }
}
