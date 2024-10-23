package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.logic.ReportTemplateService;
import hu.kvcspt.ctreportingtoolbackend.model.ReportTemplate;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report-templates")
@AllArgsConstructor
public final class ReportTemplateController {
    private final ReportTemplateService reportTemplateService;

    @GetMapping
    public List<ReportTemplate> getAllReportTemplates() {
        return reportTemplateService.getAllReportTemplates();
    }

    @GetMapping("/{id}")
    public ReportTemplate getReportTemplateById(@PathVariable Long id) {
        return reportTemplateService.getReportTemplateById(id);
    }

    @PutMapping("/{id}")
    public ReportTemplate updateReportTemplate(@PathVariable Long id, @RequestBody ReportTemplate reportTemplate) {
        reportTemplate.setId(id);
        return reportTemplateService.updateReportTemplate(reportTemplate);
    }

    @PostMapping
    public ReportTemplate createReportTemplate(@RequestBody ReportTemplate reportTemplate) {
        return reportTemplateService.createReportTemplate(reportTemplate);
    }

    @DeleteMapping("/{id}")
    public void deleteReportTemplate(@PathVariable Long id) {
        reportTemplateService.deleteReportTemplate(ReportTemplate.builder().id(id).build());
    }
}
