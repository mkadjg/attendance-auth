package com.absence.auth.services;

import com.absence.auth.models.UserOtp;
import com.absence.auth.models.Users;
import com.absence.auth.payloads.EmailPayload;
import com.absence.auth.repositories.UserOtpRepository;
import com.absence.auth.utilities.DefaultPasswordGeneratorUtil;
import com.absence.auth.utilities.OtpGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    @Autowired
    UserOtpRepository userOtpRepository;

    @Autowired
    EmailService emailService;

    @Value("${forgot.password.otp.expiration}")
    Integer otpValidUntil;

    @Override
    public Boolean sendEmailForgotPassword(Users users) {
        try {
            String otp = OtpGeneratorUtil.getRandomSixNumber();
            Map<String, Object> variables = new HashMap<>();
            variables.put("otp", otp);

            UserOtp userOtp = userOtpRepository.findUserOtpByUsersId(users.getUserId()).orElse(null);
            if (userOtp == null) {
                userOtp = new UserOtp();
            }

            userOtp.setOtpNumber(otp);
            userOtp.setUsers(users);
            userOtp.setOtpDate(new Date());
            LocalDateTime localDateTime = LocalDateTime.now();
            localDateTime = localDateTime.plusMinutes(otpValidUntil);
            userOtp.setValidUntil(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
            userOtpRepository.save(userOtp);

            String emailBody = emailService.generateHtmlEmailBody("forgot-password.html", variables);
            EmailPayload emailPayload = new EmailPayload();
            emailPayload.setSubject("OTP Forgot Password");
            emailPayload.setReceiver(users.getUsername());
            emailPayload.setEmailBody(emailBody);
            emailService.sendEmail(emailPayload);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
