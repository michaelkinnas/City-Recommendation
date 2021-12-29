
package amadeus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date",
    "text",
    "transportationType",
    "isBanned",
    "throughDate"
})

public class Transportation {

    @JsonProperty("date")
    private String date;
    @JsonProperty("text")
    private String text;
    @JsonProperty("transportationType")
    private String transportationType;
    @JsonProperty("isBanned")
    private String isBanned;
    @JsonProperty("throughDate")
    private String throughDate;

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

    @JsonProperty("transportationType")
    public String getTransportationType() {
        return transportationType;
    }

    @JsonProperty("transportationType")
    public void setTransportationType(String transportationType) {
        this.transportationType = transportationType;
    }

    @JsonProperty("isBanned")
    public String getIsBanned() {
        return isBanned;
    }

    @JsonProperty("isBanned")
    public void setIsBanned(String isBanned) {
        this.isBanned = isBanned;
    }

    @JsonProperty("throughDate")
    public String getThroughDate() {
        return throughDate;
    }

    @JsonProperty("throughDate")
    public void setThroughDate(String throughDate) {
        this.throughDate = throughDate;
    }

}
