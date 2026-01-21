package watchs.luxurytime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LuxuryTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LuxuryTimeApplication.class, args);
    }

}
