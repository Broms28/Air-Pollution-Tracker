import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * Test class for the City class that verifies its functionality.
 * Tests proper initialization, component creation, and boundary data.
 * Uses JUnit 5 and JavaFX Platform for UI component testing.
 * 
 * @author Yaal Luka Edrey Gatignol
 * @version 1.0
 */
public class CityTest {
    
    private City city;
    private DataAggregator dataAggregator;
    private static boolean isSetup = false;
    
    /**
     * Initializes the JavaFX toolkit if it hasn't been initialized yet.
     * Required for testing JavaFX components.
     */
    static void initJavaFX() {
        if (!isSetup) {
            new JFXPanel();
            isSetup = true;
        }
    }

    /**
     * Sets up the test environment before each test execution.
     * Initializes JavaFX, creates a DataAggregator with test data,
     * and instantiates a City object on the JavaFX thread.
     */
    @Before
    public void setUp() {
        initJavaFX();
        
        // Initialize DataAggregator with test data
        dataAggregator = new DataAggregator();
        
        // Create City object
        city = new City("London", dataAggregator);
    }

    /**
     * Tests that the City object is properly initialized with correct values.
     * Verifies the city name is set correctly and necessary components are not null.
     */
    @Test
    public void testCityInitialization() {
        Platform.runLater(() -> {
            assertNotNull(city);
            assertEquals("London", city.name);
            assertNotNull(city.getPane());
            assertNotNull(city.getRightBar());
            assertNotNull(city.getDataAggregator());
        });
    }

    /**
     * Tests the getPane method to verify it returns a properly configured BorderPane.
     * Checks that the BorderPane contains all expected components (center, right, bottom).
     */
    @Test
    public void testGetPane() {
        Platform.runLater(() -> {
            BorderPane pane = city.getPane();
            assertNotNull(pane);
            assertNotNull(pane.getCenter()); // Should have the anchorPane with map
            assertNotNull(pane.getRight()); // Should have the rightBar
            assertNotNull(pane.getBottom()); // Should have the coloured bar
        });
    }

    /**
     * Tests the getRightBar method to ensure it returns a GridPane with expected components.
     * Specifically checks for the presence of pollutant and year ComboBoxes.
     */
    @Test
    public void testGetRightBar() {
        Platform.runLater(() -> {
            GridPane rightBar = city.getRightBar();
            assertNotNull(rightBar);
            // Test that rightBar contains expected components
            boolean hasPollutantComboBox = false;
            boolean hasYearComboBox = false;
            
            for (javafx.scene.Node node : rightBar.getChildren()) {
                if (node instanceof ComboBox && node.getStyleClass().contains("pollutantComboBox")) {
                    hasPollutantComboBox = true;
                }
                if (node instanceof ComboBox && node.getStyleClass().contains("yearComboBox")) {
                    hasYearComboBox = true;
                }
            }
            
            assertTrue(hasPollutantComboBox);
            assertTrue(hasYearComboBox);
        });
    }

    /**
     * Tests the getDataAggregator method to ensure it returns the correct DataAggregator instance.
     * Verifies that the returned aggregator is not null and is the same instance used to create the City.
     */
    @Test
    public void testGetDataAggregator() {
        Platform.runLater(() -> {
            DataAggregator aggregator = city.getDataAggregator();
            assertNotNull(aggregator);
            assertSame(dataAggregator, aggregator);
        });
    }

    /**
     * Tests that city boundaries are properly defined and accessible.
     * Verifies that boundaries exist for all expected cities and have the correct structure.
     */
    @Test
    public void testCityBoundaries() {
        // Test that city boundaries are properly defined
        assertNotNull(City.getCitiesBoundaries());
        assertTrue(City.getCitiesBoundaries().containsKey("London"));
        assertTrue(City.getCitiesBoundaries().containsKey("Manchester"));
        assertTrue(City.getCitiesBoundaries().containsKey("Birmingham"));
        assertTrue(City.getCitiesBoundaries().containsKey("Leeds"));
        assertTrue(City.getCitiesBoundaries().containsKey("Bristol"));
        
        // Check that the boundary array has correct length
        assertEquals(5, City.getCitiesBoundaries().get("London").length);
    }
}