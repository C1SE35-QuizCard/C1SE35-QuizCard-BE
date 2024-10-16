package com.example.quizcards.exception;

import com.example.quizcards.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QuizCardApiException.class)
    public ResponseEntity<ApiResponse> resolveQuizCardApi(QuizCardApiException exception) {
        String message = exception.getMessage();
        HttpStatus status = exception.getStatus();

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setSuccess(Boolean.FALSE);
        apiResponse.setMessage(message);

        return new ResponseEntity<>(apiResponse, status);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> resolveBadCredentials(BadCredentialsException exception) {
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, exception.getMessage());

        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ApiResponse> resolveAccountStatus(AccountStatusException exception) {
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, exception.getMessage());

        return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> resolveUsernameNotFound(UsernameNotFoundException exception) {
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, exception.getMessage());

        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> resolveUnauthorized(UnauthorizedException exception) {
        ApiResponse apiResponse = exception.getApiResponse();

        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> resolveBadRequest(BadRequestException exception) {
        ApiResponse apiResponse = exception.getApiResponse();

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resolveResourceNotFound(ResourceNotFoundException exception) {
        ApiResponse apiResponse = exception.getApiResponse();

        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ApiResponse> resolveTokenRefresh(TokenRefreshException exception) {
        ApiResponse apiResponse = exception.getApiResponse();

        return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> resolveException(Exception ex) {
        System.out.println(ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "An error occurred from server");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
