package hu.kvcspt.ctreportingtoolbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r5.model.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    @ManyToOne
    @JoinColumn(name = "report_template_id")
    @JsonIgnore
    private ReportTemplate template;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scan> scans;

    public DiagnosticReport toDiagnosticReport(){
        DiagnosticReport diagnosticReport = new DiagnosticReport();
        diagnosticReport.setId(String.valueOf(id));
        diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        diagnosticReport.setCode(new CodeableConcept().setText(title));
        diagnosticReport.setIssued(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        diagnosticReport.addPerformer(new Reference(createdBy.toPractitioner()));

        for (Section section : getSections()) {
            if (section.getTitle().equals("Findings")) {
                Observation finding = new Observation();
                finding.setCode(new CodeableConcept().setText("Imaging Findings"));
                finding.setValue(new CodeableConcept().setText(section.getContent()));
                diagnosticReport.addResult(new Reference(finding));
            }
        }

        for (Section section : getSections()) {
            if (section.getTitle().equals("Conclusion")) {
                diagnosticReport.setConclusion(section.getContent());
            }
        }

        return diagnosticReport;
    }

    public DiagnosticReport toFhirDiagnosticReport() {
        org.hl7.fhir.r5.model.Patient fhirPatient = patient.toFhirPatient();
        DiagnosticReport diagnosticReport = new DiagnosticReport();
        diagnosticReport.setId(String.valueOf(id));
        diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        diagnosticReport.setCode(new CodeableConcept().setText(title));
        diagnosticReport.setSubject(new Reference(fhirPatient));
        diagnosticReport.setIssued(Date.from(createdDate.atZone(ZoneId.systemDefault()).toInstant()));

        List<ImagingStudy> imagingStudies = scans.stream().map(Scan::toImagingStudy).toList();
        for (ImagingStudy study : imagingStudies) {
            diagnosticReport.addStudy(new Reference(study));
        }

        // Add findings and conclusion sections
        for (Section section : sections) {
            if (section.getTitle().equalsIgnoreCase("Findings")) {
                Observation finding = new Observation();
                finding.setCode(new CodeableConcept().setText("Imaging Findings"));
                finding.setValue(new CodeableConcept().setText(section.getContent()));
                diagnosticReport.addResult(new Reference(finding));
            } else if (section.getTitle().equalsIgnoreCase("Conclusion")) {
                diagnosticReport.setConclusion(section.getContent());
            }
        }

        return diagnosticReport;
    }
}
