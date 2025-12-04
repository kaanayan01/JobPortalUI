package com.jobportal.service;

import com.jobportal.model.SavedJob;
import com.jobportal.repository.SavedJobRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SavedJobService {

    private final SavedJobRepository repository;

    public SavedJobService(SavedJobRepository repository) {
        this.repository = repository;
    }

    public SavedJob save(SavedJob savedJob) {
        if (repository.existsByJobSeekerIdAndJobId(savedJob.getJobSeekerId(), savedJob.getJobId())) {
            throw new RuntimeException("Job already saved by this user");
        }
        if (savedJob.getSavedDate() == null) savedJob.setSavedDate(LocalDateTime.now());
        return repository.save(savedJob);
    }

    public SavedJob findById(int id) { return repository.findById(id); }

    public List<SavedJob> findAll() { return repository.findAll(); }

    public List<SavedJob> findByJobSeekerId(int jobSeekerId) { return repository.findByJobSeekerId(jobSeekerId); }

    public List<SavedJob> findByJobId(int jobId) { return repository.findByJobId(jobId); }

    public void delete(int id) { repository.delete(id); }
}
