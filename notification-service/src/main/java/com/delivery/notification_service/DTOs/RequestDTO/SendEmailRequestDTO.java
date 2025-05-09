package com.delivery.notification_service.DTOs.RequestDTO;

import lombok.Data;

@Data
public class SendEmailRequestDTO {

    String subject;

    String senderEmail;

    String mailPurpose;

    String body;

    String util;
}
