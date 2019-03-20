package mancala;

import java.util.Vector;

import javafx.application.Application;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import mancala.Pit;

//decent widthfor each pit is about 3 mouse cursors wide. So that's about 100 pixels each. 
//TODO find some way to center the mancala board regardless of the windows' size?
//TODO add stones
//TODO add stores

public class Test1 extends Application {
	Vector<Pit> pits = initializePits();
	
	public static void main() {
		launch();
	}

	@Override
	public void start(Stage primary) throws Exception {
		primary.setTitle("Mancala!");
		Pane canvas = new Pane();
		canvas.setPrefSize(1080, 720);
		canvas.setStyle("-fx-background-color: burlywood;");
		pits = initializePits();
		canvas.getChildren().addAll(placeInitialShapes(pits));
		primary.setScene(new Scene(canvas)); //sets stage to show the scene
 		primary.show(); //shows the scene in the newly-created application
	}
	
	private Vector<Pit> initializePits() {
		//initialize location, all that for the pits. 
		//don't yet initialize the stones that will go in them. Or maybe do, idk.
		Vector<Pit> working = new Vector<Pit>();
		for (int i = 1; i < 7; i++) {
			for (int j = 1; j < 3; j++) {
				Pit working_pit = new Pit(j, 85 + 130 * i, 120 + 140 * j, i * j);
				working.add(working_pit);
			}
		}
		return working;
	}
	
	//take in vector of pits, return vector of shapes
	private Vector<javafx.scene.Node> placeInitialShapes(Vector<Pit> _pits) {
		//set up all the shapes required for the gameboard: pits, stores, initial stone placements, etc.
		Vector<javafx.scene.Node> working = new Vector<javafx.scene.Node>();
		for (int i = 0; i < 12; i++) {
			Circle temp = new Circle(_pits.get(i).x_location, _pits.get(i).y_location, 60);
			temp.setFill(Color.SADDLEBROWN);
			working.add(temp);
		}
		return working;
	}
	
	//it looks like there's stuff set up to allow events to fire when the user hovers over something, and stuff like that 
	//which is good to know! That was something of a worry.
}