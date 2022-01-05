
package amadeus;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date",
    "text",
    "ban",
    "throughDate",
    "rules",
    "exemptions",
    "bannedArea",
    "borderBan"
   
})

public class Entry {

    @JsonProperty("date")
    private String date;
    @JsonProperty("text")
    private String text;
    @JsonProperty("ban")
    private String ban;
    @JsonProperty("throughDate")
    private String throughDate;
    @JsonProperty("rules")
    private String rules;
    @JsonProperty("exemptions")
    private String exemptions;
    @JsonProperty("bannedArea")
    private List<BannedArea> bannedArea = null;
    @JsonProperty("borderBan")
    private List<BorderBan> borderBan = null;

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

    @JsonProperty("ban")
    public String getBan() {
        return ban;
    }

    @JsonProperty("ban")
    public void setBan(String ban) {
        this.ban = ban;
    }

    @JsonProperty("throughDate")
    public String getThroughDate() {
        return throughDate;
    }

    @JsonProperty("throughDate")
    public void setThroughDate(String throughDate) {
        this.throughDate = throughDate;
    }

    @JsonProperty("rules")
    public String getRules() {
        return rules;
    }

    @JsonProperty("rules")
    public void setRules(String rules) {
        this.rules = rules;
    }

    @JsonProperty("exemptions")
    public String getExemptions() {
        return exemptions;
    }

    @JsonProperty("exemptions")
    public void setExemptions(String exemptions) {
        this.exemptions = exemptions;
    }

    @JsonProperty("bannedArea")
    public List<BannedArea> getBannedArea() {
        return bannedArea;
    }

    @JsonProperty("bannedArea")
    public void setBannedArea(List<BannedArea> bannedArea) {
        this.bannedArea = bannedArea;
    }

    @JsonProperty("borderBan")
    public List<BorderBan> getBorderBan() {
        return borderBan;
    }

    @JsonProperty("borderBan")
    public void setBorderBan(List<BorderBan> borderBan) {
        this.borderBan = borderBan;
    }

}
