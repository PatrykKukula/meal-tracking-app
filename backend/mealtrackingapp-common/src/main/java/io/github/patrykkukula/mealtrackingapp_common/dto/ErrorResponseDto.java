package io.github.patrykkukula.mealtrackingapp_common.dto;

public record ErrorResponseDto(String statusMessage, int statusCode, String message, String path, String occurrenceTime) {
}
