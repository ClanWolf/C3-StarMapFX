module net.clanwolf.starmap.client.starmap {
	requires tektosyne;

	requires java.desktop;

	requires javafx.graphics;
	requires javafx.controls;
	requires javafx.media;
	requires javafx.fxml;
	requires java.logging;
	requires java.json;

	exports net.clanwolf.c3.client.starmap to javafx.graphics;
}
