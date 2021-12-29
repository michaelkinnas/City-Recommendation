package foursquare;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Child{
	@JsonProperty("fsq_id") 
	public String getFsq_id() { 
		return this.fsq_id; } 
	public void setFsq_id(String fsq_id) { 
		this.fsq_id = fsq_id; } 
	String fsq_id;
	@JsonProperty("name") 
	public String getName() { 
		return this.name; } 
	public void setName(String name) { 
		this.name = name; } 
	String name;
}
