package com.absence.auth.repositories;

import com.absence.auth.models.UserLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, String> {

    @Query("select ulh from UserLoginHistory ulh where ulh.userId =:userId and ulh.loginDate > :loginStart order by ulh.loginDate")
    List<UserLoginHistory> findFailedUserLogin(String userId, Date loginStart);

}
