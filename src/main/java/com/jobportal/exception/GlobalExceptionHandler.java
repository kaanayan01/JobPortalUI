package com.jobportal.exception;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jobportal.dto.GenericResponse;
import com.jobportal.utils.ResponseBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1️⃣ Handle validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Validation Error", errors);
    }

    // 2️⃣ Resource not found errors
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 3️⃣ Bad request errors (custom business validation)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GenericResponse<Object>> handleBadRequest(BadRequestException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<GenericResponse<Object>> handlePermissionRequest(UnauthorizedException ex) {
        return ResponseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
    
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GenericResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, "Invalid argument: " + ex.getMessage());
    }

    // 4️⃣ Generic fallback for uncaught exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<Object>> handleGenericException(Exception ex) {
        return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                List.of(ex.getMessage()));
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<GenericResponse<Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = "Database constraint violated: " + ex.getMostSpecificCause().getMessage();
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST, message);
    }
}
