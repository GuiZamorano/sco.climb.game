package it.smartcommunitylab.climb.gamification.dashboard.model;


import it.smartcommunitylab.climb.gamification.dashboard.model.Activity;

import java.util.ArrayList;
import java.util.List;

public class Discipline {
    private boolean active;
    public enum Subject {CS, STEM, PE}
    private Subject subject;
    private List<Activity> activities = new ArrayList<Activity>();

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
