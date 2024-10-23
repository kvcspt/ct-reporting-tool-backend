package hu.kvcspt.ctreportingtoolbackend.model.repository;

import hu.kvcspt.ctreportingtoolbackend.model.Scan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanRepository extends JpaRepository<Scan, Long> {
}
