package net.clanwolf.c3.client.starmap.tools;

import javafx.scene.layout.Pane;
import net.clanwolf.c3.client.starmap.universe.StarSystem;
import org.kynosarges.tektosyne.geometry.PointD;
import org.kynosarges.tektosyne.geometry.PolygonGrid;
import org.kynosarges.tektosyne.geometry.RegularPolygon;
import org.kynosarges.tektosyne.graph.*;
import org.kynosarges.tektosyne.subdivision.Subdivision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class GraphManager<T> implements GraphAgent<T> {

	private final Graph<T> graph;
	private final int maxCost;
	private final PointD maxWorld;
	private final double scaleCost;

	private final List<T> highlights = new ArrayList<>(2);
	private final Map<T, Integer> nodeCosts;

	GraphManager(Graph<T> graph, int maxCost, Pane output) {
		this.graph = graph;
		this.maxCost = maxCost;
		maxWorld = new PointD(output.getWidth(), output.getHeight());

		// set random step costs for all nodes
		nodeCosts = new HashMap<>(graph.nodeCount());
		for (T node : graph.nodes()) {
			nodeCosts.put(node, 1);
		}

		// scaling factor for Subdivision (see getStepCost)
		if (graph instanceof Subdivision)
			scaleCost = maxWorld.x + maxWorld.y;
		else
			scaleCost = 1;
	}

	@SuppressWarnings("unused")
	List<T> runAStar(StarSystem sourceSystem, StarSystem targetSystem) {
		List<T> locations;

		final T source = findNode(sourceSystem);
		final T target = findNode(targetSystem);

		highlights.clear();
		highlights.add(source);
		highlights.add(target);

		// find best path from source to target
		final AStar<T> aStar = new AStar<>(graph);
		aStar.useWorldDistance = true;
		final boolean success = aStar.findBestPath(this, source, target);
		locations = aStar.nodes();

		return locations;
	}

	@SuppressWarnings("unused")
	List<T> runCoverage(StarSystem ss) {
		List<T> locations;
		final T source = findSource(ss);

		// find all nodes reachable from source
		// (note: scaling maximum step cost for Subdivision)
		final Coverage<T> coverage = new Coverage<>(graph);
		final boolean success = coverage.findReachable(this, source, scaleCost * 10);
		locations = coverage.nodes();

		return locations;
	}

	@SuppressWarnings("unused")
	List<T> runFloodFill(StarSystem ss) {
		List<T> locations;
		final T source = findSource(ss);

		// find all nodes reachable from source node
		final FloodFill<T> floodFill = new FloodFill<>(graph);
		final Predicate<T> match = (p -> nodeCosts.get(p) <= maxCost / 2);
		final boolean success = floodFill.findMatching(match, source);
		locations = floodFill.nodes();

		return locations;
	}

	@SuppressWarnings("unused")
	List<T> runVisibility(StarSystem ss, double threshold) {
		List<T> locations;
		final T source = findSource(ss);

		// find all nodes visible from source node
		final Visibility<T> visibility = new Visibility<>(graph);
		visibility.setThreshold(threshold);
		final Predicate<T> isOpaque = (p -> nodeCosts.get(p) >= maxCost);
		final boolean success = visibility.findVisible(isOpaque, source, 0);
		locations = visibility.nodes();

		return locations;
	}

	@SuppressWarnings("unused")
	boolean setVertexNeighbors(boolean value) {
		if (!(graph instanceof PolygonGrid))
			return false;

		final PolygonGrid grid = (PolygonGrid) graph;
		final RegularPolygon element = grid.element();
		if (element.sides != 4 || element.vertexNeighbors == value)
			return false;

		grid.setElement(new RegularPolygon(element.length, 4, element.orientation, value));
		return true;
	}

	@Override
	public boolean relaxedRange() {
		return false;
	}

	@Override
	public boolean canMakeStep(T source, T target) {
		return (nodeCosts.get(target) < maxCost);
	}

	@Override
	public boolean canOccupy(T target) {
		return true;
	}

	@Override
	public double getStepCost(T source, T target) {
		/*
		 * Subdivision graphs must scale step costs by world distance because Graph<T>
		 * requires that the step cost is never less than the getDistance result. Step costs
		 * must be multiplied with the scaling factor (and not added) so that multiple cheap
		 * steps are preferred to a single, more expensive step.
		 *
		 * 1. Using the current distance makes pathfinding sensitive to both world distance
		 *    and step cost. For best results, we would average out the step costs of source
		 *    and target. This corresponds exactly to the visible Voronoi region shading,
		 *    as Delaunay edges are always halved by region boundaries.
		 *
		 * 2. Using a fixed value that equals or exceeds the maximum possible distance
		 *    between any two nodes makes pathfinding sensitive only to assigned step costs.
		 *    This effectively replicates the behavior on a PolygonGrid.
		 */
		//double distance = graph.getDistance(source, target);
		//return (distance * (nodeCosts.get(source) + nodeCosts.get(target)) / 2);

		return scaleCost * nodeCosts.get(target);
	}

	private T findNode(StarSystem ss) {
		PointD p = new PointD(ss.getScreenX(), ss.getScreenY());
		return graph.findNearestNode(p);
	}

	private T findSource(StarSystem ss) {
		T source;
		if (!highlights.isEmpty()) {
			source = highlights.get(0);
		} else {
			source = findNode(ss);
		}

		highlights.clear();
		highlights.add(source);

		return source;
	}

	@Override
	public boolean isNearTarget(T source, T target, double distance) {
		return (distance == 0);
	}
}
