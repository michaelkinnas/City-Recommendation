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
	"eligiblePerson",
	"quarantineType",
	"duration",
	"rules",
	"mandateList",
	"quarantineOnArrivalAreas"
})

public class QuarantineModality {

	@JsonProperty("date")
	private String date;
	@JsonProperty("text")
	private String text;
	@JsonProperty("eligiblePerson")
	private String eligiblePerson;
	@JsonProperty("quarantineType")
	private String quarantineType;
	@JsonProperty("duration")
	private Integer duration;
	@JsonProperty("rules")
	private String rules;
	@JsonProperty("mandateList")
	private String mandateList;
	@JsonProperty("quarantineOnArrivalAreas")
	private List<QuarantineOnArrivalArea> quarantineOnArrivalAreas = null;
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

	@JsonProperty("eligiblePerson")
	public String getEligiblePerson() {
		return eligiblePerson;
	}

	@JsonProperty("eligiblePerson")
	public void setEligiblePerson(String eligiblePerson) {
		this.eligiblePerson = eligiblePerson;
	}

	@JsonProperty("quarantineType")
	public String getQuarantineType() {
		return quarantineType;
	}

	@JsonProperty("quarantineType")
	public void setQuarantineType(String quarantineType) {
		this.quarantineType = quarantineType;
	}

	@JsonProperty("duration")
	public Integer getDuration() {
		return duration;
	}

	@JsonProperty("duration")
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	@JsonProperty("rules")
	public String getRules() {
		return rules;
	}

	@JsonProperty("rules")
	public void setRules(String rules) {
		this.rules = rules;
	}

	@JsonProperty("mandateList")
	public String getMandateList() {
		return mandateList;
	}

	@JsonProperty("mandateList")
	public void setMandateList(String mandateList) {
		this.mandateList = mandateList;
	}

	@JsonProperty("quarantineOnArrivalAreas")
	public List<QuarantineOnArrivalArea> getQuarantineOnArrivalAreas() {
		return quarantineOnArrivalAreas;
	}

	@JsonProperty("quarantineOnArrivalAreas")
	public void setQuarantineOnArrivalAreas(List<QuarantineOnArrivalArea> quarantineOnArrivalAreas) {
		this.quarantineOnArrivalAreas = quarantineOnArrivalAreas;
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