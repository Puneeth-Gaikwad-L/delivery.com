package com.delivery.user_service.Events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerifiedEvent {
    String userEmail;
    String util;
}
