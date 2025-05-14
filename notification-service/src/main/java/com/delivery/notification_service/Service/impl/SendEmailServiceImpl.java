package com.delivery.notification_service.Service.impl;

import com.delivery.notification_service.DTOs.RequestDTO.SendEmailRequestDTO;
import com.delivery.notification_service.Service.SendEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class SendEmailServiceImpl implements SendEmailService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public boolean sendEmail(SendEmailRequestDTO requestDTO) {
        if (requestDTO.getSenderEmail().isEmpty()) {
            log.info("Failed to send Email: please specify sender email");
            return false;
        }

        if (requestDTO.getMailPurpose().isEmpty() && requestDTO.getSubject().isEmpty()){
            log.info("Failed to send Email: subject and email purpose are empty. \n email purpose: {}, email subject: {}", requestDTO.getMailPurpose(), requestDTO.getSubject() );
        }
        String subject = "";
        Context context = new Context();
        String html = null;
        String body = null;

        switch (requestDTO.getMailPurpose()){
            case "SIGNIN_OTP":
                if (requestDTO.getUtil().isEmpty()){
                    log.error("util is empty: {}", requestDTO.getUtil());
                    break;
                }
                subject="Please verify your account";
                context.setVariable("otp", String.valueOf(requestDTO.getUtil()));
                html = templateEngine.process("OtpEmailTemplate", context);
                break;
            case "LOGIN_OTP":
                if (requestDTO.getUtil().isEmpty()){
                    log.error("util is empty: {}", requestDTO.getUtil());
                    break;
                }
                subject="Authentication Code for Login";
                context.setVariable("otp", String.valueOf(requestDTO.getUtil()));
                html = templateEngine.process("OtpEmailTemplate", context);
                break;
            case "Welcome":
                subject = "Hey " + requestDTO.getUtil().split(" ")[0] + ", Welcome to Delivery.com 🍔🍕";
                html = templateEngine.process("AccountActivationTemplate.html", context);
                break;
        }

        if (body == null) {
            body= requestDTO.getBody();
        }

        try {
            sendEmail(requestDTO.getSenderEmail(), subject.isEmpty() ? requestDTO.getSubject() : subject, body, html);
        } catch (Exception e) {
            log.error("Error occurred while sending OTP: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return true;
    }

    public void sendEmail(String toEmail, String subject, String body, String html) throws UnsupportedEncodingException {

        String senderName = "Delivery.com";
        String fromEmail = "puneeth.gl@elfonze.com";

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
