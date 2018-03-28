package net.clanwolf.c3.client.starmap;

import javafx.scene.layout.Region;
import javafx.scene.shape.Path;
import org.kynosarges.tektosyne.geometry.PointD;

import java.util.ArrayList;

public class Faction {
	private String name;
	private String shortName;
	private String color;
	private Path backgroundPath;
	private ArrayList<PointD[]> voronoiRegions = new ArrayList<>();

	public ArrayList<PointD[]> getVoronoiRegions() {
		return voronoiRegions;
	}

	public void setVoronoiRegions(ArrayList<PointD[]> voronoiRegions) {
		this.voronoiRegions = voronoiRegions;
	}

	public void addVoronoiRegion(PointD[] voronoiRegion) {
		this.voronoiRegions.add(voronoiRegion);
	}

	public Path getBackgroundPath() {
		return backgroundPath;
	}

	public void setBackgroundPath(Path backgroundPath) {
		this.backgroundPath = backgroundPath;
	}

	@SuppressWarnings("unused")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
}
