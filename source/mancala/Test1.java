package mancala;

import java.util.Vector;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


//decent widthfor each pit is about 3 mouse cursors wide. So that's about 100 pixels each. 
//TODO find some way to center the mancala board regardless of the windows' size?
//TODO add stones
//TODO add stores

/* Set each node in the graph to have an ID that consists of a label followed by a space and a number.
 * Then we can split by space, and take the last element of that, convert it to a number, access that element of the vector for that object
 * So... in the event handler for pit 4, we can go from the pit4 javafx object, to the 4th pit object, and do things with that.
 * This is extremely janky I think. I can't really be sure, unfortunately. 
 */

public class Test1 extends Application {
	Vector<Pit> pits = initializePits();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primary) throws Exception {
		primary.setTitle("Mancala!");
		Pane canvas = new Pane();
		canvas.setPrefSize(1080, 720);
		canvas.setStyle("-fx-background-color: burlywood;");
		pits = initializePits();
		canvas.getChildren().addAll(pits);
		//canvas.getChildren().addAll(placeInitialShapes(pits));
		primary.setScene(new Scene(canvas)); //sets stage to show the scene
 		primary.show(); //shows the scene in the newly-created application
	}
	
	private Vector<Pit> initializePits() {
		//initialize location, all that for the pits. 
		//don't yet initialize the stones that will go in them. Or maybe do, idk.
		Vector<Pit> working = new Vector<Pit>();
		/* 7 8 9 10 11 12
		   1 2 3 4  5  6 */
		for (int j = 1; j < 3; j++) {
			for (int i = 1; i < 7; i++) {
				Pit working_pit = new Pit(85 + 130 * i, 120 + 140 * j, i * j, j);
				working_pit.setFill(j == 1 ? Color.SADDLEBROWN : Color.DARKGOLDENROD);
				working.add(working_pit);

				//Event handlers for the mouse hovering over, leaving the area of, and clicking on the pits
				//These could... probably be moved to the constructor for pits, maybe?
		        working_pit.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
		        	@Override public void handle(MouseEvent event) {
		        		working_pit.setFill(Color.DARKRED);
		        		//something something create a text box above the pit when mouse is over it
		        		//set the ID to something specific so that the mouse_exited item can remove it. 
		        	}
		        });
		        
		        working_pit.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
		        	@Override public void handle(MouseEvent event) {
		        		working_pit.setFill(Color.AQUA);
		        		//destroy the object created when the mouse entered this pit
		        	}
		        });
		        
		        working_pit.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		        	@Override public void handle(MouseEvent event) { 
		        		//handle the pit being clicked on. Validate move, do a move... whatever, that's not my problem right now.
		        		working_pit.setFill(Color.BISQUE);
		        	}
		        });
		        
			}
		}
		return working;
	}
}