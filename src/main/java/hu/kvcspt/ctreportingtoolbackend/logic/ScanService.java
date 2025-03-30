package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.dto.PatientDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.enums.Gender;
import hu.kvcspt.ctreportingtoolbackend.util.DicomUtils;
import hu.kvcspt.ctreportingtoolbackend.util.GeneralUtils;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Log4j2
@Getter
public class ScanService {
    public static final String INSTANCES_URL = "/instances";
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
        List<String> studyIds = fetchStudyIds(restTemplate);

        List<ScanDTO> scans = new ArrayList<>();
        assert studyIds != null;
        for (String studyId : studyIds) {
            Map<String, Object> studyDetails = fetchStudyDetails(studyId, restTemplate);

            List<Map<String, Object>> seriesList = fetchSeriesList(studyId, restTemplate);

            if (seriesList != null && !seriesList.isEmpty()) {
                for (Map<String, Object> series : seriesList) {
                    assert studyDetails != null;
                    ScanDTO scan = getScanDTOFromStudyDetails(series, studyDetails);

                    scans.add(scan);
                }
            }
        }
        return scans;
    }

    public ScanDTO processDicomFile(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        ResponseEntity<String> response = restTemplate.exchange(orthancServerUrl + INSTANCES_URL, HttpMethod.POST, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IOException("Failed to upload DICOM file to Orthanc server");
        }
        return getScanDTOFromMultipartFile(file);
    }

    private ScanDTO getScanDTOFromStudyDetails(Map<String, Object> series, Map<String, Object> studyDetails) {
        Map<String, String> mainDicomTags = (Map<String, String>) studyDetails.get(MAIN_DICOM_TAGS);
        Map<String, String> seriesMainDicom = (Map<String, String>) series.get(MAIN_DICOM_TAGS);
        return ScanDTO.builder()
                .id(UUID.randomUUID())
                .modality(seriesMainDicom.get("Modality"))
                .scanDate(GeneralUtils.parseScanDateToLocalDateTime(mainDicomTags.get("StudyDate"), mainDicomTags.get("StudyTime")))
                .description(mainDicomTags.get("StudyDescription"))
                .bodyPart(seriesMainDicom.get("BodyPartExamined"))
                .patient(getPatientDetails(studyDetails))
                .performer(mainDicomTags.get("ReferringPhysicianName"))
                .resultsInterpreter((String) studyDetails.get("PerformingPhysicianName"))
                .studyUid(mainDicomTags.get("StudyInstanceUID"))
                .seriesUid(seriesMainDicom.get("SeriesInstanceUID"))
                .build();
    }

    @Nullable
    private List<Map<String, Object>> fetchSeriesList(String studyId, RestTemplate restTemplate) {
        String seriesUrl = orthancServerUrl + STUDIES_URL + "/" + studyId + SERIES_URL;
        return restTemplate.getForObject(seriesUrl, List.class);
    }

    @Nullable
    private Map<String, Object> fetchStudyDetails(String studyId, RestTemplate restTemplate) {
        String studyUrl = orthancServerUrl + STUDIES_URL + "/" + studyId;
        return restTemplate.getForObject(studyUrl, Map.class);
    }

    @Nullable
    private List<String> fetchStudyIds(RestTemplate restTemplate) {
        String studiesUrl = orthancServerUrl + STUDIES_URL;
        return restTemplate.getForObject(studiesUrl, List.class);
    }

    private PatientDTO getPatientDetails(Map<String, Object> studyDetails) {
        Map<String, String> patientMainDicomTags = (Map<String, String>) studyDetails.get(PATIENT + MAIN_DICOM_TAGS);

        return PatientDTO.builder()
                .name(patientMainDicomTags.get("PatientName"))
                .dateOfBirth(GeneralUtils.parseScanDateToLocalDate((patientMainDicomTags.get("PatientBirthDate"))))
                .id(patientMainDicomTags.get("PatientID"))
                .gender(GeneralUtils.parseGender(patientMainDicomTags.get("PatientSex")))
                .build();
    }

    private ScanDTO getScanDTOFromMultipartFile(MultipartFile file) throws IOException {
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

        Gender gender = GeneralUtils.parseGender(patientSex);

        PatientDTO patient = PatientDTO.builder()
                .id(patientId)
                .name(patientName)
                .gender(gender)
                .dateOfBirth(GeneralUtils.dateToLocalDate(patientDateOfBirth))
                .mothersMaidenName(mothersMaidenName)
                .build();

        return ScanDTO.builder()
                .id(UUID.randomUUID())
                .scanDate(GeneralUtils.dateToLocalDateTime(scanDate))
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
