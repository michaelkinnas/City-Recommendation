
package amadeus;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "borderType",
    "status"
})

public class BorderBan {

    @JsonProperty("borderType")
    private String borderType;
    @JsonProperty("status")
    private String status;

    @JsonProperty("borderType")
    public String getBorderType() {
        return borderType;
    }

    @JsonProperty("borderType")
    public void setBorderType(String borderType) {
        this.borderType = borderType;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

}
