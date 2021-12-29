
package exceptions;

public class AmadeusNoCountryException extends Exception {

	private static final long serialVersionUID = 1L;
	static int numExceptions=0;
	private String countryCode;
	
	public AmadeusNoCountryException(String in_countryCode) {
		numExceptions++;
		this.countryCode=in_countryCode;
	}
	
	public String getMessage() {
		
		return "There is not a country with the code "+countryCode+".";
	}
}