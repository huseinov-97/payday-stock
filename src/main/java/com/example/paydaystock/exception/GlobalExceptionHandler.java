package com.example.paydaystock.exception;

import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception,
                                                                       WebRequest webRequest) {
        var path = ((ServletWebRequest) webRequest).getRequest().getRequestURL().toString();
        log.error("Exception {}", exception.getLocalizedMessage());
        exception.printStackTrace();
        return ResponseEntity.status(400).body(ErrorResponse.builder()
                .status(400)
                .message("Bad request")
                .detail("User not found")
                .timestamp(OffsetDateTime.now())
                .path(path)
                .build());
    }


    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(RoleNotFoundException exception,
                                                                       WebRequest webRequest) {

        var path = ((ServletWebRequest) webRequest).getRequest().getRequestURL().toString();
        log.error("Exception {}", exception.getLocalizedMessage());
        exception.printStackTrace();
        return ResponseEntity.status(400).body(ErrorResponse.builder()
                .status(400)
                .message("Bad request")
                .detail("Role not found")
                .timestamp(OffsetDateTime.now())
                .path(path)
                .build());
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> emailAlreadyExistException(EmailAlreadyExistException exception,
                                                                    WebRequest webRequest) {

        var path = ((ServletWebRequest) webRequest).getRequest().getRequestURL().toString();
        log.error("Exception {}", exception.getLocalizedMessage());
        exception.printStackTrace();
        return ResponseEntity.status(400).body(ErrorResponse.builder()
                .status(400)
                .message("Bad request")
                .detail("Given email is already exists ")
                .timestamp(OffsetDateTime.now())
                .path(path)
                .build());
    }

    @ExceptionHandler(EmailOrPasswordInvalid.class)
    public ResponseEntity<ErrorResponse> emailOrPasswordInvalid(EmailOrPasswordInvalid exception,
                                                                WebRequest webRequest) {

        var path = ((ServletWebRequest) webRequest).getRequest().getRequestURL().toString();
        log.error("Exception {}", exception.getLocalizedMessage());
        exception.printStackTrace();
        return ResponseEntity.status(400).body(ErrorResponse.builder()
                .status(400)
                .message("Bad request")
                .detail("Email or password is invalid ")
                .timestamp(OffsetDateTime.now())
                .path(path)
                .build());
    }

    @ExceptionHandler(EnableCheckException.class)
    public ResponseEntity<ErrorResponse> enableCheckException(EnableCheckException exception,
                                                              WebRequest webRequest) {

        var path = ((ServletWebRequest) webRequest).getRequest().getRequestURL().toString();
        log.error("Exception {}", exception.getLocalizedMessage());
        exception.printStackTrace();
        return ResponseEntity.status(400).body(ErrorResponse.builder()
                .status(400)
                .message("Bad request")
                .detail("Please confirm your account from email invitation and sign in again ")
                .timestamp(OffsetDateTime.now())
                .path(path)
                .build());
    }
}
