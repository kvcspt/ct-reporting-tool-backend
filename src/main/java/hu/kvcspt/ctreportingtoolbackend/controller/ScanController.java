package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.logic.ScanService;
import hu.kvcspt.ctreportingtoolbackend.model.Scan;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scans")
@AllArgsConstructor
public final class ScanController {

    private final ScanService scanService;

    @GetMapping
    public List<Scan> getAllScans() {
        return scanService.getAllScans();
    }

    @GetMapping("/{id}")
    public Scan getScanById(@PathVariable Long id) {
        return scanService.getScanById(id);
    }

    @PutMapping("/{id}")
    public Scan updateScan(@PathVariable Long id, @RequestBody Scan scan) {
        scan.setId(id);
        return scanService.updateScan(scan);
    }

    @PostMapping
    public Scan addScan(@RequestBody Scan scan) {
        return scanService.createScan(scan);
    }

    @DeleteMapping("/{id}")
    public void deleteScan(@PathVariable Long id) {
        scanService.deleteScan(Scan.builder().id(id).build());
    }
}