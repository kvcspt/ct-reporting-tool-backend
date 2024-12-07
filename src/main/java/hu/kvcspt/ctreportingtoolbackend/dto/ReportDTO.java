package hu.kvcspt.ctreportingtoolbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {
    private UUID id;
    private String title;
    private LocalDateTime createdDate;
    private PatientDTO patient;
    private UserDTO createdBy;
    private ReportTemplateDTO template;
    private Map<String, String> sections = new HashMap<>();
    private ScanDTO scan;
    private List<LesionDTO> lesions;
}
