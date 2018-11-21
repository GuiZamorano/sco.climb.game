package it.smartcommunitylab.climb.domain.model.monitoring;

import java.util.ArrayList;
import java.util.List;

public class MonitoringItinerary {
	private String objectId;
	private String name;
	private List<String> reachedLegs = new ArrayList<String>();
	private List<String> legs = new ArrayList<String>();
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getReachedLegs() {
		return reachedLegs;
	}
	public void setReachedLegs(List<String> reachedLegs) {
		this.reachedLegs = reachedLegs;
	}
	public List<String> getLegs() {
		return legs;
	}
	public void setLegs(List<String> legs) {
		this.legs = legs;
	}
}
