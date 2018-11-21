package it.smartcommunitylab.climb.domain.model.multimedia;

import java.util.List;

public class MultimediaResult {
	private String link;
	private String type;
	private List<ContentInfo> info; // lon/lat (for mongodb)
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<ContentInfo> getInfo() {
		return info;
	}
	public void setInfo(List<ContentInfo> info) {
		this.info = info;
	}
	
}
