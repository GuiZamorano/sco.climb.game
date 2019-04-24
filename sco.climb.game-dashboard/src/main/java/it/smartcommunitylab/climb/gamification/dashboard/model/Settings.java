package it.smartcommunitylab.climb.gamification.dashboard.model;

import it.smartcommunitylab.climb.gamification.dashboard.model.events.BaseObject;

import java.util.ArrayList;
import java.util.List;

public class Settings extends BaseObject {

    List<Activity.Subject> selectedSubjects;
    List<Integer> selectedGradeLevels;
    List<String> selectedTeks;

    public Settings() {
        selectedSubjects = new ArrayList<>();
        selectedGradeLevels = new ArrayList<>();
        selectedTeks = new ArrayList<>();
    }

    public void saveSubjects(List<Activity.Subject> newSelectedSubjects) {
        selectedSubjects = newSelectedSubjects;
    }

    public List<Activity.Subject> getSubjects() {
        return selectedSubjects;
    }

    public void saveGradeLevels(List<Integer> newSelectedGradeLevels) {
        selectedGradeLevels = newSelectedGradeLevels;
    }

    public List<Integer> getGradeLevels() {
        return selectedGradeLevels;
    }

    public void saveTeks(List<String> newSelectedTeks) {
        selectedTeks = newSelectedTeks;
    }

    public List<String> getTeks() {
        return selectedTeks;
    }
}
