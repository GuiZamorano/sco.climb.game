package it.smartcommunitylab.climb.contextstore.model;

import java.util.ArrayList;
import java.util.List;

public class Stop extends BaseObject {
	private String name;
	private String routeId;
	private String departureTime;
	private boolean start;
	private boolean destination;
	private boolean school;
	private double[] geocoding;
	private double distance;
	private String wsnId;
	private int position;
	private List<String> passengerList = new ArrayList<String>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	public boolean isStart() {
		return start;
	}
	public void setStart(boolean start) {
		this.start = start;
	}
	public boolean isDestination() {
		return destination;
	}
	public void setDestination(boolean destination) {
		this.destination = destination;
	}
	public boolean isSchool() {
		return school;
	}
	public void setSchool(boolean school) {
		this.school = school;
	}
	public List<String> getPassengerList() {
		return passengerList;
	}
	public void setPassengerList(List<String> passengerList) {
		this.passengerList = passengerList;
	}
	public String getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}
	public double[] getGeocoding() {
		return geocoding;
	}
	public void setGeocoding(double[] geocoding) {
		this.geocoding = geocoding;
	}
	public String getWsnId() {
		return wsnId;
	}
	public void setWsnId(String wsnId) {
		this.wsnId = wsnId;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
}
