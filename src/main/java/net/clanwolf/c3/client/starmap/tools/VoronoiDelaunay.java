package net.clanwolf.c3.client.starmap.tools;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import net.clanwolf.c3.client.starmap.Config;
import net.clanwolf.c3.client.starmap.universe.Faction;
import net.clanwolf.c3.client.starmap.universe.StarSystem;
import net.clanwolf.c3.client.starmap.universe.Universe;
import org.kynosarges.tektosyne.geometry.*;
import org.kynosarges.tektosyne.subdivision.Subdivision;

import java.util.HashMap;

public class VoronoiDelaunay {

//	private static ArrayList<VoronoiEdge> innerEdges = new ArrayList<>();

	public static Pane getAreas() {
		final Pane borderPane = new Pane();
		PointD[] points = new PointD[Universe.starSystems.size()];

		// paint the background circles
		int count = 0;
		for (StarSystem ss : Universe.starSystems.values()) {
			PointD p = new PointD(ss.getScreenX(), ss.getScreenY());
			points[count] = p;
			count++;

			// create clipped circles to render the inner areas
			Faction faction = Universe.factions.get(ss.getAffiliation());
			Path path;
			if (faction.getBackgroundPath() != null) {
				path = faction.getBackgroundPath();
			} else {
				path = new Path();
			}
			Circle fc = new Circle(p.x, p.y, Config.MAP_BACKGROUND_AREA_RADIUS - Config.MAP_BACKGROUND_AREA_RADIUS_BORDER_WIDTH);
			path = (Path) Path.union(path, fc);
			faction.setBackgroundPath(path);
		}

		final RectD clip = new RectD(0, 0, Config.MAP_WIDTH, Config.MAP_HEIGHT);
		Universe.voronoiResults = Voronoi.findAll(points, clip);

		// check what voronoi region contains the current star system
		for (PointD[] region : Universe.voronoiResults.voronoiRegions()) {
			for (StarSystem ss : Universe.starSystems.values()) {
				PointD p = new PointD(ss.getScreenX(), ss.getScreenY());
				PolygonLocation location = GeoUtils.pointInPolygon(p, region);
				if ("INSIDE".equals(location.toString())) {
					Faction faction = Universe.factions.get(ss.getAffiliation());
					ss.setVoronoiRegion(region);
					faction.addVoronoiRegion(region);
				}
			}
		}

		for (Faction faction : Universe.factions.values()) {
			if (faction.getBackgroundPath() != null) {
				Shape shape = faction.getBackgroundPath();
				Shape regions = null;
				for (PointD[] region : faction.getVoronoiRegions()) {
					Polygon polygon = new Polygon(PointD.toDoubles(region));
					if (regions == null) {
						regions = polygon;
					} else {
						regions = Shape.union(regions, polygon);
					}
				}
				if (regions != null) {
					Shape factionBackground = Shape.intersect(shape, regions);
					String colorString = faction.getColor();
					Color color = Color.web(colorString);
					factionBackground.setFill(color.deriveColor(1,1,1,0.2));
					factionBackground.setStrokeWidth(Config.MAP_BACKGROUND_AREA_RADIUS_BORDER_WIDTH);
					factionBackground.setStrokeLineJoin(StrokeLineJoin.ROUND);
					factionBackground.setStroke(Config.MAP_BACKGROUND_AREA_BORDER_COLOR.deriveColor(.7, .7,.7, 1));
					borderPane.getChildren().add(factionBackground);
				}
			}
		}

		// draw edges of Voronoi diagram
//		for (VoronoiEdge edge : results.voronoiEdges) {
//			//  (e.lSite && e.rSite && e.lSite.type !== e.rSite.type)
//			if (edge.site1 != edge.site2) {
//				innerEdges.add(edge);
//			}
//
//			// paint the edges
//			final PointD start = results.voronoiVertices[edge.vertex1];
//			final PointD end = results.voronoiVertices[edge.vertex2];
//			final Line line = new Line(start.x, start.y, end.x, end.y);
//			line.setStroke(Color.DARKGRAY);
//			borderPane.getChildren().add(line);
//		}

		// draw edges of Delaunay triangulation
//		for (LineD edge : results.delaunayEdges()) {
//			final Line line = new Line(edge.start.x, edge.start.y, edge.end.x, edge.end.y);
//			line.getStrokeDashArray().addAll(3.0, 2.0);
//			line.setStroke(Color.BLUE);
//			borderPane.getChildren().add(line);
//		}

		Universe.delaunaySubdivision = Universe.voronoiResults.toDelaunaySubdivision(clip, true);
		Universe.graphManager = new GraphManager<>(Universe.delaunaySubdivision, 8, borderPane);

		return borderPane;
	}
}
