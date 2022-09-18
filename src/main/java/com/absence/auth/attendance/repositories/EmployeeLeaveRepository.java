package com.absence.auth.attendance.repositories;

import com.absence.auth.attendance.models.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, String> {
}