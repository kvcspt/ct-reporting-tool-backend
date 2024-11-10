package hu.kvcspt.ctreportingtoolbackend.logic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import hu.kvcspt.ctreportingtoolbackend.dto.ReportDTO;
import hu.kvcspt.ctreportingtoolbackend.mapper.ReportMapper;
import hu.kvcspt.ctreportingtoolbackend.model.Report;
import hu.kvcspt.ctreportingtoolbackend.model.repository.ReportRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.hl7.fhir.r5.model.DiagnosticReport;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class ReportService {
    private ReportRepository reportRepository;
    public List<ReportDTO> getAllReports(){
        return reportRepository.findAll().stream().map(ReportMapper.INSTANCE::fromEntity).collect(Collectors.toList());

    }
    public ReportDTO getReportById(@NonNull Long id){
        return reportRepository.findById(id).map(ReportMapper.INSTANCE::fromEntity).orElseThrow(() -> new IllegalArgumentException("Report with" + id +" ID does not exist!"));

    }

    public ReportDTO updateReport(@NonNull ReportDTO reportDTO) {
        Report report = reportRepository
                .findById(reportDTO.getId())
                .orElse(ReportMapper.INSTANCE.toEntity(reportDTO));
        return ReportMapper.INSTANCE.fromEntity(reportRepository.save(report));
    }

    public ReportDTO createReport(@NonNull ReportDTO reportDTO){
        Report report = ReportMapper.INSTANCE.toEntity(reportDTO);
        Report savedReport = reportRepository.save(report);
        return ReportMapper.INSTANCE.fromEntity(savedReport);
    }

    public void deleteReport(@NonNull Long id){
        if (reportRepository.existsById(id)) {
            reportRepository.deleteById(id);
            log.debug("Report is deleted successfully");
        } else {
            throw new NoSuchElementException("Report with ID " + id + " not found.");
        }
    }
    public String generateDiagnosticReport(Long reportId) {
        Report report = ReportMapper.INSTANCE.toEntity(getReportById(reportId));
        DiagnosticReport diagnosticReport = report.toFhirDiagnosticReport();
        FhirContext ctxR5 = FhirContext.forR5();
        IParser jsonParser = ctxR5.newJsonParser();
        jsonParser.setPrettyPrint(true);

        return jsonParser.encodeResourceToString(diagnosticReport);
    }
}
