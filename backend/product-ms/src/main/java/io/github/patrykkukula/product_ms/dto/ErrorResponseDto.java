package io.github.patrykkukula.product_ms.dto;

public record ErrorResponseDto(String statusMessage, int statusCode, String message, String path, String occurrenceTime) {
}
