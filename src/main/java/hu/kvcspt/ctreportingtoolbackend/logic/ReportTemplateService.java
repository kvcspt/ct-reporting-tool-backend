package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.model.ReportTemplate;
import hu.kvcspt.ctreportingtoolbackend.model.repository.ReportTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
@Log4j2
@Service
@AllArgsConstructor
public class ReportTemplateService {
    private ReportTemplateRepository reportTemplateRepository;
    public List<ReportTemplate> getAllReportTemplates(){
        return reportTemplateRepository.findAll();
    }
    public ReportTemplate getReportTemplateById(Long id){
        return reportTemplateRepository.getReferenceById(id);
    }
    public ReportTemplate updateReportTemplate(ReportTemplate reportTemplate){
        if(reportTemplateRepository.existsById(reportTemplate.getId())){
            return reportTemplateRepository.save(reportTemplate);
        }
        throw new IllegalArgumentException("ReportTemplate not found!");
    }

    public ReportTemplate createReportTemplate(ReportTemplate reportTemplate){
        return reportTemplateRepository.save(reportTemplate);
    }

    public void deleteReportTemplate(ReportTemplate reportTemplate){
        reportTemplateRepository.delete(reportTemplate);
        log.debug("ReportTemplate is deleted successfully");
    }
}
