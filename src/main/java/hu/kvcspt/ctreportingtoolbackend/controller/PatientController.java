package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.PatientDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/patients")
@AllArgsConstructor
public final class PatientController {
    private final PatientService patientService;

    @GetMapping("/generate-patient")
    public String generateFhirPatient(@RequestBody PatientDTO patientDTO) {
        return patientService.generateFhirPatient(patientDTO);
    }
}
