package com.absence.auth.services;

import com.absence.auth.dtos.RegisterEmployeeRequestDto;
import com.absence.auth.models.Employee;
import com.absence.auth.models.UserRole;
import com.absence.auth.models.Users;
import com.absence.auth.payloads.EmailPayload;
import com.absence.auth.repositories.*;
import com.absence.auth.utilities.DefaultPasswordGeneratorUtil;
import com.absence.auth.utilities.PasswordHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    EmailService emailService;

    @Value("${login.url}")
    String loginUrl;

    private static final String HRD_DIV_ID = "fcb90b90-b56b-4a98-b778-83848096cbc6";
    private static final String HRD_ROLE_ID = "b618e0ef-9932-40bc-9f7c-174cf2b6aebe";
    private static final String EMPLOYEE_ROLE_ID = "afcb7c89-4638-4ae5-9329-47bfbc5b00b9";
    private static final String SPV_ROLE_ID = "e12ce118-ca9d-4da5-8d74-8b7f2696f57a";

    @Override
    public Object register(RegisterEmployeeRequestDto dto, String userAuditId) {
        Users users = new Users();
        users.setUsername(dto.getEmployeeEmail());
        users.setStatus(1);
        users.setCreatedBy(userAuditId);
        Users newUsers = usersRepository.save(users);

        Employee employee = new Employee();
        employee.setEmployeeName(dto.getEmployeeName());
        employee.setEmployeeAddress(dto.getEmployeeAddress());
        employee.setEmployeeNumber(dto.getEmployeeNumber());
        employee.setEmployeeBirthdate(dto.getEmployeeBirthdate());
        employee.setEmployeeBirthplace(dto.getEmployeeBirthplace());
        employee.setEmployeeEmail(dto.getEmployeeEmail());
        employee.setEmployeePhoneNumber(dto.getEmployeePhoneNumber());
        employee.setEmployeeGender(dto.getEmployeeGender());
        employee.setIsSupervisor(dto.getIsSupervisor());
        employee.setUsers(newUsers);
        employee.setCreatedBy(userAuditId);
        employee.setDivision(divisionRepository.findById(dto.getDivisionId()).orElse(null));

        UserRole employeeRole = new UserRole();
        employeeRole.setUsers(newUsers);
        employeeRole.setCreatedBy(userAuditId);
        employeeRole.setRole(roleRepository.findById(EMPLOYEE_ROLE_ID).orElse(null));
        userRoleRepository.save(employeeRole);

        if (employee.getDivision().getDivisionId().equals(HRD_DIV_ID)) {
            UserRole hrdRole = new UserRole();
            hrdRole.setUsers(newUsers);
            hrdRole.setCreatedBy(userAuditId);
            hrdRole.setRole(roleRepository.findById(HRD_ROLE_ID).orElse(null));
            userRoleRepository.save(hrdRole);
        }

        if (dto.getIsSupervisor() == 1) {
            UserRole supervisorRole = new UserRole();
            supervisorRole.setUsers(newUsers);
            supervisorRole.setCreatedBy(userAuditId);
            supervisorRole.setRole(roleRepository.findById(SPV_ROLE_ID).orElse(null));
            userRoleRepository.save(supervisorRole);
        }

        return employeeRepository.save(employee);
    }

    @Override
    public Boolean sendEmailUserRegistration(Users users) {
        try {
            String defaultPassword = DefaultPasswordGeneratorUtil.generateRandomPassword();
            users.setPassword(PasswordHashUtil.generate(defaultPassword));
            usersRepository.save(users);

            Map<String, Object> variables = new HashMap<>();
            variables.put("username", users.getUsername());
            variables.put("password", defaultPassword);
            variables.put("loginUrl", loginUrl);

            String emailBody = emailService.generateHtmlEmailBody("user-activation.html", variables);
            EmailPayload emailPayload = new EmailPayload();
            emailPayload.setSubject("New Employee Registration");
            emailPayload.setReceiver(users.getUsername());
            emailPayload.setEmailBody(emailBody);
            emailService.sendEmail(emailPayload);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
