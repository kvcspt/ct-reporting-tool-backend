package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.dto.PatientDTO;
import hu.kvcspt.ctreportingtoolbackend.mapper.PatientMapper;
import hu.kvcspt.ctreportingtoolbackend.model.Patient;
import hu.kvcspt.ctreportingtoolbackend.model.repository.PatientRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class PatientService {
    private PatientRepository patientRepository;
    public List<PatientDTO> getAllPatients(){
        return patientRepository.findAll().stream().map(PatientMapper.INSTANCE::fromEntity).collect(Collectors.toList());
    }
    public Optional<PatientDTO> getPatientById(@NonNull String id){
        return patientRepository.findById(id).map(PatientMapper.INSTANCE::fromEntity);
    }

    public PatientDTO updatePatient(@NonNull PatientDTO patientDTO) {
        Patient patient = patientRepository
                .findById(patientDTO.getId())
                .orElse(PatientMapper.INSTANCE.toEntity(patientDTO));
        return PatientMapper.INSTANCE.fromEntity(patientRepository.save(patient));
    }

    public PatientDTO createPatient(@NonNull PatientDTO patientDTO){
        Patient patient = PatientMapper.INSTANCE.toEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patient);
        return PatientMapper.INSTANCE.fromEntity(savedPatient);
    }

    public void deletePatient(@NonNull String id){
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            log.debug("Patient is deleted successfully");
        } else {
            throw new NoSuchElementException("Patient with ID " + id + " not found.");
        }
    }
}
