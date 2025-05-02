package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.ScanService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("api/scans")
@AllArgsConstructor
public final class ScanController {

    private final ScanService scanService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ScanDTO>> uploadDicomFiles(@RequestParam("files") MultipartFile[] files) throws IOException {
        List<ScanDTO> createdScans = new ArrayList<>();
        for (MultipartFile file : files) {
            createdScans.add(scanService.processDicomFile(file));
        }

        return ResponseEntity.ok(createdScans);
    }

    @GetMapping
    public ResponseEntity<List<ScanDTO>> getScans() {
        return ResponseEntity.ok(scanService.getScans());
    }

    @DeleteMapping("/{studyInstanceUID}")
    public void deleteScan(@PathVariable String studyInstanceUID){
        try {
            scanService.deleteScanStudy(studyInstanceUID);
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }

}