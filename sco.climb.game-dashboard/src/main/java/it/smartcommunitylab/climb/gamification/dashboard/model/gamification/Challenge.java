package it.smartcommunitylab.climb.gamification.dashboard.model.gamification;

import java.util.Set;

public class Challenge {
	private String gameId;
	private String playerId;
	private Set<Object> state;
	
	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public Set<Object> getState() {
		return state;
	}

	public void setState(Set<Object> state) {
		this.state = state;
	}

}
