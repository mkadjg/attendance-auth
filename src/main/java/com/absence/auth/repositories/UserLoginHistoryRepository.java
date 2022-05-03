package com.absence.auth.repositories;

import com.absence.auth.models.UserLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, String> {

    @Query("select ulh from UserLoginHistory ulh where ulh.userId =:userId and ulh.loginDate > :loginStart order by ulh.loginDate")
    List<UserLoginHistory> findFailedUserLogin(String userId, Date loginStart);

    @Query(value = "select * from User_Login_History " +
            "where user_id =:userId " +
            "order by login_date desc " +
            "limit 1", nativeQuery = true)
    Optional<UserLoginHistory> findUserLoginHistory(String userId);

}
