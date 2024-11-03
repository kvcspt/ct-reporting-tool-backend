package hu.kvcspt.ctreportingtoolbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.ImagingStudy;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "scans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modality;
    private LocalDate scanDate;
    private String description;
    private String bodyPart;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    public ImagingStudy toImagingStudy() {
        ImagingStudy imagingStudy = new ImagingStudy();

        imagingStudy.setId(String.valueOf(id));

        Coding modalityCoding = new Coding();
        modalityCoding.setSystem("https://dicom.nema.org/resources/ontology/DCM");
        modalityCoding.setCode(modality);

        CodeableConcept modalityConcept = new CodeableConcept();
        modalityConcept.addCoding(modalityCoding);

        imagingStudy.setModality(List.of(modalityConcept));

        imagingStudy.setStarted(java.util.Date.from(Instant.from(scanDate)));

        imagingStudy.setDescription(description);

        return imagingStudy;
    }
}

