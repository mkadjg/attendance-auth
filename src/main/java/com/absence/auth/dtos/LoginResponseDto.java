package com.absence.auth.dtos;

import com.absence.auth.payloads.DivisionResponsePayload;
import com.absence.auth.payloads.JobTitleResponsePayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {
    private String userId;
    private String username;
    private String token;
    private String employeeId;
    private String employeeName;
    private String employeePhoto;
    private List<String> roleName;
    private JobTitleResponsePayload jobTitle;
}
