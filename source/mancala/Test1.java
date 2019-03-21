package mancala;

import java.util.Vector;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
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
	Vector<Store> stores;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primary) throws Exception {
		primary.setTitle("Mancala!");
		Pane root = new Pane();
		root.setPrefSize(1080, 720);
		root.setStyle("-fx-background-color: burlywood;");
		
		GameManager gm = new GameManager();
		pits = initializePits(root, gm);
		stores = initializeStores(root, gm);
		root.getChildren().addAll(pits);
		
		//canvas.getChildren().addAll(placeInitialShapes(pits));
		primary.setScene(new Scene(root)); //sets stage to show the scene
 		primary.show(); //shows the scene in the newly-created application
	}
	
	private Vector<Pit> initializePits(Pane root, GameManager gm) {
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
		        		//create new stack pane for the box, since these automatically center things
		        		StackPane text_box = new StackPane();
		        		text_box.setLayoutX(working_pit.getCenterX() - 22.5);
		        		text_box.setLayoutY(working_pit.getCenterY() - 90);
		        		text_box.setId("temp_box");

		        		Rectangle size_label = new javafx.scene.shape.Rectangle(22.5, 17.5, 45, 35);
		        		size_label.setFill(Color.RED);
		        		Text number = new Text(Integer.toString(gm.board[working_pit.place]));
		        		
		        		text_box.getChildren().addAll(size_label, number);
		        		size_label.setId("temp");
		        		root.getChildren().add(text_box);
		        		//something something create a text box above the pit when mouse is over it
		        		//set the ID to something specific so that the mouse_exited item can remove it. 
		        	}
		        });
		        
		        working_pit.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
		        	@Override public void handle(MouseEvent event) {
		        		javafx.scene.Node size_label = root.lookup("#temp_box");
		        		root.getChildren().remove(size_label);
		        		//destroy the object created when the mouse entered this pit
		        	}
		        });
		        
		        working_pit.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		        	@Override public void handle(MouseEvent event) { 
		        		//handle the pit being clicked on. Validate move, do a move... whatever, that's not my problem right now.
		        		working_pit.setFill(Color.BISQUE);
		        		int move_result = gm.move(working_pit.place, 1);
		        		/*if (move_result < 2) {
		        			Event.fireEvent(working_pit, MouseEvent.MOUSE_ENTERED);
		        		}*/
		        	}
		        });
		        
			}			
			
		}
		return working;
	}
	
	private Vector<Store> initializeStores(Pane root, GameManager gm) {
		Vector<Store> working_vec = new Vector<Store>();
		for (int i = 0; i < 2; i++) {
			Store working = new Store(85 + i * 910, 330, 70, 125, i);
			working.setFill(i == 0 ? Color.SADDLEBROWN : Color.DARKGOLDENROD);
			root.getChildren().add(working);
			
	        working.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
	        	@Override public void handle(MouseEvent event) {
	        		//create new stack pane for the box, since these automatically center things
	        		StackPane text_box = new StackPane();
	        		text_box.setLayoutX(working.getCenterX() - 22.5);
	        		text_box.setLayoutY(working.getCenterY() - 90);
	        		text_box.setId("temp_box");

	        		Rectangle size_label = new javafx.scene.shape.Rectangle(22.5, 17.5, 45, 35);
	        		size_label.setFill(Color.RED);
	        		Text number = new Text(Integer.toString(gm.board[working.player * 6 + 7]));
	        		
	        		text_box.getChildren().addAll(size_label, number);
	        		size_label.setId("temp");
	        		root.getChildren().add(text_box);
	        		//something something create a text box above the pit when mouse is over it
	        		//set the ID to something specific so that the mouse_exited item can remove it. 
	        	}
	        });
	        
	        working.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
	        	@Override public void handle(MouseEvent event) {
	        		javafx.scene.Node size_label = root.lookup("#temp_box");
	        		root.getChildren().remove(size_label);
	        		//destroy the object created when the mouse entered this pit
	        	}
	        });
		}
		return working_vec;
	}
}