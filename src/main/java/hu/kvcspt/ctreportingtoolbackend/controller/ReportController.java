package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.logic.ReportService;
import hu.kvcspt.ctreportingtoolbackend.model.Report;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r5.model.DiagnosticReport;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
public final class ReportController {
    private final ReportService reportService;

    @GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public Report getReportById(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    @PutMapping("/{id}")
    public Report updateReport(@PathVariable Long id, @RequestBody Report report) {
        report.setId(id);
        return reportService.updateReport(report);
    }

    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return reportService.createReport(report);
    }

    @DeleteMapping("/{id}")
    public void deleteReport(@PathVariable Long id) {
        reportService.deleteReport(Report.builder().id(id).build());
    }

    @GetMapping("/{reportId}/diagnosticReport")
    public DiagnosticReport generateDiagnosticReport(@PathVariable Long reportId) {
        return reportService.generateDiagnosticReport(reportId);
    }
}
