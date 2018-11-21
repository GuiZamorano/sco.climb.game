package it.smartcommunitylab.climb.domain.model.multimedia;

import java.util.List;

public class ContentInfo {
	private String name;
	private String legName;
	private double[] geocoding; // lon/lat (for mongodb)
	private List<String> tags;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double[] getGeocoding() {
		return geocoding;
	}
	public void setGeocoding(double[] geocoding) {
		this.geocoding = geocoding;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getLegName() {
		return legName;
	}
	public void setLegName(String legName) {
		this.legName = legName;
	}

}
