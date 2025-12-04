package com.jobportal.service;

import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.JobPost;
import com.jobportal.model.JobSeeker;
import com.jobportal.model.SkillMatch;
import com.jobportal.repository.SkillMatchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SkillMatchService {

    private final SkillMatchRepository repository;
    
    @Autowired
    private JobSeekerService jobSeekerService;
    
    @Autowired
    private JobPostService jobPostService;
    

    public SkillMatchService(SkillMatchRepository repository) {
        this.repository = repository;
    }

    public SkillMatch create(SkillMatch skillMatch) {
    	JobSeeker jobSeeker = jobSeekerService.findById(skillMatch.getJobSeekerId());
    	JobPost jobPost = jobPostService.findById(skillMatch.getJobId());
    	if(jobSeeker == null || jobPost == null) {
    		throw new ResourceNotFoundException("Either job or seeker doesn't exist");
    	}
    	skillMatch.setMatchPercentage(calculateMatchPercentage(jobPost.getRequirements(), jobSeeker.getSkills()));
    	skillMatch.setCalculatedDate(LocalDateTime.now());
    	if(getByJobIdAndSeekerId(skillMatch.getJobId(), skillMatch.getJobSeekerId()) == null) {
	        return repository.save(skillMatch);
    	}else {
    		return update(skillMatch);
    	}
    }

    public SkillMatch getById(int id) {
        return repository.findById(id);
    }

    public List<SkillMatch> getByJobSeekerId(int jobSeekerId) {
        return repository.findByJobSeekerId(jobSeekerId);
    }

    public List<SkillMatch> getByJobId(int jobId) {
        return repository.findByJobId(jobId);
    }
    
    public SkillMatch getByJobIdAndSeekerId(int jobId, int jobSeekerId) {
    	try {
        return repository.findByJobIdAndSeekerId(jobId, jobSeekerId);
    	}catch(ResourceNotFoundException ex) {
    		return null;
    	}
    }

    public SkillMatch update(SkillMatch skillMatch) {
        repository.update(skillMatch);
        return skillMatch;
    }

    public void delete(int matchId) {
        repository.delete(matchId);
    }
    
    public double calculateMatchPercentage(List<String> requirements, List<String> seekerSkills) {
 
        Set<String> setA = new HashSet<>(requirements);

        long matches = seekerSkills.stream()
                        .filter(setA::contains)
                        .count();

        if (seekerSkills.isEmpty()) {
            return 0.0;
        }
        
        double percentage = (matches * 100.0) / seekerSkills.size();
        return Math.round(percentage * 100.0) / 100.0;
        
    }

	public List<SkillMatch> getAll() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}
}
