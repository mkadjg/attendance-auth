package com.absence.auth.repositories;

import com.absence.auth.models.Division;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DivisionRepository extends JpaRepository<Division, String> {
}
