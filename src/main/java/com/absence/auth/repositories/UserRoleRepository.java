package com.absence.auth.repositories;

import com.absence.auth.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    @Query("select ur from UserRole ur where ur.users.userId =:userId")
    List<UserRole> findByUserId(String userId);

}
