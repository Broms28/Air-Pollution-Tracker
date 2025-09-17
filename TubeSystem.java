import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TubeSystem {
    private TubeLine bakerloo = new TubeLine("Bakerloo");
    private TubeLine central = new TubeLine("Central");
    private TubeLine circle = new TubeLine("Circle");
    private TubeLine district = new TubeLine("District");
    private TubeLine hammersmithAndCity = new TubeLine("Hammersmith & City");
    private TubeLine jubilee = new TubeLine("Jubilee");
    private TubeLine metropolitan = new TubeLine("Metropolitan");
    private TubeLine northernBB = new TubeLine("Northern (via Bank)");
    private TubeLine northernCCB = new TubeLine("Northern (via Charing Cross)");
    private TubeLine piccadilly = new TubeLine("Piccadilly");
    private TubeLine victoria = new TubeLine("Victoria");
    private TubeLine waterlooAndCity = new TubeLine("Waterloo & City");
    private TubeLine elizabethLine = new TubeLine("Elizabeth Line");
    private ArrayList <TubeLine> tubeLines = new ArrayList<>();

    /**
     * Constructor for TubeSystem class. Initializes the tube lines and their stations.
     */
    public TubeSystem(){
        String[] bakerlooStations = {"elephant & castle", "lambeth north", "waterloo", "embankment", "charing cross", "piccadilly circus", "oxford circus", "regents park", "baker street", "marleybone", "edgware road", "paddington"};
        bakerloo.addStations(bakerlooStations);

        String[] centralStations = {"liverpool street", "bank", "st pauls", "chancery lane", "holborn", "tottenham court road", "oxford circus", "bond street", "marble arch", "lancaster gate", "queensway", "notting hill gate"};
        central.addStations(centralStations);

        String[] hammersmithAndCityStations = {"aldgate east", "liverpool street", "moorgate", "barbican", "farringdon", "kings cross", "euston square", "great portland street", "baker street", "edgware road", "paddington"};
        hammersmithAndCity.addStations(hammersmithAndCityStations);

        String[] circleStations = {"edgware road", "baker street", "great portland street", "euston square", "kings cross", "farringdon", "barbican", "moorgate", "liverpool street", "aldgate", "tower hill", "monument", "cannon street", "mansion house", "blackfriars", "temple", "embankment", "westminster", "st jamess park", "victoria", "sloane square", "south kensington", "gloucester road", "high street kensington", "notting hill gate", "bayswater", "paddington"};
        circle.addStations(circleStations);

        String[] districtStations = {"aldgate east", "tower hill", "monument", "cannon street", "mansion house", "blackfriars", "temple", "embankment", "westminster", "st jamess park", "victoria", "sloane square", "south kensington", "gloucester road", "high street kensington", "notting hill gate", "bayswater", "paddington", "earls court"};
        district.addStations(districtStations);

        String[] jubileeStations = {"london bridge", "southwark", "waterloo", "westminster", "green park", "bond street",  "baker street"};
        jubilee.addStations(jubileeStations);

        String[] metropolitanStations = {"aldgate", "liverpool street", "moorgate", "barbican", "farringdon", "kings cross", "euston square", "great portland street", "baker street"};
        metropolitan.addStations(metropolitanStations);

        String[] northernStationsBB = {"euston", "kings cross", "angel", "old street", "moorgate", "bank", "london bridge", "borough", "elephant & castle", "kennington"};
        String[] northernStationsCCB = {"euston", "warren street", "goodge street", "tottenham court road", "leicester square", "charing cross", "embankment", "waterloo", "kennington", "nine elms", "battersea power station"};
        northernBB.addStations(northernStationsBB);
        northernCCB.addStations(northernStationsCCB);

        String[] piccadillyStations = {"kings cross", "russell square", "holborn", "covent garden", "leicester square", "piccadilly circus", "green park", "hyde park corner", "knightsbridge", "south kensington", "gloucester road", "earls court"};
        piccadilly.addStations(piccadillyStations);

        String[] victoriaStations = {"kings cross", "euston", "warren street", "oxford circus", "green park", "victoria", "pimlico", "vauxhall"};
        victoria.addStations(victoriaStations);

        String[] waterlooAndCityStations = {"bank", "waterloo"};
        waterlooAndCity.addStations(waterlooAndCityStations);

        String[] elizabethLineStations = {"liverpool street", "farringdon", "tottenham court road", "bond street", "paddington"};
        elizabethLine.addStations(elizabethLineStations);

        tubeLines.add(bakerloo);
        tubeLines.add(central);
        tubeLines.add(hammersmithAndCity);
        tubeLines.add(circle);
        tubeLines.add(district);
        tubeLines.add(jubilee);
        tubeLines.add(metropolitan);
        tubeLines.add(northernBB);
        tubeLines.add(northernCCB);
        tubeLines.add(piccadilly);
        tubeLines.add(victoria);
        tubeLines.add(waterlooAndCity);
        tubeLines.add(elizabethLine);
    }

    /**
     * Calculates the shortest journey between two stations.
     * 
     * @param start The name of the starting station.
     * @param finish The name of the destination station.
     * @return A list of station names representing the shortest journey (with the least changes)
     */
    public List<String> calculateJourney(String start, String finish){
        List<String> journey = bothStationsOnLine(start,finish);
        if(journey!=null){
            return journey;
        }
        return changeLines(start, finish);
    }

    /**
     * Finds the shortest journey between two stations when both are on the same line.
     * 
     * @param station1 The name of the starting station.
     * @param station2 The name of the destination station.
     * @return A list of station names representing the shortest journey
     */
    private List<String> bothStationsOnLine(String station1, String station2) {
        // Finds the tube line that contains both stations
        for (TubeLine line : tubeLines){
            if (line.isStationOnLine(station1) && line.isStationOnLine(station2)){
                // Makes sure the stations are in the right order
                if(line.getStations().indexOf(station1)< line.getStations().indexOf(station2)+1){
                    return line.getStations().subList(line.getStations().indexOf(station1), line.getStations().indexOf(station2)+1);
                }
                List<String> journey = line.getStations().subList(line.getStations().indexOf(station2), line.getStations().indexOf(station1)+1);
                List<String> reversedJourney = new ArrayList<>();
                // Might reverse the order of the stations if the start station is after the end station
                for (int i = journey.size() - 1; i >= 0; i--) {
                    reversedJourney.add(journey.get(i));
                }
                return reversedJourney;
            }
        } 
        return null;  
    }

    /**
     * Finds the shortest journey between two stations when changing Tube lines is necessary.
     * The shortest journey is returned based on the number of stations traveled.
     * 
     * @param start The name of the starting station.
     * @param end The name of the destination station.
     * @return A list of station names representing the shortest journey
     */
    private List<String> changeLines(String start, String end) {
        ArrayList<TubeLine> startLines = new ArrayList<>();
        ArrayList<TubeLine> endLines = new ArrayList<>();
        HashMap<Integer, List<String>> journeyLength = new HashMap<>();
        
        // Identify which lines the start and end stations belong to
        for (TubeLine line : tubeLines) {
            if (line.isStationOnLine(start)) {
                startLines.add(line);
            }
            if (line.isStationOnLine(end)) {
                endLines.add(line);
            }
        }
        
        String changeStation;
        
        // Iterate through possible start and end lines to find a valid transfer station
        for (TubeLine line1 : startLines) {
            for (TubeLine line2 : endLines) {
                changeStation = findHub(line1, line2); 
                
                if (changeStation != null) {
                    // Retrieve station lists for each segment of the journey
                    List<String> journey1 = bothStationsOnLine(start, changeStation);
                    List<String> journey2 = bothStationsOnLine(changeStation, end);
                    
                    // Combine the two parts of the journey into a full journey
                    List<String> fullJourney = new ArrayList<>();
                    fullJourney.addAll(List.copyOf(journey1)); 
                    fullJourney.addAll(List.copyOf(journey2)); 
                    fullJourney.remove(changeStation); // Remove duplicate transfer station
                    
                    journeyLength.put(fullJourney.size(), List.copyOf(fullJourney));
                }
            }
        }
        
        // Return the shortest journey found (smallest key in journeyLength map)
        return journeyLength.get(getMinKey(journeyLength));
    }


    /**
     * Finds a common station (hub) between two Tube lines.
     * 
     * @param line1 The first Tube line.
     * @param line2 The second Tube line.
     * @return The name of the common station if found, otherwise returns null.
     */
    private String findHub(TubeLine line1, TubeLine line2) {
        // Iterate through each station in line1
        for (String station : line1.getStations()) {
            // Check if the station also exists in line2
            if (line2.isStationOnLine(station)) {
                return station; 
            }
        }
        return null; 
    }

    /**
     * Finds and returns the smallest key in a given HashMap.
     * 
     * @param map The HashMap to search for the minimum key.
     * @return The smallest key found, or 0 if the map is empty.
     */
    private int getMinKey(HashMap<Integer, List<String>> map) {
        if (map.isEmpty()) {
            return 0; 
        }
        
        int minKey = Integer.MAX_VALUE; 
        
        // Find min algorithm
        for (int key : map.keySet()) {
            if (key < minKey) { 
                minKey = key;
            }
        }
        return minKey;
    }

    
}
