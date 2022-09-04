package com.absence.auth.controllers;

import com.absence.auth.authentication.CustomAuthenticationManager;
import com.absence.auth.authentication.JwtConfig;
import com.absence.auth.dtos.LoginRequestDto;
import com.absence.auth.dtos.LoginResponseDto;
import com.absence.auth.dtos.ResponseDto;
import com.absence.auth.exceptions.BadCredentialException;
import com.absence.auth.models.Users;
import com.absence.auth.payloads.EmployeeResponsePayload;
import com.absence.auth.repositories.*;
import com.absence.auth.services.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    CustomAuthenticationManager customAuthenticationManager;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UserLoginHistoryRepository userLoginHistoryRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDto loginRequestDto) throws BadCredentialException {
        Authentication authentication = customAuthenticationManager.authenticate(loginRequestDto, usersRepository, userLoginHistoryRepository,
                    userRoleRepository, jwtConfig);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ObjectMapper mapper = new ObjectMapper();
        long now = System.currentTimeMillis();

        Users users = mapper.convertValue(authentication.getDetails(), Users.class);
        EmployeeResponsePayload employee = employeeService.findEmployeeByUserId(users.getUserId());

        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authorities", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000L))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
                .compact();

        assert employee != null;
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .userId(users.getUserId())
                .username(users.getUsername())
                .employeeId(employee.getEmployeeId())
                .employeeName(employee.getEmployeeName())
                .token(token)
                .jobTitle(employee.getJobTitle())
                .employeePhoto(employee.getEmployeePhoto())
                .roleName(authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .build();

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(loginResponseDto)
                .message("Successfully Login!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

}
