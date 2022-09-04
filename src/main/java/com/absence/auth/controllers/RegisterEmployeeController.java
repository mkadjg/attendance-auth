package com.absence.auth.controllers;

import com.absence.auth.dtos.RegisterEmployeeRequestDto;
import com.absence.auth.dtos.ResponseDto;
import com.absence.auth.exceptions.ResourceNotFoundException;
import com.absence.auth.exceptions.SendEmailException;
import com.absence.auth.models.Users;
import com.absence.auth.payloads.EmployeeResponsePayload;
import com.absence.auth.repositories.UserRoleRepository;
import com.absence.auth.repositories.UsersRepository;
import com.absence.auth.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/employee")
public class RegisterEmployeeController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterEmployeeRequestDto dto, @RequestHeader("user-audit-id") String userAuditId) throws SendEmailException, ResourceNotFoundException {
        Users users = usersRepository.findByUsername(dto.getEmployeeEmail()).orElse(null);
        if (users != null) {
            throw new ResourceNotFoundException("Email is already registered!");
        }
        EmployeeResponsePayload employee = (EmployeeResponsePayload) employeeService.register(dto, userAuditId);
        if (employeeService.sendEmailUserRegistration(employee.getUserId())) {
            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(employee)
                    .message("Successfully Register User!")
                    .build();
            return ResponseEntity.ok(responseDto);
        } else {
            throw new SendEmailException("Failed to send email notification!");
        }
    }

//    @PostMapping("/delete/{employeeId}")
//    public ResponseEntity<Object> deActivate(@PathVariable String employeeId) throws ResourceNotFoundException {
//        Employee employee = employeeRepository.findById(employeeId).orElse(null);
//        if (employee == null) {
//            throw new ResourceNotFoundException("Employee not found!");
//        }
//        List<UserRole> userRoles = userRoleRepository.findByUserId(employee.getUsers().getUserId());
//        userRoleRepository.deleteAll(userRoles);
//        employeeRepository.delete(employee);
//        usersRepository.delete(employee.getUsers());
//        ResponseDto responseDto = ResponseDto.builder()
//                .code(HttpStatus.OK.toString())
//                .status("success")
//                .data(null)
//                .message("Successfully delete Employee!")
//                .build();
//        return ResponseEntity.ok(responseDto);
//    }
}
