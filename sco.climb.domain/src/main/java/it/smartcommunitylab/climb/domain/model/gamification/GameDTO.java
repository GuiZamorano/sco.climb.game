package it.smartcommunitylab.climb.domain.model.gamification;

import java.util.ArrayList;
import java.util.List;

public class GameDTO extends GameConcept {
	private String name;
	private List<String> actions = new ArrayList<>();
	private List<BadgeCollectionConcept> badgeCollectionConcept = new ArrayList<>();
	private List<PointConcept> pointConcept = new ArrayList<>();
	private List<RuleDTO> rules = new ArrayList<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getActions() {
		return actions;
	}
	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	public List<BadgeCollectionConcept> getBadgeCollectionConcept() {
		return badgeCollectionConcept;
	}
	public void setBadgeCollectionConcept(List<BadgeCollectionConcept> badgeCollectionConcept) {
		this.badgeCollectionConcept = badgeCollectionConcept;
	}
	public List<PointConcept> getPointConcept() {
		return pointConcept;
	}
	public void setPointConcept(List<PointConcept> pointConcept) {
		this.pointConcept = pointConcept;
	}
	public List<RuleDTO> getRules() {
		return rules;
	}
	public void setRules(List<RuleDTO> rules) {
		this.rules = rules;
	}
}
