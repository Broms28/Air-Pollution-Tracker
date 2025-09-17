/**
 * Objects of class TubeDataPoint hold one tube data point in the dataset. A data point
 * is a single value for one station in zone 1.
 * 
 * The geographic location is provided in two ways:
 * 
 *   - A station name 
 *   - A gridcode. This is the UK grid code reference as defined by the UK
 *   - An x/y coordinate pair, representing Ordinance Survey National Grid Eastings and Northings 
 *      (see https://getoutside.ordnancesurvey.co.uk/guides/beginners-guide-to-grid-references/)
 *   - Street pollution value
*    - Tube pollution value
 * 
 * @author Rom Steinberg
 * @version 02.05
 */
public record TubeDataPoint(String station, int gridCode, int x, int y, double streetData, double tubeData)
{
}
