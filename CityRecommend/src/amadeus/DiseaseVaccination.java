
package amadeus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({
    "date",
    "text",
    "isRequired",
    "referenceLink",
    "acceptedCertificates",
    "qualifiedVaccines",
    "policy",
    "exemptions",
    "details"
})

public class DiseaseVaccination {

    @JsonProperty("date")
    private String date;
    @JsonProperty("text")
    private String text;
    @JsonProperty("isRequired")
    private String isRequired;
    @JsonProperty("referenceLink")
    private String referenceLink;
    @JsonProperty("acceptedCertificates")
    private List<String> acceptedCertificates = null;
    @JsonProperty("qualifiedVaccines")
    private List<String> qualifiedVaccines = null;
    @JsonProperty("policy")
    private String policy;
    @JsonProperty("exemptions")
    private String exemptions;
    @JsonProperty("details")
    private String details;
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

    @JsonProperty("referenceLink")
    public String getReferenceLink() {
        return referenceLink;
    }

    @JsonProperty("referenceLink")
    public void setReferenceLink(String referenceLink) {
        this.referenceLink = referenceLink;
    }

    @JsonProperty("acceptedCertificates")
    public List<String> getAcceptedCertificates() {
        return acceptedCertificates;
    }

    @JsonProperty("acceptedCertificates")
    public void setAcceptedCertificates(List<String> acceptedCertificates) {
        this.acceptedCertificates = acceptedCertificates;
    }

    @JsonProperty("qualifiedVaccines")
    public List<String> getQualifiedVaccines() {
        return qualifiedVaccines;
    }

    @JsonProperty("qualifiedVaccines")
    public void setQualifiedVaccines(List<String> qualifiedVaccines) {
        this.qualifiedVaccines = qualifiedVaccines;
    }

    @JsonProperty("policy")
    public String getPolicy() {
        return policy;
    }

    @JsonProperty("policy")
    public void setPolicy(String policy) {
        this.policy = policy;
    }

    @JsonProperty("exemptions")
    public String getExemptions() {
        return exemptions;
    }

    @JsonProperty("exemptions")
    public void setExemptions(String exemptions) {
        this.exemptions = exemptions;
    }
    
    @JsonProperty("details")
    public String getDetails() {
        return details;
    }

    @JsonProperty("details")
    public void setDetails(String details) {
        this.details = details;
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
