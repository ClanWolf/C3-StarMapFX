package net.clanwolf.c3.starmap;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import org.kynosarges.tektosyne.geometry.*;

public class GeoTools {

	protected static Pane getAreas(PointD[] points) {
		final RectD clip = new RectD(0, 0, Config.MAP_WIDTH, Config.MAP_HEIGHT);
		final VoronoiResults results = Voronoi.findAll(points, clip);

		final Pane _output = new Pane();
		final double diameter = 4;

		// draw interior of Voronoi regions
		for (PointD[] region : results.voronoiRegions()) {
			final Polygon polygon = new Polygon(PointD.toDoubles(region));
			polygon.setFill(Color.PALEGOLDENROD);
			polygon.setStroke(Color.WHITE);
			polygon.setStrokeWidth(6);
			_output.getChildren().add(polygon);
		}

		// draw edges of Voronoi diagram
		for (VoronoiEdge edge : results.voronoiEdges) {
			final PointD start = results.voronoiVertices[edge.vertex1];
			final PointD end = results.voronoiVertices[edge.vertex2];

			final Line line = new Line(start.x, start.y, end.x, end.y);
			line.setStroke(Color.RED);
			_output.getChildren().add(line);
		}

		// draw edges of Delaunay triangulation
		for (LineD edge : results.delaunayEdges()) {
			final Line line = new Line(edge.start.x, edge.start.y, edge.end.x, edge.end.y);
			line.getStrokeDashArray().addAll(3.0, 2.0);
			line.setStroke(Color.BLUE);
			_output.getChildren().add(line);
		}

		// draw generator points
		for (PointD point : points) {
			final Circle shape = new Circle(point.x, point.y, diameter / 2);
			shape.setFill(Color.BLACK);
			shape.setStroke(Color.BLACK);
			_output.getChildren().add(shape);
		}

		return _output;
	}
}
