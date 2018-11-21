package it.smartcommunitylab.climb.domain.model.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitoringStats {
	private Double gameScore;
	private Double maxGameScore;
	private Map<String, Double> scoreModeMap = new HashMap<String, Double>();
	private List<MonitoringItinerary> itineraries = new ArrayList<MonitoringItinerary>();
	private Map<String, MonitoringPlay>  plays = new HashMap<String, MonitoringPlay>();
	private List<MonitoringChallenge> challenges = new ArrayList<MonitoringChallenge>();
	
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
	public List<MonitoringItinerary> getItineraries() {
		return itineraries;
	}
	public void setItineraries(List<MonitoringItinerary> itineraries) {
		this.itineraries = itineraries;
	}
	public Map<String, MonitoringPlay> getPlays() {
		return plays;
	}
	public void setPlays(Map<String, MonitoringPlay> plays) {
		this.plays = plays;
	}
	public List<MonitoringChallenge> getChallenges() {
		return challenges;
	}
	public void setChallenges(List<MonitoringChallenge> challenges) {
		this.challenges = challenges;
	}
}
