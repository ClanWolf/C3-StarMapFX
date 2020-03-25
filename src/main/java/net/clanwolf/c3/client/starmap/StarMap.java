package net.clanwolf.c3.client.starmap;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.clanwolf.c3.client.starmap.tools.Route;
import net.clanwolf.c3.client.starmap.tools.VoronoiDelaunay;
import net.clanwolf.c3.client.starmap.universe.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.kynosarges.tektosyne.geometry.PointD;

import javax.json.*;

class PannableCanvas extends Pane {
	private DoubleProperty myScale = new SimpleDoubleProperty(1.0);
	private Canvas grid_500 = null;
	private Canvas grid_250 = null;
	private Canvas grid_center = null;
	private Pane starPane = null;
	private Pane attacksPane = null;
	private HashMap<Integer, ArrayList<Circle>> starPanelsStarLists = new HashMap<>();

	private boolean starSystemLabelsVisible = true;

	private Circle starSystemMarkerCircle30ly;
	private Circle starSystemMarkerCircle60ly;

	PannableCanvas() {
		setPrefSize(Config.MAP_WIDTH, Config.MAP_HEIGHT);
		setStyle("-fx-background-color:transparent;-fx-border-width:5px;-fx-border-color:gray;");

		// add scale transform
		scaleXProperty().bind(myScale);
		scaleYProperty().bind(myScale);

		double radius2 = 30 * 2 * Config.MAP_COORDINATES_MULTIPLICATOR; // 60 Lightyears
		starSystemMarkerCircle60ly = new Circle(radius2);
		starSystemMarkerCircle60ly.setStroke(Config.MAP_RANGE_CIRCLE_COLOR.deriveColor(.5, .5, .5, 1.0));
		starSystemMarkerCircle60ly.setStrokeWidth(2);
		starSystemMarkerCircle60ly.getStrokeDashArray().setAll(50d, 20d, 50d, 20d);
		starSystemMarkerCircle60ly.setFill(null);
		starSystemMarkerCircle60ly.setVisible(false);

		double radius = 30 * Config.MAP_COORDINATES_MULTIPLICATOR; // 30 Lightyears
		starSystemMarkerCircle30ly = new Circle(radius);
		starSystemMarkerCircle30ly.setStroke(Config.MAP_RANGE_CIRCLE_COLOR.deriveColor(.5, .5, .5, 1.0));
		starSystemMarkerCircle30ly.setStrokeWidth(2);
		starSystemMarkerCircle30ly.getStrokeDashArray().setAll(10d, 10d, 10d, 10d);
		starSystemMarkerCircle30ly.setFill(Config.MAP_RANGE_CIRCLE_COLOR.deriveColor(.6, .6, .6, 0.1));
		starSystemMarkerCircle30ly.setVisible(false);

		this.getChildren().add(starSystemMarkerCircle60ly);
		this.getChildren().add(starSystemMarkerCircle30ly);
	}

	public void setAttacksPane(Pane attacksPane) {
		this.attacksPane = attacksPane;
	}

	public void hideStarSystemMarker() {
		starSystemMarkerCircle60ly.setVisible(false);
		starSystemMarkerCircle30ly.setVisible(false);
	}

	public void showStarSystemMarker(StarSystem system) {
		double x = system.getScreenX();
		double y = system.getScreenY();

		starSystemMarkerCircle60ly.setCenterX(x);
		starSystemMarkerCircle60ly.setCenterY(y);
		starSystemMarkerCircle60ly.setVisible(true);

		starSystemMarkerCircle30ly.setCenterX(x);
		starSystemMarkerCircle30ly.setCenterY(y);
		starSystemMarkerCircle30ly.setVisible(true);
	}

	/**
	 * Add a grid to the canvas, send it to back
	 */
	public void addStarPane() {
		double w = Config.MAP_WIDTH;
		double h = Config.MAP_HEIGHT;

		if (starPane == null) {
			starPane = new Pane();
			starPane.setMaxWidth(w);
			starPane.setMaxHeight(h);
			starPane.setMouseTransparent(true);
		}

		for (int[] layer : Config.BACKGROUND_STARS_LAYERS) {
			int level = layer[0];
			int number = layer[1];

			ArrayList<Circle> l = new ArrayList<>();

			for (int i = 0; i < number; i++) {
				double x = (((Math.random()) * w + 1));
				double y = w - (((Math.random()) * h + 1));
				int size = (int) ((Math.random()) * Config.BACKGROUND_STARS_MAX_SIZE + 1);

				Circle c = new Circle(x, y, size);
				c.setStrokeWidth(0);
				c.setFill(Color.WHITESMOKE.deriveColor(1, 1, 1, 0.4));
				l.add(c);
				starPane.getChildren().add(c);
			}
			starPanelsStarLists.put(level, l);
			if (!getChildren().contains(starPane)) {
				getChildren().add(starPane);
				starPane.toBack();
			}
		}
	}

	public void moveBackgroundStarPane(int level, double x, double y) {
		Platform.runLater(() -> {
			ArrayList<Circle> l = starPanelsStarLists.get(level);
			for (Circle c : l) {
				c.setCenterX(c.getCenterX() + x);
				c.setCenterY(c.getCenterY() + y);
			}
		});
	}

	public void addGrid_500() {
		double w = Config.MAP_WIDTH;
		double h = Config.MAP_HEIGHT;

		grid_500 = new Canvas(w, h);
		grid_500.setMouseTransparent(true);

		GraphicsContext gc = grid_500.getGraphicsContext2D();
		gc.setStroke(Color.ORANGE);
		gc.setLineWidth(1);

		// draw grid lines
		double offset = 500;
		for (double i = offset; i < w; i += offset) {
			gc.strokeLine(i, 0, i, h);
			gc.strokeLine(0, i, w, i);
		}
		getChildren().add(grid_500);
		grid_500.toBack();
	}

	private void setGrid_500_Visible() {
		if (grid_500 != null) {
			if (myScale.get() >= Config.zoomLevelToHideGrid1) {
				grid_500.setVisible(true);
			} else if (myScale.get() < Config.zoomLevelToHideGrid1) {
				grid_500.setVisible(false);
			}
		}
	}

	public void addGrid_250() {
		double w = Config.MAP_WIDTH;
		double h = Config.MAP_HEIGHT;

		grid_250 = new Canvas(w, h);
		grid_250.setMouseTransparent(true);

		GraphicsContext gc = grid_250.getGraphicsContext2D();
		gc.setStroke(Color.GRAY);
		gc.setLineWidth(1);

		// draw grid lines
		double offset = 500;
		for (double i = 250; i < w; i += offset) {
			gc.strokeLine(i, 0, i, h);
			gc.strokeLine(0, i, w, i);
		}
		getChildren().add(grid_250);
		grid_250.toBack();
	}

	private void setGrid_250_Visible() {
		if (grid_250 != null) {
			if (myScale.get() >= Config.zoomLevelToHideGrid2) {
				grid_250.setVisible(true);
				grid_center.setVisible(true);
			} else if (myScale.get() < Config.zoomLevelToHideGrid2) {
				grid_250.setVisible(false);
				grid_center.setVisible(false);
			}
		}
	}

	public void addGrid_Center() {
		double w = Config.MAP_WIDTH;
		double h = Config.MAP_HEIGHT;

		grid_center = new Canvas(w, h);
		grid_center.setMouseTransparent(true);

		GraphicsContext gc = grid_center.getGraphicsContext2D();
		gc.setStroke(Color.RED);
		gc.setLineWidth(2);

		gc.strokeLine(w / 2, 0, w / 2, h);
		gc.strokeLine(0, h / 2, h, h / 2);

		getChildren().add(grid_center);
		grid_center.toBack();
	}

	public double getScale() {
		return myScale.get();
	}

	public void setPivot(double x, double y) {
		setTranslateX(getTranslateX() - x);
		setTranslateY(getTranslateY() - y);
	}

	public void setScale(double scale) {
		myScale.set(scale);
		setStarSystemLabelsVisible();
		setStarSystemCirclesVisible();
		setGrid_500_Visible();
		setGrid_250_Visible();
		setAttacksVisible();
	}

	private void setStarSystemLabelsVisible() {
		if (Universe.starSystems != null) {
			if (myScale.get() >= Config.zoomLevelToHideStarSystemLabels) {
				if (!starSystemLabelsVisible) {
					starSystemLabelsVisible = true;
					for (StarSystem ss : Universe.starSystems.values()) {
						Label l = ss.getStarSystemLabel();
						l.setVisible(starSystemLabelsVisible);
					}
				}
			} else if (myScale.get() < Config.zoomLevelToHideStarSystemLabels) {
				if (starSystemLabelsVisible) {
					starSystemLabelsVisible = false;
					for (StarSystem ss : Universe.starSystems.values()) {
						Label l = ss.getStarSystemLabel();
						l.setVisible(starSystemLabelsVisible);
					}
				}
			}
		}
	}

	private void setStarSystemCirclesVisible() {
		if (Universe.starSystems != null) {
			if (myScale.get() >= Config.zoomLevelToHideStarSystemCircles) {
				for (StarSystem ss : Universe.starSystems.values()) {
					Circle c = ss.getStarSystemCircle();
					c.setVisible(true);
				}
			} else if (myScale.get() < Config.zoomLevelToHideStarSystemCircles) {
				for (StarSystem ss : Universe.starSystems.values()) {
					Circle c = ss.getStarSystemCircle();
					c.setVisible(false);
				}
			}
		}
	}

	private void setAttacksVisible() {
		if (Universe.starSystems != null && attacksPane != null) {
			if (myScale.get() >= Config.zoomLevelToHideAttacks) {
				attacksPane.setVisible(true);
			} else if (myScale.get() < Config.zoomLevelToHideAttacks) {
				attacksPane.setVisible(false);
				for (Jumpship ship : Universe.jumpships.values()) {
					if (ship.getJumpshipImage() != null) {
						ship.getJumpshipImage().setVisible(false);
//						ship.getPredictedRouteLine().setVisible(false);
					}
				}
			}
			if (myScale.get() >= Config.zoomLevelToHideJumpships) {
				for (Jumpship ship : Universe.jumpships.values()) {
					if (ship.getJumpshipImage() != null) {
						ship.getJumpshipImage().setVisible(true);
//						ship.getPredictedRouteLine().setVisible(true);
					}
				}
			} else if (myScale.get() < Config.zoomLevelToHideJumpships) {
				for (Jumpship ship : Universe.jumpships.values()) {
					if (ship.getJumpshipImage() != null) {
						ship.getJumpshipImage().setVisible(false);
//						ship.getPredictedRouteLine().setVisible(false);
					}
				}
			}
		}
	}

	public void setVisibility() {
		setStarSystemLabelsVisible();
		setStarSystemCirclesVisible();
		setGrid_500_Visible();
		setGrid_250_Visible();
		setAttacksVisible();
	}
}

/**
 * Mouse drag context used for scene and nodes.
 */
class DragContext {
	double mouseAnchorX;
	double mouseAnchorY;
	double translateAnchorX;
	double translateAnchorY;
}

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if
 * parent is zoomed.
 */
class NodeGestures {
	private DragContext nodeDragContext = new DragContext();
	private PannableCanvas canvas;

	NodeGestures(PannableCanvas canvas) {
		this.canvas = canvas;
	}

	@SuppressWarnings("unused")
	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
		return getOnMouseClickedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseDragDetectedEventHandler() {
		return onMouseDragDetectedEventHandler;
	}

	@SuppressWarnings("unused")
	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	public EventHandler<MouseEvent> getOnStarSystemHoverEnteredEventHandler() {
		return onStarSystemHoverEnteredEventHandler;
	}

	public EventHandler<MouseEvent> getOnStarSystemHoverExitedEventHandler() {
		return onStarSystemHoverExitedEventHandler;
	}

	public EventHandler<MouseDragEvent> getOnStarSystemDragEnteredEventHandler() {
		return onStarSystemDragEnteredEventHandler;
	}

	public EventHandler<MouseDragEvent> getOnStarSystemDragExitedEventHandler() {
		return onStarSystemDragExitedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseReleasedEventHandler() {
		return onMouseReleasedEventHandler;
	}

	private EventHandler<MouseEvent> onMouseReleasedEventHandler = event -> {
		Node node = (Node) event.getSource();
		if (node instanceof ImageView) {
			Jumpship js = Universe.jumpships.get(node.getId());
			js.getPredictedRouteLine().toFront();
			node.toFront();
			Universe.currentlyDraggedJumpship.getPredictedRouteLine().setVisible(false);
		}
	};

	private EventHandler<MouseEvent> onMouseDragDetectedEventHandler = event -> {
		Node sourceNode = (Node) event.getSource();
		sourceNode.startFullDrag();
	};

	private EventHandler<MouseDragEvent> onStarSystemDragEnteredEventHandler = event -> {
		Node node = (Node) event.getSource();
		Circle c = (Circle) node;
		c.setRadius(8);
		event.consume();

		// TODO: Get the route
		// Starting system and dragged target can be taken from new member vars to be created
		StarSystem startSystem = Universe.starSystems.get(Universe.currentlyDraggedJumpship.getCurrentSystemID());
		StarSystem hovered = Universe.starSystems.get(Integer.parseInt(c.getId()));

		System.out.println(Universe.currentlyDraggedJumpship.getShipName() + " : " + startSystem.getName() + " : " + hovered.getName());
		List<StarSystem> route = Route.getRoute(startSystem, hovered);

		if (canvas.getChildren().contains(Universe.currentlyDraggedJumpship.routeLines)) {
			canvas.getChildren().remove(Universe.currentlyDraggedJumpship.routeLines);
		}
		Universe.currentlyDraggedJumpship.routeLines = new Group();
		for (int y = 0; y < route.size() - 1; y++) {
			StarSystem s1 = (StarSystem) route.get(y);
			StarSystem s2 = (StarSystem) route.get(y + 1);

			// Dotted line to every stop on the route
			Line line = new Line(s1.getScreenX(), s1.getScreenY(), s2.getScreenX(), s2.getScreenY());
			line.setStrokeWidth(4);
			line.getStrokeDashArray().setAll(5d, 5d, 5d, 5d);
			if (y == 0) {
				line.setStroke(Color.GREEN);
			} else {
				line.setStroke(Color.LIGHTGREEN);
			}
			line.setStrokeLineCap(StrokeLineCap.ROUND);
			Universe.currentlyDraggedJumpship.routeLines.getChildren().add(line);

			// Filled dots for every stop on the route (S1)
			Circle circleS1 = new Circle();
			circleS1.setCenterX(s1.getScreenX());
			circleS1.setCenterY(s1.getScreenY());
			circleS1.setRadius(s1.getStarSystemCircle().getRadius());
			circleS1.setStrokeWidth(s1.getStarSystemCircle().getStrokeWidth());
			circleS1.setStroke(Color.web(Universe.factions.get(s1.getAffiliation()).getColor()));
			circleS1.setFill(Color.web(Universe.factions.get(s1.getAffiliation()).getColor()));
			circleS1.setOpacity(1.0);
			circleS1.setVisible(true);
			Universe.currentlyDraggedJumpship.routeLines.getChildren().add(circleS1);

			// Filled dots for every stop on the route (S2)
			Circle circleS2 = new Circle();
			circleS2.setCenterX(s2.getScreenX());
			circleS2.setCenterY(s2.getScreenY());
			circleS2.setRadius(s2.getStarSystemCircle().getRadius());
			circleS2.setStrokeWidth(s2.getStarSystemCircle().getStrokeWidth());
			circleS2.setStroke(Color.web(Universe.factions.get(s2.getAffiliation()).getColor()));
			circleS2.setFill(Color.web(Universe.factions.get(s2.getAffiliation()).getColor()));
			circleS2.setOpacity(1.0);
			circleS2.setVisible(true);
			Universe.currentlyDraggedJumpship.routeLines.getChildren().add(circleS2);
		}
		canvas.getChildren().add(Universe.currentlyDraggedJumpship.routeLines);
		Universe.currentlyDraggedJumpship.routeLines.toBack();
	};

	private EventHandler<MouseDragEvent> onStarSystemDragExitedEventHandler = event -> {
		Node node = (Node) event.getSource();
		Circle c = (Circle) node;
		c.setRadius(5);
		event.consume();
	};

	private EventHandler<MouseEvent> onStarSystemHoverEnteredEventHandler = event -> {
		if (Config.MAP_HIGHLIGHT_HOVERED_STARSYSTEM) {
			Node node = (Node) event.getSource();
			Circle c = (Circle) node;
			c.setRadius(8);
		}
	};

	private EventHandler<MouseEvent> onStarSystemHoverExitedEventHandler = event -> {
		if (Config.MAP_HIGHLIGHT_HOVERED_STARSYSTEM) {
			Node node = (Node) event.getSource();
			Circle c = (Circle) node;
			c.setRadius(5);
		}
	};

	private EventHandler<MouseEvent> getOnMouseClickedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			if (event.isSecondaryButtonDown()) {
				System.out.println("RIGHTCLICK");
			}

			// left mouse button click
			if (!event.isPrimaryButtonDown()) {
				return;
			}
			Node node = (Node) event.getSource();
			StarSystem clickedStarSystem = Universe.starSystems.get(Integer.parseInt(node.getId()));
			System.out.println("System: "
					+ clickedStarSystem.getName()
					+ " (x: " + clickedStarSystem.getX()
					+ " | y: " + clickedStarSystem.getY()
					+ ") - "
					+ "["
					+ clickedStarSystem.getId()
					+ "]");

			canvas.showStarSystemMarker(clickedStarSystem);
		}
	};

	private EventHandler<MouseEvent> onMousePressedEventHandler = event -> {
		// left mouse button => dragging
		if (!event.isPrimaryButtonDown()) {
			return;
		}

		nodeDragContext.mouseAnchorX = event.getSceneX();
		nodeDragContext.mouseAnchorY = event.getSceneY();

		Node node = (Node) event.getSource();

		nodeDragContext.translateAnchorX = node.getTranslateX();
		nodeDragContext.translateAnchorY = node.getTranslateY();
	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			// left mouse button => dragging
			if (!event.isPrimaryButtonDown()) {
				return;
			}

			double scale = canvas.getScale();
			double newTranslateX = nodeDragContext.translateAnchorX + ((event.getSceneX() - nodeDragContext.mouseAnchorX) / scale);
			double newTranslateY = nodeDragContext.translateAnchorY + ((event.getSceneY() - nodeDragContext.mouseAnchorY) / scale);

			Node node = (Node) event.getSource();
			if (node instanceof ImageView) { // must be a jumpship
				Universe.currentlyDraggedJumpship = Universe.jumpships.get(node.getId());
				node.toBack();
				String name = node.getId();
				Jumpship ship = Universe.jumpships.get(name);
				canvas.showStarSystemMarker(Universe.starSystems.get(ship.getCurrentSystemID()));
				Line routeLine = ship.getPredictedRouteLine();
				double startX = Universe.starSystems.get(ship.getCurrentSystemID()).getScreenX();
				double startY = Universe.starSystems.get(ship.getCurrentSystemID()).getScreenY();

				routeLine.setStartX(startX);
				routeLine.setStartY(startY);
				routeLine.setEndX(newTranslateX + 20);
				routeLine.setEndY(newTranslateY + 10);
				routeLine.toBack();
				routeLine.setVisible(true);
				routeLine.setOpacity(0.1);
				if (!canvas.getChildren().contains(routeLine)) {
					canvas.getChildren().add(routeLine);
				}
			}

			node.setTranslateX(newTranslateX);
			node.setTranslateY(newTranslateY);
			event.consume();
		}
	};
}

/**
 * Listeners for making the scene's canvas draggable and zoomable
 */
class SceneGestures {
	private double previousX;
	private double previousY;

	private DragContext sceneDragContext = new DragContext();
	private PannableCanvas canvas;

	SceneGestures(PannableCanvas canvas) {
		this.canvas = canvas;
	}

	EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	EventHandler<ScrollEvent> getOnScrollEventHandler() {
		return onScrollEventHandler;
	}

	EventHandler<MouseEvent> getOnMouseMovedEventHandler() {
		return onMouseMovedEventHandler;
	}

	private EventHandler<MouseEvent> onMouseMovedEventHandler = event -> {
		// TODO: Do something with hovered coordinates
		// Fire an action to inform the surrounding frame about the currently hovered universe coordinates
		// double universeX = getUniverseX(event.getX());
		// double universeY = getUniverseX(event.getY());
		// System.out.println("[" + universeX + ", " + universeY + "]");
	};

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {

			if (event.isPrimaryButtonDown()) {
				canvas.hideStarSystemMarker();
			}

			// right mouse button => panning
			if (!event.isSecondaryButtonDown()) {
				return;
			}

			previousX = event.getX();
			previousY = event.getY();

			sceneDragContext.mouseAnchorX = event.getSceneX();
			sceneDragContext.mouseAnchorY = event.getSceneY();

			sceneDragContext.translateAnchorX = canvas.getTranslateX();
			sceneDragContext.translateAnchorY = canvas.getTranslateY();
		}
	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {

			// right mouse button => panning
			if (!event.isSecondaryButtonDown()) {
				return;
			}

			double diffX = sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX;
			double diffY = sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY;

//			String directionH = "";
//			String directionV = "";
			double multix = 0;
			double multiy = 0;
			double x = event.getX();
			double y = event.getY();
			if (x < previousX) {
//				directionH = "right";
				multix = -1;
			} else if (x > previousX) {
//				directionH = "left";
				multix = 1;
			}
			if (y < previousY) {
//				directionV = "up";
				multiy = -1;
			} else if (y > previousY) {
//				directionV = "down";
				multiy = 1;
			}
			for (int[] layer : Config.BACKGROUND_STARS_LAYERS) {
				int level = layer[0];
				int factor = layer[2];
				canvas.moveBackgroundStarPane(level, factor * multix, factor * multiy);
			}

			previousX = x;
			previousY = y;

			canvas.setTranslateX(diffX);
			canvas.setTranslateY(diffY);

			event.consume();
		}
	};

	/**
	 * Mouse wheel handler: zoom to pivot point
	 */
	private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

		@Override
		public void handle(ScrollEvent event) {
			double delta = 1.2;
			double scale = canvas.getScale(); // only use Y, same value for X
			double oldScale = scale;

			if (event.getDeltaY() < 0) {
				scale /= delta;
			} else {
				scale *= delta;
			}

			scale = clamp(scale);
			double f = (scale / oldScale) - 1;

			// maxX = right overhang, maxY = lower overhang
			double maxX = canvas.getBoundsInParent().getMaxX()
					- canvas.localToParent(canvas.getPrefWidth(), canvas.getPrefHeight()).getX();
			double maxY = canvas.getBoundsInParent().getMaxY()
					- canvas.localToParent(canvas.getPrefWidth(), canvas.getPrefHeight()).getY();

			// minX = left overhang, minY = upper overhang
			double minX = canvas.localToParent(0, 0).getX() - canvas.getBoundsInParent().getMinX();
			double minY = canvas.localToParent(0, 0).getY() - canvas.getBoundsInParent().getMinY();

			// adding the overhangs together, as we only consider the width of
			// canvas itself
			double subX = maxX + minX;
			double subY = maxY + minY;

			// subtracting the overall overhang from the width and only the left
			// and upper overhang from the upper left point
			double dx = (event.getSceneX() - ((canvas.getBoundsInParent().getWidth() - subX) / 2
					+ (canvas.getBoundsInParent().getMinX() + minX)));
			double dy = (event.getSceneY() - ((canvas.getBoundsInParent().getHeight() - subY) / 2
					+ (canvas.getBoundsInParent().getMinY() + minY)));

			canvas.setScale(scale);

			// note: pivot value must be untransformed, i. e. without scaling
			canvas.setPivot(f * dx, f * dy);

			event.consume();
		}
	};

	private static double clamp(double value) {
		if (Double.compare(value, Config.MAP_MIN_SCALE) < 0) {
			return Config.MAP_MIN_SCALE;
		}
		if (Double.compare(value, Config.MAP_MAX_SCALE) > 0) {
			return Config.MAP_MAX_SCALE;
		}
		return value;
	}

	@SuppressWarnings("unused")
	private double getUniverseX(double screenX) {
		double universeX = screenX - (Config.MAP_WIDTH / 2);
		universeX = universeX / Config.MAP_COORDINATES_MULTIPLICATOR;
		return universeX;
	}

	@SuppressWarnings("unused")
	private double getUniverseY(double screenY) {
		double universeY = screenY - (Config.MAP_HEIGHT / 2);
		universeY = universeY / Config.MAP_COORDINATES_MULTIPLICATOR;
		return universeY;
	}
}

/**
 * An application with a zoomable and pannable canvas.
 */
public class StarMap extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	private static void createFactions(JsonValue value) {
		JsonObject object;
		if (value.getValueType() == JsonValue.ValueType.OBJECT) {
			object = (JsonObject) value;
			for (Entry<String, JsonValue> set : object.entrySet()) {
				if (set.getValue() instanceof JsonArray) {
					createFactions(set.getValue());
				}
			}
		} else if (value.getValueType() == JsonValue.ValueType.ARRAY) {
			JsonArray array = (JsonArray) value;
			for (JsonValue val : array) {
				if (val instanceof JsonObject) {
					JsonObject obj = (JsonObject) val;
					Faction f = new Faction();
					f.setName(obj.getString("name"));
					f.setShortName(obj.getString("short"));
					f.setColor(obj.getString("color"));

					Universe.factions.put(f.getShortName(), f);
				}
			}
		}
	}

	private static void createUniverse(JsonValue value) {
		JsonObject object;
		if (value.getValueType() == JsonValue.ValueType.OBJECT) {
			object = (JsonObject) value;
			for (Entry<String, JsonValue> set : object.entrySet()) {
				if (set.getValue() instanceof JsonArray) {
					createUniverse(set.getValue());
				}
			}
		} else if (value.getValueType() == JsonValue.ValueType.ARRAY) {
			JsonArray array = (JsonArray) value;
			for (JsonValue val : array) {
				if (val instanceof JsonObject) {
					JsonObject obj = (JsonObject) val;
					StarSystem s = new StarSystem();
					String name = obj.getString("name");
					Integer id = obj.getInt("sid");
					s.setName(name);
					s.setX(obj.getJsonNumber("x").bigDecimalValue());
					s.setY(obj.getJsonNumber("y").bigDecimalValue());
					s.setAffiliation(obj.getString("affiliation"));
					s.setId(id);

					Universe.starSystems.put(id, s);
				}
			}
		}
	}

	private static void createAttacks(JsonValue value) {
		JsonObject object;
		if (value.getValueType() == JsonValue.ValueType.OBJECT) {
			object = (JsonObject) value;
			for (Entry<String, JsonValue> set : object.entrySet()) {
				if (set.getValue() instanceof JsonArray) {
					createAttacks(set.getValue());
				}
			}
		} else if (value.getValueType() == JsonValue.ValueType.ARRAY) {
			JsonArray array = (JsonArray) value;
			for (JsonValue val : array) {
				if (val instanceof JsonObject) {
					JsonObject obj = (JsonObject) val;
					Attack a = new Attack();
					a.setId(obj.getInt("aid"));
					a.setSeason(obj.getInt("season"));
					a.setRound(obj.getInt("round"));
					a.setStarSystemId(obj.getInt("starsystem"));
					a.setStarSystemDataId(obj.getInt("starsystemdata"));
					a.setAttackedFromStarSystem(obj.getInt("attackedfromstarsystem"));
					a.setAttackerId(obj.getInt("attacker"));
					a.setAttackerId(obj.getInt("defender"));
					Universe.attacks.add(a);
				}
			}
		}
	}

	private static void createJumpships(JsonValue value) {
		JsonObject object;
		if (value.getValueType() == JsonValue.ValueType.OBJECT) {
			object = (JsonObject) value;
			for (Entry<String, JsonValue> set : object.entrySet()) {
				if (set.getValue() instanceof JsonArray) {
					createJumpships(set.getValue());
				}
			}
		} else if (value.getValueType() == JsonValue.ValueType.ARRAY) {
			JsonArray array = (JsonArray) value;
			for (JsonValue val : array) {
				if (val instanceof JsonObject) {
					JsonObject obj = (JsonObject) val;
					Jumpship js = new Jumpship();
					js.setShipID(obj.getInt("jsid"));
					js.setShipName(obj.getString("jumpshipName"));
					js.setFactionID(obj.getInt("jumpshipFactionID"));
					js.setStarSystemHistory(obj.getString("starHist"));
					js.setLastMovedInRound(obj.getInt("lastMovedInRound"));
					if (obj.getInt("attackReady") == 1) {
						js.setCombatReady(true);
					} else {
						js.setCombatReady(false);
					}
					Universe.jumpships.put(js.getShipName(), js);
				}
			}
		}
	}

	private void initUniverse() throws IOException {
		FileReader fr;
		JsonStructure struct;
		fr = new FileReader("mapdata_Factions.json");
		JsonReader reader = Json.createReader(fr);
		struct = reader.read();
		JsonValue value = struct;
		createFactions(value);
		reader.close();
		fr.close();

		FileReader fr2;
		JsonStructure struct2;
		fr2 = new FileReader("mapdata_HH_StarSystems.json");
		JsonReader reader2 = Json.createReader(fr2);
		struct2 = reader2.read();
		JsonValue value2 = struct2;
		createUniverse(value2);
		reader2.close();
		fr2.close();

		FileReader fr3;
		JsonStructure struct3;
		fr3 = new FileReader("mapdata_HH_Attacks.json");
		JsonReader reader3 = Json.createReader(fr3);
		struct3 = reader3.read();
		JsonValue value3 = struct3;
		createAttacks(value3);
		reader3.close();
		fr3.close();

		FileReader fr4;
		JsonStructure struct4;
		fr4 = new FileReader("mapdata_HH_Jumpships.json");
		JsonReader reader4 = Json.createReader(fr4);
		struct4 = reader4.read();
		JsonValue value4 = struct4;
		createJumpships(value4);
		reader4.close();
		fr4.close();
	}

	@Override
	public void start(Stage stage) {
		try {
			initUniverse();

			PannableCanvas canvas = new PannableCanvas();
			canvas.setTranslateX(Config.MAP_INITIAL_TRANSLATE_X);
			canvas.setTranslateY(Config.MAP_INITIAL_TRANSLATE_Y);

			// create sample nodes which can be dragged
			NodeGestures nodeGestures = new NodeGestures(canvas);

			for (StarSystem starSystem : Universe.starSystems.values()) {
				String name = starSystem.getName();
				Integer id = starSystem.getId();
				double x = starSystem.getScreenX();
				double y = starSystem.getScreenY();

				Group starSystemGroup = new Group();
				starSystemGroup.setId(id.toString());
				StackPane stackPane = new StackPane();

				Label starSystemLabel = new Label(name);
				starSystemLabel.setCacheHint(CacheHint.SCALE);
				starSystemLabel.setPadding(new Insets(25, 0, 0, 0));
				starSystemLabel.setStyle("-fx-font-family:'Arial';-fx-font-size:10px;-fx-text-fill:#ffffff;");

				starSystem.setStarSystemLabel(starSystemLabel);
				stackPane.getChildren().add(0, starSystemLabel);

				String colorString = Universe.factions.get(starSystem.getAffiliation()).getColor();
				Color c = Color.web(colorString);
				Circle starSystemCircle = new Circle(4);
				starSystemCircle.setId(starSystem.getId().toString());
				starSystemCircle.setStroke(c.deriveColor(1, 1, 1, 0.8));
				starSystemCircle.setFill(c.deriveColor(1, 1, 1, 0.4));
				starSystemCircle.setVisible(true);
				starSystemCircle.toFront();
				starSystemCircle.setCacheHint(CacheHint.SCALE);
				starSystemCircle.addEventFilter(MouseEvent.MOUSE_ENTERED, nodeGestures.getOnStarSystemHoverEnteredEventHandler());
				starSystemCircle.addEventFilter(MouseEvent.MOUSE_EXITED, nodeGestures.getOnStarSystemHoverExitedEventHandler());
				starSystemCircle.addEventFilter(MouseDragEvent.MOUSE_DRAG_ENTERED, nodeGestures.getOnStarSystemDragEnteredEventHandler());
				starSystemCircle.addEventFilter(MouseDragEvent.MOUSE_DRAG_EXITED, nodeGestures.getOnStarSystemDragExitedEventHandler());

				starSystem.setStarSystemCircle(starSystemCircle);
				stackPane.getChildren().add(1, starSystemCircle);

				starSystemGroup.getChildren().add(stackPane);
				starSystemGroup.setTranslateX(x);
				starSystemGroup.setTranslateY(y);
				starSystemGroup.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMouseClickedEventHandler());

				starSystem.setStarSystemStackPane(stackPane);
				starSystem.setStarSystemGroup(starSystemGroup);
				canvas.getChildren().add(starSystemGroup);
			}

			canvas.addStarPane();
			canvas.addGrid_Center();
			canvas.addGrid_500();
			canvas.addGrid_250();
			canvas.setVisibility();

			Circle circle1 = new Circle(3000, 3000, 40);
			circle1.setStroke(Color.ORANGE);
			circle1.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));
			circle1.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
			circle1.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
			canvas.getChildren().add(circle1);

			Pane borders = VoronoiDelaunay.getAreas();
			canvas.getChildren().add(borders);
			borders.toBack();

			// Attacks pane
			Pane attacksPane = new Pane();
			canvas.setAttacksPane(attacksPane);

			for (Attack attack : Universe.attacks) {
				if (attack.getSeason().equals(Universe.currentSeason) && attack.getRound().equals(Universe.currentRound)) {
					StarSystem attackedSystem;
					StarSystem attackerStartedFromSystem;

					attackedSystem = Universe.starSystems.get(attack.getStarSystemId());
					attackerStartedFromSystem = Universe.starSystems.get(attack.getAttackedFromStarSystem());

					if (attackedSystem != null && attackerStartedFromSystem != null) {
						if (Config.MAP_FLASH_ATTACKED_SYSTEMS) {
							PointD[] points = attackedSystem.getVoronoiRegion();
							if (points != null) {
								Circle circle = new Circle(attackedSystem.getScreenX(), attackedSystem.getScreenY(), Config.MAP_BACKGROUND_AREA_RADIUS);
								circle.setVisible(false);
								Shape systemBackground = Shape.intersect(new Polygon(PointD.toDoubles(points)), circle);
								String colorString = Universe.factions.get(attackerStartedFromSystem.getAffiliation()).getColor();
								Color c = Color.web(colorString);
								systemBackground.setFill(c);
								FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.0), (systemBackground));
								fadeTransition.setFromValue(0.5);
								fadeTransition.setToValue(0.0);
								fadeTransition.setAutoReverse(true);
								fadeTransition.setCycleCount(Animation.INDEFINITE);
								fadeTransition.play();
								canvas.getChildren().add(systemBackground);
								systemBackground.setVisible(true);
								systemBackground.toBack();
							}
						}

						double attackedSysX = attackedSystem.getScreenX();
						double attackedSysY = attackedSystem.getScreenY();
						double attackedFromSysX = attackerStartedFromSystem.getScreenX();
						double attackedFromSysY = attackerStartedFromSystem.getScreenY();

						Line line = new Line(attackedSysX, attackedSysY, attackedFromSysX, attackedFromSysY);
						line.getStrokeDashArray().setAll(50d, 20d, 5d, 20d);
						line.setStrokeWidth(3);
						line.setStroke(Color.RED);
						line.setStrokeLineCap(StrokeLineCap.ROUND);

						final double maxOffset = line.getStrokeDashArray().stream().reduce(0d, (a, b) -> a + b);

						Timeline timeline = new Timeline(
								new KeyFrame(
										Duration.ZERO,
										new KeyValue(
												line.strokeDashOffsetProperty(),
												0,
												Interpolator.LINEAR
										)
								),
								new KeyFrame(
										Duration.seconds(1),
										new KeyValue(
												line.strokeDashOffsetProperty(),
												maxOffset,
												Interpolator.LINEAR
										)
								)
						);
						timeline.setCycleCount(Timeline.INDEFINITE);
						timeline.play();
						attacksPane.getChildren().add(line);
					}
				}
			}
			canvas.getChildren().add(attacksPane);
			attacksPane.toBack();

			for (Jumpship js : Universe.jumpships.values()) {
				// TODO: Jumpships
				Integer currentSystemID = js.getCurrentSystemID();
				ArrayList<Integer> hist = js.getStarSystemHistoryArray();

				if (currentSystemID != null) {
					ImageView jumpshipImage;
					if (js.isCombatReady()) {
						jumpshipImage = new ImageView(new Image(StarMap.class.getResourceAsStream("/images/map/jumpship_left_blue.png")));
						jumpshipImage.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
						jumpshipImage.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
						jumpshipImage.addEventFilter(MouseEvent.DRAG_DETECTED, nodeGestures.getOnMouseDragDetectedEventHandler());
						jumpshipImage.addEventFilter(MouseEvent.MOUSE_RELEASED, nodeGestures.getOnMouseReleasedEventHandler());
					} else {
						jumpshipImage = new ImageView(new Image(StarMap.class.getResourceAsStream("/images/map/jumpship_left_red.png")));
					}
					jumpshipImage.setId(js.getShipName());
					jumpshipImage.setPreserveRatio(true);
					jumpshipImage.setFitWidth(30);
					jumpshipImage.setCacheHint(CacheHint.QUALITY);
					jumpshipImage.setSmooth(false);
					jumpshipImage.setTranslateX(Universe.starSystems.get(currentSystemID).getScreenX() - 35);
					jumpshipImage.setTranslateY(Universe.starSystems.get(currentSystemID).getScreenY() - 8);
					jumpshipImage.setMouseTransparent(false);
					jumpshipImage.toFront();
					jumpshipImage.setVisible(false);
					canvas.getChildren().add(jumpshipImage);

					js.setJumpshipImage(jumpshipImage);
				}
			}

			String image = "images/map/background.jpg";
			String style = "";
			style = style + "-fx-background-image:url('";
			style = style + image;
			style = style + "');-fx-background-position:center center;-fx-background-repeat:repeat;";

			Pane p = new Pane();
			p.setStyle(style);
			p.getChildren().add(canvas);

			// create scene which can be dragged and zoomed
			Scene scene = new Scene(p, 1024, 768);

			SceneGestures sceneGestures = new SceneGestures(canvas);
			scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
			scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
			scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
			canvas.addEventFilter(MouseEvent.MOUSE_MOVED, sceneGestures.getOnMouseMovedEventHandler());

			stage.setScene(scene);
			stage.show();

			// do this after stage.show in order for the stackpane to have an actual size!
			for (StarSystem ss : Universe.starSystems.values()) {
				StackPane sp = ss.getStarSystemStackPane();
				Group g = ss.getStarSystemGroup();
				g.setLayoutX(-sp.getWidth() / 2);
				g.setLayoutY(-sp.getHeight() / 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
