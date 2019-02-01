package it.smartcommunitylab.climb.gamification.dashboard.model;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CalendarDay extends BaseObject {
	private String gameId;
	private Date day;
	private String name;
	private Integer index;
	private String classRoom;
	private String weather;
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
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
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


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
}
