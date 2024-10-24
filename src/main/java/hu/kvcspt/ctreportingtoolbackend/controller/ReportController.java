package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.ReportDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
public final class ReportController {
    private final ReportService reportService;

    @GetMapping
    public List<ReportDTO> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public ReportDTO getReportById(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    @PutMapping("/{id}")
    public ReportDTO updateReport(@PathVariable Long id, @RequestBody ReportDTO report) {
        report.setId(id);
        return reportService.updateReport(report);
    }

    @PostMapping
    public ReportDTO createReport(@RequestBody ReportDTO report) {
        return reportService.createReport(report);
    }

    @DeleteMapping("/{id}")
    public void deleteReport(@PathVariable Long id) {
        reportService.deleteReport(ReportDTO.builder().id(id).build());
    }

    @GetMapping("/{reportId}/diagnosticReport")
    public String generateDiagnosticReport(@PathVariable Long reportId) {
        return reportService.generateDiagnosticReport(reportId);
    }
}
