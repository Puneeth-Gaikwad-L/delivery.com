package com.delivery.notification_service.Service;

import com.delivery.notification_service.DTOs.RequestDTO.SendEmailRequestDTO;

public interface SendEmailService {
    void sendEmail(SendEmailRequestDTO requestDTO);
}
