package hu.kvcspt.ctreportingtoolbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanDTO {
    private Long id;
    private String modality;
    private LocalDate scanDate;
    private String description;
    private String bodyPart;
    private String patientId;
    private Long reportId;
}
