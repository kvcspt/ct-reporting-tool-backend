package hu.kvcspt.ctreportingtoolbackend.model.repository;

import hu.kvcspt.ctreportingtoolbackend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
