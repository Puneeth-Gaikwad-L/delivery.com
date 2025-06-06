package com.delivery.Controller;

import com.delivery.DTOs.RequestDTO.SendEmailRequestDto;
import com.delivery.Service.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notify/email")
public class SendEmailController {

    @Autowired
    private SendEmailService sendEmailService;

    public void sendEmail(@RequestBody SendEmailRequestDto emailRequestDto){
        sendEmailService.sendEmail(emailRequestDto);
    }
}
