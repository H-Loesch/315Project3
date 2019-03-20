package mancala;

import java.util.Vector;

import javafx.application.Application;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

//decent widthfor each pit is about 3 mouse cursors wide. So that's about 100 pixels each. 

public class Test1 extends Application {
	public static void main() {
		launch();
	}

	@Override
	public void start(Stage primary) throws Exception {
		primary.setTitle("Mancala!");
		Pane canvas = new Pane();
		canvas.setStyle("-fx-background-color: burlywood;");
		//canvas.getChildren().addAll(placeInitialShapes(pits));
		primary.setScene(new Scene(canvas)); //sets stage to show the scene
 		primary.show(); //shows the scene in the newly-created application
	}
	
	//take in vector of pits, return vector of shapes
	/*private Vector<javafx.scene.Node> placeInitialShapes(Vector<Pit> pits) {
		//set up all the shapes required for the gameboard: pits, stores, initial stone placements, etc.
	}
	
	private Vector<Pit> initializePits() {
		//initialize location, all that for the pits. 
		//don't yet initialize the stones that will go in them. Or maybe do, idk.
		return 
	}*/
	
	//it looks like there's stuff set up to allow events to fire when the user hovers over something, and stuff like that 
	//which is good to know! That was something of a worry.
}