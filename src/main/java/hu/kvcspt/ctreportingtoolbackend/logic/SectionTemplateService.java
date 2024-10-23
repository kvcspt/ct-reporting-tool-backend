package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.model.SectionTemplate;
import hu.kvcspt.ctreportingtoolbackend.model.repository.SectionTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class SectionTemplateService {
    private SectionTemplateRepository sectionTemplateRepository;
    public List<SectionTemplate> getAllSectionTemplates(){
        return sectionTemplateRepository.findAll();
    }
    public SectionTemplate getSectionTemplateById(Long id){
        return sectionTemplateRepository.getReferenceById(id);
    }
    public SectionTemplate updateSectionTemplate(SectionTemplate sectionTemplate){
        if(sectionTemplateRepository.existsById(sectionTemplate.getId())){
            return sectionTemplateRepository.save(sectionTemplate);
        }
        throw new IllegalArgumentException("SectionTemplate not found!");
    }

    public SectionTemplate createSectionTemplate(SectionTemplate sectionTemplate){
        return sectionTemplateRepository.save(sectionTemplate);
    }

    public void deleteSectionTemplate(SectionTemplate sectionTemplate){
        sectionTemplateRepository.delete(sectionTemplate);
        log.debug("SectionTemplate is deleted successfully");
    }
}
