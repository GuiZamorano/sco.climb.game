package it.smartcommunitylab.climb.domain.model;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.climb.contextstore.model.BaseObject;

public class PedibusItineraryLeg extends BaseObject implements Comparable<PedibusItineraryLeg> {
	private String pedibusGameId;
	private String itineraryId;
	private String badgeId;
	private String name;
	private String description;
	private int position;
	private double[] geocoding; // lon/lat (for mongodb)
	private List<Link> externalUrls = new ArrayList<Link>();
	private String imageUrl;
	private String polyline;
	private int score;
	private String transport;
	private String icon;
	private List<Marker> additionalPoints = new ArrayList<Marker>();
	
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
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public double[] getGeocoding() {
		return geocoding;
	}
	public void setGeocoding(double[] geocoding) {
		this.geocoding = geocoding;
	}
	public String getPolyline() {
		return polyline;
	}
	public void setPolyline(String polyline) {
		this.polyline = polyline;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int points) {
		this.score = points;
	}
	public String getBadgeId() {
		return badgeId;
	}
	public void setBadgeId(String badgeId) {
		this.badgeId = badgeId;
	}
	
	@Override
	public int compareTo(PedibusItineraryLeg o) {
		return position - o.position;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getTransport() {
		return transport;
	}
	public void setTransport(String transport) {
		this.transport = transport;
	}
	public List<Link> getExternalUrls() {
		return externalUrls;
	}
	public void setExternalUrls(List<Link> externalUrls) {
		this.externalUrls = externalUrls;
	}
	public String getItineraryId() {
		return itineraryId;
	}
	public void setItineraryId(String itineraryId) {
		this.itineraryId = itineraryId;
	}
	public String getPedibusGameId() {
		return pedibusGameId;
	}
	public void setPedibusGameId(String pedibusGameId) {
		this.pedibusGameId = pedibusGameId;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public List<Marker> getAdditionalPoints() {
		return additionalPoints;
	}
	public void setAdditionalPoints(List<Marker> additionalPoints) {
		this.additionalPoints = additionalPoints;
	}
}
