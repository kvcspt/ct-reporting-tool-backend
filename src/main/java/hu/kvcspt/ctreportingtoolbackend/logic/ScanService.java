package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.dto.PatientDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.enums.Gender;
import hu.kvcspt.ctreportingtoolbackend.util.DicomUtils;
import hu.kvcspt.ctreportingtoolbackend.util.GeneralUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service
@AllArgsConstructor
@Log4j2
public class ScanService {
    public ScanDTO processDicomFile(MultipartFile file) throws IOException {
        Attributes dicomObject = DicomUtils.parseDicom(file.getInputStream());
        if (dicomObject == null) throw new IOException("Invalid DICOM data");

        String description = dicomObject.getString(Tag.StudyDescription);
        String modality = dicomObject.getString(Tag.Modality);
        Date scanDate = dicomObject.getDate(Tag.StudyDate);
        Date patientDateOfBirth = dicomObject.getDate(Tag.PatientBirthDate);
        String practicioner = dicomObject.getString(Tag.ReferringPhysicianName);
        String resultsInterpreter = dicomObject.getString(Tag.InterpretationAuthor);
        String patientName = dicomObject.getString(Tag.PatientName);
        String patientSex = dicomObject.getString(Tag.PatientSex);
        String patientId = dicomObject.getString(Tag.PatientID);
        String bodyPart = dicomObject.getString(Tag.BodyPartExamined);
        String mothersMaidenName = dicomObject.getString(Tag.PatientMotherBirthName);

        Gender gender = "M".equals(patientSex) ? Gender.MALE : "F".equals(patientSex) ? Gender.FEMALE : Gender.OTHER;

        PatientDTO patient = PatientDTO.builder()
                .id(patientId)
                .name(patientName)
                .gender(gender)
                .dateOfBirth(GeneralUtils.dateToLocalDate(patientDateOfBirth))
                .mothersMaidenName(mothersMaidenName)
                .build();

        return ScanDTO.builder()
                .scanDate(GeneralUtils.dateToLocalDate(scanDate))
                .description(description)
                .modality(modality)
                .patient(patient)
                .bodyPart(bodyPart)
                .performer(practicioner)
                .resultsInterpreter(resultsInterpreter)
                .build();
    }
}
