package com.case_wallet.apirest.common.exception;

import com.case_wallet.apirest.domain.user.exception.InvalidPasswordException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ApiError apiError = buildApiError(
            request,
            HttpStatus.FORBIDDEN,
            "Access Denied",
            "No tienes permisos suficientes para acceder a este recurso"
        );
        log.error("Access Denied Error: {} - URL: {}", ex.getMessage(), request.getRequestURL());
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        ApiError apiError = buildApiError(
            request,
            HttpStatus.UNAUTHORIZED,
            "Authentication Failed",
            "Error de autenticación: " + ex.getMessage()
        );
        log.error("Authentication Error: {} - URL: {}", ex.getMessage(), request.getRequestURL());
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        ApiError apiError = buildApiError(
            request,
            HttpStatus.UNAUTHORIZED,
            "Invalid Credentials",
            "Credenciales inválidas"
        );
        log.error("Bad Credentials Error - URL: {}", request.getRequestURL());
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
        ApiError apiError = buildApiError(
            request,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            ex.getMessage()
        );
        log.error("Unexpected Error: {} - URL: {}", ex.getMessage(), request.getRequestURL(), ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Object> handleInvalidPasswordException(InvalidPasswordException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(
            UsernameNotFoundException ex, 
            HttpServletRequest request) {
        ApiError apiError = buildApiError(
            request,
            HttpStatus.NOT_FOUND,
            "Not Found",
            ex.getMessage()
        );
        log.error("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalStateException(
            IllegalStateException ex, 
            HttpServletRequest request) {
        ApiError apiError = buildApiError(
            request,
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            ex.getMessage()
        );
        log.error("Invalid state: {}", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiError apiError = buildApiError(
            request,
            HttpStatus.BAD_REQUEST,
            "Validation Error",
            "Invalid input data"
        );
        apiError.setDetails(errors);
        
        log.error("Validation error: {}", errors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ApiError apiError = buildApiError(
                request,
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage() // Use the exception message
        );

        log.error("IllegalArgumentException: {}", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    private ApiError buildApiError(HttpServletRequest request, HttpStatus status, String error, String message) {
        return ApiError.builder()
                .path(request.getRequestURI())
                .method(request.getMethod())
                .status(status.value())
                .error(error)
                .message(message)
                .details(new HashMap<>())
                .timestamp(LocalDateTime.now())
                .build();
    }
}