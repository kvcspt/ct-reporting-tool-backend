package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.dto.PatientDTO;
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
    public List<PatientDTO> getAllPatients(){
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(this::convertToDTO).toList();
    }
    public PatientDTO getPatientDTOById(String id){
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Patient does not exist!"));
        return convertToDTO(patient);
    }

    public Patient getPatientById(String id){
        return patientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Patient does not exist!"));
    }

    public PatientDTO updatePatient(PatientDTO patientDTO) {
        if (patientRepository.existsById(patientDTO.getId())) {
            Patient patient = convertToEntity(patientDTO);
            return convertToDTO(patientRepository.save(patient));
        }
        throw new IllegalArgumentException("Patient not found!");
    }

    public PatientDTO createPatient(PatientDTO patientDTO){
        Patient patient = convertToEntity(patientDTO);
        return convertToDTO(patientRepository.save(patient));
    }

    public void deletePatient(PatientDTO patientDTO){
        patientRepository.delete(convertToEntity(patientDTO));
        log.debug("Patient is deleted successfully");
    }
    public Patient convertToEntity(PatientDTO patientDTO) {
        return Patient.builder()
                .id(patientDTO.getId())
                .name(patientDTO.getName())
                .dateOfBirth(patientDTO.getDateOfBirth())
                .gender(patientDTO.getGender())
                .phoneNumber(patientDTO.getPhoneNumber())
                .address(patientDTO.getAddress())
                .build();
    }

    public PatientDTO convertToDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .name(patient.getName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .phoneNumber(patient.getPhoneNumber())
                .address(patient.getAddress())
                .build();
    }
}
