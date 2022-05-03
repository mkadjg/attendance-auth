package com.absence.auth.services;

import com.absence.auth.models.Users;

public interface ForgotPasswordService {
    Boolean sendEmailForgotPassword(Users users);

}
