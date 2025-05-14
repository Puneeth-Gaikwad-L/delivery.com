package com.delivery.user_service.Service;

import com.delivery.user_service.DTOs.RequestDTOs.ResendOtpRequestDto;
import com.delivery.user_service.DTOs.RequestDTOs.UserSignUpRequestDTO;
import com.delivery.user_service.DTOs.RequestDTOs.VerifyOtpRequestDto;
import com.delivery.user_service.DTOs.ResponseDTOs.CommonMessageResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ResponseEntity<CommonMessageResponseDTO> createUser(UserSignUpRequestDTO userSignUpRequestDTO);

    ResponseEntity<CommonMessageResponseDTO> verifyOtp(VerifyOtpRequestDto requestDto);

    ResponseEntity<CommonMessageResponseDTO> login(String email);

    ResponseEntity<CommonMessageResponseDTO> resendOtp(ResendOtpRequestDto resendOtpRequestDto);
}
