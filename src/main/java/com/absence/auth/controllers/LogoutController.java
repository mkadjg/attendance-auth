package com.absence.auth.controllers;

import com.absence.auth.dtos.LogoutRequestDto;
import com.absence.auth.dtos.ResponseDto;
import com.absence.auth.exceptions.ResourceNotFoundException;
import com.absence.auth.models.UserLoginHistory;
import com.absence.auth.models.Users;
import com.absence.auth.repositories.UserLoginHistoryRepository;
import com.absence.auth.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class LogoutController {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UserLoginHistoryRepository userLoginHistoryRepository;

    @GetMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        Users users = usersRepository.findById(userAuditId).orElse(null);
        if (users != null) {
            UserLoginHistory userLoginHistory =  userLoginHistoryRepository.findUserLoginHistory(users.getUserId()).orElse(null);
            assert userLoginHistory != null;
            userLoginHistory.setLoginDate(new Date());
            userLoginHistoryRepository.save(userLoginHistory);
            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(null)
                    .message("Successfully logout!")
                    .build();
            return ResponseEntity.ok(responseDto);
        } else {
            throw new ResourceNotFoundException("Email not registered!");
        }

    }

}
