package net.clanwolf.c3.starmap;

import javafx.scene.paint.Color;

public class Config {

	// Map
	protected static final int MAP_COORDINATES_MULTIPLICATOR = 5;
	protected static final Color MAP_BACKGROUND_AREA_BORDER_COLOR = Color.DARKORANGE;
	protected static final Color MAP_RANGE_CIRCLE_COLOR = Color.LIGHTBLUE;
	protected static final boolean MAP_HIGHLIGHT_HOVERED_STARSYSTEM = true;
	protected static final boolean MAP_FLASH_ATTACKED_SYSTEMS = true;
	protected static final double MAP_WIDTH = 6000;
	protected static final double MAP_HEIGHT = 6000;
	protected static final double MAP_MAX_SCALE = 3.0d;
	protected static final double MAP_MIN_SCALE = .2d;
	protected static final double MAP_INITIAL_TRANSLATE_X = -2500;
	protected static final double MAP_INITIAL_TRANSLATE_Y = -1620;
	protected static final double MAP_BACKGROUND_AREA_RADIUS = 90;
	protected static final double MAP_BACKGROUND_AREA_RADIUS_BORDER_WIDTH = 3;

	// Hide on zoom
	protected static final double zoomLevelToHideGrid1 = 0.4;
	protected static final double zoomLevelToHideGrid2 = 0.7;
	protected static final double zoomLevelToHideStarSystemLabels = 1.0;
	protected static final double zoomLevelToHideStarSystemCircles = 0.5;
	protected static final double zoomLevelToHideAttacks = 0.5;
	protected static final double zoomLevelToHideJumpships = 1.3;

	// Background stars
	protected static final int BACKGROUND_STARS_MAX_SIZE = 4;
	static final int BACKGROUND_STARS_LAYERS[][] = {{1, 300, 1}, {2, 200, 2}, {3, 100, 4}}; // level, number of stars, moving factor

	private Config() {
		// private constructor
	}
}
