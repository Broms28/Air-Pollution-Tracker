
/**
 * Objects of class DataPoint hold one data point in the dataset. A data point
 * is a single value for one geographic location.
 * 
 * The geographic location is provided in two ways:
 * 
 *   - A gridcode. This is the UK grid code reference as defined by the UK
 *     Ordinance Survey National Grid 
 *     (see https://www.ordnancesurvey.co.uk/documents/resources/guide-to-nationalgrid.pdf)
 *   - An x/y coordinate pair, representing Ordinance Survey National Grid Eastings and Northings 
 *      (see https://getoutside.ordnancesurvey.co.uk/guides/beginners-guide-to-grid-references/)
 *
 * @author Michael Kölling
 * @version 1.0
 */
public record DataPoint(int gridCode, int x, int y, double value)
{
}
