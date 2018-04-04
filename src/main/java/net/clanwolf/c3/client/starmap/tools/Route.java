package net.clanwolf.c3.client.starmap.tools;

import net.clanwolf.c3.client.starmap.universe.StarSystem;
import net.clanwolf.c3.client.starmap.universe.Universe;

import java.util.ArrayList;
import java.util.List;

public class Route {

	public static List<StarSystem> getRoute(StarSystem source, StarSystem destination) {
		List<StarSystem> route = Universe.graphManager.runAStar(source, destination);
		return route;
	}

	private Route() {
		//
	}
}
