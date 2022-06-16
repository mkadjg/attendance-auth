package com.absence.auth.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasePayload {
    private Date createdDate;
    private String createdBy;
    private Date updatedDate;
    private String updatedBy;
}
