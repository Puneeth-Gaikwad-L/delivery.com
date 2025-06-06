package com.delivery.Service;

import com.delivery.DTOs.RequestDTO.SendEmailRequestDto;
import org.springframework.http.ResponseEntity;

public interface SendEmailService {

    void sendEmail(SendEmailRequestDto email);
}
