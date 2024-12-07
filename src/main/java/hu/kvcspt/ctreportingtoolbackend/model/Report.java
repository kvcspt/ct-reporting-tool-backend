package hu.kvcspt.ctreportingtoolbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r5.model.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    private static final String FINDINGS_KEY = "Findings";
    private static final String CONCLUSION_KEY = "Conclusion";
    private UUID id;
    private String title;
    private LocalDateTime createdDate;
    private Patient patient;
    private User createdBy;
    private Map<String, String> sections = new HashMap<>();
    private ReportTemplate template;
    private Scan scan;
    private List<Lesion> lesions;

    public DiagnosticReport toFhirDiagnosticReport() {
        org.hl7.fhir.r5.model.Patient fhirPatient = patient.toFhirPatient();
        DiagnosticReport diagnosticReport = new DiagnosticReport();
        diagnosticReport.setId(String.valueOf(id));
        diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        diagnosticReport.setCode(new CodeableConcept().setText(title));
        diagnosticReport.setSubject(new Reference(fhirPatient));
        diagnosticReport.setIssued(Date.from(createdDate.atZone(ZoneId.systemDefault()).toInstant()));
        diagnosticReport.addPerformer(new Reference(createdBy.toPractitioner()));

        if(scan != null){
            ImagingStudy imagingStudy = scan.toImagingStudy();
            diagnosticReport = diagnosticReport.addStudy(new Reference(imagingStudy));
        }

        for (Map.Entry<String, String> section : getSections().entrySet()) {
            if (section.getKey().equalsIgnoreCase(FINDINGS_KEY)) {
                Observation finding = new Observation();
                finding.setCode(new CodeableConcept().setText("Imaging Findings"));
                finding.setValue(new CodeableConcept().setText(section.getValue()));
                diagnosticReport.addResult(new Reference(finding));
            } else if (section.getKey().equalsIgnoreCase(CONCLUSION_KEY)) {
                diagnosticReport.setConclusion(section.getValue());
            }
        }

        if (lesions != null && !lesions.isEmpty()) {
            for (Lesion lesion : lesions) {
                Observation lesionObservation = new Observation();
                lesionObservation.setCode(new CodeableConcept().setText("Lesion Details"));

                lesionObservation.addComponent()
                        .setCode(new CodeableConcept().setText("Diameter X"))
                        .setValue(new Quantity().setValue(lesion.getDiameterX()).setUnit("mm"));
                lesionObservation.addComponent()
                        .setCode(new CodeableConcept().setText("Diameter Y"))
                        .setValue(new Quantity().setValue(lesion.getDiameterY()).setUnit("mm"));
                lesionObservation.addComponent()
                        .setCode(new CodeableConcept().setText("Diameter Z"))
                        .setValue(new Quantity().setValue(lesion.getDiameterZ()).setUnit("mm"));

                diagnosticReport.addResult(new Reference(lesionObservation));
            }
        }

        return diagnosticReport;
    }
}
