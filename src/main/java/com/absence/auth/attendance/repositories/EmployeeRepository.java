package com.absence.auth.attendance.repositories;

import com.absence.auth.attendance.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

}
