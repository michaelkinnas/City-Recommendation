package amadeus;

import java.util.HashMap;
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
	"documentRequired",
	"healthDocumentationLink",
	"travelDocumentationLink"
})

public class DeclarationDocuments {

	@JsonProperty("date")
	private String date;
	@JsonProperty("text")
	private String text;
	@JsonProperty("documentRequired")
	private String documentRequired;
	@JsonProperty("healthDocumentationLink")
	private String healthDocumentationLink;
	@JsonProperty("travelDocumentationLink")
	private String travelDocumentationLink;
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

	@JsonProperty("documentRequired")
	public String getDocumentRequired() {
		return documentRequired;
	}

	@JsonProperty("documentRequired")
	public void setDocumentRequired(String documentRequired) {
		this.documentRequired = documentRequired;
	}

	@JsonProperty("healthDocumentationLink")
	public String getHealthDocumentationLink() {
		return healthDocumentationLink;
	}

	@JsonProperty("healthDocumentationLink")
	public void setHealthDocumentationLink(String healthDocumentationLink) {
		this.healthDocumentationLink = healthDocumentationLink;
	}
	
	@JsonProperty("travelDocumentationLink")
	public String getTravelDocumentationLink() {
		return travelDocumentationLink;
	}

	@JsonProperty("travelDocumentationLink")
	public void setTravelDocumentationLink(String travelDocumentationLink) {
		this.travelDocumentationLink = travelDocumentationLink;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}