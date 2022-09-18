package com.absence.auth.attendance.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee")
@Entity
public class Employee extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "employee_id", nullable = false, unique = true)
    private String employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "employee_number")
    private String employeeNumber;

    @Type(type = "text")
    @Column(name = "employee_address")
    private String employeeAddress;

    @Column(name = "employee_birthdate")
    private Date employeeBirthdate;

    @Column(name = "employee_birthplace")
    private String employeeBirthplace;

    @Column(name = "employee_email")
    private String employeeEmail;

    @Column(name = "employee_phone_number")
    private String employeePhoneNumber;

    @Column(name = "employee_gender")
    private Integer employeeGender;

    @Column(name = "is_supervisor")
    private Integer isSupervisor;

    @Column(name = "userId")
    private String userId;

    @Lob
    @Column(name = "employee_photo", columnDefinition = "BLOB")
    private byte[] employeePhoto;

    @ManyToOne
    @JoinColumn(name = "job_title_id", referencedColumnName = "job_title_id")
    private JobTitle jobTitle;
}
