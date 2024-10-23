package hu.kvcspt.ctreportingtoolbackend.logic;

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
    public List<Scan> getAllScans(){
        return scanRepository.findAll();
    }
    public Scan getScanById(Long id){
        return scanRepository.getReferenceById(id);
    }
    public Scan updateScan(Scan scan){
        if(scanRepository.existsById(scan.getId())){
            return scanRepository.save(scan);
        }
        throw new IllegalArgumentException("Scan not found!");
    }

    public Scan createScan(Scan scan){
        return scanRepository.save(scan);
    }

    public void deleteScan(Scan scan){
        scanRepository.delete(scan);
        log.debug("Scan is deleted successfully");
    }
}
