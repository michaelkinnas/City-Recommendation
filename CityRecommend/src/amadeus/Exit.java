
package amadeus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date",
    "text",
    "specialRequirements",
    "rulesLink",
    "isBanned"
})

public class Exit {

    @JsonProperty("date")
    private String date;
    @JsonProperty("text")
    private String text;
    @JsonProperty("specialRequirements")
    private String specialRequirements;
    @JsonProperty("rulesLink")
    private String rulesLink;
    @JsonProperty("isBanned")
    private String isBanned;

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

    @JsonProperty("specialRequirements")
    public String getSpecialRequirements() {
        return specialRequirements;
    }

    @JsonProperty("specialRequirements")
    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    @JsonProperty("rulesLink")
    public String getRulesLink() {
        return rulesLink;
    }

    @JsonProperty("rulesLink")
    public void setRulesLink(String rulesLink) {
        this.rulesLink = rulesLink;
    }

    @JsonProperty("isBanned")
    public String getIsBanned() {
        return isBanned;
    }

    @JsonProperty("isBanned")
    public void setIsBanned(String isBanned) {
        this.isBanned = isBanned;
    }

}
