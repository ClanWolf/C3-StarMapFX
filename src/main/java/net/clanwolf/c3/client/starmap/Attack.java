package net.clanwolf.c3.client.starmap;

public class Attack {

	private Integer id = 0;
	private Integer season = 0;
	private Integer round = 0;
	private Integer starSystemId = 0;
	private Integer starSystemDataId = 0;
	private Integer attackedFromStarSystem = 0;
	private Integer attackerId = 0;
	private Integer defenderId = 0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAttackedFromStarSystem() {
		return attackedFromStarSystem;
	}

	public void setAttackedFromStarSystem(Integer attackedFromStarSystem) {
		this.attackedFromStarSystem = attackedFromStarSystem;
	}

	public Integer getSeason() {
		return season;
	}

	public void setSeason(Integer season) {
		this.season = season;
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(Integer round) {
		this.round = round;
	}

	public Integer getStarSystemId() {
		return starSystemId;
	}

	public void setStarSystemId(Integer starSystemId) {
		this.starSystemId = starSystemId;
	}

	public Integer getStarSystemDataId() {
		return starSystemDataId;
	}

	public void setStarSystemDataId(Integer starSystemDataId) {
		this.starSystemDataId = starSystemDataId;
	}

	public Integer getAttackerId() {
		return attackerId;
	}

	public void setAttackerId(Integer attackerId) {
		this.attackerId = attackerId;
	}

	public Integer getDefenderId() {
		return defenderId;
	}

	public void setDefenderId(Integer defenderId) {
		this.defenderId = defenderId;
	}

	Attack() {
		// empty constructor
	}
}
