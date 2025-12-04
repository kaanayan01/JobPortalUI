package com.jobportal.repository;

import com.jobportal.model.SavedJob;
import java.util.List;

public interface SavedJobRepository {
    SavedJob save(SavedJob savedJob);
    SavedJob findById(int id);
    List<SavedJob> findAll();
    List<SavedJob> findByJobSeekerId(int jobSeekerId);
    List<SavedJob> findByJobId(int jobId);
    void delete(int id);
    boolean existsByJobSeekerIdAndJobId(int jobSeekerId, int jobId);
}
