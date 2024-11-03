package hu.kvcspt.ctreportingtoolbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanDTO {
    private Long id;
    private String modality;
    private LocalDateTime scanDate;
    private String description;
    private String bodyPart;
    private String patientId;
    private Long reportId;
}
