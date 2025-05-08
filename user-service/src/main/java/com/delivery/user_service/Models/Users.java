package com.delivery.user_service.Models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String userName;

    @Column(nullable = false, unique = true)
    String phoneNumber;

    @Column(nullable = false, unique = true)
    String userEmailId;

    boolean phoneNumberVerified;

    @CreationTimestamp
    Date createdOn;
}
