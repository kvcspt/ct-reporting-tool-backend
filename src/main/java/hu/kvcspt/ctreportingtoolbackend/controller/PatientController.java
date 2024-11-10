package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.PatientDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.PatientService;
import hu.kvcspt.ctreportingtoolbackend.mapper.PatientMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/patients")
@AllArgsConstructor
public final class PatientController {
    private final PatientService patientService;

    @GetMapping
    public List<PatientDTO> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public PatientDTO getPatientById(@PathVariable String id) {
        return patientService.getPatientById(id).orElseThrow(() -> new IllegalArgumentException("Patient with" + id +" ID does not exist!"));
    }

    @PutMapping("/{id}")
    public PatientDTO updatePatient(@PathVariable String id, @RequestBody PatientDTO patient) {
        patient.setId(id);
        return patientService.updatePatient(patient);
    }

    @PostMapping
    public PatientDTO createPatient(@RequestBody PatientDTO patient) {
        return patientService.createPatient(patient);
    }

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
    }

}
