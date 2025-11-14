package com.example.scrapingtings.repository;

import com.example.scrapingtings.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer> {

    // Returns a list of all job applications for a given userId and jobId
    List<JobApplication> findAllByUserIdAndJobId(int userId, int jobId);

}
