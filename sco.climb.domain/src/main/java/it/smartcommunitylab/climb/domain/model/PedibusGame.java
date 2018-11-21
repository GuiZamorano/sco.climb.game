package it.smartcommunitylab.climb.domain.model;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedibusGame extends BaseObject {
	
	private String instituteId;
	private String schoolId;
	private String schoolName;
	private List<String> classRooms;
	private String gameId;
	private String gameName;
	private String gameDescription;
	private String gameOwner;
	private Date from;
	private Date to;
	private String globalTeam;
	private String fromHour;
	private String toHour;
	private String lastDaySeen;
	private boolean lateSchedule;
	private boolean usingPedibusData; 
	private boolean deployed;
	private String confTemplateId;
	private Map<String, String> params = new HashMap<>();
	private String shortName;
	
	private Map<String, Boolean> pollingFlagMap = new HashMap<String, Boolean>();
	
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
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
	public boolean isLateSchedule() {
		return lateSchedule;
	}
	public void setLateSchedule(boolean lateSchedule) {
		this.lateSchedule = lateSchedule;
	}
	public boolean isUsingPedibusData() {
		return usingPedibusData;
	}
	public void setUsingPedibusData(boolean usingPedibusData) {
		this.usingPedibusData = usingPedibusData;
	}
	public boolean isDeployed() {
		return deployed;
	}
	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
	}
	public String getConfTemplateId() {
		return confTemplateId;
	}
	public void setConfTemplateId(String confTemplateId) {
		this.confTemplateId = confTemplateId;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
}
