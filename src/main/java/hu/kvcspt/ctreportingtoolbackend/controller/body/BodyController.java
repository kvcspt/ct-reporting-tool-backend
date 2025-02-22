package hu.kvcspt.ctreportingtoolbackend.controller.body;

import hu.kvcspt.ctreportingtoolbackend.dto.BodyReportDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.BodyTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.BodyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/body")
@AllArgsConstructor
public class BodyController {
    private final BodyService bodyService;

    @PostMapping("/dynamic/html")
    public ResponseEntity<ByteArrayResource> generateDynamicHtmlReport(@RequestBody @Valid List<BodyReportDTO> formData) {
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
    public ResponseEntity<ByteArrayResource> generateDynamicPdfReport(@RequestBody @Valid List<BodyReportDTO> formData) {
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

    private String generateFilename(String ext){
        return UUID.randomUUID()+"."+ext;
    }
}
