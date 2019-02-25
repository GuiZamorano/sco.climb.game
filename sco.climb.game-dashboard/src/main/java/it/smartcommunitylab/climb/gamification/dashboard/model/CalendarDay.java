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
	private String meteo;

	private Integer duration;
	private Double distance;

	private int EActive=0;
	private int VActive=0;
	private int FActive=0;
	private int IActive=0;
	private Map<String, String> modeMap = new HashMap<String, String>();
	private String activityType;
	private Map<String, Double> activityMap = new HashMap<String, Double>();
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
	public String getActivityType() {return activityType;}
	public void setActivityType(String activityType) {this.activityType = activityType;}
	public Map<String, Double> getActivityMap() {return activityMap;}
	public void setActivityMap(Map<String, Double> activityMap) {this.activityMap = activityMap;}
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

	public Integer getDuration() {return duration;}

	public void setDuration(Integer duration) {this.duration = duration;}

	public Double getDistance() {return distance;}

	public void setDistance(Double distance) {this.distance = distance;}

	public int getEActive() {
		return EActive;
	}

	public void setEActive(int EActive) {
		this.EActive = EActive;
	}

	public int getVActive() {
		return VActive;
	}

	public void setVActive(int VActive) {
		this.VActive = VActive;
	}

	public int getFActive() {
		return FActive;
	}

	public void setFActive(int FActive) {
		this.FActive = FActive;
	}

	public int getIActive() {
		return IActive;
	}

	public void setIActive(int IActive) {
		this.IActive = IActive;
	}
}
