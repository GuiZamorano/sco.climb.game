package it.smartcommunitylab.climb.gamification.dashboard.model;

import java.util.HashMap;
import java.util.Map;

public class Stats {
	private Double gameScore;
	private Double maxGameScore;
	private Map<String, Double> scoreModeMap = new HashMap<String, Double>();
	
	public Double getGameScore() {
		return gameScore;
	}
	public void setGameScore(Double gameScore) {
		this.gameScore = gameScore;
	}
	public Double getMaxGameScore() {
		return maxGameScore;
	}
	public void setMaxGameScore(Double maxGameScore) {
		this.maxGameScore = maxGameScore;
	}
	public Map<String, Double> getScoreModeMap() {
		return scoreModeMap;
	}
	public void setScoreModeMap(Map<String, Double> scoreModeMap) {
		this.scoreModeMap = scoreModeMap;
	} 
}
