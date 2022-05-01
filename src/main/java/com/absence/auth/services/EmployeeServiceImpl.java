package com.absence.auth.services;

import com.absence.auth.dtos.RegisterEmployeeRequestDto;
import com.absence.auth.models.Employee;
import com.absence.auth.models.UserRole;
import com.absence.auth.models.Users;
import com.absence.auth.repositories.*;
import com.absence.auth.utilities.DefaultPasswordGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    DivisionRepository divisionRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Override
    public Object register(RegisterEmployeeRequestDto dto) {
        Users users = new Users();
        users.setUsername(dto.getEmployeeEmail());
        users.setPassword(DefaultPasswordGeneratorUtil.generateRandomPassword());
        Users newUsers = usersRepository.save(users);

        UserRole userRole = new UserRole();
        userRole.setUsers(newUsers);
        userRole.setRole(roleRepository.findById(dto.getRoleId()).orElse(null));
        userRoleRepository.save(userRole);

        Employee employee = new Employee();
        employee.setEmployeeName(dto.getEmployeeName());
        employee.setEmployeeAddress(dto.getEmployeeAddress());
        employee.setEmployeeNumber(dto.getEmployeeNumber());
        employee.setEmployeeBirthdate(dto.getEmployeeBirthdate());
        employee.setEmployeeBirthplace(dto.getEmployeeBirthplace());
        employee.setEmployeeEmail(dto.getEmployeeEmail());
        employee.setEmployeePhoneNumber(dto.getEmployeePhoneNumber());
        employee.setUsers(newUsers);
        employee.setDivision(divisionRepository.findById(dto.getDivisionId()).orElse(null));
        return employeeRepository.save(employee);
    }
}
