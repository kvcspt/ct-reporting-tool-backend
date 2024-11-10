package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.enums.Gender;
import hu.kvcspt.ctreportingtoolbackend.logic.ScanService;
import hu.kvcspt.ctreportingtoolbackend.model.Patient;
import hu.kvcspt.ctreportingtoolbackend.model.Scan;
import hu.kvcspt.ctreportingtoolbackend.util.DicomUtils;
import hu.kvcspt.ctreportingtoolbackend.util.GeneralUtils;
import lombok.AllArgsConstructor;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/scans")
@AllArgsConstructor
public final class ScanController {

    private final ScanService scanService;

    @GetMapping
    public List<ScanDTO> getAllScans() {
        return scanService.getAllScans();
    }

    @GetMapping("/{id}")
    public ScanDTO getScanById(@PathVariable Long id) {
        return scanService.getScanById(id);
    }

    @PutMapping("/{id}")
    public ScanDTO updateScan(@PathVariable Long id, @RequestBody ScanDTO scan) {
        scan.setId(id);
        return scanService.updateScan(scan);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDicomFiles(@RequestParam("files") MultipartFile[] files) {
        List<String> errors = new ArrayList<>();
        List<Scan> createdScans = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Attributes dicomObject = DicomUtils.parseDicom(file.getInputStream());
                if (dicomObject == null) throw new IOException("Invalid DICOM data");

                String description = dicomObject.getString(Tag.StudyDescription);
                String modality = dicomObject.getString(Tag.Modality);
                Date scanDate = dicomObject.getDate(Tag.StudyDate);
                Date patientDateOfBirth = dicomObject.getDate(Tag.PatientBirthDate);
                String patientName = dicomObject.getString(Tag.PatientName);
                String patientSex = dicomObject.getString(Tag.PatientSex);
                String patientId = dicomObject.getString(Tag.PatientID);
                String bodyPart = dicomObject.getString(Tag.BodyPartExamined);
                Gender gender = "M".equals(patientSex) ? Gender.MALE : "F".equals(patientSex) ? Gender.FEMALE : Gender.OTHER;
                Patient patient = Patient.builder()
                        .id(patientId)
                        .name(patientName)
                        .gender(gender)
                        .dateOfBirth(GeneralUtils.dateToLocalDate(patientDateOfBirth))
                        .build();
                Scan scan = Scan.builder()
                        .scanDate(GeneralUtils.dateToLocalDate(scanDate))
                        .description(description)
                        .modality(modality)
                        .patient(patient)
                        .bodyPart(bodyPart)
                        .build();
                createdScans.add(scanService.createScan(scan));
            } catch (IOException e) {
                errors.add("Error processing file: " + file.getOriginalFilename() + " - " + e.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(Map.of("createdScans", createdScans, "errors", errors));
        }
        return ResponseEntity.ok(createdScans);
    }

    @PostMapping
    public ScanDTO addScan(@RequestBody ScanDTO scan) {
        return scanService.createScan(scan);
    }

    @DeleteMapping("/{id}")
    public void deleteScan(@PathVariable Long id) {
        scanService.deleteScan(id);
    }
}