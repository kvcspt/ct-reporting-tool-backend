package hu.kvcspt.ctreportingtoolbackend.model;

import hu.kvcspt.ctreportingtoolbackend.enums.Gender;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    private String id;
    private String name;
    private String placeOfBirth;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phoneNumber;
    private String address;
    private String mothersMaidenName;

    public org.hl7.fhir.r5.model.Patient toFhirPatient() {
        org.hl7.fhir.r5.model.Patient fhirPatient = new org.hl7.fhir.r5.model.Patient();
        fhirPatient.setId(id);
        setPhoneNumber(fhirPatient);
        setAddress(fhirPatient);
        setName(fhirPatient);
        setDateOfBirth(fhirPatient);
        setGender(fhirPatient);
        return fhirPatient;
    }

    private void setPhoneNumber(org.hl7.fhir.r5.model.Patient fhirPatient) {
        if (phoneNumber != null) {
            ContactPoint contact = new ContactPoint();
            contact.setSystem(ContactPoint.ContactPointSystem.PHONE);
            contact.setValue(phoneNumber);
            fhirPatient.addTelecom(contact);
        }
    }

    private void setAddress(org.hl7.fhir.r5.model.Patient fhirPatient) {
        if (address != null) {
            Address fhirAddress = new Address();
            fhirAddress.setText(address);
            fhirPatient.addAddress(fhirAddress);
        }
    }

    private void setName(org.hl7.fhir.r5.model.Patient fhirPatient) {
        if (name != null) {
            HumanName humanName = new HumanName();
            humanName.setText(name);
            fhirPatient.addName(humanName);
        }
    }

    private void setDateOfBirth(org.hl7.fhir.r5.model.Patient fhirPatient) {
        if (dateOfBirth != null) {
            fhirPatient.setBirthDate(Date.from(dateOfBirth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
    }

    private void setGender(org.hl7.fhir.r5.model.Patient fhirPatient) {
        if (gender != null) {
            fhirPatient.setGender(convertGenderToFhir(gender));
        }
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
