package com.delivery.DTOs.RequestDTO;

import lombok.Data;

@Data
public class SendEmailRequestDto {

    String subject;

    String senderEmail;

    String emailType;

    String body;

    String util;
}
