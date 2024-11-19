package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.ScanService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("api/scans")
@AllArgsConstructor
public final class ScanController {

    private final ScanService scanService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDicomFiles(@RequestParam("files") MultipartFile[] files) {
        List<ScanDTO> createdScans = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                ScanDTO createdScan = scanService.processDicomFile(file);
                createdScans.add(createdScan);
            } catch (IOException e) {
                log.error("Error processing file: {} - {}", file.getOriginalFilename(), e.getMessage());
                errors.add("Error processing file: " + file.getOriginalFilename()+ "-" + e.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(Map.of("createdScans", createdScans, "errors", errors));
        }
        return ResponseEntity.ok(createdScans);
    }
}