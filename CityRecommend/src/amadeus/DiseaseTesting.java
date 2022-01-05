
package amadeus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date",
    "text",
    "isRequired",
    "when",
    "requirement",
    "rules",
    "testType",
    "minimumAge",
    "validityPeriod"
})

public class DiseaseTesting {

    @JsonProperty("date")
    private String date;
    @JsonProperty("text")
    private String text;
    @JsonProperty("isRequired")
    private String isRequired;
    @JsonProperty("when")
    private String when;
    @JsonProperty("requirement")
    private String requirement;
    @JsonProperty("rules")
    private String rules;
    @JsonProperty("testType")
    private String testType;
    @JsonProperty("minimumAge")
    private Integer minimumAge;
    @JsonProperty("validityPeriod")
    private ValidityPeriod validityPeriod;

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

    @JsonProperty("when")
    public String getWhen() {
        return when;
    }

    @JsonProperty("when")
    public void setWhen(String when) {
        this.when = when;
    }

    @JsonProperty("requirement")
    public String getRequirement() {
        return requirement;
    }

    @JsonProperty("requirement")
    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    @JsonProperty("rules")
    public String getRules() {
        return rules;
    }

    @JsonProperty("rules")
    public void setRules(String rules) {
        this.rules = rules;
    }

    @JsonProperty("testType")
    public String getTestType() {
        return testType;
    }

    @JsonProperty("testType")
    public void setTestType(String testType) {
        this.testType = testType;
    }

    @JsonProperty("minimumAge")
    public Integer getMinimumAge() {
        return minimumAge;
    }

    @JsonProperty("minimumAge")
    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }

    @JsonProperty("validityPeriod")
    public ValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }

    @JsonProperty("validityPeriod")
    public void setValidityPeriod(ValidityPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

}
