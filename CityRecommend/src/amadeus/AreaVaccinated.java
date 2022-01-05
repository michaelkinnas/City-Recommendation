
package amadeus;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date",
    "vaccinationDoseStatus",
    "percentage",
    "text"
})

public class AreaVaccinated {

    @JsonProperty("date")
    private String date;
    @JsonProperty("vaccinationDoseStatus")
    private String vaccinationDoseStatus;
    @JsonProperty("percentage")
    private Double percentage;
    @JsonProperty("text")
    private Double text;

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("vaccinationDoseStatus")
    public String getVaccinationDoseStatus() {
        return vaccinationDoseStatus;
    }

    @JsonProperty("vaccinationDoseStatus")
    public void setVaccinationDoseStatus(String vaccinationDoseStatus) {
        this.vaccinationDoseStatus = vaccinationDoseStatus;
    }

    @JsonProperty("percentage")
    public Double getPercentage() {
        return percentage;
    }

    @JsonProperty("percentage")
    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
    
    @JsonProperty("text")
    public Double getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(Double text) {
        this.text = text;
    }

}
