package io.github.patrykkukula.dto;

public record ErrorResponseDto(String statusMessage, int statusCode, String message, String path, String occurrenceTime) {
}
