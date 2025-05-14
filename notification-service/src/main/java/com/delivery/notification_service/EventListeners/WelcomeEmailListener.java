package com.delivery.notification_service.EventListeners;

import com.delivery.notification_service.DTOs.RequestDTO.SendEmailRequestDTO;
import com.delivery.notification_service.Events.EmailVerifiedEvent;
import com.delivery.notification_service.Service.SendEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WelcomeEmailListener {

    private final SendEmailService sendEmailService;

    @KafkaListener(topics = "user-email-verified", groupId = "notification-group")
    public void handleEmailVerifiedNotification(EmailVerifiedEvent event){
        log.info("Received event for welcome email notification for: {}", event.getUserEmail());
        SendEmailRequestDTO emailRequestDTO = new SendEmailRequestDTO();
        emailRequestDTO.setSenderEmail(event.getUserEmail());
        emailRequestDTO.setMailPurpose("Welcome");
        emailRequestDTO.setUtil(event.getUtil());
        sendEmailService.sendEmail(emailRequestDTO);
    }
}
