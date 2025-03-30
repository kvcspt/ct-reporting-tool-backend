package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.BodyReportDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.BodyTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.DicomSRDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.BodyService;
import hu.kvcspt.ctreportingtoolbackend.logic.ScanService;
import lombok.AllArgsConstructor;
import net.sourceforge.plantuml.utils.Log;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/body")
@AllArgsConstructor
public class BodyController {
    private final BodyService bodyService;
    private final ScanService scanService;


    @PostMapping("/dynamic/html")
    public ResponseEntity<ByteArrayResource> generateDynamicHtmlReport(@RequestBody List<BodyReportDTO> formData) {
        try {
            String htmlContent = bodyService.generateHtml(formData);
            byte[] htmlBytes = htmlContent.getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setContentDispositionFormData("attachment", generateFilename("html"));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(htmlBytes));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/dynamic/pdf")
    public ResponseEntity<ByteArrayResource> generateDynamicPdfReport(@RequestBody List<BodyReportDTO> formData) {
        try {
            String htmlContent = bodyService.generateHtml(formData);

            byte[] pdfReport = bodyService.generatePdfFromHtml(htmlContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setContentDispositionFormData("attachment", generateFilename("pdf"));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(pdfReport));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<BodyTemplateDTO> createTemplate(@RequestBody BodyTemplateDTO bodyTemplateDTO){
        try {
            BodyTemplateDTO res = bodyService.createBodyTemplateDTO(bodyTemplateDTO);

            return ResponseEntity.ok(res);
        } catch (Exception e){
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/templates")
    public ResponseEntity<List<BodyTemplateDTO>> getTemplates(){
        return ResponseEntity.ok(bodyService.getTemplates());
    }

    @PutMapping("/templates")
    public ResponseEntity<BodyTemplateDTO> updateTemplate(@RequestBody BodyTemplateDTO bodyTemplateDTO){
        return ResponseEntity.ok(bodyService.updateTemplate(bodyTemplateDTO));
    }

    @DeleteMapping("/templates/{title}")
    public ResponseEntity<?> deleteTemplate(@PathVariable(name = "title") String title){
        try {
            bodyService.deleteBodyTemplate(title);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/dicomsr")
    public ResponseEntity<String> createAndUploadSR(@RequestBody DicomSRDTO body) {
        try {
            File srFile = bodyService.generateDicomSR(body);

            return uploadToOrthanc(srFile);
        } catch (Exception e) {
            Log.error(e.getMessage());
            return new ResponseEntity<>("Failed to create/upload SR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> uploadToOrthanc(File file) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String orthancUrl = scanService.getOrthancServerUrl() + ScanService.INSTANCES_URL;

        ResponseEntity<String> response = restTemplate.postForEntity(
                orthancUrl,
                requestEntity,
                String.class
        );

        return response.getStatusCode() == HttpStatus.OK ?
                new ResponseEntity<>("SR uploaded successfully", HttpStatus.OK) :
                new ResponseEntity<>("Failed to upload SR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String generateFilename(String ext){
        return UUID.randomUUID()+"."+ext;
    }
}
