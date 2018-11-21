package it.smartcommunitylab.climb.contextstore.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VolunteerCalendar extends BaseObject {
	private Date date;
	private String schoolId;
	private String instituteId;
	private String routeId;
	private String driverId;
	private List<String> helperList = new ArrayList<String>();
	
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
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public List<String> getHelperList() {
		return helperList;
	}
	public void setHelperList(List<String> helperList) {
		this.helperList = helperList;
	}
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
}
