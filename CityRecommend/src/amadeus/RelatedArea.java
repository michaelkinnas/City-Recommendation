
package amadeus;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "methods",
    "rel"
})

public class RelatedArea {

    @JsonProperty("methods")
    private List<String> methods = null;
    @JsonProperty("rel")
    private String rel;

    @JsonProperty("methods")
    public List<String> getMethods() {
        return methods;
    }

    @JsonProperty("methods")
    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    @JsonProperty("rel")
    public String getRel() {
        return rel;
    }

    @JsonProperty("rel")
    public void setRel(String rel) {
        this.rel = rel;
    }

}
