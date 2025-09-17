import java.io.*;
import java.net.*;

/**
 * This class loads a UK DEFRA air pollution file from disk and returns the file data
 * in an object. 
 * 
 * DEFRA files are csv files in a specific format. See https://uk-air.defra.gov.uk/data/pcm-data
 * for detailed information.
 *
 * @author Michael Kölling, Rom Steinberg
 * @version 1.0
 */
public class DataLoader
{
    private static final String COMMA_DELIMITER = ",";
 
    /** 
     * Read a data file from disk. The data must be a csv file, and must be in the
     * DEFRA air pollution file format. The data is returned in a DataSet object.
     * 
     * @param fileName The name of the file to be read
     * @return A DataSet object holding the complete dataset
     */
    public DataSet loadDataFile(String fileName) 
    {
        System.out.println("Loading file " + fileName + "...");
        
        URL url = getClass().getResource(fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI()).getAbsolutePath()))) {
            // the first four lines of the file hold special information; read them in:
            String pollutant = readDataHeader(br);
            String year = readDataHeader(br);
            String metric = readDataHeader(br);
            String units = readDataHeader(br);
            
            // discard the next two lines. the first is empty, and the next holds 
            // the column labels for the data points.
            br.readLine();
            br.readLine();
            DataSet dataSet;
            if(fileName.contains("Tube"))
                dataSet = new TubeDataSet(pollutant, year, metric, units);
            else
                dataSet = new DataSet(pollutant, year, metric, units);
            
            // read all the data lines
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                dataSet.addData(values);
            }
            System.out.println("Loading file... done.");
            return dataSet;
        }        
        catch(IOException | URISyntaxException e) {
            System.out.println("Could not read file " + fileName);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Read one piece of information out of the header of the data file.
     * Each line in the header has the format
     *    DATA,,,
     * That is, it is a four column csv entry where the first column holds the data we
     * want and the other three columns are empty.
     * We read and return only the data point from the first column.
     * 
     * @return The data from the next header line of the file currently being read.
     */
    private String readDataHeader(BufferedReader br)
        throws java.io.IOException
    {
        String line = br.readLine();
        String[] values = line.split(COMMA_DELIMITER);
        return values[0];
    }
}
