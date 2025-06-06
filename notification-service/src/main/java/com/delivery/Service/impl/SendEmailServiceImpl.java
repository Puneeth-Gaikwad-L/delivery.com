package com.delivery.Service.impl;

import com.delivery.DTOs.RequestDTO.SendEmailRequestDto;
import com.delivery.Exceptions.FailedToSendMailException;
import com.delivery.Service.SendEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
public class SendEmailServiceImpl implements SendEmailService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(SendEmailRequestDto email) {
        Context context = new Context();
        String html = "";
        String subject = "";
        switch (email.getEmailType()){
            case "OtpEmailTemplate":
                subject = "Please verify your account";
                context.setVariable("otp", String.valueOf(email.getUtil()));
                html = templateEngine.process("OtpEmailTemplate", context);
                break;
            case "loginEmailTemplate":
                subject = "Use This Code to Log In";
                context.setVariable("otp", String.valueOf(email.getUtil()));
                html = templateEngine.process("OtpEmailTemplate", context);
                break;
            default:
                subject = "";
        }

        if(!subject.isEmpty()){
            try{
                sendEmail(email.getSenderEmail(), subject, null, html);
            }catch (Exception e){
                log.error("Failed to send email notification to {}", email.getSenderEmail());
                throw new FailedToSendMailException(e.getMessage());
            }
        }
    }

    public void sendEmail(String toEmail, String subject, String body, String html) throws UnsupportedEncodingException {

        String senderName = "Delivery.com";
        String fromEmail = "shubahmverma007@gmail.com";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(new InternetAddress(fromEmail, senderName));
            helper.setTo(toEmail);
            helper.setSubject(subject);
            if (html != null) {
                helper.setText(html, true);
            } else {
                helper.setText(body, true);
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
