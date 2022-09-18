package com.absence.auth.services;

import com.absence.auth.attendance.models.Employee;
import com.absence.auth.dtos.RegisterEmployeeRequestDto;
import com.absence.auth.models.Users;
import com.absence.auth.payloads.EmployeeResponsePayload;

public interface EmployeeService {
    Employee register(RegisterEmployeeRequestDto dto, String userAuditId);
    Boolean sendEmailUserRegistration(String userId);
    EmployeeResponsePayload findEmployeeByUserId(String userId);
}
