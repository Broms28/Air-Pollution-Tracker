import java.util.*;

/**
 * A DataSet object holds all the data from a pollution data file.
 * 
 * It is assumed that the data is derived from a DEFRA air pollution file (see
 * https://uk-air.defra.gov.uk/data/pcm-data). 
 * 
 * The data consists of a few bits of information about the nature of the data, and a list
 * of data points.
 * 
 * @author Michael Kölling
 * @version 1.0
 */
public class  DataSet
{
    private String pollutant;
    private String year;
    private String metric;
    private String units;
    
    private List<DataPoint> data;

    /**
     * Constructor for objects of class DataSet
     */
    public DataSet(String pollutant, String year, String metric, String units)
    {
        this.pollutant = pollutant;
        this.year = year;
        this.metric = metric;
        this.units = units;
        
        data = new ArrayList<DataPoint>();
    }

    /**
     * Return the pollutant information for this dataset.
     */
    public String getPollutant()
    {
        return pollutant;
    }
    
    /**
     * Return the year information for this dataset.
     */
    public String getYear()
    {
        return year;
    }
    
    /**
     * Return the metric information for this dataset.
     */
    public String getMetric()
    {
        return metric;
    }
    
    /**
     * Return the units information for this dataset.
     */
    public String getUnits()
    {
        return units;
    }
    
    /**
     * Return the data points of this dataset.
     */
    public List<DataPoint> getData()
    {
        return data;
    }

    public double getMax() {
        double max = Integer.MIN_VALUE;
        for (DataPoint dataPoint : data) {
            if (dataPoint.value() > max){
                max = dataPoint.value();
            }
        }
        return max;
    }

    public double getMin() {
        double min = Integer.MAX_VALUE;
        for (DataPoint dataPoint : data) {
            if (dataPoint.value() < min){
                min = dataPoint.value();
            }
        }
        return min;
    }

    /**
     * Returns the nearest data point from the given x and y coordinates.
     * @param x coordinates in the map
     * @param y coordinates in the map
     * @return The nearest data point
     */
    public DataPoint findNearestDataPoint(int x, int y) {
        double minDistance = Double.MAX_VALUE;
        DataPoint nearestDataPoint = null;

        for (DataPoint dataPoint : data) {
            double distance = Math.sqrt(Math.pow(dataPoint.x() - x, 2) + Math.pow(dataPoint.y() - y, 2));
            if (distance < minDistance) {
                minDistance = distance;
                nearestDataPoint = dataPoint;
            }
        }
        return nearestDataPoint;
    }
    
    /**
     * Add a data point to this dataset. 
     * A data point consists of 4 pieces od data:
     * 
     *     gridcode, x, y, value
     *     
     * The data is provided in a String array of length 4. If the value is invalid or
     * missing, it will be stored as -1.
     *
     * @param  values  An array with the four data values (as Strings)
     */
    public void addData(String[] values)
    {
        data.add(new DataPoint(toInt(values[0]), 
                               toInt(values[1]), 
                               toInt(values[2]), 
                               toDouble(values[3]))); 
    }
    
    /**
     * Convert a string to int. 
     * @param intString  The String holding the int value
     * @return  The int value, or -1 if the string is not a readable number
     */
    protected int toInt(String intString)
    {
        try {
            return Integer.parseInt(intString);
        }
        catch (NumberFormatException exc) {
            return -1;
        }
    }

    /**
     * Convert a string to double. 
     * @param doubleString  The String holding the double value
     * @return  The double value, or -1.0 if the string is not a readable number
     */
    protected double toDouble(String doubleString)
    {
        try {
            return Double.parseDouble(doubleString);
        }
        catch (NumberFormatException exc) {
            return -1.0;
        }
    }

    /**
     * Return a string representation of this dataset info.
     */
    public String toString()
    {
        return String.format("Dataset: Pollutant: %s, Year: %s, Metric: %s, Units: %s (%d data points)",
                             pollutant, year, metric, units, data.size());
    }
}
