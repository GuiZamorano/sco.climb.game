package it.smartcommunitylab.climb.gamification.dashboard.model.gamification;

import java.util.HashMap;
import java.util.Map;

public class Notification {
	private String gameId;
	private String playerId;
	private long timestamp;
	private String badge;
	private String collectionName;
	private String key;
	private Map<String, Object> data = new HashMap<String, Object>();	
	
	public Notification() {
		timestamp = System.currentTimeMillis();
	}

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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getBadge() {
		return badge;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
