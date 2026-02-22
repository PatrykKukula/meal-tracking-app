package io.github.patrykkukula.mealtrackingapp_common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BasicUtils {
    private BasicUtils() {}

    // Set resource URI location
    public static URI setLocation(Long id, HttpServletRequest request) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/products" + "/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    // Set current time in ISO local date time
    public static String setOccurrenceTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
