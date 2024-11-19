package hu.kvcspt.ctreportingtoolbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesion {
    private Long id;
    private String location;
    private double diameterX;
    private double diameterY;
    private double diameterZ;
}
