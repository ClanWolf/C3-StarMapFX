package net.clanwolf.c3.starmap;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import org.kynosarges.tektosyne.geometry.PointD;

import java.math.BigDecimal;

public class StarSystem {

	private Integer id;
	private String name;
	private BigDecimal x;
	private BigDecimal y;
	private String affiliation;
	private Circle starSystemCircle;
	private Label starSystemLabel;
	private StackPane starSystemStackPane;
	private Group starSystemGroup;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getScreenX() {
		double screenX = x.doubleValue() * Config.MAP_COORDINATES_MULTIPLICATOR;
		screenX = (Config.MAP_WIDTH / 2) + screenX;
		return screenX;
	}

	public double getScreenY() {
		double screenY = y.doubleValue() * Config.MAP_COORDINATES_MULTIPLICATOR;
		screenY = Config.MAP_HEIGHT - ((Config.MAP_HEIGHT /2) + screenY);
		return screenY;
	}

	public double getX() {
		return x.doubleValue();
	}

	public void setX(BigDecimal x) {
		this.x = x;
	}

	public double getY() {
		return y.doubleValue();
	}

	public void setY(BigDecimal y) {
		this.y = y;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	@SuppressWarnings("unused")
	public Circle getStarSystemCircle() {
		return starSystemCircle;
	}

	public void setStarSystemCircle(Circle starSystemCircle) {
		this.starSystemCircle = starSystemCircle;
	}

	public Label getStarSystemLabel() {
		return starSystemLabel;
	}

	public void setStarSystemLabel(Label starSystemLabel) {
		this.starSystemLabel = starSystemLabel;
	}

	public Group getStarSystemGroup() {
		return starSystemGroup;
	}

	public void setStarSystemGroup(Group starSystemGroup) {
		this.starSystemGroup = starSystemGroup;
	}

	public StackPane getStarSystemStackPane() {
		return starSystemStackPane;
	}

	public void setStarSystemStackPane(StackPane starSystemStackPane) {
		this.starSystemStackPane = starSystemStackPane;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
