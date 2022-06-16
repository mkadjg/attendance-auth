package com.absence.auth.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobTitleResponsePayload {
    private String jobTitleId;
    private String jobTitleName;
    private DivisionResponsePayload division;
}
