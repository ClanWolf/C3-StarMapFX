package net.clanwolf.c3.starmap;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javax.json.*;
import javax.json.JsonValue.ValueType;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

class PannableCanvas extends Pane {

	private DoubleProperty myScale = new SimpleDoubleProperty(1.0);
	private HashMap<String, StarSystem> universe = null;
	private Canvas grid1 = null;
	private Canvas grid2 = null;
	private Pane starPane = null;
	private HashMap<String, ArrayList<Circle>> starPanelsStarLists = new HashMap<>();

	private int mapWidth = 6000;
	private int mapHeight = 6000;

	private boolean starSystemLabelsVisible = true;

	private Circle starSystemMarkerCircle30ly;
	private Circle starSystemMarkerCircle60ly;

	PannableCanvas() {
		setPrefSize(mapWidth, mapHeight);
		setStyle("-fx-background-color:transparent;-fx-border-width:10px;-fx-border-color:gray;");

		// add scale transform
		scaleXProperty().bind(myScale);
		scaleYProperty().bind(myScale);

		double radius2 = 300;
		starSystemMarkerCircle60ly = new Circle(radius2);
		starSystemMarkerCircle60ly.setStroke(new Color(1, 1, 1, 0.1));
		starSystemMarkerCircle60ly.setFill(new Color(1, 1, 1, 0.05));
		starSystemMarkerCircle60ly.setVisible(false);

		double radius = 150;
		starSystemMarkerCircle30ly = new Circle(radius);
		starSystemMarkerCircle30ly.setStroke(new Color(1, 1, 1, 0.1));
		starSystemMarkerCircle30ly.setFill(new Color(1, 1, 1, 0.05));
		starSystemMarkerCircle30ly.setVisible(false);

		this.getChildren().add(starSystemMarkerCircle60ly);
		this.getChildren().add(starSystemMarkerCircle30ly);
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
	public void addStarPane(String level, int number) {
		ArrayList<Circle> l = new ArrayList<>();

		double w = mapWidth;
		double h = mapHeight;

		if (starPane == null) {
			starPane = new Pane();
			starPane.setMaxWidth(w);
			starPane.setMaxHeight(h);
			starPane.setMouseTransparent(true);
		}

		for (int i = 0; i < number; i++) {
			double x = (((Math.random()) * w + 1));
			double y = w - (((Math.random()) * h + 1));
			int size = (int) ((Math.random()) * 4 + 1);

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

	public void moveBackgroundStarPane(String level, double x, double y) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				ArrayList<Circle> l = starPanelsStarLists.get(level);
				for (Circle c : l) {
					c.setCenterX(c.getCenterX() + x);
					c.setCenterY(c.getCenterY() + y);
				}
			}
		});
	}

	/**
	 * Add a grid to the canvas, send it to back
	 */
	public void addGrid1() {
		double w = mapWidth;
		double h = mapHeight;

		grid1 = new Canvas(w, h);
		grid1.setMouseTransparent(true);

		GraphicsContext gc = grid1.getGraphicsContext2D();
		gc.setStroke(Color.ORANGE);
		gc.setLineWidth(1);

		// draw grid lines
		double offset = 500;
		for (double i = offset; i < w; i += offset) {
			gc.strokeLine(i, 0, i, h);
			gc.strokeLine(0, i, w, i);
		}
		getChildren().add(grid1);
		grid1.toBack();
	}

	private void setGrid1Visible() {
		if (grid1 != null) {

			double zoomLevelToHideGrid1 = .4;

			if (myScale.get() >= zoomLevelToHideGrid1) {
				grid1.setVisible(true);
			} else if (myScale.get() < zoomLevelToHideGrid1) {
				grid1.setVisible(false);
			}
		}
	}

	/**
	 * Add a grid to the canvas, send it to back
	 */
	public void addGrid2() {
		double w = mapWidth;
		double h = mapHeight;

		grid2 = new Canvas(w, h);
		grid2.setMouseTransparent(true);

		GraphicsContext gc = grid2.getGraphicsContext2D();
		gc.setStroke(Color.GRAY);
		gc.setLineWidth(1);

		// draw grid lines
		double offset = 500;
		for (double i = 250; i < w; i += offset) {
			gc.strokeLine(i, 0, i, h);
			gc.strokeLine(0, i, w, i);
		}
		getChildren().add(grid2);
		grid2.toBack();
	}

	private void setGrid2Visible() {
		if (grid2 != null) {

			double zoomLevelToHideGrid2 = .7;

			if (myScale.get() >= zoomLevelToHideGrid2) {
				grid2.setVisible(true);
			} else if (myScale.get() < zoomLevelToHideGrid2) {
				grid2.setVisible(false);
			}
		}
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
		setGrid1Visible();
		setGrid2Visible();
	}

	private void setStarSystemLabelsVisible() {
		if (universe != null) {

			double zoomLevelToHideStarSystemLabels = 1.0;

			if (myScale.get() >= zoomLevelToHideStarSystemLabels) {
				if (!starSystemLabelsVisible) {
					starSystemLabelsVisible = true;
					for (StarSystem ss : universe.values()) {
						Label l = ss.getStarSystemLabel();
						l.setVisible(starSystemLabelsVisible);
					}
				}
			} else if (myScale.get() < zoomLevelToHideStarSystemLabels) {
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

	public void setStarSystemLabels(HashMap<String, StarSystem> universe) {
		this.universe = universe;
		setStarSystemLabelsVisible();
		setGrid1Visible();
		setGrid2Visible();
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

	private static final double MAX_SCALE = 3.0d;
	private static final double MIN_SCALE = .2d;
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
			canvas.moveBackgroundStarPane("level1", 1 * multix,1 * multiy);
			canvas.moveBackgroundStarPane("level2", 2 * multix,2 * multiy);
			canvas.moveBackgroundStarPane("level3", 4 * multix,4 * multiy);

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

			scale = clamp(scale, MIN_SCALE, MAX_SCALE);
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
		double universeX = screenX - 3000;
		universeX = universeX / 5;
		return universeX;
	}

	private double getUniverseY(double screenY) {
		double universeY = screenY - 3000;
		universeY = universeY / 5;
		return universeY;
	}
}

/**
 * An application with a zoomable and pannable canvas.
 */
public class StarMap extends Application {

	private static HashMap<String, StarSystem> universe = new HashMap<>();
	private static HashMap<String, Faction> factions = new HashMap<>();

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

					universe.put(name, s);
				}
			}
		}
	}

	public void init() throws IOException {
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
	}

	@Override
	public void start(Stage stage) {
		try {
			init();

			PannableCanvas canvas = new PannableCanvas();
			canvas.setTranslateX(-2500);
			canvas.setTranslateY(-1620);

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
			canvas.addStarPane("level1", 300);
			canvas.addStarPane("level2", 200);
			canvas.addStarPane("level3", 100);
			canvas.addGrid1();
			canvas.addGrid2();
			canvas.setStarSystemLabels(universe);

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
				System.out.println(sp.getWidth());
				Group g = ss.getStarSystemGroup();
				g.setLayoutX(-sp.getWidth() / 2);
				g.setLayoutY(-sp.getHeight() / 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
