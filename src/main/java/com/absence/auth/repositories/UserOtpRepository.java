package com.absence.auth.repositories;

import com.absence.auth.models.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserOtpRepository extends JpaRepository<UserOtp, String> {

    @Query("select uo from UserOtp uo where users.userId =:userId")
    Optional<UserOtp> findUserOtpByUsersId(String userId);

}
