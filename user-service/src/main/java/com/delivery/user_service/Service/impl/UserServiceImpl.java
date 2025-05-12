package com.delivery.user_service.Service.impl;

import com.delivery.user_service.DTOs.RequestDTOs.SendEmailRequestDTO;
import com.delivery.user_service.DTOs.RequestDTOs.UserSignUpRequestDTO;
import com.delivery.user_service.DTOs.ResponseDTOs.CommonMessageResponseDTO;
import com.delivery.user_service.Models.Users;
import com.delivery.user_service.Repositories.UserRepository;
import com.delivery.user_service.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public ResponseEntity<CommonMessageResponseDTO> createUser(UserSignUpRequestDTO signUpRequestDTO) {
        CommonMessageResponseDTO responseDTO = new CommonMessageResponseDTO();
        if (signUpRequestDTO.getUserName().isEmpty() || signUpRequestDTO.getEmailId().isEmpty() || signUpRequestDTO.getPhoneNumber().isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("Please verify all fields");
            responseDTO.setResponseCode("PVF");
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }

        Optional<Users> usersPhoneCheck = userRepository.findByPhoneNumber(signUpRequestDTO.getPhoneNumber());
        if (usersPhoneCheck.isPresent()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("Account exists with this phone number. Please proceed to login.");
            responseDTO.setResponseCode("PAE");
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }

        Optional<Users> usersEmailCheck = userRepository.findByUserEmailId(signUpRequestDTO.getEmailId());
        if (usersEmailCheck.isPresent()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("An account with this email already exists.");
            responseDTO.setResponseCode("PAE");
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
        Users users = new Users();
        users.setUserName(signUpRequestDTO.getUserName());
        users.setUserEmailId(signUpRequestDTO.getEmailId());
        users.setPhoneNumber(signUpRequestDTO.getPhoneNumber());

        int otp = generateSixDigitCode();

        SendEmailRequestDTO sendEmailRequestDTO = new SendEmailRequestDTO();

        sendEmailRequestDTO.setSenderEmail(users.getUserEmailId());
        sendEmailRequestDTO.setMailPurpose("SIGNIN_OTP");
        sendEmailRequestDTO.setUtil(String.valueOf(otp));

        Boolean result = false;

        try{
            result = webClientBuilder.build().post()
                    .uri("http://notification-service/api/notification/sendEmail")
                    .bodyValue(sendEmailRequestDTO)
                    .retrieve()
                    .bodyToMono(boolean.class)
                    .block();
        }catch (Exception e){
            log.error("failed to call notification service: {}", e.getMessage());
        }

        if (Boolean.TRUE.equals(result)) {
            Users savedUser = userRepository.save(users);
            try{
                redisTemplate.opsForValue().set(savedUser.getUserEmailId(), String.valueOf(otp), 3, TimeUnit.MINUTES);
            }catch (Exception e){
                log.error("Failed to cache OTP: {}", e.getMessage());
            }
            responseDTO.setResponseCode("VOTP");
            responseDTO.setMessage("Verification OTP sent Successfully");
            responseDTO.setSuccess(true);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }else {
            log.error("failed to send OTP");
            responseDTO.setResponseCode("FOTP");
            responseDTO.setMessage("Failed to send OTP");
            responseDTO.setSuccess(false);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<CommonMessageResponseDTO> verifyOtp(String email, String otp) {
        CommonMessageResponseDTO responseDTO = new CommonMessageResponseDTO();

        Optional<Users> usersOptional = userRepository.findByUserEmailId(email);
        if (usersOptional.isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("No user account found with the provided email");
            responseDTO.setResponseCode("OTPEXP");
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
        String cachedOtp = redisTemplate.opsForValue().get(email);

        if (cachedOtp == null) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("OTP has expired");
            responseDTO.setResponseCode("OTPEXP");
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }

        if (cachedOtp.equals(otp)) {
            // Remove OTP after successful verification
            log.info("cashed OTP: {}", cachedOtp);
            log.info("OTP entered by user: {}", otp);
            redisTemplate.delete(email);
            Users user = usersOptional.get();
            user.setPhoneNumberVerified(true);
            userRepository.save(user);
            responseDTO.setSuccess(true);
            responseDTO.setMessage("OTP verified successfully");
            responseDTO.setResponseCode("VOTPSUCC");
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("Invalid OTP");
            responseDTO.setResponseCode("INVOTP");
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<CommonMessageResponseDTO> login(String email) {
        CommonMessageResponseDTO responseDTO = new CommonMessageResponseDTO();
        Optional<Users> emailCheck = userRepository.findByUserEmailId(email);

        if (emailCheck.isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("We couldn't find an account matching the provided credentials!");
            responseDTO.setResponseCode("NOA");
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
        Users user = emailCheck.get();
        if (!user.isPhoneNumberVerified()) {
            userRepository.deleteById(user.getId());
            responseDTO.setMessage("Please verify your account!");
            responseDTO.setSuccess(false);
            responseDTO.setResponseCode("LOGS");
            return new ResponseEntity<>(responseDTO, HttpStatus.FORBIDDEN);
        }
        try {
            responseDTO.setMessage("User logged in successfully");
            responseDTO.setSuccess(true);
            responseDTO.setResponseCode("LOGS");
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            responseDTO.setMessage("failed to login");
            responseDTO.setSuccess(false);
            responseDTO.setResponseCode("LOGF");
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }

    }

    public int generateSixDigitCode() {
        Random random = new Random();
        return 100000 + random.nextInt(900000); // generates a number between 100000 and 999999
    }


}
