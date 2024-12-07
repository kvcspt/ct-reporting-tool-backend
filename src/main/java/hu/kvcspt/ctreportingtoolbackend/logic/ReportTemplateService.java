package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.dto.ReportTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.mapper.ReportTemplateMapper;
import hu.kvcspt.ctreportingtoolbackend.model.ReportTemplate;
import hu.kvcspt.ctreportingtoolbackend.model.repository.ReportTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class ReportTemplateService {
    private ReportTemplateRepository reportTemplateRepository;
    public List<ReportTemplateDTO> getAllReportTemplates(){
        return reportTemplateRepository.findAll().stream().map(ReportTemplateMapper.INSTANCE::fromEntity).collect(Collectors.toList());

    }
    public ReportTemplateDTO getReportTemplateById(@NonNull Long id){
        return reportTemplateRepository.findById(id).map(ReportTemplateMapper.INSTANCE::fromEntity).orElseThrow(() -> new IllegalArgumentException("Template with" + id +" ID does not exist!"));
    }

    public ReportTemplateDTO updateReportTemplate(@NonNull ReportTemplateDTO reportTemplateDTO){
        ReportTemplate existingReportTemplate = reportTemplateRepository
                .findById(reportTemplateDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("ReportTemplate not found!"));

        ReportTemplate newTemplate = ReportTemplateMapper.INSTANCE.toEntity(reportTemplateDTO);

        existingReportTemplate.setName(newTemplate.getName());
        existingReportTemplate.setSections(newTemplate.getSections());

        reportTemplateRepository.save(existingReportTemplate);
        return ReportTemplateMapper.INSTANCE.fromEntity(existingReportTemplate);
    }

    public ReportTemplateDTO createReportTemplate(@NonNull ReportTemplateDTO reportTemplateDTO){
        ReportTemplate reportTemplate = ReportTemplateMapper.INSTANCE.toEntity(reportTemplateDTO);
        ReportTemplate savedTemplate = reportTemplateRepository.save(reportTemplate);
        return ReportTemplateMapper.INSTANCE.fromEntity(savedTemplate);
    }

    public void deleteReportTemplate(@NonNull Long id){
        if (reportTemplateRepository.existsById(id)) {
            reportTemplateRepository.deleteById(id);
            log.debug("ReportTemplate is deleted successfully");
        } else {
            log.debug("ReportTemplate with ID " + id + " not found.");
        }
    }
}
