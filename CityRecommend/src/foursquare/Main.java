package foursquare;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Main{
	@JsonProperty("latitude") 
	public double getLatitude() { 
		return this.latitude; } 
	public void setLatitude(double latitude) { 
		this.latitude = latitude; } 
	double latitude;
	
	@JsonProperty("longitude") 
	public double getLongitude() { 
		return this.longitude; } 
	public void setLongitude(double longitude) { 
		this.longitude = longitude; } 
	double longitude;
}
