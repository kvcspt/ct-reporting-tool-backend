package hu.kvcspt.ctreportingtoolbackend.dto;

import hu.kvcspt.ctreportingtoolbackend.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class PatientDTO {
    private String id;
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phoneNumber;
    private String address;

}
