package com.absence.auth.controllers;

import com.absence.auth.authentication.JwtConfig;
import com.absence.auth.dtos.NewPasswordRequestDto;
import com.absence.auth.dtos.ResponseDto;
import com.absence.auth.dtos.ValidateOtpRequestDto;
import com.absence.auth.dtos.ValidateOtpResponseDto;
import com.absence.auth.exceptions.InputValidationException;
import com.absence.auth.exceptions.ResourceNotFoundException;
import com.absence.auth.exceptions.SendEmailException;
import com.absence.auth.models.UserOtp;
import com.absence.auth.models.UserRole;
import com.absence.auth.models.Users;
import com.absence.auth.repositories.UserOtpRepository;
import com.absence.auth.repositories.UserRoleRepository;
import com.absence.auth.repositories.UsersRepository;
import com.absence.auth.services.ForgotPasswordService;
import com.absence.auth.utilities.PasswordHashUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class ForgotPasswordController {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UserOtpRepository userOtpRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    ForgotPasswordService forgotPasswordService;

    @Autowired
    JwtConfig jwtConfig;

    @GetMapping("/forgot-password/send-otp")
    public ResponseEntity<Object> sendOtp(@RequestParam("email") String email) throws ResourceNotFoundException, SendEmailException {
        Users users = usersRepository.findByUsername(email).orElse(null);
        if (users != null) {
            if (forgotPasswordService.sendEmailForgotPassword(users)) {
                ResponseDto responseDto = ResponseDto.builder()
                        .code(HttpStatus.OK.toString())
                        .status("success")
                        .data(null)
                        .message("Successfully send email otp!")
                        .build();
                return ResponseEntity.ok(responseDto);
            } else {
                throw new SendEmailException("Failed to send email notification!");
            }
        } else {
            throw new ResourceNotFoundException("Email not registered!");
        }
    }

    @PostMapping("/forgot-password/validate-otp")
    public ResponseEntity<Object> validateOtp(@RequestBody ValidateOtpRequestDto dto) throws ResourceNotFoundException, InputValidationException {
        Users users = usersRepository.findByUsername(dto.getEmail()).orElse(null);
        if (users != null) {
            UserOtp userOtp = userOtpRepository.findUserOtpByUsersId(users.getUserId()).orElse(null);
            if (userOtp == null) {
                throw new InputValidationException("User doesn't have actual otp!");
            }
            if (userOtp.getValidUntil().compareTo(new Date()) < 0) {
                throw new InputValidationException("Otp number is expired!");
            }
            if (userOtp.getOtpNumber().equals(dto.getOtpNumber())) {
                List<UserRole> userRoles = userRoleRepository.findByUserId(users.getUserId());
                long now = System.currentTimeMillis();
                String token = Jwts.builder()
                        .setSubject(users.getUsername())
                        .claim("authorities", userRoles.stream()
                                .map(userRole -> userRole.getRole().getRoleName()).collect(Collectors.toList()))
                        .setIssuedAt(new Date(now))
                        .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000L))
                        .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
                        .compact();

                ResponseDto responseDto = ResponseDto.builder()
                        .code(HttpStatus.OK.toString())
                        .status("success")
                        .data(ValidateOtpResponseDto.builder().token(token).build())
                        .message("Successfully validate otp!")
                        .build();
                return ResponseEntity.ok(responseDto);
            } else {
                throw new InputValidationException("Invalid otp!");
            }
        } else {
            throw new ResourceNotFoundException("Email not registered!");
        }
    }

    @PostMapping("/forgot-password/new-password")
    public ResponseEntity<Object> newPassword(@RequestBody NewPasswordRequestDto dto) throws ResourceNotFoundException {
        Users users = usersRepository.findByUsername(dto.getEmail()).orElse(null);
        if (users != null) {
            users.setPassword(PasswordHashUtil.generate(dto.getNewPassword()));
            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(null)
                    .message("Successfully save new password!")
                    .build();
            return ResponseEntity.ok(responseDto);
        } else {
            throw new ResourceNotFoundException("Email not registered!");
        }
    }
}
