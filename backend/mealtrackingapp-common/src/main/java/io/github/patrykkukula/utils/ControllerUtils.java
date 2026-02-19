package io.github.patrykkukula.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class ControllerUtils {
    private ControllerUtils() {}

    public static URI setLocation(Long id, HttpServletRequest request) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/products" + "/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
