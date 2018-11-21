package it.smartcommunitylab.climb.contextstore.model;

public class Child extends BaseObject {
	private String externalId;
	private String name;
	private String surname;
	private String parentName;
	private String phone;
	private String schoolId;
	private String instituteId;
	private String classRoom;
	private String wsnId;
	private String imageUrl;
	private String cf;
	private boolean activeForPedibus = true;
	private boolean activeForGame = true;
	
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getClassRoom() {
		return classRoom;
	}
	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}
	public String getWsnId() {
		return wsnId;
	}
	public void setWsnId(String wsnId) {
		this.wsnId = wsnId;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getInstituteId() {
		return instituteId;
	}
	public void setInstituteId(String instituteId) {
		this.instituteId = instituteId;
	}
	public String getCf() {
		return cf;
	}
	public void setCf(String cf) {
		this.cf = cf;
	}
	public boolean isActiveForPedibus() {
		return activeForPedibus;
	}
	public void setActiveForPedibus(boolean activeForPedibus) {
		this.activeForPedibus = activeForPedibus;
	}
	public boolean isActiveForGame() {
		return activeForGame;
	}
	public void setActiveForGame(boolean activeForGame) {
		this.activeForGame = activeForGame;
	}
}
