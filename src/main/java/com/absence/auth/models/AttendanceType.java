package com.absence.auth.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attendance_type")
@Entity
public class AttendanceType extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "attendance_type_id", nullable = false, unique = true)
    private String attendanceTypeId;

    @Column(name = "attendance_type_name")
    private String attendanceTypeName;

    @Column(name = "attendance_type_desc")
    private String attendanceTypeDesc;

}
