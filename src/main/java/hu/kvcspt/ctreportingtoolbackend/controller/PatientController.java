package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.PatientDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
