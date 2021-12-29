package foursquare;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location{
	@JsonProperty("address") 
	public String getAddress() { 
		return this.address; } 

	public void setAddress(String address) { 
		this.address = address; } 
	String address;

	@JsonProperty("country") 
	public String getCountry() { 
		return this.country; } 

	public void setCountry(String country) { 
		this.country = country; } 
	String country;

	@JsonProperty("cross_street") 
	public String getCross_street() { 
		return this.cross_street; } 

	public void setCross_street(String cross_street) { 
		this.cross_street = cross_street; } 
	String cross_street;

	@JsonProperty("locality") 
	public String getLocality() { 
		return this.locality; } 

	public void setLocality(String locality) { 
		this.locality = locality; } 
	String locality;

	@JsonProperty("region") 
	public String getRegion() { 
		return this.region; } 

	public void setRegion(String region) { 
		this.region = region; } 
	String region;

	@JsonProperty("admin_region") 
	public String getAdmin_region() { 
		return this.admin_region; } 

	public void setAdmin_region(String admin_region) { 
		this.admin_region = admin_region; } 
	String admin_region;

	@JsonProperty("post_town") 
	public String getPost_town() { 
		return this.post_town; } 

	public void setPost_town(String post_town) { 
		this.post_town = post_town; } 
	String post_town;

	@JsonProperty("address_extended") 
	public String getAddress_extended() { 
		return this.address_extended; } 

	public void setAddress_extended(String address_extended) { 
		this.address_extended = address_extended; } 
	String address_extended;

	@JsonProperty("postcode") 
	public String getPostcode() { 
		return this.postcode; } 

	public void setPostcode(String postcode) { 
		this.postcode = postcode; } 
	String postcode;
	
	@JsonProperty("dma") 
	public String getDma() { 
		return this.dma; } 

	public void setDma(String dma) { 
		this.dma = dma; } 
	String dma;
	
	@JsonProperty("po_box") 
	public String getPo_box() { 
		return this.po_box; } 

	public void setPo_box(String po_box) { 
		this.po_box = po_box; } 
	String po_box;

	@JsonProperty("neighborhood") 
	public List<String> getNeighborhood() { 
		return this.neighborhood; } 

	public void setNeighborhood(List<String> neighborhood) { 
		this.neighborhood = neighborhood; } 
	List<String> neighborhood;
}
