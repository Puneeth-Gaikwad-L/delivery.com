package com.delivery.notification_service.Service;

import com.delivery.notification_service.DTOs.RequestDTO.SendEmailRequestDTO;

public interface SendEmailService {
    boolean sendEmail(SendEmailRequestDTO requestDTO);
}
