package com.delivery.user_service.Service;

import com.delivery.user_service.DTOs.RequestDTOs.UserSignUpRequestDTO;
import com.delivery.user_service.DTOs.ResponseDTOs.CommonMessageResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ResponseEntity<CommonMessageResponseDTO> createUser(UserSignUpRequestDTO userSignUpRequestDTO);

    ResponseEntity<CommonMessageResponseDTO> verifyOtp(String email, String Otp);

    ResponseEntity<CommonMessageResponseDTO> login(String email);
}
