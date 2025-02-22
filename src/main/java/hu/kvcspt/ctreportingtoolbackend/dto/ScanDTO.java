package hu.kvcspt.ctreportingtoolbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanDTO {
    private UUID id;
    private String modality;
    private LocalDateTime scanDate;
    private String description;
    private String bodyPart;
    private PatientDTO patient;
    private String performer;
    private String resultsInterpreter;

    // WADO UID fields for Cornerstone (DICOM object retrieval)
    private String studyUid;
    private String seriesUid;
}
