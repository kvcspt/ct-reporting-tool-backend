package hu.kvcspt.ctreportingtoolbackend.logic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import hu.kvcspt.ctreportingtoolbackend.dto.PatientDTO;
import hu.kvcspt.ctreportingtoolbackend.mapper.PatientMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hl7.fhir.r5.model.Patient;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class PatientService {
    public String generateFhirPatient(PatientDTO patientDTO){
        hu.kvcspt.ctreportingtoolbackend.model.Patient patient = PatientMapper.INSTANCE.toEntity(patientDTO);
        Patient fhirPatient = patient.toFhirPatient();
        FhirContext ctxR5 = FhirContext.forR5();
        IParser jsonParser = ctxR5.newJsonParser();
        jsonParser.setPrettyPrint(true);

        return jsonParser.encodeResourceToString(fhirPatient);
    }
}
