package net.clanwolf.c3.client.starmap;

import javafx.scene.image.ImageView;

public class Jumpship {

    private Integer factionID = 0;
    private Integer shipID = 0;
    private String shipName = "";
    private boolean movedInCurrentRound = false;
    private ImageView jumpshipImage;

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
