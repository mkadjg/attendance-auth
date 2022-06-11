package com.absence.auth.services;

import com.absence.auth.dtos.RegisterEmployeeRequestDto;
import com.absence.auth.models.Users;

public interface EmployeeService {
    Object register(RegisterEmployeeRequestDto dto, String userAuditId);
    Boolean sendEmailUserRegistration(Users users);
}
