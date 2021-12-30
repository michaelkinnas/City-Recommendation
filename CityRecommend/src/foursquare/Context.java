
package foursquare;

import java.util.HashMap;
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
    "geo_bounds"
})
@Generated("jsonschema2pojo")
public class Context {

    @JsonProperty("geo_bounds")
    private GeoBounds geoBounds;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("geo_bounds")
    public GeoBounds getGeoBounds() {
        return geoBounds;
    }

    @JsonProperty("geo_bounds")
    public void setGeoBounds(GeoBounds geoBounds) {
        this.geoBounds = geoBounds;
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
