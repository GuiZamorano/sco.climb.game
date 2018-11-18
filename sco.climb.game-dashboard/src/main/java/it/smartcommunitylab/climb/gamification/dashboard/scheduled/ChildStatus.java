package it.smartcommunitylab.climb.gamification.dashboard.scheduled;

import java.util.Set;

import com.google.common.collect.Sets;

public class ChildStatus {

	private String childId;
	private Set<String> stops;
	private boolean inRange = false;
	private boolean inPedibus = false;
	private boolean arrived = false;
	
	private Double score;
	
	public ChildStatus(String id) {
		this.childId = id;
		stops = Sets.newLinkedHashSet();
	}

	public String getChildId() {
		return childId;
	}

	public Set<String> getStops() {
		return stops;
	}

	public void setStops(Set<String> anchors) {
		this.stops = anchors;
	}

	public boolean isInRange() {
		return inRange;
	}

	public void setInRange(boolean inRange) {
		this.inRange = inRange;
	}

	public boolean isInPedibus() {
		return inPedibus;
	}

	public void setInPedibus(boolean inPedibus) {
		this.inPedibus = inPedibus;
	}

	public boolean isArrived() {
		return arrived;
	}

	public void setArrived(boolean arrived) {
		this.arrived = arrived;
	}
	
	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return childId + " (" + inRange + "," + inPedibus + "," + arrived + ") => " + stops + " = " + ((score != null)? score: "");
	}
}
