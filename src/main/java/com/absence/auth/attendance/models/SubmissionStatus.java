package com.absence.auth.attendance.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "submission_status")
@Entity
public class SubmissionStatus extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "submission_status_id", nullable = false, unique = true)
    private String submissionStatusId;

    @Column(name = "submission_status_name")
    private String submissionStatusName;

    @Column(name = "submission_status_desc")
    private String submissionStatusDesc;

}
