package com.example.paydaystock.service;


import com.example.paydaystock.model.User;
import com.example.paydaystock.resource.LoginRequest;
import com.example.paydaystock.resource.RegisterRequest;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface AuthService {
    User register(RegisterRequest registerRequest, String siteUrl) throws MessagingException, UnsupportedEncodingException;

    ResponseEntity<?> login(LoginRequest loginRequest);
}
