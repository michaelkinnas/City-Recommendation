package foursquare;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RelatedPlaces{
	@JsonProperty("children") 
	public List<Child> getChildren() { 
		return this.children; } 
	public void setChildren(List<Child> children) { 
		this.children = children; } 
	List<Child> children;
	@JsonProperty("parent") 
	public Parent getParent() { 
		return this.parent; } 
	public void setParent(Parent parent) { 
		this.parent = parent; } 
	Parent parent;
}
