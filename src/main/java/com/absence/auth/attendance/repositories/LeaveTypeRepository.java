package com.absence.auth.attendance.repositories;

import com.absence.auth.attendance.models.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, String> {
}