package hu.kvcspt.ctreportingtoolbackend.util;

import hu.kvcspt.ctreportingtoolbackend.enums.Gender;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeneralUtils {
    public static LocalDate dateToLocalDate(Date date){
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime dateToLocalDateTime(Date date){
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime parseScanDateToLocalDateTime(String studyDate, String studyTime) {
        if (studyDate == null || studyDate.isEmpty()) {
            return null;
        }

        try {
            String dateTimeString = studyDate + studyTime.substring(0, 6); // remove the fractional seconds for parsing
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + studyDate, e);
        }
    }

    public static LocalDate parseScanDateToLocalDate(String studyDate) {
        if (studyDate == null || studyDate.isEmpty()) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(studyDate, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + studyDate, e);
        }
    }

    public static Gender parseGender(String patientSex) {
        if ("M".equals(patientSex)) {
            return Gender.MALE;
        } else if ("F".equals(patientSex)) {
            return Gender.FEMALE;
        } else {
            return Gender.OTHER;
        }
    }

    private static String camelToTitleCase(String camelCase) {
        return StringUtils.capitalize(StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(camelCase),
                StringUtils.SPACE
        ));
    }

    public static Map<String, Object> transformKeys(Map<String, Object> data) {
        Map<String, Object> transformedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String formattedKey = camelToTitleCase(entry.getKey());
            transformedMap.put(formattedKey, entry.getValue());
        }
        return transformedMap;
    }
}
