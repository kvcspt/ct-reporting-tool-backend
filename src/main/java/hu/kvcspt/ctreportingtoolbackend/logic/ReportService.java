package hu.kvcspt.ctreportingtoolbackend.logic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import hu.kvcspt.ctreportingtoolbackend.dto.ReportDTO;
import hu.kvcspt.ctreportingtoolbackend.model.Patient;
import hu.kvcspt.ctreportingtoolbackend.model.Report;
import hu.kvcspt.ctreportingtoolbackend.model.Scan;
import hu.kvcspt.ctreportingtoolbackend.model.repository.ReportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hl7.fhir.r5.model.DiagnosticReport;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class ReportService {
    private ReportRepository reportRepository;
    private PatientService patientService;
    public List<ReportDTO> getAllReports(){
        List<Report> patients = reportRepository.findAll();
        return patients.stream().map(this::convertToDTO).toList();
    }
    public ReportDTO getReportDTOById(Long id){
        Report report = reportRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Report ID does not exist!"));
        return convertToDTO(report);
    }

    public Report getReportById(Long id){
        return reportRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Report ID does not exist!"));
    }
    public ReportDTO updateReport(ReportDTO reportDTO) {
        if (reportRepository.existsById(reportDTO.getId())) {
            Report report = convertToEntity(reportDTO);
            return convertToDTO(reportRepository.save(report));
        }
        throw new IllegalArgumentException("Report not found!");
    }

    public ReportDTO createReport(ReportDTO reportDTO){
        Report report = convertToEntity(reportDTO);
        return convertToDTO(reportRepository.save(report));
    }

    public void deleteReport(ReportDTO reportDTO){
        reportRepository.delete(convertToEntity(reportDTO));
        log.debug("Report is deleted successfully");
    }
    public String generateDiagnosticReport(Long reportId) {
        Report report = convertToEntity(getReportDTOById(reportId));
        DiagnosticReport diagnosticReport = report.toFhirDiagnosticReport();
        FhirContext ctxR5 = FhirContext.forR5();
        IParser jsonParser = ctxR5.newJsonParser();
        jsonParser.setPrettyPrint(true);

        return jsonParser.encodeResourceToString(diagnosticReport);
    }

    private ReportDTO convertToDTO(Report report) {
        if (report == null) return null;

        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(report.getId());
        reportDTO.setTitle(report.getTitle());
        reportDTO.setCreatedDate(report.getCreatedDate());
        reportDTO.setPatientId(report.getPatient().getId());
        reportDTO.setCreatedById(report.getCreatedBy().getId());
        reportDTO.setTemplateId(report.getTemplate().getId());
        reportDTO.setSections(report.getSections());
        reportDTO.setScanIds(report.getScans().stream().map(Scan::getId).collect(Collectors.toList()));

        return reportDTO;
    }

    private Report convertToEntity(ReportDTO reportDTO) {
        if (reportDTO == null) return null;

        Report report = new Report();
        report.setId(reportDTO.getId());
        report.setTitle(reportDTO.getTitle());
        report.setCreatedDate(reportDTO.getCreatedDate());
        if (reportDTO.getPatientId() != null) {
            Patient patient = patientService.convertToEntity(patientService.getPatientDTOById(reportDTO.getPatientId()));
            report.setPatient(patient);
        }
        report.setSections(reportDTO.getSections());
        return report;
    }
}
