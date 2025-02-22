package hu.kvcspt.ctreportingtoolbackend.controller.body;

import hu.kvcspt.ctreportingtoolbackend.logic.body.AbdomenService;
import hu.kvcspt.ctreportingtoolbackend.logic.body.BodyService;
import hu.kvcspt.ctreportingtoolbackend.logic.body.ChestService;
import hu.kvcspt.ctreportingtoolbackend.logic.body.KneeService;
import hu.kvcspt.ctreportingtoolbackend.enums.BodyType;
import hu.kvcspt.ctreportingtoolbackend.logic.BodyService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
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
            assert bodyService != null;
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

    private String generateFilename(String ext){
        return UUID.randomUUID()+"."+ext;
    }
}
