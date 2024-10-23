package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.logic.SectionService;
import hu.kvcspt.ctreportingtoolbackend.model.Section;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@AllArgsConstructor
public final class SectionController {
    private final SectionService sectionService;

    @GetMapping
    public List<Section> getAllSections() {
        return sectionService.getAllSections();
    }

    @GetMapping("/{id}")
    public Section getSectionById(@PathVariable Long id) {
        return sectionService.getSectionById(id);
    }

    @PutMapping("/{id}")
    public Section updateSection(@PathVariable Long id, @RequestBody Section section) {
        section.setId(id);
        return sectionService.updateSection(section);
    }

    @PostMapping
    public Section addSection(@RequestBody Section section) {
        return sectionService.createSection(section);
    }

    @DeleteMapping("/{id}")
    public void deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(Section.builder().id(id).build());
    }
}
