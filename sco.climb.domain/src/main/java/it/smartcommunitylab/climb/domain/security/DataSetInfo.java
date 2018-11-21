package it.smartcommunitylab.climb.domain.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataSetInfo implements Serializable {
	private static final long serialVersionUID = -130084868920590202L;

	private List<String> ownerIds = new ArrayList<String>();
	private List<String> roles = new ArrayList<String>();
	private String subject;
	private String name;
	private String surname;
	private String email;
	private String cf;
	private String token;
	private long expiration;
	private String refreshToken;
	
	@Override
	public String toString() {
		return ownerIds.toString() + "-" + subject;
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

	public List<String> getOwnerIds() {
		return ownerIds;
	}

	public void setOwnerIds(List<String> ownerIds) {
		this.ownerIds = ownerIds;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
