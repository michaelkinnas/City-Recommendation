package foursquare;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Icon{
	@JsonProperty("prefix") 
	public String getPrefix() { 
		return this.prefix; } 
	public void setPrefix(String prefix) { 
		this.prefix = prefix; } 
	String prefix;
	@JsonProperty("suffix") 
	public String getSuffix() { 
		return this.suffix; } 
	public void setSuffix(String suffix) { 
		this.suffix = suffix; } 
	String suffix;
}