package com.absence.auth.repositories;

import com.absence.auth.models.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DivisionRepository extends JpaRepository<Division, String> {
}
