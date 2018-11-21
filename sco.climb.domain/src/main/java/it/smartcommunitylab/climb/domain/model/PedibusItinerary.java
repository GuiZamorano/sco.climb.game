package it.smartcommunitylab.climb.domain.model;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

public class PedibusItinerary extends BaseObject {
	private String pedibusGameId;
	private String name;
	private String description;
	private List<String> classRooms = new ArrayList<String>();
	
	public List<String> getClassRooms() {
		return classRooms;
	}
	public void setClassRooms(List<String> classRooms) {
		this.classRooms = classRooms;
	}
	public String getPedibusGameId() {
		return pedibusGameId;
	}
	public void setPedibusGameId(String pedibusGameId) {
		this.pedibusGameId = pedibusGameId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
