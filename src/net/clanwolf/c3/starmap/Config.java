package net.clanwolf.c3.starmap;

public class Config {

    // Map
    protected static final int MAP_COORDINATES_MULTIPLICATOR = 5;
    protected static final double MAP_WIDTH = 6000;
    protected static final double MAP_HEIGHT = 6000;
    protected static final double MAP_MAX_SCALE = 3.0d;
    protected static final double MAP_MIN_SCALE = .2d;
    protected static final double MAP_INITIAL_TRANSLATE_X = -2500;
    protected static final double MAP_INITIAL_TRANSLATE_Y = -1620;

    // Background stars
    protected static final int BACKGROUND_STARS_MAX_SIZE = 4;
    protected static final int BACKGROUND_STARS_LAYERS[][] = {{1, 300, 1}, {2, 200, 2}, {3, 100, 4}}; // level, number of stars, moving factor

    private Config() {
        // private constructor
    }
}
