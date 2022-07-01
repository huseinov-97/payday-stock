package com.example.paydaystock.service;


import com.example.paydaystock.model.User;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface MailService {
    void sendWithoutAttachment(String to, String sender, String text);

    void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException;

    boolean verify(String code);
}
