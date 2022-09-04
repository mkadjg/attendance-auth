package com.absence.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterEmployeeRequestDto {
    private String employeeNumber;
    private String employeeName;
    private String employeeAddress;
    private Date employeeBirthdate;
    private String employeeBirthplace;
    private String employeeEmail;
    private String employeePhoneNumber;
    private int employeeGender;
    private String divisionId;
    private String jobTitleId;
    private String roleId;
    private int isSupervisor;
}
