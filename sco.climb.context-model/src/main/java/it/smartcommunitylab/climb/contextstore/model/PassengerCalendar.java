package it.smartcommunitylab.climb.contextstore.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PassengerCalendar extends BaseObject {
	private Date date;
	private String routeId;
	private String stopId;
	private List<String> absenteeList = new ArrayList<String>();
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	public String getStopId() {
		return stopId;
	}
	public void setStopId(String stopId) {
		this.stopId = stopId;
	}
	public List<String> getAbsenteeList() {
		return absenteeList;
	}
	public void setAbsenteeList(List<String> absenteeList) {
		this.absenteeList = absenteeList;
	}
}
