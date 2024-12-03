package com.project.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.module.Activity;
import com.project.repository.ActivityJpaRepository;

@Service
public class ActivityJpaService {
    @Autowired
    private ActivityJpaRepository activityRepository;

    public List<Activity> getRecentActivities() {
        return activityRepository.findTop10ByOrderByActivityIdDesc();
    }

    public void logActivity(String activityType, String user) {
        Activity activity = new Activity();
        activity.setActivityDescription(activityType);
        activity.setDate(LocalDate.now());
        activity.setUser(user);
        activityRepository.save(activity);
    }
}
