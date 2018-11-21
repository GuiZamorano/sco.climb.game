package it.smartcommunitylab.climb.domain.model.monitoring;

public class MonitoringChallenge {
	private String name;
	private long from;
	private long to;
	private String status;
	private String player;
	private String virtualPrize;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getFrom() {
		return from;
	}
	public void setFrom(long from) {
		this.from = from;
	}
	public long getTo() {
		return to;
	}
	public void setTo(long to) {
		this.to = to;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public String getVirtualPrize() {
		return virtualPrize;
	}
	public void setVirtualPrize(String virtualPrize) {
		this.virtualPrize = virtualPrize;
	}
}
