package hu.kvcspt.ctreportingtoolbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class BodyReportDTO {
    public String name;
    @NotNull
    public String label;
    @NotNull
    public String type;
    public String value;
}
