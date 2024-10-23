package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.model.Report;
import hu.kvcspt.ctreportingtoolbackend.model.repository.ReportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hl7.fhir.r5.model.DiagnosticReport;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class ReportService {
    private ReportRepository reportRepository;
    public List<Report> getAllReports(){
        return reportRepository.findAll();
    }
    public Report getReportById(Long id){
        return reportRepository.getReferenceById(id);
    }
    public Report updateReport(Report report){
        if(reportRepository.existsById(report.getId())){
            return reportRepository.save(report);
        }
        throw new IllegalArgumentException("Report not found!");
    }

    public Report createReport(Report report){
        return reportRepository.save(report);
    }

    public void deleteReport(Report report){
        reportRepository.delete(report);
        log.debug("Report is deleted successfully");
    }

    public DiagnosticReport generateDiagnosticReport(Long reportId) {
        Report report = getReportById(reportId);
        return report.toFhirDiagnosticReport();
    }
}
