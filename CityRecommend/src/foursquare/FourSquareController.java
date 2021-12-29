package foursquare;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FourSquareController{
	@JsonProperty("results") 
	public List<Result> getResults() { 
		return this.results; } 
	public void setResults(List<Result> results) { 
		this.results = results; } 
	List<Result> results;
	
	@JsonProperty("context") 
	public Context getContext() { 
		return this.context; } 
	public void setContext(Context context) { 
		this.context = context; } 
	Context context;
}
