package io.github.patrykkukula.diet_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication(scanBasePackages = {"io.github.patrykkukula.mealtrackingapp_common",
		"io.github.patrykkukula.diet_ms"})
@EnableMethodSecurity
@EnableScheduling
public class DietMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DietMicroserviceApplication.class, args);
	}

}
