package io.github.patrykkukula.product_ms;

import io.github.patrykkukula.mealtrackingapp_common.events.EventBindingConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication(scanBasePackages = {"io.github.patrykkukula.mealtrackingapp_common",
        "io.github.patrykkukula.product_ms"}
)
@EnableMethodSecurity
@EnableScheduling
@ConfigurationPropertiesScan(basePackages = "io.github.patrykkukula.mealtrackingapp_common")
@Slf4j
@RequiredArgsConstructor
public class ProductMicroserviceApplication {
    private final EventBindingConfig config;

    public static void main(String[] args) {
        SpringApplication.run(ProductMicroserviceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        log.info("Bindings loaded: {}", config);
    }

}
