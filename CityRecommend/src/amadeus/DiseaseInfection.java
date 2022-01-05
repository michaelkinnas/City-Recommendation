
package amadeus;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date",
    "level",
    "rate",
    "infectionMapLink"
})

public class DiseaseInfection {

    @JsonProperty("date")
    private String date;
    @JsonProperty("level")
    private String level;
    @JsonProperty("rate")
    private Double rate;
    @JsonProperty("infectionMapLink")
    private String infectionMapLink;

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("level")
    public String getLevel() {
        return level;
    }

    @JsonProperty("level")
    public void setLevel(String level) {
        this.level = level;
    }

    @JsonProperty("rate")
    public Double getRate() {
        return rate;
    }

    @JsonProperty("rate")
    public void setRate(Double rate) {
        this.rate = rate;
    }

    @JsonProperty("infectionMapLink")
    public String getInfectionMapLink() {
        return infectionMapLink;
    }

    @JsonProperty("infectionMapLink")
    public void setInfectionMapLink(String infectionMapLink) {
        this.infectionMapLink = infectionMapLink;
    }

}
