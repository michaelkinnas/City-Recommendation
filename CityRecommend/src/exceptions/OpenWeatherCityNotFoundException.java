
package exceptions;
/**
 * 
 * @author it22046
 *
 */
public class OpenWeatherCityNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	static int numExceptions=0;
	private String city;
	
	public OpenWeatherCityNotFoundException(String city) {
		numExceptions++;
		this.city=city;
	}
	
	public String getMessage() {
		
		return "No city found with the name: "+city+".";
	}
}