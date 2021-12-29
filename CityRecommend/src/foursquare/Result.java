package foursquare;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result{
	@JsonProperty("fsq_id") 
	public String getFsq_id() { 
		return this.fsq_id; } 
	public void setFsq_id(String fsq_id) { 
		this.fsq_id = fsq_id; } 
	String fsq_id;
	@JsonProperty("categories") 
	public List<Category> getCategories() { 
		return this.categories; } 
	public void setCategories(List<Category> categories) { 
		this.categories = categories; } 
	List<Category> categories;
	@JsonProperty("chains") 
	public List<Object> getChains() { 
		return this.chains; } 
	public void setChains(List<Object> chains) { 
		this.chains = chains; } 
	List<Object> chains;
	@JsonProperty("distance") 
	public int getDistance() { 
		return this.distance; } 
	public void setDistance(int distance) { 
		this.distance = distance; } 
	int distance;
	@JsonProperty("geocodes") 
	public Geocodes getGeocodes() { 
		return this.geocodes; } 
	public void setGeocodes(Geocodes geocodes) { 
		this.geocodes = geocodes; } 
	Geocodes geocodes;
	@JsonProperty("location") 
	public Location getLocation() { 
		return this.location; } 
	public void setLocation(Location location) { 
		this.location = location; } 
	Location location;
	@JsonProperty("name") 
	public String getName() { 
		return this.name; } 
	public void setName(String name) { 
		this.name = name; } 
	String name;
	@JsonProperty("related_places") 
	public RelatedPlaces getRelated_places() { 
		return this.related_places; } 
	public void setRelated_places(RelatedPlaces related_places) { 
		this.related_places = related_places; } 
	RelatedPlaces related_places;
	@JsonProperty("timezone") 
	public String getTimezone() { 
		return this.timezone; } 
	public void setTimezone(String timezone) { 
		this.timezone = timezone; } 
	String timezone;
}