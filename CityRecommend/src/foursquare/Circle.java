package foursquare;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Circle{
	@JsonProperty("center") 
	public Center getCenter() { 
		return this.center; } 
	public void setCenter(Center center) { 
		this.center = center; } 
	Center center;
	@JsonProperty("radius") 
	public int getRadius() { 
		return this.radius; } 
	public void setRadius(int radius) { 
		this.radius = radius; } 
	int radius;
}
