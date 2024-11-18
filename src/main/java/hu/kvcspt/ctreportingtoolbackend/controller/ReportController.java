package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.ReportDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/reports")
@AllArgsConstructor
public final class ReportController {
    private final ReportService reportService;

    @GetMapping("/diagnostic-report")
    public String generateDiagnosticReport(@RequestBody ReportDTO reportDTO) {
        return reportService.generateDiagnosticReport(reportDTO);
    }
}
