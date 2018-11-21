package it.smartcommunitylab.climb.contextstore.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
	private String objectId;
	private Date creationDate;
	private Date lastUpdate;
	private String subject;
	private String name;
	private String surname;
	private String email;
	private String cf;
	private Map<String, List<Authorization>> roles = new HashMap<String, List<Authorization>>();
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCf() {
		return cf;
	}
	public void setCf(String cf) {
		this.cf = cf;
	}
	public Map<String, List<Authorization>> getRoles() {
		return roles;
	}
	public void setRoles(Map<String, List<Authorization>> roles) {
		this.roles = roles;
	}
	
}
