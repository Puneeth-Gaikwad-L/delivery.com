package com.delivery.user_service.Service.impl;

import com.delivery.user_service.DTOs.RequestDTOs.UserSignUpRequestDTO;
import com.delivery.user_service.DTOs.ResponseDTOs.CommonMessageResponseDTO;
import com.delivery.user_service.Models.Users;
import com.delivery.user_service.Repositories.UserRepository;
import com.delivery.user_service.Service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
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
    private CacheManager cacheManager;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;

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

        Users savedUser = userRepository.save(users);
        int otp = generateSixDigitCode();

        // Store OTP in Redis with 3 minutes expiration
        redisTemplate.opsForValue().set(savedUser.getUserEmailId(), String.valueOf(otp), 3, TimeUnit.MINUTES);
        try {
            responseDTO.setResponseCode("VOTP");
            responseDTO.setMessage("Verification OTP sent Successfully");
            responseDTO.setSuccess(true);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.error("failed to send OTP: {}", e.getMessage());
            userRepository.deleteById(savedUser.getId());
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
        try{
            responseDTO.setMessage("User logged in successfully");
            responseDTO.setSuccess(true);
            responseDTO.setResponseCode("LOGS");
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }catch (Exception e){
            responseDTO.setMessage("failed to login");
            responseDTO.setSuccess(false);
            responseDTO.setResponseCode("LOGF");
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }

    }


    private void sendOTP(String email, String otp) {
        String subject = "Please verify your account!";
        Context context = new Context();
        context.setVariable("otp", String.valueOf(otp));
        String html = templateEngine.process("OtpEmailTemplate", context);

        try {
            sendEmail(email, subject, null, html);
        } catch (Exception e) {
            log.error("Error occurred while sending OTP: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public int generateSixDigitCode() {
        Random random = new Random();
        return 100000 + random.nextInt(900000); // generates a number between 100000 and 999999
    }

    public void sendEmail(String toEmail, String subject, String body, String html) throws UnsupportedEncodingException {

        String senderName = "Delivery.com";
        String fromEmail = "shubahmverma007@gmail.com";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(new InternetAddress(fromEmail, senderName));
            helper.setTo(toEmail);
            helper.setSubject(subject);
            if (html != null) {
                helper.setText(html, true);
            } else {
                helper.setText(body, true);
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


}
