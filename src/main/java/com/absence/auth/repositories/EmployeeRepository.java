package com.absence.auth.repositories;

import com.absence.auth.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Query("select e from Employee e where e.users.userId =:userId")
    Optional<Employee> findByUserId(String userId);
}
