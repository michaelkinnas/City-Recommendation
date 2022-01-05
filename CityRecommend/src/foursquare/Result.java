
package foursquare;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fsq_id",
    "categories",
    "chains",
    "distance",
    "geocodes",
    "location",
    "name",
    "related_places",
    "timezone"
})
@Generated("jsonschema2pojo")
public class Result {

    @JsonProperty("fsq_id")
    private String fsqId;
    @JsonProperty("categories")
    private List<Category> categories = null;
    @JsonProperty("chains")
    private List<Object> chains = null;
    @JsonProperty("distance")
    private Integer distance;
    @JsonProperty("geocodes")
    private Geocodes geocodes;
    @JsonProperty("location")
    private Location location;
    @JsonProperty("name")
    private String name;
    @JsonProperty("related_places")
    private RelatedPlaces relatedPlaces;
    @JsonProperty("timezone")
    private String timezone;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("fsq_id")
    public String getFsqId() {
        return fsqId;
    }

    @JsonProperty("fsq_id")
    public void setFsqId(String fsqId) {
        this.fsqId = fsqId;
    }

    @JsonProperty("categories")
    public List<Category> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @JsonProperty("chains")
    public List<Object> getChains() {
        return chains;
    }

    @JsonProperty("chains")
    public void setChains(List<Object> chains) {
        this.chains = chains;
    }

    @JsonProperty("distance")
    public Integer getDistance() {
        return distance;
    }

    @JsonProperty("distance")
    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    @JsonProperty("geocodes")
    public Geocodes getGeocodes() {
        return geocodes;
    }

    @JsonProperty("geocodes")
    public void setGeocodes(Geocodes geocodes) {
        this.geocodes = geocodes;
    }

    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(Location location) {
        this.location = location;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("related_places")
    public RelatedPlaces getRelatedPlaces() {
        return relatedPlaces;
    }

    @JsonProperty("related_places")
    public void setRelatedPlaces(RelatedPlaces relatedPlaces) {
        this.relatedPlaces = relatedPlaces;
    }

    @JsonProperty("timezone")
    public String getTimezone() {
        return timezone;
    }

    @JsonProperty("timezone")
    public void setTimezone(String timezone) {
        this.timezone = timezone;
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
