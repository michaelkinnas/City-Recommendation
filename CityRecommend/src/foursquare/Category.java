package foursquare;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Category{
	@JsonProperty("id") 
	public int getId() { 
		return this.id; } 
	public void setId(int id) { 
		this.id = id; } 
	int id;
	@JsonProperty("name") 
	public String getName() { 
		return this.name; } 
	public void setName(String name) { 
		this.name = name; } 
	String name;
	@JsonProperty("icon") 
	public Icon getIcon() { 
		return this.icon; } 
	public void setIcon(Icon icon) { 
		this.icon = icon; } 
	Icon icon;
}
