package hu.kvcspt.ctreportingtoolbackend.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class GeneralUtils {
    public static LocalDate dateToLocalDate(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
