package io.github.patrykkukula.product_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication(scanBasePackages = {"io.github.patrykkukula.mealtrackingapp_common",
        "io.github.patrykkukula.product_ms"}
)
@EnableMethodSecurity
public class ProductMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductMicroserviceApplication.class, args);
    }

}
