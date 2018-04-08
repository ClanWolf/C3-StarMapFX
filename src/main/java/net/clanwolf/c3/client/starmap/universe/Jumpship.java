package net.clanwolf.c3.client.starmap.universe;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

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
	private Line predictedRouteLine = null;
	public static Group routeLines = null;

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

	@SuppressWarnings("unused")
	public Integer getCurrentSystemID() {
		if (currentSystemID == null) {
			ArrayList<Integer> hist = getStarSystemHistoryArray();
		}
		return currentSystemID;
	}

	@SuppressWarnings("unused")
	public Line getPredictedRouteLine() {
		if (predictedRouteLine == null) {
			predictedRouteLine = new Line();
			predictedRouteLine.setStrokeWidth(3);
			predictedRouteLine.setStroke(Color.ORANGE);
			predictedRouteLine.setStrokeLineCap(StrokeLineCap.ROUND);
		}
		return predictedRouteLine;
	}

	@SuppressWarnings("unused")
	public void setPredictedRouteLine(Line predictedRouteLine) {
		this.predictedRouteLine = predictedRouteLine;
	}

	@SuppressWarnings("unused")
	public boolean isCombatReady() {
		return combatReady;
	}

	@SuppressWarnings("unused")
	public void setCombatReady(boolean combatReady) {
		this.combatReady = combatReady;
	}

	@SuppressWarnings("unused")
	public String getStarSystemHistory() {
		return starSystemHistory;
	}

	@SuppressWarnings("unused")
	public void setStarSystemHistory(String starSystemHistory) {
		this.starSystemHistory = starSystemHistory;
	}

	@SuppressWarnings("unused")
	public Integer getLastMovedInRound() {
		return lastMovedInRound;
	}

	@SuppressWarnings("unused")
	public void setLastMovedInRound(Integer lastMovedInRound) {
		this.lastMovedInRound = lastMovedInRound;
	}

	@SuppressWarnings("unused")
	public Integer getFactionID() {
		return factionID;
	}

	@SuppressWarnings("unused")
	public void setFactionID(Integer factionID) {
		this.factionID = factionID;
	}

	@SuppressWarnings("unused")
	public boolean isMovedInCurrentRound() {
		return movedInCurrentRound;
	}

	@SuppressWarnings("unused")
	public void setMovedInCurrentRound(boolean movedInCurrentRound) {
		this.movedInCurrentRound = movedInCurrentRound;
	}

	@SuppressWarnings("unused")
	public ImageView getJumpshipImage() {
		return jumpshipImage;
	}

	@SuppressWarnings("unused")
	public void setJumpshipImage(ImageView jumpshipImage) {
		this.jumpshipImage = jumpshipImage;
	}

	@SuppressWarnings("unused")
	public Integer getShipID() {
		return shipID;
	}

	@SuppressWarnings("unused")
	public void setShipID(Integer shipID) {
		this.shipID = shipID;
	}

	@SuppressWarnings("unused")
	public String getShipName() {
		return shipName;
	}

	@SuppressWarnings("unused")
	public void setShipName(String shipName) {
		this.shipName = shipName;
	}
}
