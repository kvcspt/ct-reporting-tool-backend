package hu.kvcspt.ctreportingtoolbackend.logic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import hu.kvcspt.ctreportingtoolbackend.dto.*;
import hu.kvcspt.ctreportingtoolbackend.mapper.ReportMapper;
import hu.kvcspt.ctreportingtoolbackend.model.Lesion;
import hu.kvcspt.ctreportingtoolbackend.model.Report;
import hu.kvcspt.ctreportingtoolbackend.util.FhirClient;
import hu.kvcspt.ctreportingtoolbackend.util.FieldExtractor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hl7.fhir.r5.model.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@AllArgsConstructor
@Log4j2
public class ReportService {
    private static final String FINDINGS_KEY = "Findings";
    private static final String CONCLUSION_KEY = "Conclusion";
    private UserService userService;
    private FhirClient client;

    public byte[] generatePdf(ReportDTO reportDTO) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font contentFont = new Font(Font.FontFamily.HELVETICA, 12);

            document.open();
            document.addTitle(reportDTO.getTitle());
            document.add(new Paragraph(reportDTO.getTitle(), titleFont));
            document.add(Chunk.NEWLINE);

            for (Map.Entry<String,String> sectionEntry : reportDTO.getSections().entrySet()) {
                document.add(new Paragraph(sectionEntry.getKey() + ": " + sectionEntry.getValue(), contentFont));
            }

            if (reportDTO.getLesions() != null && !reportDTO.getLesions().isEmpty()) {
                document.add(new Paragraph("Lesions:", sectionFont));
                PdfPTable table = new PdfPTable(4); // columns
                table.setWidthPercentage(100);
                table.setSpacingBefore(10);
                table.setSpacingAfter(10);

                table.addCell(new PdfPCell(new Paragraph("Index", contentFont)));
                table.addCell(new PdfPCell(new Paragraph("Diameter X", contentFont)));
                table.addCell(new PdfPCell(new Paragraph("Diameter Y", contentFont)));
                table.addCell(new PdfPCell(new Paragraph("Diameter Z", contentFont)));

                for (int i = 0; i < reportDTO.getLesions().size(); i++) {
                    LesionDTO lesion = reportDTO.getLesions().get(i);

                    table.addCell(new PdfPCell(new Paragraph(String.valueOf(i + 1), contentFont)));
                    table.addCell(new PdfPCell(new Paragraph(String.valueOf(lesion.getDiameterX()), contentFont)));
                    table.addCell(new PdfPCell(new Paragraph(String.valueOf(lesion.getDiameterY()), contentFont)));
                    table.addCell(new PdfPCell(new Paragraph(String.valueOf(lesion.getDiameterZ()), contentFont)));
                }

                document.add(table);
            }

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return outputStream.toByteArray();
    }

    public String generateDiagnosticReport(FhirSRDTO fhirSRDTO) {
        Report report = ReportMapper.INSTANCE.toEntity(fhirSRDTO.getReport());
        report.setCreatedDate(LocalDateTime.now());
        DiagnosticReport diagnosticReport = toFhirDiagnosticReport(report,fhirSRDTO.getForm(),false);
        FhirContext ctxR5 = FhirContext.forR5();
        IParser jsonParser = ctxR5.newJsonParser();
        jsonParser.setPrettyPrint(true);

        return jsonParser.encodeResourceToString(diagnosticReport);
    }

    public ReportDTO fillReportSections(ReportDTO reportDTO) {
        ReportTemplateDTO template = reportDTO.getTemplate();

        reportDTO.setCreatedBy(userService.getUserFromContext());
        if (template == null || template.getSections() == null) {
            log.warn("ReportTemplate or its sections cannot be null");
            throw new IllegalArgumentException("ReportTemplate or its sections cannot be null");
        }

        Map<String, String> filledSections = reportDTO.getSections();

        for (Map.Entry<String, String> entry : template.getSections().entrySet()) {
            String placeholder = entry.getValue();
            String resolvedValue = resolvePlaceholder(placeholder, reportDTO);
            filledSections.put(entry.getKey(), resolvedValue);
        }

        return reportDTO;
    }

    public DiagnosticReport uploadToFhirServer(FhirSRDTO fhirSRDTO) {
        fhirSRDTO.getReport().setCreatedDate(LocalDateTime.now());
        Report report = ReportMapper.INSTANCE.toEntity(fhirSRDTO.getReport());
        return toFhirDiagnosticReport(report, fhirSRDTO.getForm(), true);
    }

    public DiagnosticReport toFhirDiagnosticReport(Report report, List<BodyReportDTO> form, boolean upload) {
        org.hl7.fhir.r5.model.Patient fhirPatient = report.getPatient().toFhirPatient();

        DiagnosticReport diagnosticReport = new DiagnosticReport();
        diagnosticReport.setId(String.valueOf(report.getId()));
        diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        diagnosticReport.setCode(new CodeableConcept().setText(report.getTitle()));
        diagnosticReport.setIssued(Date.from(report.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()));
        diagnosticReport.addPerformer(new Reference(report.getCreatedBy().toPractitioner()));

        processSections(report, diagnosticReport);

        if (report.getLesions() != null && !report.getLesions().isEmpty()) {
            for (Lesion lesion : report.getLesions()) {
                Observation lesionObservation = createLesionObservation(upload, lesion);
                diagnosticReport.addResult(new Reference(lesionObservation));
            }
        }

        if (form != null && !form.isEmpty()) {
            for (BodyReportDTO entry : form) {
                Observation srObservation = new Observation();
                srObservation.setStatus(Enumerations.ObservationStatus.FINAL);
                srObservation.setCode(new CodeableConcept().setText(entry.getLabel()));
                srObservation.setValue(new StringType(entry.getValue()));

                if (upload) {
                    client.validateAndCreate(srObservation);
                }

                diagnosticReport.addResult(new Reference(srObservation));
            }
        }

        if(upload){
            Patient createdPatient = client.validateAndCreate(fhirPatient);
            diagnosticReport.setSubject(new Reference(createdPatient));
        }

        if(report.getScan() != null){
            ImagingStudy imagingStudy = report.getScan().toImagingStudy((Patient) diagnosticReport.getSubject().getResource());
            Extension reportReferenceExtension = new Extension();
            reportReferenceExtension.setUrl("http://example.org/fhir/StructureDefinition/diagnosticReportReference");
            reportReferenceExtension.setValue(new Reference(diagnosticReport));

            imagingStudy.addExtension(reportReferenceExtension);

            if (upload){
                client.validateAndCreate(imagingStudy);
            }

            diagnosticReport = diagnosticReport.addStudy(new Reference(imagingStudy));
        }

        if(upload){
            client.validateAndCreate(diagnosticReport);
        }

        return diagnosticReport;
    }

    private Observation createLesionObservation(boolean upload, Lesion lesion) {
        Observation lesionObservation = new Observation();
        lesionObservation.setCategory(Collections.singletonList(
                new CodeableConcept().addCoding(new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                        .setCode("imaging")
                        .setDisplay("Imaging"))));

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

        if (upload) {
            lesionObservation = client.validateAndCreate(lesionObservation);
        }
        return lesionObservation;
    }

    private void processSections(Report report, DiagnosticReport diagnosticReport) {
        for (Map.Entry<String, String> section : report.getSections().entrySet()) {
            if (section.getKey().equalsIgnoreCase(FINDINGS_KEY)) {
                Observation finding = new Observation();
                finding.setCode(new CodeableConcept().setText("Imaging Findings"));
                finding.setValue(new CodeableConcept().setText(section.getValue()));
                diagnosticReport.addResult(new Reference(finding));
            } else if (section.getKey().equalsIgnoreCase(CONCLUSION_KEY)) {
                diagnosticReport.setConclusion(section.getValue());
            }
        }
    }

    private String resolvePlaceholder(String placeholder, Object rootObject) {
        String[] path = placeholder.split("\\.");
        Object currentObject = rootObject;

        for (String fieldName : path) {
            if (currentObject == null) {
                return "";
            }

            if(Objects.equals(fieldName, FieldExtractor.OTHER)){
                return "";
            }

            try {
                if (isModelClass(fieldName)) {
                    currentObject = navigateToModelClass(fieldName, rootObject);
                    if (currentObject == null) {
                        return "";
                    }
                    continue;
                }

                Field field = currentObject.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                currentObject = field.get(currentObject);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("NoSuchFieldException: {0}", e);
                return "";
            }
        }

        return currentObject != null ? currentObject.toString() : "";
    }

    private boolean isModelClass(String className) {
        for (Class<?> clazz : FieldExtractor.modelClasses) {
            if (clazz.getSimpleName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    private Object navigateToModelClass(String className, Object rootObject) {
        if (rootObject instanceof ReportDTO report) {
            if (className.equals("Scan") && report.getScan() != null) {
                return report.getScan();
            }

            if (className.equals("Patient") && report.getPatient() != null) {
                return report.getPatient();
            }
        }

        return null;
    }

}
