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
 * @author Michael Kölling & Rom Steinberg
 * @version 3.14159
 */
public class  TubeDataSet extends DataSet
{   
    private List<TubeDataPoint> data;

    /**
     * Constructor for objects of class DataSet
     */
    public TubeDataSet(String pollutant, String year, String metric, String units)
    {
        super(pollutant, year, metric, units);
        
        data = new ArrayList<TubeDataPoint>();
    }
    

    public TubeDataPoint findStationData(String station) {
        for (TubeDataPoint dp : data) {
            if (dp.station().equals(station)) {
                return dp;
            }
        }
        return null;
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
        data.add(new TubeDataPoint(values[0], 
                               toInt(values[1]), 
                               toInt(values[2]), 
                               toInt(values[3]),
                               toDouble(values[4]),
                               toDouble(values[5]))); 
    }
    

}
