package com.absence.auth.controllers;

import com.absence.auth.dtos.RegisterEmployeeRequestDto;
import com.absence.auth.dtos.ResponseDto;
import com.absence.auth.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
public class RegisterEmployeeController {

    @Autowired
    EmployeeService employeeService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterEmployeeRequestDto dto){
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(employeeService.register(dto))
                .message("Successfully Register User!")
                .build();
        return ResponseEntity.ok(responseDto);
    }


}