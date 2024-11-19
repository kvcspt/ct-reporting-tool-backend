package hu.kvcspt.ctreportingtoolbackend.util;

import hu.kvcspt.ctreportingtoolbackend.model.Patient;
import hu.kvcspt.ctreportingtoolbackend.model.Report;
import hu.kvcspt.ctreportingtoolbackend.model.Scan;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateReplacer {

    public static String replacePlaceholders(String template, Object dataObject) {
        String pattern = "([A-Za-z]+)\\.([A-Za-z0-9]+)";
        String replacedTemplate = template;

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(template);

        while (m.find()) {
            String className = m.group(1);
            String fieldName = m.group(2);

            try {
                Class<?> clazz = Class.forName("hu.kvcspt.ctreportingtoolbackend.model." + className);
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(dataObject);

                replacedTemplate = replacedTemplate.replace(m.group(0), value != null ? value.toString() : "");
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return replacedTemplate;
    }
}
