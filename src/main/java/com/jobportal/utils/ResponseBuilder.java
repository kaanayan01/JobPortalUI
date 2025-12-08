package com.jobportal.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jobportal.dto.GenericResponse;

import java.util.Map;
import java.util.List;

public class ResponseBuilder {

    // Success
    public static <T> ResponseEntity<GenericResponse<T>> success(T data, String message) {
        GenericResponse<T> response = new GenericResponse<>(HttpStatus.OK.value(), message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

 
    public static <T> ResponseEntity<GenericResponse<T>> created(T data, String message) {
        GenericResponse<T> response = new GenericResponse<>(HttpStatus.CREATED.value(), message, data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Error with field errors
    public static ResponseEntity<GenericResponse<Object>> error(
            HttpStatus status, String message, Map<String, String> fieldErrors) {
        GenericResponse<Object> response = new GenericResponse<>(status.value(), message, fieldErrors);
        return new ResponseEntity<>(response, status);
    }

    // Error with global errors list
    public static ResponseEntity<GenericResponse<Object>> error(
            HttpStatus status, String message, List<String> globalErrors) {
        GenericResponse<Object> response = new GenericResponse<>();
        response.setTimestamp(java.time.LocalDateTime.now());
        response.setStatus(status.value());
        response.setMessage(message);
        response.setGlobalErrors(globalErrors);
        return new ResponseEntity<>(response, status);
    }

    // Generic error with no data
    public static ResponseEntity<GenericResponse<Object>> error(HttpStatus status, String message) {
        GenericResponse<Object> response = new GenericResponse<>();
        response.setTimestamp(java.time.LocalDateTime.now());
        response.setStatus(status.value());
        response.setMessage(message);
        return new ResponseEntity<>(response, status);
    }
}
