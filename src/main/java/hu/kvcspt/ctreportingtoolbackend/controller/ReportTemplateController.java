package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.ReportTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.ReportTemplateService;
import hu.kvcspt.ctreportingtoolbackend.util.FieldExtractor;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/report-templates")
@AllArgsConstructor
public final class ReportTemplateController {
    private static final String MODEL_PACKAGE_NAME = "hu.kvcspt.ctreportingtoolbackend.model.";
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
        reportTemplateService.deleteReportTemplate(id);
    }

    @GetMapping("/fields")
    public List<String> getFields(@RequestParam(value = "className", required = false) String className) {
        if(className == null || className.isBlank()){
            return FieldExtractor.getFields();
        }
        try {
            Class<?> clazz = Class.forName(MODEL_PACKAGE_NAME + className);
            return FieldExtractor.getFields(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return List.of("Class not found");
        }
    }
}
