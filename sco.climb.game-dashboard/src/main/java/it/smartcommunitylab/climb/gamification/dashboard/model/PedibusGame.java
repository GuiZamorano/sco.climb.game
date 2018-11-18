package it.smartcommunitylab.climb.gamification.dashboard.model;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedibusGame extends BaseObject {
	
	private String schoolId;
	private String schoolName;
	private List<String> classRooms;
	private String gameId;
	private String gameName;
	private String gameDescription;
	private String gameOwner;
	private Date from;
	private Date to;
	private String token;
	private String globalTeam;
	
	private String fromHour;
	private String toHour;
	private String lastDaySeen;
	
	private Map<String, Boolean> pollingFlagMap = new HashMap<String, Boolean>();

	public PedibusGame(String schoolId, String schoolName, List<String> classRooms, String gameId, String gameName, String gameDescription, String gameOwner, Date from, Date to){
		this.schoolId = schoolId;
		this.schoolName = schoolName;
		this.classRooms = classRooms;
		this.gameId = gameId;
		this.gameName = gameName;
		this.gameDescription = gameDescription;
		this.gameOwner = gameOwner;
		this.from = from;
		this.to = to;

	}

	public PedibusGame() {
	}

	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public String getGameOwner() {
		return gameOwner;
	}
	public void setGameOwner(String gameOwner) {
		this.gameOwner = gameOwner;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	public String getGameDescription() {
		return gameDescription;
	}
	public void setGameDescription(String gameDescription) {
		this.gameDescription = gameDescription;
	}
	public List<String> getClassRooms() {
		return classRooms;
	}
	public void setClassRooms(List<String> classRooms) {
		this.classRooms = classRooms;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getGlobalTeam() {
		return globalTeam;
	}
	public void setGlobalTeam(String globalTeam) {
		this.globalTeam = globalTeam;
	}
	public String getFromHour() {
		return fromHour;
	}
	public void setFromHour(String fromHour) {
		this.fromHour = fromHour;
	}
	public String getToHour() {
		return toHour;
	}
	public void setToHour(String toHour) {
		this.toHour = toHour;
	}
	public String getLastDaySeen() {
		return lastDaySeen;
	}
	public void setLastDaySeen(String lastDaySeen) {
		this.lastDaySeen = lastDaySeen;
	}
	public Map<String, Boolean> getPollingFlagMap() {
		return pollingFlagMap;
	}
	public void setPollingFlagMap(Map<String, Boolean> pollingFlagMap) {
		this.pollingFlagMap = pollingFlagMap;
	}

}
