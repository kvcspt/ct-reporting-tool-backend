package hu.kvcspt.ctreportingtoolbackend.logic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import hu.kvcspt.ctreportingtoolbackend.dto.ReportDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.ReportTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.mapper.ReportMapper;
import hu.kvcspt.ctreportingtoolbackend.model.Report;
import hu.kvcspt.ctreportingtoolbackend.util.FieldExtractor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hl7.fhir.r5.model.DiagnosticReport;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Map;

@Service
@AllArgsConstructor
@Log4j2
public class ReportService {
    private UserService userService;
    public byte[] generatePdf(ReportDTO reportDTO) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font contentFont = new Font(Font.FontFamily.HELVETICA, 12);

            document.open();
            document.addTitle(reportDTO.getTitle());
            document.add(new Paragraph(reportDTO.getTitle(), titleFont));
            document.add(Chunk.NEWLINE);

            for (Map.Entry<String,String> sectionEntry : reportDTO.getSections().entrySet()) {
                document.add(new Paragraph(sectionEntry.getKey() + ": " + sectionEntry.getValue(), contentFont));
            }

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return outputStream.toByteArray();
    }
    public String generateDiagnosticReport(ReportDTO reportDTO) {
        Report report = ReportMapper.INSTANCE.toEntity(reportDTO);
        DiagnosticReport diagnosticReport = report.toFhirDiagnosticReport();
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

    private String resolvePlaceholder(String placeholder, Object rootObject) {
        String[] path = placeholder.split("\\."); // e.g., ["Scan", "performer"]
        Object currentObject = rootObject;

        for (String fieldName : path) {
            if (currentObject == null) {
                return ""; // If any part of the hierarchy is null, return empty string
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
                return ""; // Return empty if field resolution fails
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
