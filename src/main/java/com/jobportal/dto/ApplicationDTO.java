package com.jobportal.dto;


import com.jobportal.model.Application;
import com.jobportal.model.JobSeeker;
import com.jobportal.model.User;

public class ApplicationDTO {

    private Application application;
    private JobSeeker jobSeeker;
    private User user;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public JobSeeker getJobSeeker() {
        return jobSeeker;
    }

    public void setJobSeeker(JobSeeker jobSeeker) {
        this.jobSeeker = jobSeeker;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
