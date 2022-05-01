package com.absence.auth.controllers;

import com.absence.auth.dtos.ChangePasswordRequestDto;
import com.absence.auth.dtos.ResponseDto;
import com.absence.auth.exceptions.InputValidationException;
import com.absence.auth.exceptions.ResourceNotFoundException;
import com.absence.auth.models.Users;
import com.absence.auth.repositories.UsersRepository;
import com.absence.auth.utilities.PasswordHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class ChangePasswordController {

    @Autowired
    UsersRepository usersRepository;

    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(@RequestHeader("user-audit-id") String userAuditId,
            @RequestBody ChangePasswordRequestDto dto) throws ResourceNotFoundException, InputValidationException {
        Users users = usersRepository.findByUsername(dto.getEmail()).orElse(null);
        if (users != null) {
            String oldPasswordHex = PasswordHashUtil.generate(dto.getOldPassword());
            if (oldPasswordHex.equals(users.getPassword())) {
                users.setPassword(dto.getNewPassword());
                usersRepository.save(users);

                ResponseDto responseDto = ResponseDto.builder()
                        .code(HttpStatus.OK.toString())
                        .status("success")
                        .data(null)
                        .message("Successfully change password!")
                        .build();

                return ResponseEntity.ok(responseDto);
            } else {
                throw new InputValidationException("Invalid Old Password!");
            }
        } else {
            throw new ResourceNotFoundException("Email not found!");
        }
    }

}
