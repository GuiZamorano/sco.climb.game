package it.smartcommunitylab.climb.gamification.dashboard.model;

import it.smartcommunitylab.climb.gamification.dashboard.model.events.BaseObject;
import it.smartcommunitylab.climb.gamification.dashboard.storage.RepositoryManager;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Settings extends BaseObject {

    private Map<String, Boolean> subjects;
    private Map<String, Boolean> gradeLevels;
    private Map<String, Boolean> teks;
    private String gameId;
    private String classRoom;

    public Settings() {
        subjects = new TreeMap<>();
        gradeLevels = new TreeMap<>();
        teks = new TreeMap<>();
    }

    public void setup(List<PedibusItineraryLeg> legs) {
        for (PedibusItineraryLeg leg: legs) {
            List<Activity> activities = leg.getActivities();

            for (Activity activity: activities) {
                subjects.put(activity.getSubject().toString(), Boolean.TRUE);
                gradeLevels.put(Integer.toString(activity.getGradeLevel()), Boolean.TRUE);
                teks.put(activity.getTeks(), Boolean.TRUE);
            }
        }
    }

    public void saveSubjects(Map<String, Boolean> subjects) {
        this.subjects = subjects;
    }
    public Map<String, Boolean> getSubjects() {
        return subjects;
    }

    public void saveGradeLevels(Map<String, Boolean> gradeLevels) {
        this.gradeLevels = gradeLevels;
    }
    public Map<String, Boolean> getGradeLevels() {
        return gradeLevels;
    }

    public void saveTeks(Map<String, Boolean> teks) {
        this.teks = teks;
    }
    public Map<String, Boolean> getTeks() {
        return teks;
    }

    public String getGameId() {
        return gameId;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getClassRoom() {
        return classRoom;
    }
    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }
}
