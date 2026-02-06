package io.github.patrykkukula.product_ms.exception;

import io.github.patrykkukula.product_ms.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleProductNotFoundException(ProductNotFoundException ex, HttpServletRequest request) {
        log.warn(
                "ProductNotFoundException occurred in Product MS. path={}",
                request.getRequestURI(),
                ex
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        HttpStatus.NOT_FOUND.value(), ex.getMessage(),
                        request.getRequestURI(),
                        setOccurrenceTime()
                )
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleInvalidProductCategoryException(InvalidProductCategoryException ex, HttpServletRequest request) {
        log.warn(
                "ProductNotFoundException occurred in Product MS. path={}",
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
    public ResponseEntity<ErrorResponseDto> handleCustomProductAmountExceededException(CustomProductAmountExceededException ex, HttpServletRequest request) {
        log.warn(
                "CustomProductAmountExceededException occurred in Product MS. path={}",
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
                "AccessDeniedException occurred in Product MS after SecurityFilterChain. path={}",
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
                "MethodArgumentNotValidException occurred in Product MS. path={}",
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
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException ex, WebRequest webRequest) {
        log.warn(
                "ConstraintViolationException occurred in Product MS. path={}",
                webRequest.getDescription(false),
                ex
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDto(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getConstraintViolations().stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", ")),
                        webRequest.getDescription(false),
                        setOccurrenceTime()
                )
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest webRequest) {
        log.error(
                "IllegalArgumentException occurred in Product MS. path={}",
                webRequest.getDescription(false),
                ex
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponseDto(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
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

    // Set current time in ISO local date time
    private String setOccurrenceTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
