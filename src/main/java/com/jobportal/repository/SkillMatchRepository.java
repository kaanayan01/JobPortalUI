package com.jobportal.repository;

import com.jobportal.model.SkillMatch;

import java.util.List;

public interface SkillMatchRepository {
    SkillMatch save(SkillMatch skillMatch);
    SkillMatch findById(int matchId);
    List<SkillMatch> findAll();
    List<SkillMatch> findByJobSeekerId(int jobSeekerId);
    List<SkillMatch> findByJobId(int jobId);
    void update(SkillMatch skillMatch);
    void delete(int matchId);
	SkillMatch findByJobIdAndSeekerId(int jobId, int jobSeekerId);
	
}
