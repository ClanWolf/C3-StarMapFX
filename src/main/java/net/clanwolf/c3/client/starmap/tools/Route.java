package net.clanwolf.c3.client.starmap.tools;

import net.clanwolf.c3.client.starmap.universe.StarSystem;
import net.clanwolf.c3.client.starmap.universe.Universe;
import org.kynosarges.tektosyne.geometry.PointD;

import java.util.ArrayList;
import java.util.List;

public class Route {

	public static List<StarSystem> getRoute(StarSystem source, StarSystem destination) {
		List<StarSystem> calculatedRoute = new ArrayList<>();

//		Universe.graphManager.setVertexNeighbors(true);
		List<PointD> route = Universe.graphManager.runAStar(source, destination);
		for (PointD p : route) {
			StarSystem ss = Universe.getStarSystemByPoint(p);
			calculatedRoute.add(ss);
		}
		return calculatedRoute;
	}

	private Route() {
		// private constructor
	}
}
