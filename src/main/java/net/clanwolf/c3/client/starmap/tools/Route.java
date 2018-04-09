package net.clanwolf.c3.client.starmap.tools;

import net.clanwolf.c3.client.starmap.Config;
import net.clanwolf.c3.client.starmap.universe.StarSystem;
import net.clanwolf.c3.client.starmap.universe.Universe;
import org.kynosarges.tektosyne.geometry.PointD;

import java.util.ArrayList;
import java.util.List;

public class Route {

//	// Get the delaunay route (no jumps but node by node)
//	public static List<StarSystem> getRoute(StarSystem source, StarSystem destination) {
//		List<StarSystem> calculatedRoute = new ArrayList<>();
//
//		List<PointD> route = Universe.graphManager.runAStar(source, destination);
//		for (PointD p : route) {
//			StarSystem ss = Universe.getStarSystemByPoint(p);
//			calculatedRoute.add(ss);
//		}
//		return calculatedRoute;
//	}

//	// Calculate the route manually (brute force)
//	public static List<StarSystem> getRoute(StarSystem source, StarSystem destination) {
//		List<StarSystem> calculatedRoute = new ArrayList<>();
//
//		return calculatedRoute;
//	}

	public static List<StarSystem> getRoute(StarSystem source, StarSystem destination) {
		List<StarSystem> calculatedRoute = new ArrayList<>();

		// Calculating the distance between current node and current node + 2 of the route.
		// If that distance is smaller than 30LYs, the second node will be removed.
		// This needs to be done to simulate jumps instead of going through all
		// nodes.
		List<PointD> route = Universe.graphManager.runAStar(source, destination);
		StarSystem ss = Universe.getStarSystemByPoint(route.get(0));
		calculatedRoute.add(ss);

		System.out.println("---------------------------");
		for (int i=0; i < route.size() - 1; i++) {
			PointD p1 = route.get(i);
			StarSystem s1 = Universe.getStarSystemByPoint(p1);
			System.out.println("### Starting from " + s1.getName());

			for (int j=i; j < route.size(); j++) {
				if (j + 3 < route.size()) {
					PointD p4 = route.get(j + 3);
					double distance14 = Universe.delaunaySubdivision.getDistance(p1, p4) / Config.MAP_COORDINATES_MULTIPLICATOR;

					if (distance14 <= 30) {
						System.out.println("Does this ever happen?");
						throw new RuntimeException("Jumped over two systems in a route within 30LYs. Should not happen!");
					}
				}
				if (j + 2 < route.size()) {
					PointD p2 = route.get(j + 1);
					PointD p3 = route.get(j + 2);
					double distance12 = Universe.delaunaySubdivision.getDistance(p1, p2) / Config.MAP_COORDINATES_MULTIPLICATOR;
					double distance13 = Universe.delaunaySubdivision.getDistance(p1, p3) / Config.MAP_COORDINATES_MULTIPLICATOR;

					System.out.println("Distance 1-2: " + distance12);
					System.out.println("Distance 1-3: " + distance13);

					if (distance13 > 30) {
						StarSystem s2 = Universe.getStarSystemByPoint(p2);
						if (!calculatedRoute.contains(s2)) {
							calculatedRoute.add(s2);
						}
						StarSystem s3 = Universe.getStarSystemByPoint(p3);
						if (!calculatedRoute.contains(s3)) {
							calculatedRoute.add(s3);
						}
						System.out.println("Adding 2 and 3 (" + s2.getName() + ", " + s3.getName() + ")");
						break;
					} else {
						StarSystem s2 = Universe.getStarSystemByPoint(p2);
						if (calculatedRoute.contains(s2)) {
							System.out.println("Removing previously added 2 (" + s2.getName() + ")");
							calculatedRoute.remove(s2);
						}
						StarSystem s3 = Universe.getStarSystemByPoint(p3);
						if (!calculatedRoute.contains(s3)) {
							calculatedRoute.add(s3);
						}
						i++;
						System.out.println("Adding 3 (" + s3.getName() + ")");
						break;
					}
				} else if (j + 2 == route.size()) {
					System.out.println("Only one jump necessary.");
					PointD p2 = route.get(j + 1);
					StarSystem s2 = Universe.getStarSystemByPoint(p2);
					if (!calculatedRoute.contains(s2)) {
						calculatedRoute.add(s2);
					}
					break;
				}
			}
		}
		System.out.println("===========================");
		return calculatedRoute;
	}

	private Route() {
		// private constructor
	}
}
