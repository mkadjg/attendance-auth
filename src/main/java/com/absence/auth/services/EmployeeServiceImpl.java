package com.absence.auth.services;

import com.absence.auth.attendance.models.Employee;
import com.absence.auth.attendance.models.EmployeeLeave;
import com.absence.auth.attendance.models.LeaveType;
import com.absence.auth.attendance.repositories.*;
import com.absence.auth.dtos.RegisterEmployeeRequestDto;
import com.absence.auth.enums.LeaveTypeEnum;
import com.absence.auth.models.UserRole;
import com.absence.auth.models.Users;
import com.absence.auth.payloads.EmailPayload;
import com.absence.auth.payloads.EmployeeRequestPayload;
import com.absence.auth.payloads.EmployeeResponsePayload;
import com.absence.auth.repositories.RoleRepository;
import com.absence.auth.repositories.UserRoleRepository;
import com.absence.auth.repositories.UsersRepository;
import com.absence.auth.utilities.DefaultPasswordGeneratorUtil;
import com.absence.auth.utilities.EmployeeNumberGeneratorUtil;
import com.absence.auth.utilities.PasswordHashUtil;
import com.google.gson.Gson;
import com.netflix.discovery.converters.Auto;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    JobTitleRepository jobTitleRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    LeaveTypeRepository leaveTypeRepository;

    @Autowired
    EmployeeLeaveRepository employeeLeaveRepository;

    @Autowired
    EmailService emailService;

    @Value("${login.url}")
    String loginUrl;

    @Value("${absence.base.url}")
    String absenceBaseUrl;

    private static final String HRD_DIV_ID = "fcb90b90-b56b-4a98-b778-83848096cbc6";
    private static final String HRD_ROLE_ID = "b618e0ef-9932-40bc-9f7c-174cf2b6aebe";
    private static final String EMPLOYEE_ROLE_ID = "afcb7c89-4638-4ae5-9329-47bfbc5b00b9";
    private static final String SPV_ROLE_ID = "e12ce118-ca9d-4da5-8d74-8b7f2696f57a";

    @Override
    public Employee register(RegisterEmployeeRequestDto dto, String userAuditId) {
        Users users = new Users();
        users.setUsername(dto.getEmployeeEmail());
        users.setStatus(1);
        users.setCreatedBy(userAuditId);
        Users newUsers = usersRepository.save(users);

        UserRole employeeRole = new UserRole();
        employeeRole.setUsers(newUsers);
        employeeRole.setCreatedBy(userAuditId);
        employeeRole.setRole(roleRepository.findById(EMPLOYEE_ROLE_ID).orElse(null));
        userRoleRepository.save(employeeRole);

        if (dto.getDivisionId().equals(HRD_DIV_ID)) {
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

        Employee employee = new Employee();
        employee.setEmployeeName(dto.getEmployeeName());
        employee.setEmployeeAddress(dto.getEmployeeAddress());
        employee.setEmployeeNumber(EmployeeNumberGeneratorUtil.generate());
        employee.setEmployeeBirthdate(dto.getEmployeeBirthdate());
        employee.setEmployeeBirthplace(dto.getEmployeeBirthplace());
        employee.setEmployeeEmail(dto.getEmployeeEmail());
        employee.setEmployeePhoneNumber(dto.getEmployeePhoneNumber());
        employee.setEmployeeGender(dto.getEmployeeGender());
        employee.setIsSupervisor(dto.getIsSupervisor());
        jobTitleRepository.findById(dto.getJobTitleId()).ifPresent(employee::setJobTitle);
        employee.setUserId(newUsers.getUserId());

        Employee result = employeeRepository.save(employee);

        // cuti tahunan
        EmployeeLeave yearLeave = new EmployeeLeave();
        yearLeave.setEmployee(result);
        LeaveType yearleaveType = leaveTypeRepository.findById(LeaveTypeEnum.CUTI_TAHUNAN.label)
                .orElse(null);
        if (yearleaveType != null) {
            yearLeave.setLeaveType(yearleaveType);
            yearLeave.setAvailable(yearleaveType.getDefaultValue());
            yearLeave.setUsed(0);
        }
        employeeLeaveRepository.save(yearLeave);

        // cuti lintas tahun
        EmployeeLeave crossYearLeave = new EmployeeLeave();
        crossYearLeave.setEmployee(result);
        LeaveType crossYearLeaveType = leaveTypeRepository.findById(LeaveTypeEnum.CUTI_LINTAS_TAHUN.label)
                .orElse(null);
        if (crossYearLeaveType != null) {
            crossYearLeave.setLeaveType(crossYearLeaveType);
            crossYearLeave.setAvailable(crossYearLeaveType.getDefaultValue());
            crossYearLeave.setUsed(0);
        }
        employeeLeaveRepository.save(crossYearLeave);

        return result;
    }

    @Override
    public Boolean sendEmailUserRegistration(String userId) {
        try {
            Users users = usersRepository.findById(userId).orElse(null);
            String defaultPassword = DefaultPasswordGeneratorUtil.generateRandomPassword();
            assert users != null;
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

    @Override
    public EmployeeResponsePayload findEmployeeByUserId(String userId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = absenceBaseUrl + "/employee/find-by-user-id/" + userId;
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return toEmployeeResponsePayload(response.getBody());
        } else {
            return null;
        }
    }

    private EmployeeResponsePayload toEmployeeResponsePayload(String root) {
        JSONObject jsonRoot = new JSONObject(root);
        JSONObject jsonBody = new JSONObject(jsonRoot.get("data").toString());
        Gson gson = new Gson();
        return gson.fromJson(jsonBody.toString(), EmployeeResponsePayload.class);
    }
}
