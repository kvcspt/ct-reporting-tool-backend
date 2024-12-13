package hu.kvcspt.ctreportingtoolbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.ImagingStudy;
import org.hl7.fhir.r5.model.Reference;

import java.time.LocalDateTime;
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
    private LocalDateTime scanDate;
    private String description;
    private String bodyPart;
    private Patient patient;
    private String performer;
    private String resultsInterpreter;
    private String studyUid;
    private String seriesUid;

    public ImagingStudy toImagingStudy(org.hl7.fhir.r5.model.Patient fhirPatient) {
        ImagingStudy imagingStudy = new ImagingStudy();
        imagingStudy.setId(studyUid);
        imagingStudy.setDescription(description);

        if (fhirPatient != null) {
            imagingStudy.setSubject(new Reference(fhirPatient));
        } else {
            imagingStudy.setSubject(new Reference()); // Handle null case
        }

        Coding modalityCoding = new Coding();
        modalityCoding.setSystem("https://dicom.nema.org/resources/ontology/DCM");
        modalityCoding.setCode(modality);

        CodeableConcept modalityConcept = new CodeableConcept();
        modalityConcept.addCoding(modalityCoding);

        imagingStudy.setModality(List.of(modalityConcept));

        if (scanDate != null) {
            imagingStudy.setStarted(java.util.Date.from(scanDate.atZone(ZoneId.systemDefault()).toInstant()));
        }

        return imagingStudy;
    }
}

