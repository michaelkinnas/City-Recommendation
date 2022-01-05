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
"covidDashboardLink",
"healthDepartementSiteLink",
"governmentSiteLink"
})

public class DataSources {

@JsonProperty("covidDashboardLink")
private String covidDashboardLink;
@JsonProperty("healthDepartementSiteLink")
private String healthDepartementSiteLink;
@JsonProperty("governmentSiteLink")
private String governmentSiteLink;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("covidDashboardLink")
public String getCovidDashboardLink() {
return covidDashboardLink;
}

@JsonProperty("covidDashboardLink")
public void setCovidDashboardLink(String covidDashboardLink) {
this.covidDashboardLink = covidDashboardLink;
}

@JsonProperty("healthDepartementSiteLink")
public String getHealthDepartementSiteLink() {
return healthDepartementSiteLink;
}

@JsonProperty("healthDepartementSiteLink")
public void setHealthDepartementSiteLink(String healthDepartementSiteLink) {
this.healthDepartementSiteLink = healthDepartementSiteLink;
}

@JsonProperty("governmentSiteLink")
public String getGovernmentSiteLink() {
return governmentSiteLink;
}

@JsonProperty("governmentSiteLink")
public void setGovernmentSiteLink(String governmentSiteLink) {
this.governmentSiteLink = governmentSiteLink;
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