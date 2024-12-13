package hu.kvcspt.ctreportingtoolbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.kvcspt.ctreportingtoolbackend.dto.ReportDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.ReportTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.ReportService;
import hu.kvcspt.ctreportingtoolbackend.logic.ReportTemplateService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.UUID;

@RestController
@RequestMapping("api/reports")
@AllArgsConstructor
public final class ReportController {
    private final ReportService reportService;
    private final ReportTemplateService reportTemplateService;
    private SpringTemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    @PostMapping("/pdf")
    public ResponseEntity<byte[]> saveAsPdf(@RequestBody ReportDTO reportDTO){
        byte[] pdfBytes = reportService.generatePdf(reportDTO);

        String filename = getFilename(reportDTO, "pdf");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/html")
    public ResponseEntity<ByteArrayResource> saveAsHtml(@RequestBody ReportDTO reportDTO){
        Context context = new Context();
        context.setVariable("report", reportDTO);

        String htmlContent = templateEngine.process("reportTemplate", context);

        String filename = getFilename(reportDTO, "html");

        byte[] htmlBytes = htmlContent.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ByteArrayResource(htmlBytes));
    }

    @PostMapping("/json")
    public ResponseEntity<byte[]> saveAsJson(@RequestBody ReportDTO reportDTO){
        try {
            String jsonResponse = objectMapper.writeValueAsString(reportDTO);
            String fileName = getFilename(reportDTO, ".json");

            byte[] jsonBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonBytes);
        } catch (Exception e) {
            String errorResponse = "{\"error\": \"Error generating JSON: " + e.getMessage() + "\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse.getBytes(StandardCharsets.UTF_8));
        }
    }

    @PostMapping("/fhir-json")
    public ResponseEntity<byte[]> saveAsFhirJson(@RequestBody ReportDTO reportDTO) {
        try {
            String fhirJson = this.reportService.generateDiagnosticReport(reportDTO);

            byte[] jsonBytes = fhirJson.getBytes(StandardCharsets.UTF_8);
            String fileName = getFilename(reportDTO, ".json");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonBytes);
        } catch (Exception e) {
            String errorResponse = "{\"error\": \"Error generating FHIR JSON: " + e.getMessage() + "\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse.getBytes(StandardCharsets.UTF_8));
        }
    }

    @PostMapping("/fhircast")
    public ResponseEntity<?> uploadToFhirCast(@RequestBody ReportDTO reportDTO){
        var resp = reportService.uploadToFhirServer(reportDTO);
        if (resp == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/template/{templateId}")
    public ResponseEntity<ReportDTO> generateReportFromTemplate(@PathVariable(name = "templateId") Long reportTemplateId, @RequestBody ScanDTO scanDTO){
        if (scanDTO == null || scanDTO.getPatient() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        ReportTemplateDTO reportTemplateDTO = reportTemplateService.getReportTemplateById(reportTemplateId);
        if (reportTemplateDTO == null) {
            return ResponseEntity.notFound().build();
        }

        ReportDTO reportDTO = ReportDTO.builder()
                .template(reportTemplateDTO)
                .sections(new HashMap<>())
                .title(reportTemplateDTO.getName())
                .createdDate(LocalDateTime.now())
                .patient(scanDTO.getPatient())
                .scan(scanDTO)
                .id(UUID.randomUUID())
                .build();
        reportDTO = reportService.fillReportSections(reportDTO);

        return ResponseEntity.ok(reportDTO);
    }

    private static String getFilename(ReportDTO reportDTO, String ext) {
        return new StringJoiner(".").add(String.valueOf(reportDTO.getId())).add(ext).toString();
    }
}
