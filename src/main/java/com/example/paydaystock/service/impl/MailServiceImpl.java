package com.example.paydaystock.service.impl;

import com.example.paydaystock.model.User;
import com.example.paydaystock.repository.UserRepository;
import com.example.paydaystock.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {

    private static final String TASK_ASSIGNMENT = "Task Assignment";
    private final UserRepository userRepository;

    @Override
    public void sendWithoutAttachment(String to, String sender, String text) {
        JavaMailSenderImpl mailSender = javaMailSender();
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo(to);
        msg.setSubject(TASK_ASSIGNMENT);
        msg.setText(text);

        mailSender.send(msg);
    }

    private JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("mhuseinov7@gmail.com");
        mailSender.setPassword("tlvamdpoilqxvcmy");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return mailSender;
    }

    public void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "mhuseinov7@gmail.com";
        String senderName = "Mahir Huseynov";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Your company name.";

        JavaMailSenderImpl mailSender = javaMailSender();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getName());
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @Override
    public boolean verify(String code) {
        User user = userRepository.findByVerificationCode(code);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(code);
            user.setEnabled(true);
            userRepository.save(user);

            return true;
        }
    }
}