package net.clanwolf.c3.starmap;

public class Attack {

	Integer id = 0;
	Integer season = 0;
	Integer round = 0;
	Integer starSystemId = 0;
	Integer starSystemDataId = 0;
	Integer attackedFromStarSystem = 0;
	Integer attackerId = 0;
	Integer defenderId = 0;

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

	public Attack() {
		// empty constructor
	}
}
