package hu.kvcspt.ctreportingtoolbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {
    private Long id;
    private String title;
    private LocalDateTime createdDate;
    private Long patientId;
    private Long createdById;
    @NotNull
    private Long templateId;
    private Map<String, String> sections = new HashMap<>();
    private List<Long> scanIds;

}
