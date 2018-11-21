package it.smartcommunitylab.climb.domain.model.gamification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;


public class PedibusGameConfTemplate extends BaseObject {
	
	private String name;
	private String version;
	private String description;
	private List<String> ruleFileTemplates = new ArrayList<>();
	private List<String> actions = new ArrayList<>();
	private List<String> badgeCollections = new ArrayList<>();
	private Map<String, List<String>> challengeModels = new HashMap<>();
	private Map<String, List<String>> points = new HashMap<>();
	private Map<String, Map<String, String>> tasks = new HashMap<>();
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getRuleFileTemplates() {
		return ruleFileTemplates;
	}
	public void setRuleFileTemplates(List<String> ruleFileTemplates) {
		this.ruleFileTemplates = ruleFileTemplates;
	}
	public List<String> getActions() {
		return actions;
	}
	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	public List<String> getBadgeCollections() {
		return badgeCollections;
	}
	public void setBadgeCollections(List<String> badgeCollections) {
		this.badgeCollections = badgeCollections;
	}
	public Map<String, List<String>> getChallengeModels() {
		return challengeModels;
	}
	public void setChallengeModels(Map<String, List<String>> challengeModels) {
		this.challengeModels = challengeModels;
	}
	public Map<String, List<String>> getPoints() {
		return points;
	}
	public void setPoints(Map<String, List<String>> points) {
		this.points = points;
	}
	public Map<String, Map<String, String>> getTasks() {
		return tasks;
	}
	public void setTasks(Map<String, Map<String, String>> tasks) {
		this.tasks = tasks;
	}

}
