import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for the TubeSystem class that verifies route calculation functionality.
 * Tests various journey scenarios within London's tube network, including direct journeys,
 * journeys requiring transfers, and edge cases.
 * 
 * @author Yaal Luka Edrey Gatignol
 * @version 1.0
 */
public class TubeSystemTest {
    
    private TubeSystem tubeSystem;
    
    /**
     * Sets up the test environment before each test execution.
     * Initializes a new TubeSystem instance for testing.
     */
    @Before
    public void setUp() {
        tubeSystem = new TubeSystem();
    }
    
    /**
     * Tests journey calculation for stations on the same line with direct connection.
     * Verifies that the journey contains exactly two stations (start and destination)
     * in the correct order.
     */
    @Test
    public void testCalculateJourney_SameLineDirectJourney() {
        List<String> journey = tubeSystem.calculateJourney("oxford circus", "bond street");
        
        assertNotNull("Journey should not be null", journey);
        assertEquals("Journey should contain expected number of stations", 2, journey.size());
        assertEquals("Journey should start with oxford circus", "oxford circus", journey.get(0));
        assertEquals("Journey should end with bond street", "bond street", journey.get(1));
    }
    
    /**
     * Tests journey calculation for stations on the same line but in reverse order.
     * Ensures the system handles bidirectional travel on the same line correctly.
     */
    @Test
    public void testCalculateJourney_SameLineReversedJourney() {
        List<String> journey = tubeSystem.calculateJourney("bond street", "oxford circus");
        
        assertNotNull("Journey should not be null", journey);
        assertEquals("Journey should contain expected number of stations", 2, journey.size());
        assertEquals("Journey should start with bond street", "bond street", journey.get(0));
        assertEquals("Journey should end with oxford circus", "oxford circus", journey.get(1));
    }
    
    /**
     * Tests journey calculation for stations that require traversing multiple stations on the same line.
     * Verifies that the journey starts and ends at the specified stations.
     */
    @Test
    public void testCalculateJourney_SameLineMultipleStationsJourney() {
        List<String> journey = tubeSystem.calculateJourney("baker street", "paddington");
        
        assertNotNull("Journey should not be null", journey);
        assertTrue("Journey should contain at least 2 stations", journey.size() >= 2);
        assertEquals("Journey should start with baker street", "baker street", journey.get(0));
        assertEquals("Journey should end with paddington", "paddington", journey.get(journey.size() - 1));
    }
    
    /**
     * Tests journey calculation for stations requiring one line change.
     * Verifies that the journey includes a valid transfer station between lines.
     */
    @Test
    public void testCalculateJourney_OneChangeRequired() {
        List<String> journey = tubeSystem.calculateJourney("westminster", "bond street");
        
        assertNotNull("Journey should not be null", journey);
        assertTrue("Journey should contain at least 3 stations", journey.size() >= 3);
        assertEquals("Journey should start with westminster", "westminster", journey.get(0));
        assertEquals("Journey should end with bond street", "bond street", journey.get(journey.size() - 1));
        
        assertTrue("Journey should contain the transfer station green park", 
                  journey.contains("green park"));
    }
    
    /**
     * Tests journey calculation for a longer route across the tube network.
     * Verifies the journey has the correct start and end stations.
     */
    @Test
    public void testCalculateJourney_LongerJourney() {
        List<String> journey = tubeSystem.calculateJourney("euston", "notting hill gate");
        
        assertNotNull("Journey should not be null", journey);
        assertTrue("Journey should have stations", journey.size() > 0);
        assertEquals("Journey should start with euston", "euston", journey.get(0));
        assertEquals("Journey should end with notting hill gate", "notting hill gate", 
                    journey.get(journey.size() - 1));
    }
    
    /**
     * Tests the behavior with an invalid input (non-existent station).
     * Verifies that the system knows how to handle such cases by returning null or an empty list.
     */
    @Test
    public void testCalculateJourney_NonExistentStation() {
        List<String> journey = tubeSystem.calculateJourney("oxford circus", "fake station");
        
        // Depending on how TubeSystem is implemented, it might return null, an empty list,
        // or throw an exception for non-existent stations
        if (journey != null) {
            assertTrue("Journey with non-existent station should be empty", journey.isEmpty());
        }
    }
    
    /**
     * Tests journey calculation when the start and end stations are the same.
     * Verifies that the system correctly handles this case by returning a single-station journey.
     */
    @Test
    public void testCalculateJourney_SameStation() {
        List<String> journey = tubeSystem.calculateJourney("oxford circus", "oxford circus");
        
        assertNotNull("Journey should not be null", journey);
        assertEquals("Journey to same station should contain only one station", 1, journey.size());
        assertEquals("Journey should only contain oxford circus", "oxford circus", journey.get(0));
    }
    
    /**
     * Tests journey calculation between stations that appear on multiple lines.
     * Verifies that the system finds the most efficient (direct) route.
     */
    @Test
    public void testCalculateJourney_StationsOnManyLines() {
        List<String> journey = tubeSystem.calculateJourney("green park", "oxford circus");
        
        assertNotNull("Journey should not be null", journey);
        assertTrue("Journey should be direct (2 stations)", journey.size() == 2);
        assertEquals("Journey should start with green park", "green park", journey.get(0));
        assertEquals("Journey should end with oxford circus", "oxford circus", journey.get(1));
    }
}