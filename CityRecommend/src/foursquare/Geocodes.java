package foursquare;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Geocodes{
	@JsonProperty("main") 
	public Main getMain() { 
		return this.main; } 
	public void setMain(Main main) { 
		this.main = main; } 
	Main main;
}
