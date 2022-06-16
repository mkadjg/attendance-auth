package com.absence.auth.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DivisionResponsePayload {
    private String divisionId;
    private String divisionName;
    private String divisionDesc;
}
