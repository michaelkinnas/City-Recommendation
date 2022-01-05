
package exceptions;
/**
 * 
 * @author it22046
 *
 */
public class FoursquareNoCityException extends Exception {

	private static final long serialVersionUID = 1L;
	static int numExceptions=0;
	private String cityName;
	
	public FoursquareNoCityException(String in_cityName) {
		numExceptions++;
		this.cityName=in_cityName;
	}
	
	public String getMessage() {
		
		return "Error retrieveing data from "+cityName+".";
	}
}