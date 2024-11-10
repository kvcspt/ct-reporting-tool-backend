package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.mapper.ScanMapper;
import hu.kvcspt.ctreportingtoolbackend.model.Scan;
import hu.kvcspt.ctreportingtoolbackend.model.repository.ScanRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class ScanService {
    private ScanRepository scanRepository;
    //private PatientService patientService;
    //private ReportService reportService;
    public List<ScanDTO> getAllScans(){
        return scanRepository.findAll().stream().map(ScanMapper.INSTANCE::fromEntity).collect(Collectors.toList());
    }
    public ScanDTO getScanById(@NonNull Long id){
        return scanRepository.findById(id).map(ScanMapper.INSTANCE::fromEntity).orElseThrow(() -> new IllegalArgumentException("Scan with" + id +" ID does not exist!"));
    }
    public ScanDTO updateScan(@NonNull ScanDTO scanDTO){
        Scan scan = scanRepository
                .findById(scanDTO.getId())
                .orElse(ScanMapper.INSTANCE.toEntity(scanDTO));
        return ScanMapper.INSTANCE.fromEntity(scanRepository.save(scan));
    }

    public ScanDTO createScan(@NonNull ScanDTO scanDTO){
        Scan scan = ScanMapper.INSTANCE.toEntity(scanDTO);
        Scan savedUser = scanRepository.save(scan);
        return ScanMapper.INSTANCE.fromEntity(savedUser);
    }

    public void deleteScan(@NonNull Long id){
        if (scanRepository.existsById(id)) {
            scanRepository.deleteById(id);
            log.debug("Scan is deleted successfully");
        } else {
            throw new NoSuchElementException("Scan with ID " + id + " not found.");
        }
    }
}
