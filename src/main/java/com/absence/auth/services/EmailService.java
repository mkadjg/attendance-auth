package com.absence.auth.services;

import com.absence.auth.payloads.EmailPayload;

import java.util.Map;

public interface EmailService {
    void sendEmail(EmailPayload emailPayload) throws Exception;
    String generateHtmlEmailBody(String htmlFilenamePath, Map<String, Object> variables);
}
