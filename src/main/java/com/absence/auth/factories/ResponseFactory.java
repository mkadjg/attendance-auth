package com.absence.auth.factories;

import com.absence.auth.dtos.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseFactory {

    static ObjectMapper objectMapper = new ObjectMapper();

    public static void sendAccessDeniedErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(ResponseDto.builder()
                .code(HttpStatus.FORBIDDEN.toString())
                .status("error")
                .message("Access Denied!")
                .build()));
    }

    public static void sendUnAuthorizeErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(ResponseDto.builder()
                .code(HttpStatus.UNAUTHORIZED.toString())
                .status("error")
                .message("User does not have access!!")
                .build()));
    }
}
