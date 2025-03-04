package hu.kvcspt.ctreportingtoolbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class BodyTemplateElementDTO {
    @NotNull
    private String label;
    @NotNull
    private String name;
    @NotNull
    private String type;
    private boolean duplicate;
    private List<String> options;
}
