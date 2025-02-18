package hu.kvcspt.ctreportingtoolbackend.controller.body;

import hu.kvcspt.ctreportingtoolbackend.logic.body.AbdomenService;
import hu.kvcspt.ctreportingtoolbackend.logic.body.BodyService;
import hu.kvcspt.ctreportingtoolbackend.logic.body.KneeService;
import hu.kvcspt.ctreportingtoolbackend.enums.BodyType;
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
    private AbdomenService abdomenService;
    private final KneeService kneeService;

    @PostMapping("/html")
    public ResponseEntity<ByteArrayResource> generateHtmlReport(@RequestBody Map<String, Object> formData, @RequestParam("body") String bodyType) {
        BodyService bodyService = null;
        if (bodyType.equals(BodyType.KNEE.toString().toLowerCase())){
            bodyService = kneeService;
        } else if(bodyType.equals(BodyType.ABDOMEN.toString().toLowerCase())){
            bodyService = abdomenService;
        }

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

    @PostMapping("/pdf")
    public ResponseEntity<ByteArrayResource> kneeHtml(@RequestBody Map<String, Object> formData, @RequestParam("body") String bodyType){
        BodyService bodyService = null;
        if (bodyType.equals(BodyType.KNEE.toString().toLowerCase())){
            bodyService = kneeService;
        } else if(bodyType.equals(BodyType.ABDOMEN.toString().toLowerCase())){
            bodyService = abdomenService;
        }

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
