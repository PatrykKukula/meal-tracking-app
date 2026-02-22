package io.github.patrykkukula.diet_ms.exception;

import io.github.patrykkukula.mealtrackingapp_common.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.util.stream.Collectors;

import static io.github.patrykkukula.mealtrackingapp_common.utils.BasicUtils.setOccurrenceTime;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleProductSnapshotNotFoundException(ProductSnapshotNotFoundException ex, HttpServletRequest request) {
        log.warn(
                "ProductSnapshotNotFoundException occurred in Diet MS. path={}",
                request.getRequestURI(),
                ex
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
                        request.getRequestURI(),
                        setOccurrenceTime()
                )
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.warn(
                "AccessDeniedException occurred in Diet MS after SecurityFilterChain. path={}",
                request.getRequestURI(),
                ex
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        HttpStatus.FORBIDDEN.value(), ex.getMessage(),
                        request.getRequestURI(),
                        setOccurrenceTime()
                )
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest webRequest) {
        log.warn(
                "MethodArgumentNotValidException occurred in Diet MS. path={}",
                webRequest.getDescription(false),
                ex
        );

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(
                new ErrorResponseDto(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        HttpStatus.BAD_REQUEST.value(),
                        message,
                        webRequest.getDescription(false),
                        setOccurrenceTime()
                )
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex, WebRequest webRequest) {
        log.error(
                "Unexpected exception occurred in Product MS. path={} | exception={}",
                webRequest.getDescription(false),
                ex.getClass(),
                ex
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage(),
                        webRequest.getDescription(false),
                        setOccurrenceTime()
                )
        );
    }
}
