package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.ReportTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.ReportTemplateService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report-templates")
@AllArgsConstructor
public final class ReportTemplateController {
    private final ReportTemplateService reportTemplateService;

    @GetMapping
    public List<ReportTemplateDTO> getAllReportTemplates() {
        return reportTemplateService.getAllReportTemplates();
    }

    @GetMapping("/{id}")
    public ReportTemplateDTO getReportTemplateById(@PathVariable Long id) {
        return reportTemplateService.getReportTemplateById(id);
    }

    @PutMapping("/{id}")
    public ReportTemplateDTO updateReportTemplate(@PathVariable Long id, @RequestBody ReportTemplateDTO reportTemplate) {
        reportTemplate.setId(id);
        return reportTemplateService.updateReportTemplate(reportTemplate);
    }

    @PostMapping
    public ReportTemplateDTO createReportTemplate(@RequestBody ReportTemplateDTO reportTemplate) {
        return reportTemplateService.createReportTemplate(reportTemplate);
    }

    @DeleteMapping("/{id}")
    public void deleteReportTemplate(@PathVariable Long id) {
        reportTemplateService.deleteReportTemplate(ReportTemplateDTO.builder().id(id).build());
    }
}
