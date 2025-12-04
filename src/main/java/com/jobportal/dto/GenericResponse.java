package com.jobportal.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class GenericResponse<T> {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;
    private Map<String, String> errors; // for field errors
    private List<String> globalErrors;   // for general errors if needed

    public GenericResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // Success constructor
    public GenericResponse(int status, String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Error constructor
    public GenericResponse(int status, String message, Map<String, String> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    // Getters & Setters

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }

    public List<String> getGlobalErrors() { return globalErrors; }
    public void setGlobalErrors(List<String> globalErrors) { this.globalErrors = globalErrors; }
}
