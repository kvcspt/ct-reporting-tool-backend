package hu.kvcspt.ctreportingtoolbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CtReportingToolBackendApplication {
    static {
        //nu.pattern.OpenCV.loadLocally();
    }
    public static void main(String[] args) {
        SpringApplication.run(CtReportingToolBackendApplication.class, args);
    }

}
