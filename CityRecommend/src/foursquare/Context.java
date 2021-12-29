package foursquare;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Context{
	@JsonProperty("geo_bounds") 
	public GeoBounds getGeo_bounds() { 
		return this.geo_bounds; } 
	public void setGeo_bounds(GeoBounds geo_bounds) { 
		this.geo_bounds = geo_bounds; } 
	GeoBounds geo_bounds;
}