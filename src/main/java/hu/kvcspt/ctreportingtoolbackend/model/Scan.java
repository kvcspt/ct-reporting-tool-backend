package hu.kvcspt.ctreportingtoolbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.ImagingStudy;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scan {
    private UUID id;
    private String modality;
    private LocalDate scanDate;
    private String description;
    private String bodyPart;
    private Patient patient;
    private String performer;
    private String resultsInterpreter;

    public ImagingStudy toImagingStudy() {
        ImagingStudy imagingStudy = new ImagingStudy();

        imagingStudy.setId(String.valueOf(id));

        Coding modalityCoding = new Coding();
        modalityCoding.setSystem("https://dicom.nema.org/resources/ontology/DCM");
        modalityCoding.setCode(modality);

        CodeableConcept modalityConcept = new CodeableConcept();
        modalityConcept.addCoding(modalityCoding);

        imagingStudy.setModality(List.of(modalityConcept));

        if (scanDate != null) {
            imagingStudy.setStarted(java.util.Date.from(scanDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        imagingStudy.setDescription(description);

        return imagingStudy;
    }
}

