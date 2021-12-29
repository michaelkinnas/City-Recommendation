
package exceptions;

/**
 * 
 * @author it22046
 *
 */
public class AmadeusErrorException extends Exception {

	private static final long serialVersionUID = 1L;
	static int numExceptions=0;
	private String countryCode;
	
	public AmadeusErrorException(String in_countryCode) {
		numExceptions++;
		this.countryCode=in_countryCode;
	}
	
	public String getMessage() {
		
		return "Error retrieving Covid data for "+countryCode+".";
	}
}