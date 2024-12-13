package hu.kvcspt.ctreportingtoolbackend.model;

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
public class Report {
    private UUID id;
    private String title;
    private LocalDateTime createdDate;
    private Patient patient;
    private User createdBy;
    private Map<String, String> sections = new HashMap<>();
    private ReportTemplate template;
    private Scan scan;
    private List<Lesion> lesions;
}
