package com.absence.auth.attendance.repositories;

import com.absence.auth.attendance.models.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobTitleRepository extends JpaRepository<JobTitle, String> {

}