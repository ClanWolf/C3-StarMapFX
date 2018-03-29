package net.clanwolf.c3.client.starmap;

import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class Jumpship {

	private String shipName = "";
	private String starSystemHistory = "";
	private Integer shipID = 0;
	private Integer factionID = 0;
	private Integer lastMovedInRound = 0;
	private boolean movedInCurrentRound = false;
	private boolean combatReady;
	private ImageView jumpshipImage;
	private Integer currentSystemID = null;

	public ArrayList<Integer> getStarSystemHistoryArray() {
		ArrayList<Integer> hist = null;
		if (!"".equals(starSystemHistory)) {
			if (starSystemHistory.endsWith(";")) {
				starSystemHistory = starSystemHistory.substring(0, starSystemHistory.length() - 1);
			}
			if (!starSystemHistory.contains(";")) {
				try {
					currentSystemID = Integer.parseInt(starSystemHistory);
				} catch (NumberFormatException nfe) {
					// not a valid id
				}
			} else {
				String[] hs = starSystemHistory.split(";");
				hist = new ArrayList<>();
				for (String s : hs) {
					try {
						Integer i = Integer.parseInt(s);
						hist.add(i);
						currentSystemID = i;
					} catch (NumberFormatException nfe) {
						// not a valid id
					}
				}
			}
		}
		return hist;
	}

	public Integer getCurrentSystemID() {
		if (currentSystemID == null) {
			ArrayList<Integer> hist = getStarSystemHistoryArray();
		}
		return currentSystemID;
	}

	public boolean isCombatReady() {
		return combatReady;
	}

	public void setCombatReady(boolean combatReady) {
		this.combatReady = combatReady;
	}

	public String getStarSystemHistory() {
		return starSystemHistory;
	}

	public void setStarSystemHistory(String starSystemHistory) {
		this.starSystemHistory = starSystemHistory;
	}

	public Integer getLastMovedInRound() {
		return lastMovedInRound;
	}

	public void setLastMovedInRound(Integer lastMovedInRound) {
		this.lastMovedInRound = lastMovedInRound;
	}

	public Integer getFactionID() {
		return factionID;
	}

	public void setFactionID(Integer factionID) {
		this.factionID = factionID;
	}

	public boolean isMovedInCurrentRound() {
		return movedInCurrentRound;
	}

	public void setMovedInCurrentRound(boolean movedInCurrentRound) {
		this.movedInCurrentRound = movedInCurrentRound;
	}

	public ImageView getJumpshipImage() {
		return jumpshipImage;
	}

	public void setJumpshipImage(ImageView jumpshipImage) {
		this.jumpshipImage = jumpshipImage;
	}

	public Integer getShipID() {
		return shipID;
	}

	public void setShipID(Integer shipID) {
		this.shipID = shipID;
	}

	public String getShipName() {
		return shipName;
	}

	public void setShipName(String shipName) {
		this.shipName = shipName;
	}
}
