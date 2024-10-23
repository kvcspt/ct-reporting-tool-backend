package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.model.Patient;
import hu.kvcspt.ctreportingtoolbackend.model.repository.PatientRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
@Log4j2
public class PatientService {
    private PatientRepository patientRepository;
    public List<Patient> getAllPatients(){
        return patientRepository.findAll();
    }
    public Patient getPatientById(Long id){
        return patientRepository.getReferenceById(id);
    }
    public Patient updatePatient(Patient patient){
        if(patientRepository.existsById(patient.getId())){
            return patientRepository.save(patient);
        }
        throw new IllegalArgumentException("Patient not found!");
    }

    public Patient createPatient(Patient patient){
        return patientRepository.save(patient);
    }

    public void deletePatient(Patient patient){
        patientRepository.delete(patient);
        log.debug("Patient is deleted successfully");
    }
}
