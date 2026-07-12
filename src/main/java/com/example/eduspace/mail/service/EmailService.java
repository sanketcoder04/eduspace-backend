package com.example.eduspace.mail.service;

import com.example.eduspace.mail.constant.MailConstants;
import com.example.eduspace.mail.template.EmailTemplateBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationOtp(String email, String name, String otp) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(MailConstants.EMAIL_VERIFICATION_SUBJECT);
            helper.setText(EmailTemplateBuilder.verificationOtpTemplate(name, otp), true);
            mailSender.send(message);

        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Unable to send verification email.", e);
        }
    }
}