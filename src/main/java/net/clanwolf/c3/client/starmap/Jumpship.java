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
    private ArrayList<Integer> hist = null;

    public ArrayList<Integer> getStarSystemHistoryArray() {
        if (hist == null) {
            Integer currentSystemID = null;
            hist = new ArrayList<>();
            if (starSystemHistory != null) {
                String[] history = starSystemHistory.split(";");
                if (history.length > 1) {
                    for (String s : history) {
                        hist.add(Integer.parseInt(s));
                    }
                }
            }
        }
        return hist;
    }

    public Integer getCurrentSystemID() {
        if (hist == null) {
            getStarSystemHistoryArray();
        }
        if (hist.size() > 0) {
            return hist.get(hist.size());
        } else {
            return null;
        }
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
