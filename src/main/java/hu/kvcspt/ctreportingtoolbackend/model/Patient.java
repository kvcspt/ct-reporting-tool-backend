package hu.kvcspt.ctreportingtoolbackend.model;

import hu.kvcspt.ctreportingtoolbackend.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r5.model.Address;
import org.hl7.fhir.r5.model.ContactPoint;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.HumanName;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "patients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scan> scans;
    private String phoneNumber;
    private String address;
    public org.hl7.fhir.r5.model.Patient toFhirPatient() {
        org.hl7.fhir.r5.model.Patient fhirPatient = new org.hl7.fhir.r5.model.Patient();

        fhirPatient.setId(String.valueOf(id));

        if (phoneNumber != null) {
            ContactPoint contact = new ContactPoint();
            contact.setSystem(ContactPoint.ContactPointSystem.PHONE);
            contact.setValue(phoneNumber);
            fhirPatient.addTelecom(contact);
        }

        if (address != null) {
            Address address = new Address();
            address.setText(String.valueOf(address));
            fhirPatient.addAddress(address);
        }

        if (name != null) {
            HumanName humanName = new HumanName();
            humanName.setText(name);
            fhirPatient.addName(humanName);
        }

        if (dateOfBirth != null) {
            fhirPatient.setBirthDate(Date.from(dateOfBirth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        if (gender != null) {
            fhirPatient.setGender(convertGenderToFhir(gender));
        }

        return fhirPatient;
    }

    private Enumerations.AdministrativeGender convertGenderToFhir(Gender gender) {
        return switch (gender) {
            case MALE -> Enumerations.AdministrativeGender.MALE;
            case FEMALE -> Enumerations.AdministrativeGender.FEMALE;
            case OTHER -> Enumerations.AdministrativeGender.OTHER;
            default -> Enumerations.AdministrativeGender.UNKNOWN;
        };
    }
}
