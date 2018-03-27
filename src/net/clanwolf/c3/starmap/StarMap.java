package net.clanwolf.c3.starmap;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.json.*;
import javax.json.JsonValue.ValueType;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import static net.clanwolf.c3.starmap.Config.BACKGROUND_STARS_LAYERS;

class PannableCanvas extends Pane {
	private DoubleProperty myScale = new SimpleDoubleProperty(1.0);
	private HashMap<String, StarSystem> universe = null;
	private Canvas grid_500 = null;
	private Canvas grid_250 = null;
	private Canvas grid_center = null;
	private Pane starPane = null;
	private Pane attacksPane = null;
	private ArrayList<ImageView> jumpshipIcons = null;
	private HashMap<Integer, ArrayList<Circle>> starPanelsStarLists = new HashMap<>();

	private boolean starSystemLabelsVisible = true;
	private boolean starSystemCirclesVisible = true;
	private boolean attacksVisible = true;
	private boolean jumpshipsVisible = false;

	private Circle starSystemMarkerCircle30ly;
	private Circle starSystemMarkerCircle60ly;

	PannableCanvas() {
		setPrefSize(Config.MAP_WIDTH, Config.MAP_HEIGHT);
		setStyle("-fx-background-color:transparent;-fx-border-width:10px;-fx-border-color:gray;");

		// add scale transform
		scaleXProperty().bind(myScale);
		scaleYProperty().bind(myScale);

		double radius2 = 30 * 2 * Config.MAP_COORDINATES_MULTIPLICATOR; // 60 Lightyears
		starSystemMarkerCircle60ly = new Circle(radius2);
		starSystemMarkerCircle60ly.setStroke(Config.MAP_RANGE_CIRCLE_COLOR.deriveColor(.5, .5, .5, 1.0));
		starSystemMarkerCircle60ly.setStrokeWidth(2);
		starSystemMarkerCircle60ly.setFill(Config.MAP_RANGE_CIRCLE_COLOR.deriveColor(.5, .5, .5, 0.2));
		starSystemMarkerCircle60ly.setVisible(false);

		double radius = 30 * Config.MAP_COORDINATES_MULTIPLICATOR; // 30 Lightyears
		starSystemMarkerCircle30ly = new Circle(radius);
		starSystemMarkerCircle30ly.setStroke(Config.MAP_RANGE_CIRCLE_COLOR.deriveColor(1, 1, 1, 1.0));
		starSystemMarkerCircle30ly.setStrokeWidth(2);
		starSystemMarkerCircle30ly.setFill(Config.MAP_RANGE_CIRCLE_COLOR.deriveColor(1, 1, 1, 0.2));
		starSystemMarkerCircle30ly.setVisible(false);

		this.getChildren().add(starSystemMarkerCircle60ly);
		this.getChildren().add(starSystemMarkerCircle30ly);
	}

	public void setAttacksPane(Pane attacksPane) {
		this.attacksPane = attacksPane;
	}

	public void setJumpshipIcons(ArrayList<ImageView> jumpshipIcons) {
		this.jumpshipIcons = jumpshipIcons;
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

		for (int[] layer : BACKGROUND_STARS_LAYERS) {
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
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ArrayList<Circle> l = starPanelsStarLists.get(level);
				for (Circle c : l) {
					c.setCenterX(c.getCenterX() + x);
					c.setCenterY(c.getCenterY() + y);
				}
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
		if (universe != null) {
			if (myScale.get() >= Config.zoomLevelToHideStarSystemLabels) {
				if (!starSystemLabelsVisible) {
					starSystemLabelsVisible = true;
					for (StarSystem ss : universe.values()) {
						Label l = ss.getStarSystemLabel();
						l.setVisible(starSystemLabelsVisible);
					}
				}
			} else if (myScale.get() < Config.zoomLevelToHideStarSystemLabels) {
				if (starSystemLabelsVisible) {
					starSystemLabelsVisible = false;
					for (StarSystem ss : universe.values()) {
						Label l = ss.getStarSystemLabel();
						l.setVisible(starSystemLabelsVisible);
					}
				}
			}
		}
	}

	private void setStarSystemCirclesVisible() {
		if (universe != null) {
			if (myScale.get() >= Config.zoomLevelToHideStarSystemCircles) {
				starSystemCirclesVisible = true;
				for (StarSystem ss : universe.values()) {
					Circle c = ss.getStarSystemCircle();
					c.setVisible(starSystemCirclesVisible);
				}
			} else if (myScale.get() < Config.zoomLevelToHideStarSystemCircles) {
				starSystemCirclesVisible = false;
				for (StarSystem ss : universe.values()) {
					Circle c = ss.getStarSystemCircle();
					c.setVisible(starSystemCirclesVisible);
				}
			}
		}
	}

	private void setAttacksVisible() {
		if (universe != null && attacksPane != null) {
			if (myScale.get() >= Config.zoomLevelToHideAttacks) {
				attacksVisible = true;
				attacksPane.setVisible(attacksVisible);
			} else if (myScale.get() < Config.zoomLevelToHideAttacks) {
				attacksVisible = false;
				attacksPane.setVisible(attacksVisible);
				for (ImageView iv : jumpshipIcons) {
					iv.setVisible(attacksVisible);
				}
			}
			if (myScale.get() >= Config.zoomLevelToHideJumpships) {
				jumpshipsVisible = true;
				for (ImageView iv : jumpshipIcons) {
					iv.setVisible(jumpshipsVisible);
				}
			} else if (myScale.get() < Config.zoomLevelToHideJumpships) {
				jumpshipsVisible = false;
				for (ImageView iv : jumpshipIcons) {
					iv.setVisible(jumpshipsVisible);
				}
			}
		}
	}

	public void setVisibility(HashMap<String, StarSystem> universe) {
		this.universe = universe;
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
	private HashMap<String, StarSystem> universe;
	private PannableCanvas canvas;

	NodeGestures(PannableCanvas canvas, HashMap<String, StarSystem> universe) {
		this.universe = universe;
		this.canvas = canvas;
	}

	@SuppressWarnings("unused")
	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
		return getOnMouseClickedEventHandler;
	}

	@SuppressWarnings("unused")
	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	public EventHandler<MouseEvent> getOnStarSystemHoverEnteredEventHandler() { return onStarSystemHovereEnteredEventHandler; }

	public EventHandler<MouseEvent> getOnStarSystemHoverExitedEventHandler() { return onStarSystemHovereExitedEventHandler; }

	private EventHandler<MouseEvent> onStarSystemHovereEnteredEventHandler = new EventHandler<MouseEvent>() {
		public void handle (MouseEvent event){
			if (Config.MAP_HIGHLIGHT_HOVERED_STARSYSTEM) {
				Node node = (Node) event.getSource();
				Circle c = (Circle) node;
				c.setRadius(8);
			}
		}
	};

	private EventHandler<MouseEvent> onStarSystemHovereExitedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			if (Config.MAP_HIGHLIGHT_HOVERED_STARSYSTEM) {
				Node node = (Node) event.getSource();
				Circle c = (Circle) node;
				c.setRadius(5);
			}
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
			StarSystem clickedStarSystem = universe.get(node.getId());
			System.out.println("System: " + clickedStarSystem.getName() + " (x: " + clickedStarSystem.getX() + " | y: " + clickedStarSystem.getY() + ")");

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
			Node node = (Node) event.getSource();

			node.setTranslateX(
					nodeDragContext.translateAnchorX + ((event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
			node.setTranslateY(
					nodeDragContext.translateAnchorY + ((event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));
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

	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	public EventHandler<ScrollEvent> getOnScrollEventHandler() {
		return onScrollEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseMovedEventHandler() {
		return onMouseMovedEventHandler;
	}

	private EventHandler<MouseEvent> onMouseMovedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			// Fire an action to inform the surrounding frame about the currently hovered universe coordinates
			System.out.println("[" + getUniverseX(event.getX()) + ", " + getUniverseY(event.getY()) + "]");
		}
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
			// TODO: Check whether borders are reached and panning must be prevented

			double diffX = sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX;
			double diffY = sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY;
			canvas.setTranslateX(diffX);
			canvas.setTranslateY(diffY);

			double multix = 0;
			double multiy = 0;
			double x = event.getX();
			double y = event.getY();
			if (x < previousX) {
				multix = -1;
			} else if (x > previousX) {
				multix = 1;
			}
			if (y < previousY) {
				multiy = -1;
			} else if (y > previousY) {
				multiy = 1;
			}
			for (int[] layer : BACKGROUND_STARS_LAYERS) {
				int level = layer[0];
				int factor = layer[2];
				canvas.moveBackgroundStarPane(level, factor * multix, factor * multiy);
			}

			previousX = x;
			previousY = y;

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

			scale = clamp(scale, Config.MAP_MIN_SCALE, Config.MAP_MAX_SCALE);
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

	private static double clamp(double value, double min, double max) {
		if (Double.compare(value, min) < 0) {
			return min;
		}
		if (Double.compare(value, max) > 0) {
			return max;
		}
		return value;
	}

	private double getUniverseX(double screenX) {
		double universeX = screenX - (Config.MAP_WIDTH / 2);
		universeX = universeX / Config.MAP_COORDINATES_MULTIPLICATOR;
		return universeX;
	}

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

	private static HashMap<String, StarSystem> universe = new HashMap<>();
	private static HashMap<String, Faction> factions = new HashMap<>();
	private static ArrayList<Attack> attacks = new ArrayList<>();

	private Integer currentSeason = 1;
	private Integer currentRound = 6;

	public static void main(String[] args) {
		launch(args);
	}

	private static void createFactions(JsonValue value) {
		JsonObject object;
		if (value.getValueType() == ValueType.OBJECT) {
			object = (JsonObject) value;
			for (Entry<String, JsonValue> set : object.entrySet()) {
				if (set.getValue() instanceof JsonArray) {
					createFactions(set.getValue());
				}
			}
		} else if (value.getValueType() == ValueType.ARRAY) {
			JsonArray array = (JsonArray) value;
			for (JsonValue val : array) {
				if (val instanceof JsonObject) {
					JsonObject obj = (JsonObject) val;
					Faction f = new Faction();
					f.setName(obj.getString("name"));
					f.setShortName(obj.getString("short"));
					f.setColor(obj.getString("color"));

					factions.put(f.getShortName(), f);
				}
			}
		}
	}

	private static void createUniverse(JsonValue value) {
		JsonObject object;
		if (value.getValueType() == ValueType.OBJECT) {
			object = (JsonObject) value;
			for (Entry<String, JsonValue> set : object.entrySet()) {
				if (set.getValue() instanceof JsonArray) {
					createUniverse(set.getValue());
				}
			}
		} else if (value.getValueType() == ValueType.ARRAY) {
			JsonArray array = (JsonArray) value;
			for (JsonValue val : array) {
				if (val instanceof JsonObject) {
					JsonObject obj = (JsonObject) val;
					StarSystem s = new StarSystem();
					String name = obj.getString("name");
					s.setName(name);
					s.setX(obj.getJsonNumber("x").bigDecimalValue());
					s.setY(obj.getJsonNumber("y").bigDecimalValue());
					s.setAffiliation(obj.getString("affiliation"));
					s.setId(obj.getInt("sid"));

					universe.put(name, s);
				}
			}
		}
	}

	private static void createAttacks(JsonValue value) {
		JsonObject object;
		if (value.getValueType() == ValueType.OBJECT) {
			object = (JsonObject) value;
			for (Entry<String, JsonValue> set : object.entrySet()) {
				if (set.getValue() instanceof JsonArray) {
					createAttacks(set.getValue());
				}
			}
		} else if (value.getValueType() == ValueType.ARRAY) {
			JsonArray array = (JsonArray) value;
			for (JsonValue val : array) {
				if (val instanceof JsonObject) {
					JsonObject obj = (JsonObject) val;
					Attack a = new Attack();
					a.setId(obj.getInt("aid"));
					a.setSeason(obj.getInt("season"));
					a.setRound(obj.getInt( "round"));
					a.setStarSystemId(obj.getInt("starsystem"));
					a.setStarSystemDataId(obj.getInt("starsystemdata"));
					a.setAttackedFromStarSystem(obj.getInt("attackedfromstarsystem"));
					a.setAttackerId(obj.getInt("attacker"));
					a.setAttackerId(obj.getInt("defender"));
					attacks.add(a);
				}
			}
		}
	}

	public void initUniverse() throws IOException {
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
		fr2 = new FileReader("HH_PlanetData.json");
		JsonReader reader2 = Json.createReader(fr2);
		struct2 = reader2.read();
		JsonValue value2 = struct2;
		createUniverse(value2);
		reader2.close();
		fr2.close();

		FileReader fr3;
		JsonStructure struct3;
		fr3 = new FileReader("HH_Attacks.json");
		JsonReader reader3 = Json.createReader(fr3);
		struct3 = reader3.read();
		JsonValue value3 = struct3;
		createAttacks(value3);
		reader3.close();
		fr3.close();
	}

	@Override
	public void start(Stage stage) {
		try {
			initUniverse();

			PannableCanvas canvas = new PannableCanvas();
			canvas.setTranslateX(Config.MAP_INITIAL_TRANSLATE_X);
			canvas.setTranslateY(Config.MAP_INITIAL_TRANSLATE_Y);

			// create sample nodes which can be dragged
			NodeGestures nodeGestures = new NodeGestures(canvas, universe);

			for (StarSystem starSystem : universe.values()) {
				String name = starSystem.getName();
				double x = starSystem.getScreenX();
				double y = starSystem.getScreenY();

				Group starSystemGroup = new Group();
				starSystemGroup.setId(name);
				StackPane stackPane = new StackPane();

				Label starSystemLabel = new Label(name);
				starSystemLabel.setCacheHint(CacheHint.SCALE);
				starSystemLabel.setPadding(new Insets(25, 0, 0, 0));
				starSystemLabel.setStyle("-fx-font-family:'Arial';-fx-font-size:10px;-fx-text-fill:#ffffff;");

				starSystem.setStarSystemLabel(starSystemLabel);
				stackPane.getChildren().add(0, starSystemLabel);

				String colorString = factions.get(starSystem.getAffiliation()).getColor();
				Color c = Color.web(colorString);
				Circle starSystemCircle = new Circle(4);
				starSystemCircle.setStroke(c.deriveColor(1, 1, 1, 0.8));
				starSystemCircle.setFill(c.deriveColor(1, 1, 1, 0.4));
				starSystemCircle.setVisible(true);
				starSystemCircle.toFront();
				starSystemCircle.setCacheHint(CacheHint.SCALE);
				starSystemCircle.addEventFilter(MouseEvent.MOUSE_ENTERED, nodeGestures.getOnStarSystemHoverEnteredEventHandler());
				starSystemCircle.addEventFilter(MouseEvent.MOUSE_EXITED, nodeGestures.getOnStarSystemHoverExitedEventHandler());

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
			canvas.setVisibility(universe);

			Circle circle1 = new Circle( 3000, 3000, 40);
			circle1.setStroke(Color.ORANGE);
			circle1.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));
			circle1.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
			circle1.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
			canvas.getChildren().add(circle1);
















			// Attacks pane
			Pane attacksPane = new Pane();
			ArrayList<ImageView> jumpshipIcons = new ArrayList<>();
			canvas.setAttacksPane(attacksPane);
			canvas.setJumpshipIcons(jumpshipIcons);

			for (Attack attack : attacks) {
				if (attack.getSeason().equals(currentSeason) && attack.getRound().equals(currentRound)) {

					System.out.println(attack.getId() + " --- " + attack.getSeason() + " : " + currentSeason + " | " + attack.getRound() + " : " + currentRound);

					StarSystem attackedSystem = null;
					StarSystem attackerStartedFromSystem = null;

					for (StarSystem ss : universe.values()) {
						if (attack.getStarSystemId().equals(ss.getId())) {
							attackedSystem = ss;
						}
						if (attack.getAttackedFromStarSystem().equals(ss.getId())) {
							attackerStartedFromSystem = ss;
						}
					}

					if (attackedSystem != null && attackerStartedFromSystem != null) {
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

						ImageView jumpshipImage = new ImageView(new Image(StarMap.class.getResource("jumpship_left_red.png").toExternalForm()));
						jumpshipImage.setPreserveRatio(true);
						jumpshipImage.setFitWidth(30);
						jumpshipImage.setCacheHint(CacheHint.QUALITY);
						jumpshipImage.setSmooth(false);
						jumpshipImage.setTranslateX(attackedSystem.getScreenX() - 35);
						jumpshipImage.setTranslateY(attackedSystem.getScreenY() - 8);
						jumpshipImage.setMouseTransparent(false);
						jumpshipImage.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
						jumpshipImage.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
						canvas.getChildren().add(jumpshipImage);
						jumpshipIcons.add(jumpshipImage);
						jumpshipImage.toFront();
						jumpshipImage.setVisible(false);
					}
				}
			}
			canvas.getChildren().add(attacksPane);
			attacksPane.toBack();





			Pane borders = GeoTools.getAreas(universe, factions);
			canvas.getChildren().add(borders);
			borders.toBack();

			String image = StarMap.class.getResource("background.jpg").toExternalForm();
			String style = "";
			style = style + "-fx-background-image: url('";
			style = style + image;
			style = style + "'); -fx-background-position: center center;-fx-background-repeat: repeat;";

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
			for (StarSystem ss : universe.values()) {
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
