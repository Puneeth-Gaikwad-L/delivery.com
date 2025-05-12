package com.delivery.user_service.DTOs.RequestDTOs;

import lombok.Data;

@Data
public class SendEmailRequestDTO {

    String subject;

    String senderEmail;

    String mailPurpose;

    String body;

    String util;
}
