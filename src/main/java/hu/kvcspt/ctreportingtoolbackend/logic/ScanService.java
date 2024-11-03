package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.model.Patient;
import hu.kvcspt.ctreportingtoolbackend.model.Report;
import hu.kvcspt.ctreportingtoolbackend.model.Scan;
import hu.kvcspt.ctreportingtoolbackend.model.repository.ScanRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
@Log4j2
public class ScanService {
    private ScanRepository scanRepository;
    private PatientService patientService;
    private ReportService reportService;
    public List<ScanDTO> getAllScans(){
        List<Scan> scans = scanRepository.findAll();
        return scans.stream().map(this::convertToDTO).toList();
    }
    public ScanDTO getScanById(Long id){
        Scan scan = scanRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Patient ID does not exist!"));
        return convertToDTO(scan);
    }
    public ScanDTO updateScan(ScanDTO scanDTO){
        if (scanRepository.existsById(scanDTO.getId())) {
            Scan report = convertToEntity(scanDTO);
            return convertToDTO(scanRepository.save(report));
        }
        throw new IllegalArgumentException("Scan not found!");
    }

    public ScanDTO createScan(ScanDTO scanDTO){
        Scan report = convertToEntity(scanDTO);
        return convertToDTO(scanRepository.save(report));
    }

    public void deleteScan(ScanDTO scanDTO){
        scanRepository.delete(convertToEntity(scanDTO));
        log.debug("Scan is deleted successfully");
    }
    private ScanDTO convertToDTO(Scan scan) {
        if (scan == null) return null;

        return ScanDTO.builder()
                .id(scan.getId())
                .modality(scan.getModality())
                .scanDate(scan.getScanDate())
                .description(scan.getDescription())
                .bodyPart(scan.getBodyPart())
                .patientId(scan.getPatient() != null ? scan.getPatient().getId() : null)
                .reportId(scan.getReport() != null ? scan.getReport().getId() : null)
                .build();
    }

    private Scan convertToEntity(ScanDTO scanDTO) {
        if (scanDTO == null) return null;

        Scan scan = new Scan();
        scan.setId(scanDTO.getId());
        scan.setModality(scanDTO.getModality());
        scan.setScanDate(scanDTO.getScanDate());
        scan.setDescription(scanDTO.getDescription());
        scan.setBodyPart(scanDTO.getBodyPart());
        if(scanDTO.getReportId() != null){
            Report report = reportService.getReportById(scanDTO.getReportId());
            scan.setReport(report);
        }
        if(scanDTO.getPatientId() != null){
            Patient patient = patientService.getPatientById(scanDTO.getPatientId());
            scan.setPatient(patient);
        }
        return scan;
    }
}
