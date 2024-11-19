package hu.kvcspt.ctreportingtoolbackend.util;

import hu.kvcspt.ctreportingtoolbackend.model.Lesion;
import hu.kvcspt.ctreportingtoolbackend.model.Patient;
import hu.kvcspt.ctreportingtoolbackend.model.Report;
import hu.kvcspt.ctreportingtoolbackend.model.Scan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldExtractor {
    private static final Class<?>[] modelClasses = {Patient.class, Scan.class, Lesion.class};

    public static List<String> getFields(Class<?> clazz) {
        List<String> fieldNames = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            fieldNames.add(clazz.getSimpleName() + "." + field.getName());
        }
        return fieldNames;
    }

    public static List<String> getFields() {
        List<String> fieldNames = new ArrayList<>();

        for (Class<?> clazz : modelClasses) {
            for (Field field : clazz.getDeclaredFields()) {
                fieldNames.add(clazz.getSimpleName() + "." + field.getName());
            }
        }
        return fieldNames;
    }
}
