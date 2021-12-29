package foursquare;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeoBounds{
	@JsonProperty("circle") 
	public Circle getCircle() { 
		return this.circle; } 
	public void setCircle(Circle circle) { 
		this.circle = circle; } 
	Circle circle;
}
