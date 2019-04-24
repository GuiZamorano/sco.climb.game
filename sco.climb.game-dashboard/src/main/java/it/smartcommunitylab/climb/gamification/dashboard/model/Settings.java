package it.smartcommunitylab.climb.gamification.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    List<Activity.Subject> selectedSubjects;
    List<Integer> selectedGradeLevels;
    List<String> selectedTeks;

    public Settings() {
        selectedSubjects = new ArrayList<>();
        selectedGradeLevels = new ArrayList<>();
        selectedTeks = new ArrayList<>();
    }

    public void saveSubjects(ArrayList<Activity.Subject> newSelectedSubjects) {
        selectedSubjects = newSelectedSubjects;
    }

    public List<Activity.Subject> getSubjects() {
        return selectedSubjects;
    }

    public void saveGradeLevels(ArrayList<Integer> newSelectedGradeLevels) {
        selectedGradeLevels = newSelectedGradeLevels;
    }

    public List<Integer> getGradeLevels() {
        return selectedGradeLevels;
    }

    public void saveTeks(ArrayList<String> newSelectedTeks) {
        selectedTeks = newSelectedTeks;
    }

    public List<String> getTeks() {
        return selectedTeks;
    }
}
