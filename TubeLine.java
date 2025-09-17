import java.util.ArrayList; 
/**
 * Represents a Tube line with a name and a list of stations.
 * @author Rom Steinberg
 * @version 2.0
 */
public class TubeLine {
    private String name; // Name of the tube line
    private ArrayList<String> stations; // List of stations on the tube line

    /**
     * @param name The name of the tube line.
     */
    public TubeLine(String name) {
        this.name = name;
        stations = new ArrayList<>();
    }

    /**
     * Adds a single station to the tube line.
     * @param station The station to be added.
     */
    public void addStation(String station) {
        stations.add(station);
    }

    /**
     * Adds multiple stations to the tube line.
     * @param stations An array of station names to be added.
     */
    public void addStations(String[] stations) {
        for (String station : stations) {
            addStation(station);
        }
    }

    /**
     * @return The name of the tube line.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if a station is present on this tube line.
     * @param station The station name to check.
     * @return True if the station exists on the line, false otherwise.
     */
    public boolean isStationOnLine(String station) {
        return stations.contains(station);
    }

    /**
     * @return An ArrayList containing the names of stations.
     */
    public ArrayList<String> getStations() {
        return stations;
    }

    /**
     * @return A string indicating the tube line's name.
     */
    @Override
    public String toString() {
        return "This is the " + name + " line";
    }
}

