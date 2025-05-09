package com.delivery.notification_service.Controllers;

import com.delivery.notification_service.DTOs.RequestDTO.SendEmailRequestDTO;
import com.delivery.notification_service.Service.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
public class SendEmailController {

    @Autowired
    SendEmailService sendEmailService;

    @PostMapping("/sendEmail")
    public void sendEmail(@RequestBody SendEmailRequestDTO requestDTO){
        sendEmailService.sendEmail(requestDTO);
    }
}
