package it.smartcommunitylab.climb.domain.model.gamification;

import java.util.ArrayList;
import java.util.List;

public class ChallengeModel {
	private String name;
	private String gameId;
	private String id;
	private List<String> variables = new ArrayList<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getVariables() {
		return variables;
	}
	public void setVariables(List<String> variables) {
		this.variables = variables;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
