package net.clanwolf.c3.client.starmap.universe;

import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import net.clanwolf.c3.client.starmap.tools.GraphManager;
import org.kynosarges.tektosyne.geometry.GeoUtils;
import org.kynosarges.tektosyne.geometry.PointD;
import org.kynosarges.tektosyne.geometry.PolygonLocation;
import org.kynosarges.tektosyne.geometry.VoronoiResults;
import org.kynosarges.tektosyne.subdivision.Subdivision;

import java.util.ArrayList;
import java.util.HashMap;

public class Universe {
	public static HashMap<Integer, StarSystem> starSystems = new HashMap<>();
	public static HashMap<String, Faction> factions = new HashMap<>();
	public static HashMap<String, Jumpship> jumpships = new HashMap<>();
	public static ArrayList<Attack> attacks = new ArrayList<>();

	public static Integer currentSeason = 1;
	public static Integer currentRound = 6;

	public static VoronoiResults voronoiResults = null;
	public static Subdivision delaunaySubdivision = null;
	public static GraphManager graphManager = null;

	public static Jumpship currentlyDraggedJumpship = null;

	public static StarSystem getStarSystemByPoint(PointD p) {
		PointD[] foundRegion = null;
		for (PointD[] region : Universe.voronoiResults.voronoiRegions()) {
			PolygonLocation location = GeoUtils.pointInPolygon(p, region);
			if ("INSIDE".equals(location.toString())) {
				foundRegion = region;
			}
		}
		if (foundRegion != null) {
			for (StarSystem ss : Universe.starSystems.values()) {
				if (foundRegion.equals(ss.getVoronoiRegion())) {
					return ss;
				}
			}
		}
		return null;
	}
}
