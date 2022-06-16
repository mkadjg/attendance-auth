package com.absence.auth.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class EmployeeResponsePayload extends BasePayload {
    private String employeeId;
    private String employeeName;
    private String employeeNumber;
    private String employeeAddress;
    private Date employeeBirthdate;
    private String employeeBirthplace;
    private String employeeEmail;
    private String employeePhoneNumber;
    private Integer employeeGender;
    private Integer isSupervisor;
    private String employeePhoto;
    private String userId;
    private JobTitleResponsePayload jobTitle;
}
