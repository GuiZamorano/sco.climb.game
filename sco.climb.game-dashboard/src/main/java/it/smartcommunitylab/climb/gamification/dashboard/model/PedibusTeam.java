package it.smartcommunitylab.climb.gamification.dashboard.model;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.PlayerStateDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PedibusTeam extends BaseObject implements Gamified {

	private String classRoom;
	private String gameId;
	private List<String> childrenId;
	private Map<String, Collection> badges;
	private Double score;
	private PlayerStateDTO playerState;
	private PedibusItineraryLeg previousLeg;
	private PedibusItineraryLeg currentLeg;
	
	private Double scoreToNext;

	public PedibusTeam() {
	}

	private Double scoreToEnd;

	public PedibusTeam(String classRoom, String gameId, List<String> childrenId, Double score) {
		this.classRoom = classRoom;
		this.gameId = gameId;
		this.childrenId = childrenId;
		this.score = score;
	}

	public String getClassRoom() {
		return classRoom;
	}

	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public List<String> getChildrenId() {
		return childrenId;
	}

	public void setChildrenId(List<String> childrenId) {
		this.childrenId = childrenId;
	}

	public Map<String, Collection> getBadges() {
		return badges;
	}

	public void setBadges(Map<String, Collection> badges) {
		this.badges = badges;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public PedibusItineraryLeg getPreviousLeg() {
		return previousLeg;
	}

	public void setPreviousLeg(PedibusItineraryLeg previousLeg) {
		this.previousLeg = previousLeg;
	}

	public PedibusItineraryLeg getCurrentLeg() {
		return currentLeg;
	}

	public void setCurrentLeg(PedibusItineraryLeg currentLeg) {
		this.currentLeg = currentLeg;
	}

	public Double getScoreToNext() {
		return scoreToNext;
	}

	public void setScoreToNext(Double scoreToNextLeg) {
		this.scoreToNext = scoreToNextLeg;
	}

	public Double getScoreToEnd() {
		return scoreToEnd;
	}

	public void setScoreToEnd(Double scoreToEnd) {
		this.scoreToEnd = scoreToEnd;
	}

	@Override
	public void setGameStatus(PlayerStateDTO playerState) {
		this.playerState = playerState;
	}

	@Override
	public PlayerStateDTO getGameStatus() {
		return this.playerState;
	}

}
