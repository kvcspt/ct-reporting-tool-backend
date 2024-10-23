package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.model.Section;
import hu.kvcspt.ctreportingtoolbackend.model.repository.SectionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Log4j2
@AllArgsConstructor
public class SectionService {
    private SectionRepository sectionRepository;
    public List<Section> getAllSections(){
        return sectionRepository.findAll();
    }
    public Section getSectionById(Long id){
        return sectionRepository.getReferenceById(id);
    }
    public Section updateSection(Section section){
        if(sectionRepository.existsById(section.getId())){
            return sectionRepository.save(section);
        }
        throw new IllegalArgumentException("Section not found!");
    }

    public Section createSection(Section section){
        return sectionRepository.save(section);
    }

    public void deleteSection(Section section){
        sectionRepository.delete(section);
        log.debug("Section is deleted successfully");
    }
}
