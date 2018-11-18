package it.smartcommunitylab.climb.gamification.dashboard.model;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CalendarDay extends BaseObject {
	private String gameId;
	private Date day;
	private String classRoom;
	private String meteo;
	private Map<String, String> modeMap = new HashMap<String, String>();
	private boolean closed = false;
	
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public Date getDay() {
		return day;
	}
	public void setDay(Date day) {
		this.day = day;
	}
	public String getMeteo() {
		return meteo;
	}
	public void setMeteo(String meteo) {
		this.meteo = meteo;
	}
	public Map<String, String> getModeMap() {
		return modeMap;
	}
	public void setModeMap(Map<String, String> modeMap) {
		this.modeMap = modeMap;
	}
	public String getClassRoom() {
		return classRoom;
	}
	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}
	public boolean isClosed() {
		return closed;
	}
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	
}
