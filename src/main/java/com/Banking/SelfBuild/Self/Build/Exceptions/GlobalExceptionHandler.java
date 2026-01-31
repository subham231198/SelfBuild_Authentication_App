package com.Banking.SelfBuild.Self.Build.Exceptions;

import com.Banking.SelfBuild.Self.Build.POJO.APIResponse;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        APIResponse<Void> response = new APIResponse<>();
        response.setCode(400);
        response.setReason("Bad Request");
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleGeneric(Exception ex) {
        APIResponse<Void> response = new APIResponse<>();
        response.setCode(500);
        response.setReason("Internal Server Error");
        response.setMessage("Something went wrong: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<APIResponse<Void>> handleResponseStatus(ResponseStatusException ex) {
        APIResponse<Void> response = new APIResponse<>();
        response.setCode(ex.getStatusCode().value());
        if (ex.getStatusCode().value() == 400) {
            response.setReason("Bad Request");
        }
        if (ex.getStatusCode().value() == 401) {
            response.setReason("Unauthorized");
        }
        if (ex.getStatusCode().value() == 403) {
            response.setReason("Forbidden");
        }
        if (ex.getStatusCode().value() == 404) {
            response.setReason("Not Found");
        }
        if (ex.getStatusCode().value() == 500) {
            response.setReason("Internal Server Error");
        }
        response.setMessage(ex.getReason() != null ? ex.getReason() : "Unexpected error");
        return new ResponseEntity<>(response, ex.getStatusCode());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(error -> ((FieldError) error).getField() + " " + error.getDefaultMessage())
                .orElse("Invalid input");
        APIResponse<Void> response = new APIResponse<>();
        response.setCode(400);
        response.setReason("Bad Request");
        response.setMessage(errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof UnrecognizedPropertyException unrecognized) {

            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("reason", "Bad Request");
            result.put("message", "Incorrect attribute name: " + unrecognized.getPropertyName());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(result);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("reason", "Bad Request");
        result.put("message", "Error in parsing from JSON");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(result);
    }


    @ExceptionHandler(CustomerProfileSuspendedException.class)
    public ResponseEntity<Map<String, Object>> CustomerSuspendError() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("reason", "Unauthorized");
        result.put("message", "Profile is suspended!");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(result);
    }


    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> CustomerNotFoundError(){
        Map<String, Object> result = new HashMap<>();
        result.put("code", 404);
        result.put("reason", "Not Found");
        result.put("message", "Customer not found by id!");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(result);
    }
}
