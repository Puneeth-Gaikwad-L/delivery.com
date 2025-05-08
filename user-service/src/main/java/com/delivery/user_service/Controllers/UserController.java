package com.delivery.user_service.Controllers;

import com.delivery.user_service.DTOs.RequestDTOs.UserSignUpRequestDTO;
import com.delivery.user_service.DTOs.ResponseDTOs.CommonMessageResponseDTO;
import com.delivery.user_service.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/signIn")
    public ResponseEntity<CommonMessageResponseDTO> signInUser(@RequestBody UserSignUpRequestDTO userSignUpRequestDTO) {
        return userService.createUser(userSignUpRequestDTO);
    }

    @PostMapping("/verify")
    public ResponseEntity<CommonMessageResponseDTO> verifyOTP(@RequestParam("email") String email, @RequestParam("otp") String otp){
        return userService.verifyOtp(email, otp);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonMessageResponseDTO> logIn(@RequestParam("email")String email){
        return userService.login(email);
    }
}
