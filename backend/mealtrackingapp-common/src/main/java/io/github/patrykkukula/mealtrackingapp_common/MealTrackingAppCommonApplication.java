package io.github.patrykkukula.mealtrackingapp_common;

import io.github.patrykkukula.mealtrackingapp_common.events.EventBindingConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = EventBindingConfig.class)
public class MealTrackingAppCommonApplication {

	public static void main(String[] args) {
		SpringApplication.run(MealTrackingAppCommonApplication.class, args);
	}

}
