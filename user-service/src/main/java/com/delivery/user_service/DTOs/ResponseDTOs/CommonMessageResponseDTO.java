package com.delivery.user_service.DTOs.ResponseDTOs;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonMessageResponseDTO {
    String responseCode;
    String message;
    boolean success;
    String util;
}

