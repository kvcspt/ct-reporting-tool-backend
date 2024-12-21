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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Log4j2
public class ScanService {
    private static final String INSTANCES_URL = "/instances";
    private static final String STUDIES_URL = "/studies";
    private static final String SERIES_URL = "/series";
    private static final String MAIN_DICOM_TAGS = "MainDicomTags";
    private static final String PATIENT = "Patient";

    @Value("${orthanc.server.url}")
    private String orthancServerUrl;
    private final RestTemplate restTemplate;

    public ScanService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ScanDTO> getScans() {
        RestTemplate restTemplate = new RestTemplate();
        String studiesUrl = orthancServerUrl + STUDIES_URL;
        List<String> studyIds = restTemplate.getForObject(studiesUrl, List.class);

        List<ScanDTO> scans = new ArrayList<>();
        assert studyIds != null;
        for (String studyId : studyIds) {
            String studyUrl = orthancServerUrl + STUDIES_URL + "/" + studyId;
            Map<String, Object> studyDetails = restTemplate.getForObject(studyUrl, Map.class);

            String seriesUrl = orthancServerUrl + STUDIES_URL + "/" + studyId + SERIES_URL;
            List<Map<String, Object>> seriesList = restTemplate.getForObject(seriesUrl, List.class);

            Map<String, String> mainDicomTags = (Map<String, String>) studyDetails.get(MAIN_DICOM_TAGS);

            if (seriesList != null && !seriesList.isEmpty()) {
                for (Map<String, Object> series : seriesList) {
                    Map<String, String> seriesMainDicom = (Map<String, String>) series.get(MAIN_DICOM_TAGS);
                    ScanDTO scan = ScanDTO.builder()
                            .id(UUID.randomUUID())
                            .modality(seriesMainDicom.get("Modality"))
                            .scanDate(parseScanDate(mainDicomTags.get("StudyDate")))
                            .description(mainDicomTags.get("StudyDescription"))
                            .bodyPart(seriesMainDicom.get("BodyPartExamined"))
                            .patient(getPatientDetails(studyDetails))
                            .performer(mainDicomTags.get("ReferringPhysicianName"))
                            .resultsInterpreter((String) studyDetails.get("PerformingPhysicianName"))
                            .studyUid(mainDicomTags.get("StudyInstanceUID"))
                            .seriesUid(seriesMainDicom.get("SeriesInstanceUID"))
                            .build();

                    scans.add(scan);
                }
            }
        }
        return scans;
    }

    private PatientDTO getPatientDetails(Map<String, Object> studyDetails) {
        Map<String, String> patientMainDicomTags = (Map<String, String>) studyDetails.get(PATIENT + MAIN_DICOM_TAGS);

        return PatientDTO.builder()
                .name(patientMainDicomTags.get("PatientName"))
                .dateOfBirth(parseScanDate((patientMainDicomTags.get("PatientBirthDate"))))
                .id(patientMainDicomTags.get("PatientID"))
                .gender("M".equals(patientMainDicomTags.get("PatientSex")) ? Gender.MALE : "F".equals(patientMainDicomTags.get("PatientSex")) ? Gender.FEMALE : Gender.OTHER)
                .build();
    }
    private LocalDate parseScanDate(String studyDate) {
        if (studyDate == null || studyDate.isEmpty()) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(studyDate, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + studyDate, e);
        }
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
