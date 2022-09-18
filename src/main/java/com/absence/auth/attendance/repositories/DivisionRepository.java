package com.absence.auth.attendance.repositories;

import com.absence.auth.attendance.models.Division;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DivisionRepository extends JpaRepository<Division, String> {
}
