package co.empresa.vivaeventos.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VivaeventosAnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(VivaeventosAnalyticsApplication.class, args);
    }
}
