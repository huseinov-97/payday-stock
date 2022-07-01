package com.example.paydaystock.exception;

public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 12378432748937234L;

    public AuthenticationException(String message) {
        super(message);
    }
}
