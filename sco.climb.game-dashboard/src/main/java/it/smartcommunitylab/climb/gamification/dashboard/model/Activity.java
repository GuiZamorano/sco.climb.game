package it.smartcommunitylab.climb.gamification.dashboard.model;

import it.smartcommunitylab.climb.gamification.dashboard.model.Link;
import it.smartcommunitylab.climb.gamification.dashboard.model.events.BaseObject;

import java.util.ArrayList;
import java.util.List;

public class Activity extends BaseObject{

    private String gameId;
    private String activityId;
    private boolean active;
    private int gradeLevel;
    private String teks;
    List<Link> materials = new ArrayList<Link>();
    private String description;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String legId) {
        this.activityId = legId;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getTeks() {
        return teks;
    }

    public void setTeks(String teks) {
        this.teks = teks;
    }

    public List<Link> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Link> materials) {
        this.materials = materials;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
