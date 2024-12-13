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
import hu.kvcspt.ctreportingtoolbackend.dto.LesionDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.ReportDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.ReportTemplateDTO;
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
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

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
    public String generateDiagnosticReport(ReportDTO reportDTO) {
        Report report = ReportMapper.INSTANCE.toEntity(reportDTO);
        report.setCreatedDate(LocalDateTime.now());
        DiagnosticReport diagnosticReport = toFhirDiagnosticReport(report,false);
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
            String placeholder = entry.getValue(); // e.g., "Patient.name"
            String resolvedValue = resolvePlaceholder(placeholder, reportDTO);
            filledSections.put(entry.getKey(), resolvedValue);
        }

        return reportDTO;
    }

    public DiagnosticReport uploadToFhirServer(ReportDTO reportDTO) {
        reportDTO.setCreatedDate(LocalDateTime.now());
        Report report = ReportMapper.INSTANCE.toEntity(reportDTO);
        return toFhirDiagnosticReport(report,true);
    }

    public DiagnosticReport toFhirDiagnosticReport(Report report, boolean upload) {
        org.hl7.fhir.r5.model.Patient fhirPatient = report.getPatient().toFhirPatient();


        DiagnosticReport diagnosticReport = new DiagnosticReport();
        diagnosticReport.setId(String.valueOf(report.getId()));
        diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        diagnosticReport.setCode(new CodeableConcept().setText(report.getTitle()));
        diagnosticReport.setIssued(Date.from(report.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()));
        diagnosticReport.addPerformer(new Reference(report.getCreatedBy().toPractitioner()));

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

        if (report.getLesions() != null && !report.getLesions().isEmpty()) {
            for (Lesion lesion : report.getLesions()) {
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
                diagnosticReport.addResult(new Reference(lesionObservation));
            }
        }

        Patient createdPatient = null;
        if(upload){
            createdPatient = client.validateAndCreate(fhirPatient);
            diagnosticReport.setSubject(new Reference(createdPatient));
        }

        if(report.getScan() != null){
            ImagingStudy imagingStudy = report.getScan().toImagingStudy(createdPatient);
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

    private String resolvePlaceholder(String placeholder, Object rootObject) {
        String[] path = placeholder.split("\\."); // e.g., ["Scan", "performer"]
        Object currentObject = rootObject;

        for (String fieldName : path) {
            if (currentObject == null) {
                return ""; // If any part of the hierarchy is null, return empty string
            }

            if(Objects.equals(fieldName, FieldExtractor.OTHER)){
                return "";
            }

            try {
                // Ha a mező egy ismert osztálynév (pl. "Scan"), akkor navigáljunk az osztály megfelelő mezőjére
                if (isModelClass(fieldName)) {
                    currentObject = navigateToModelClass(fieldName, rootObject);
                    if (currentObject == null) {
                        return ""; // Nem található megfelelő osztálypéldány
                    }
                    continue; // Lépjünk a következő mezőre
                }

                // Ha nem osztálynév, akkor próbáljuk meg elérni az aktuális objektum mezőjét
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

            // Példa: külön mezőkezelés, ha osztálynév "Scan"
            if (className.equals("Scan") && report.getScan() != null) {
                return report.getScan(); // Első Scan objektumot használja
            }

            if (className.equals("Patient") && report.getPatient() != null) {
                return report.getPatient(); // Navigálj a Patient példányra
            }

            // Egyéb típusok kezelése hasonlóan
        }

        return null; // Ha nem található megfelelő osztálypéldány
    }

}
