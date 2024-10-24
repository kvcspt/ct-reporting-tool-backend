package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.dto.ReportTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.model.Report;
import hu.kvcspt.ctreportingtoolbackend.model.ReportTemplate;
import hu.kvcspt.ctreportingtoolbackend.model.repository.ReportTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Log4j2
@Service
@AllArgsConstructor
public class ReportTemplateService {
    private ReportTemplateRepository reportTemplateRepository;
    public List<ReportTemplateDTO> getAllReportTemplates(){
        List<ReportTemplate> reportTemplates = reportTemplateRepository.findAll();
        return reportTemplates.stream().map(this::convertToDTO).toList();
    }
    public ReportTemplateDTO getReportTemplateById(Long id){
        ReportTemplate reportTemplate = reportTemplateRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Patient ID does not exist!"));
        return convertToDTO(reportTemplate);
    }
    public ReportTemplateDTO updateReportTemplate(ReportTemplateDTO reportTemplateDTO){
        if (reportTemplateRepository.existsById(reportTemplateDTO.getId())) {
            ReportTemplate report = convertToEntity(reportTemplateDTO);
            return convertToDTO(reportTemplateRepository.save(report));
        }
        throw new IllegalArgumentException("ReportTemplate not found!");
    }

    public ReportTemplateDTO createReportTemplate(ReportTemplateDTO reportTemplateDTO){
        ReportTemplate report = convertToEntity(reportTemplateDTO);
        return convertToDTO(reportTemplateRepository.save(report));
    }

    public void deleteReportTemplate(ReportTemplateDTO reportTemplateDTO){
        reportTemplateRepository.delete(convertToEntity(reportTemplateDTO));
        log.debug("ReportTemplate is deleted successfully");
    }

    private ReportTemplateDTO convertToDTO(ReportTemplate reportTemplate) {
        return ReportTemplateDTO.builder()
                .id(reportTemplate.getId())
                .name(reportTemplate.getName())
                .sections(reportTemplate.getSections())
                .reportIds(reportTemplate.getReports().stream()
                        .map(Report::getId)
                        .toList())
                .build();
    }

    private ReportTemplate convertToEntity(ReportTemplateDTO reportTemplateDTO) {
        return ReportTemplate.builder()
                .id(reportTemplateDTO.getId())
                .name(reportTemplateDTO.getName())
                .sections(reportTemplateDTO.getSections())
                .reports(new ArrayList<>())
                .build();
    }
}
