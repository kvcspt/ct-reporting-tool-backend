package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.logic.SectionTemplateService;
import hu.kvcspt.ctreportingtoolbackend.model.SectionTemplate;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/section-templates")
@AllArgsConstructor
public final class SectionTemplateController {
    private final SectionTemplateService sectionTemplateService;

    @GetMapping
    public List<SectionTemplate> getAllSectionTemplates() {
        return sectionTemplateService.getAllSectionTemplates();
    }

    @GetMapping("/{id}")
    public SectionTemplate getSectionTemplateById(@PathVariable Long id) {
        return sectionTemplateService.getSectionTemplateById(id);
    }

    @PutMapping("/{id}")
    public SectionTemplate updateSectionTemplate(@PathVariable Long id, @RequestBody SectionTemplate sectionTemplate) {
        sectionTemplate.setId(id);
        return sectionTemplateService.updateSectionTemplate(sectionTemplate);
    }

    @PostMapping
    public SectionTemplate createSectionTemplate(@RequestBody SectionTemplate sectionTemplate) {
        return sectionTemplateService.createSectionTemplate(sectionTemplate);
    }

    @DeleteMapping("/{id}")
    public void deleteSectionTemplate(@PathVariable Long id) {
        sectionTemplateService.deleteSectionTemplate(SectionTemplate.builder().id(id).build());
    }
}
