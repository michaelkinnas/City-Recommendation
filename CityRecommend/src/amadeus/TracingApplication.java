package amadeus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"date",
	"text",
	"isRequired",
	"iosUrl",
	"androidUrl",
	"website"
})

public class TracingApplication {

	@JsonProperty("date")
	private String date;
	@JsonProperty("text")
	private String text;
	@JsonProperty("isRequired")
	private String isRequired;
	@JsonProperty("website")
	private String website;
	@JsonProperty("iosUrl")
	private List<String> iosUrl = null;
	@JsonProperty("androidUrl")
	private List<String> androidUrl = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("date")
	public String getDate() {
		return date;
	}

	@JsonProperty("date")
	public void setDate(String date) {
		this.date = date;
	}

	@JsonProperty("text")
	public String getText() {
		return text;
	}

	@JsonProperty("text")
	public void setText(String text) {
		this.text = text;
	}

	@JsonProperty("isRequired")
	public String getIsRequired() {
		return isRequired;
	}

	@JsonProperty("isRequired")
	public void setIsRequired(String isRequired) {
		this.isRequired = isRequired;
	}

	@JsonProperty("iosUrl")
	public List<String> getIosUrl() {
		return iosUrl;
	}

	@JsonProperty("iosUrl")
	public void setIosUrl(List<String> iosUrl) {
		this.iosUrl = iosUrl;
	}

	@JsonProperty("androidUrl")
	public List<String> getAndroidUrl() {
		return androidUrl;
	}

	@JsonProperty("androidUrl")
	public void setAndroidUrl(List<String> androidUrl) {
		this.androidUrl = androidUrl;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
	
	@JsonProperty("website")
	public String getWebsite() {
		return website;
	}

	@JsonProperty("website")
	public void setWebsite(String website) {
		this.website = website;
	}

}