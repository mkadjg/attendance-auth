package com.absence.auth.services;

import com.absence.auth.dtos.RegisterEmployeeRequestDto;

public interface EmployeeService {
    Object register(RegisterEmployeeRequestDto dto);
}
