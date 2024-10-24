package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.ScanService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scans")
@AllArgsConstructor
public final class ScanController {

    private final ScanService scanService;

    @GetMapping
    public List<ScanDTO> getAllScans() {
        return scanService.getAllScans();
    }

    @GetMapping("/{id}")
    public ScanDTO getScanById(@PathVariable Long id) {
        return scanService.getScanById(id);
    }

    @PutMapping("/{id}")
    public ScanDTO updateScan(@PathVariable Long id, @RequestBody ScanDTO scan) {
        scan.setId(id);
        return scanService.updateScan(scan);
    }

    @PostMapping
    public ScanDTO addScan(@RequestBody ScanDTO scan) {
        return scanService.createScan(scan);
    }

    @DeleteMapping("/{id}")
    public void deleteScan(@PathVariable Long id) {
        scanService.deleteScan(ScanDTO.builder().id(id).build());
    }
}