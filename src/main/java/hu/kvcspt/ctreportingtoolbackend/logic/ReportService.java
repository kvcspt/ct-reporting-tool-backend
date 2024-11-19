package hu.kvcspt.ctreportingtoolbackend.logic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import hu.kvcspt.ctreportingtoolbackend.dto.ReportDTO;
import hu.kvcspt.ctreportingtoolbackend.mapper.ReportMapper;
import hu.kvcspt.ctreportingtoolbackend.model.Report;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hl7.fhir.r5.model.DiagnosticReport;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class ReportService {
    public String generateDiagnosticReport(ReportDTO reportDTO) {
        Report report = ReportMapper.INSTANCE.toEntity(reportDTO);
        DiagnosticReport diagnosticReport = report.toFhirDiagnosticReport();
        FhirContext ctxR5 = FhirContext.forR5();
        IParser jsonParser = ctxR5.newJsonParser();
        jsonParser.setPrettyPrint(true);

        return jsonParser.encodeResourceToString(diagnosticReport);
    }
}
