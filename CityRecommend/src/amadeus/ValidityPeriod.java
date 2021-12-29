
package amadeus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "delay",
    "referenceDateType"
})

public class ValidityPeriod {

    @JsonProperty("delay")
    private String delay;
    @JsonProperty("referenceDateType")
    private String referenceDateType;

    @JsonProperty("delay")
    public String getDelay() {
        return delay;
    }

    @JsonProperty("delay")
    public void setDelay(String delay) {
        this.delay = delay;
    }

    @JsonProperty("referenceDateType")
    public String getReferenceDateType() {
        return referenceDateType;
    }

    @JsonProperty("referenceDateType")
    public void setReferenceDateType(String referenceDateType) {
        this.referenceDateType = referenceDateType;
    }

}
