package it.smartcommunitylab.climb.domain.model;

public class PedibusGameReport extends PedibusGame {
	private int finalScore;
	private int legs;
	private String firstLeg;
	private String finalLeg;
	
	public PedibusGameReport() {}
	
	public PedibusGameReport(PedibusGame game) {
		this.setGameName(game.getGameName());
		this.setGameDescription(game.getGameDescription());
		this.setGameOwner(game.getGameOwner());
		this.setFrom(game.getFrom());
		this.setTo(game.getTo());
	}
	
	public int getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(int finalScore) {
		this.finalScore = finalScore;
	}
	public int getLegs() {
		return legs;
	}
	public void setLegs(int legs) {
		this.legs = legs;
	}
	public String getFirstLeg() {
		return firstLeg;
	}
	public void setFirstLeg(String firstLeg) {
		this.firstLeg = firstLeg;
	}
	public String getFinalLeg() {
		return finalLeg;
	}
	public void setFinalLeg(String finalLeg) {
		this.finalLeg = finalLeg;
	}
}
