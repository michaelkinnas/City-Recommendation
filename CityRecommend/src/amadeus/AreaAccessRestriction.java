
package amadeus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "transportation",
    "declarationDocuments",
    "entry",
    "diseaseTesting",
    "tracingApplication",
    "quarantineModality",
    "mask",
    "exit",
    "otherRestriction",
    "diseaseVaccination"
})

public class AreaAccessRestriction {

    @JsonProperty("transportation")
    private Transportation transportation;
    @JsonProperty("declarationDocuments")
    private DeclarationDocuments declarationDocuments;
    @JsonProperty("entry")
    private Entry entry;
    @JsonProperty("diseaseTesting")
    private DiseaseTesting diseaseTesting;
    @JsonProperty("tracingApplication")
    private TracingApplication tracingApplication;
    @JsonProperty("quarantineModality")
    private QuarantineModality quarantineModality;
    @JsonProperty("mask")
    private Mask mask;
    @JsonProperty("exit")
    private Exit exit;
    @JsonProperty("otherRestriction")
    private OtherRestriction otherRestriction;
    @JsonProperty("diseaseVaccination")
    private DiseaseVaccination diseaseVaccination;

    @JsonProperty("transportation")
    public Transportation getTransportation() {
        return transportation;
    }

    @JsonProperty("transportation")
    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    @JsonProperty("declarationDocuments")
    public DeclarationDocuments getDeclarationDocuments() {
        return declarationDocuments;
    }

    @JsonProperty("declarationDocuments")
    public void setDeclarationDocuments(DeclarationDocuments declarationDocuments) {
        this.declarationDocuments = declarationDocuments;
    }

    @JsonProperty("entry")
    public Entry getEntry() {
        return entry;
    }

    @JsonProperty("entry")
    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    @JsonProperty("diseaseTesting")
    public DiseaseTesting getDiseaseTesting() {
        return diseaseTesting;
    }

    @JsonProperty("diseaseTesting")
    public void setDiseaseTesting(DiseaseTesting diseaseTesting) {
        this.diseaseTesting = diseaseTesting;
    }

    @JsonProperty("tracingApplication")
    public TracingApplication getTracingApplication() {
        return tracingApplication;
    }

    @JsonProperty("tracingApplication")
    public void setTracingApplication(TracingApplication tracingApplication) {
        this.tracingApplication = tracingApplication;
    }

    @JsonProperty("quarantineModality")
    public QuarantineModality getQuarantineModality() {
        return quarantineModality;
    }

    @JsonProperty("quarantineModality")
    public void setQuarantineModality(QuarantineModality quarantineModality) {
        this.quarantineModality = quarantineModality;
    }

    @JsonProperty("mask")
    public Mask getMask() {
        return mask;
    }

    @JsonProperty("mask")
    public void setMask(Mask mask) {
        this.mask = mask;
    }

    @JsonProperty("exit")
    public Exit getExit() {
        return exit;
    }

    @JsonProperty("exit")
    public void setExit(Exit exit) {
        this.exit = exit;
    }

    @JsonProperty("otherRestriction")
    public OtherRestriction getOtherRestriction() {
        return otherRestriction;
    }

    @JsonProperty("otherRestriction")
    public void setOtherRestriction(OtherRestriction otherRestriction) {
        this.otherRestriction = otherRestriction;
    }

    @JsonProperty("diseaseVaccination")
    public DiseaseVaccination getDiseaseVaccination() {
        return diseaseVaccination;
    }

    @JsonProperty("diseaseVaccination")
    public void setDiseaseVaccination(DiseaseVaccination diseaseVaccination) {
        this.diseaseVaccination = diseaseVaccination;
    }

}
