
package amadeus;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "areaType",
    "iataCode",
    "geoCode"
})
@Generated("jsonschema2pojo")
public class Area {

    @JsonProperty("name")
    private String name;
    @JsonProperty("areaType")
    private String areaType;
    @JsonProperty("iataCode")
    private String iataCode;
    @JsonProperty("geoCode")
    private GeoCode geoCode;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("areaType")
    public String getAreaType() {
        return areaType;
    }

    @JsonProperty("areaType")
    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    @JsonProperty("iataCode")
    public String getIataCode() {
        return iataCode;
    }

    @JsonProperty("iataCode")
    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    @JsonProperty("geoCode")
    public GeoCode getGeoCode() {
        return geoCode;
    }

    @JsonProperty("geoCode")
    public void setGeoCode(GeoCode geoCode) {
        this.geoCode = geoCode;
    }

}
