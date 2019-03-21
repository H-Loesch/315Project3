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
	Vector<Pit> pits;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primary) throws Exception {
		primary.setTitle("Mancala!");
		Pane root = new Pane();
		root.setPrefSize(1080, 720);
		root.setStyle("-fx-background-color: burlywood;");
		pits = initializePits(root);
		root.getChildren().addAll(pits);
		//canvas.getChildren().addAll(placeInitialShapes(pits));
		primary.setScene(new Scene(root)); //sets stage to show the scene
 		primary.show(); //shows the scene in the newly-created application
	}
	
	private Vector<Pit> initializePits(Pane root) {
		//initialize location, all that for the pits. 
		//don't yet initialize the stones that will go in them. Or maybe do, idk.
		Vector<Pit> working = new Vector<Pit>();
		/* 7 8 9 10 11 12
		   1 2 3 4  5  6 */
		for (int j = 1; j < 3; j++) {
			for (int i = 1; i < 7; i++) {
				Pit working_pit = new Pit(85 + 130 * i, 120 + 140 * j, i * j + j - 1, j);
				working_pit.setFill(j == 1 ? Color.SADDLEBROWN : Color.DARKGOLDENROD);
				working.add(working_pit);

				//Event handlers for the mouse hovering over, leaving the area of, and clicking on the pits
				//These could... probably be moved to the constructor for pits, maybe?
		        working_pit.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
		        	@Override public void handle(MouseEvent event) {
		        		javafx.scene.shape.Rectangle size_label = new javafx.scene.shape.Rectangle(working_pit.getCenterX() - 22.5, working_pit.getCenterY() -90, 45, 35);
		        		size_label.setId("temp");
		        		size_label.setFill(Color.BLACK);
		        		root.getChildren().add(size_label);
		        		working_pit.setFill(Color.DARKRED);
		        		//something something create a text box above the pit when mouse is over it
		        		//set the ID to something specific so that the mouse_exited item can remove it. 
		        	}
		        });
		        
		        working_pit.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
		        	@Override public void handle(MouseEvent event) {
		        		javafx.scene.Node size_label = root.lookup("#temp");
		        		root.getChildren().remove(size_label);
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
			//create the kalahs here I guess, so the GUI array will line up with the gameboard array
			//assign its place to be j * 2 + j
			
			
		}
		return working;
	}
}