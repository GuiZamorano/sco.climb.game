package it.smartcommunitylab.climb.domain.model;

public class NodeState {
	private String passengerId;
	private String wsnNodeId;
	private Integer batteryLevel;
	private Integer batteryVoltage;
	private boolean manualCheckIn;
	
	public String getPassengerId() {
		return passengerId;
	}
	public void setPassengerId(String passengerId) {
		this.passengerId = passengerId;
	}
	public String getWsnNodeId() {
		return wsnNodeId;
	}
	public void setWsnNodeId(String wsnNodeId) {
		this.wsnNodeId = wsnNodeId;
	}
	public boolean isManualCheckIn() {
		return manualCheckIn;
	}
	public void setManualCheckIn(boolean manualCheckIn) {
		this.manualCheckIn = manualCheckIn;
	}
	public Integer getBatteryLevel() {
		return batteryLevel;
	}
	public void setBatteryLevel(Integer batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
	public Integer getBatteryVoltage() {
		return batteryVoltage;
	}
	public void setBatteryVoltage(Integer batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}
}
