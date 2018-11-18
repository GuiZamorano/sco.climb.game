package it.smartcommunitylab.climb.gamification.dashboard.model;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PedibusPlayer extends BaseObject {

	private String childId;
	private String name;

	public PedibusPlayer() {
	}

	public PedibusPlayer(String childId, String name, String surname, String classRoom, String gameId) {
		this.childId = childId;
		this.name = name;
		this.surname = surname;
		this.classRoom = classRoom;
		this.gameId = gameId;
	}

	private String surname;
	private String classRoom;	
	private String wsnId;
	private String gameId;

	public String getChildId() {
		return childId;
	}

	public void setChildId(String childId) {
		this.childId = childId;
	}

	public String getWsnId() {
		return wsnId;
	}

	public void setWsnId(String wsnId) {
		this.wsnId = wsnId;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getClassRoom() {
		return classRoom;
	}

	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}
	
}
