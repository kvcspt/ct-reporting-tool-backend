package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.dto.PatientDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.enums.Gender;
import hu.kvcspt.ctreportingtoolbackend.util.DicomUtils;
import hu.kvcspt.ctreportingtoolbackend.util.GeneralUtils;
import lombok.extern.log4j.Log4j2;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
@Log4j2
public class ScanService {
    private static final String INSTANCES_URL = "/instances";
    @Value("${orthanc.server.url}")
    private String orthancServerUrl;
    private final RestTemplate restTemplate;

    public ScanService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ScanDTO processDicomFile(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        ResponseEntity<String> response = restTemplate.exchange(orthancServerUrl + INSTANCES_URL, HttpMethod.POST, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IOException("Failed to upload DICOM file to Orthanc server");
        }
        return getScanDto(file);
    }

    private ScanDTO getScanDto(MultipartFile file) throws IOException {
        Attributes dicomObject = DicomUtils.parseDicom(file.getInputStream());
        if (dicomObject == null) throw new IOException("Invalid DICOM data");

        String description = dicomObject.getString(Tag.StudyDescription);
        String modality = dicomObject.getString(Tag.Modality);
        Date scanDate = dicomObject.getDate(Tag.StudyDate);
        Date patientDateOfBirth = dicomObject.getDate(Tag.PatientBirthDate);
        String practitioner = dicomObject.getString(Tag.ReferringPhysicianName);
        String resultsInterpreter = dicomObject.getString(Tag.InterpretationAuthor);
        String patientName = dicomObject.getString(Tag.PatientName);
        String patientSex = dicomObject.getString(Tag.PatientSex);
        String patientId = dicomObject.getString(Tag.PatientID);
        String bodyPart = dicomObject.getString(Tag.BodyPartExamined);
        String mothersMaidenName = dicomObject.getString(Tag.PatientMotherBirthName);
        String seriesUid = dicomObject.getString(Tag.SeriesInstanceUID);
        String studyUid = dicomObject.getString(Tag.StudyInstanceUID);

        Gender gender = "M".equals(patientSex) ? Gender.MALE : "F".equals(patientSex) ? Gender.FEMALE : Gender.OTHER;

        PatientDTO patient = PatientDTO.builder()
                .id(patientId)
                .name(patientName)
                .gender(gender)
                .dateOfBirth(GeneralUtils.dateToLocalDate(patientDateOfBirth))
                .mothersMaidenName(mothersMaidenName)
                .build();

        return ScanDTO.builder()
                .id(UUID.randomUUID())
                .scanDate(GeneralUtils.dateToLocalDate(scanDate))
                .description(description)
                .modality(modality)
                .patient(patient)
                .bodyPart(bodyPart)
                .performer(practitioner)
                .resultsInterpreter(resultsInterpreter)
                .seriesUid(seriesUid)
                .studyUid(studyUid)
                .build();
    }
}
