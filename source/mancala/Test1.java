package mancala;

import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

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


//TODO make the GUI check each cycle *not* be terrible
//TODO see about deleting objects instead of just removing them from the root pane's list (memory leak?)
//TODO improve placement for pieces inside stores
//TODO Add displays: current player, each player's score; probably other things too 
//TODO add pits and stores to a pane, then put that pane centered in the root window. 
//TODO make GUI-based input check if a move is legal before sending it to the buffer.
public class Test1 extends Application {
////////////////////////////////////////////////////////////////////////////////////////////
//Defining variables for object
	private Vector<Pit> pits;
	private Vector<Store> stores;
	//this buffer alerts an eventlistener every time it is changed.
	private DefaultListModel<String> buffer = new DefaultListModel<String>();
	public int player = 0; 
	public int numPits = 5;
	private static Random key = new Random();
	private GameManager gm = new GameManager();
	Pane root = new Pane(); //root pane
	Pane centerPiece = new Pane(); //the gameboard itself will be stored here
	Scanner scanner = new Scanner(System.in); //used for system input 

////////////////////////////////////////////////////////////////////////////////////////////
//GUI management and main

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primary) throws Exception {
		primary.setTitle("Mancala!");
		root.setPrefSize(1080, 720);
		root.setStyle("-fx-background-color: burlywood;");
				
		pits = initializePits(root, gm);
		stores = initializeStores(root, gm);
		root.getChildren().addAll(pits);
		for (Pit pit : pits) {
			for (int i = 0; i < 4; i++) {
				root.getChildren().add(pit.addPiece());
			}
		}
		update_display(root, gm);
		
		//canvas.getChildren().addAll(placeInitialShapes(pits));
		primary.setScene(new Scene(root)); //sets stage to show the scene
 		primary.show(); //shows the scene in the newly-created application
 		
 ////////////////////////////////////////////////////////////////////////////////////////////
 //Non-GUI operation
 	    
 		//this buffer vector updates whenever 
 	    buffer.addListDataListener(new BufferListener());
 	    Server honk = new Server(buffer); //placeholder, for now
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////
//Helper functions	
	private Vector<Pit> initializePits(Pane root, GameManager gm) {
		//initialize location, all that for the pits. 
		//don't yet initialize the stones that will go in them. Or maybe do, idk.
		Vector<Pit> working = new Vector<Pit>();
		/* 7 8 9 10 11 12
		   1 2 3 4  5  6 */
		for (int j = 1; j < numPits; j++) {
			for (int i = 1; i <= numPits; i++) {
				Pit working_pit;
				if (j == 1) {
					//further player's pits; these are generated right-left
					working_pit = new Pit(numPits * 130 + 85 - (i-1) * 130, 260, i, 0);
					working_pit.setFill(Color.SADDLEBROWN);
				}
				else {
					//closer player's pits; these are generated left-right
					working_pit = new Pit(working.get(numPits-1).getCenterX() + 130 * (i-1), 400, numPits + i, 1);
					working_pit.setFill(Color.DARKGOLDENROD);
				}
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
		        		//Text number = new Text(Integer.toString(working_pit.place));
		        		number.setId("text_box_number");
		        		
		        		text_box.getChildren().addAll(size_label, number);
		        		size_label.setId("temp");
		        		root.getChildren().add(text_box);
		        	}
		        });
		        
		        working_pit.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
		        	//when mouse exits pit, delete the text box created upon entry
		        	@Override public void handle(MouseEvent event) {
		        		javafx.scene.Node size_label = root.lookup("#temp_box");
		        		root.getChildren().remove(size_label);
		        		//destroy the object created when the mouse entered this pit
		        	}
		        });
		        
		        working_pit.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		        	@Override public void handle(MouseEvent event) { 
		        		//handle the pit being clicked on. Validate move, do a move... whatever, that's not my problem right now.
		        		int move_result = gm.move(working_pit.place, player);
		        		if (move_result < 2) {
		        			//if move was successful
	        				player = move_result;
	        				
	        				//empty or fill pits to correct size
	        				update_display(root, gm);
	        				
	        				//empty or fill stores to correct size
	        				/*for (Store working : stores) {
	        					while (working.size > gm.board[working.place]) 
	        						root.getChildren().remove(working.removePiece());
	        					while (working.size < gm.board[working.place])
	        						root.getChildren().add(working.addPiece());
	        				}*/
	        				
		        			/*javafx.scene.Node number_box = root.lookup("#temp_box");
		        			if (number_box.getId() != null) {
		        				working_pit.setFill(Color.BISQUE);
		        				root.getChildren().remove(number_box);
		        			}*/
		        			
		        		}
		        	}
		        });
		        
			}			
			
		}
		return working;
	}
	
	private Vector<Store> initializeStores(Pane root, GameManager gm) {
		Vector<Store> working_vec = new Vector<Store>();
		double vertical_location = (pits.get(0).getCenterY() + pits.lastElement().getCenterY()) / 2.0;
		
		for (int i = 0; i < 2; i++) {
			double horizontal_location = (130 * Math.pow(-1,  i)) + pits.get((numPits-1) * (i)).getCenterX();
			Store working = new Store(horizontal_location, vertical_location, 70, 125, i);
			working.setFill(i == 1 ? Color.SADDLEBROWN : Color.DARKGOLDENROD);
			root.getChildren().add(working);
			working_vec.add(working);
			
	        working.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
	        	//when mouse enters the pit, create a text box with the # of pieces in that pit above it.
	        	@Override public void handle(MouseEvent event) {
	        		//create new stack pane for the box, since these automatically center things
	        		StackPane text_box = new StackPane();
	        		text_box.setLayoutX(working.getCenterX() - 22.5);
	        		text_box.setLayoutY(working.getCenterY() - 90);
	        		text_box.setId("temp_box");

	        		Rectangle size_label = new javafx.scene.shape.Rectangle(22.5, 17.5, 45, 35);
	        		size_label.setFill(Color.RED);
	        		Text number = new Text(Integer.toString(gm.board[working.player * 7]));
	        		
	        		text_box.getChildren().addAll(size_label, number);
	        		size_label.setId("temp");
	        		root.getChildren().add(text_box);
	        		//something something create a text box above the pit when mouse is over it
	        		//set the ID to something specific so that the mouse_exited item can remove it. 
	        	}
	        });
	        
	        working.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
	        	//when mouse exits pit, delete the text box created upon entry
	        	@Override public void handle(MouseEvent event) {
	        		javafx.scene.Node size_label = root.lookup("#temp_box");
	        		root.getChildren().remove(size_label);
	        		//destroy the object created when the mouse entered this pit
	        	}
	        });
		}
		return working_vec;
	}
	
	void update_display(Pane root, GameManager gm) {
		for (Pit change_pit : pits) {
			while (change_pit.size > gm.board[change_pit.place]) 
				root.getChildren().remove(change_pit.removePiece());
			while (change_pit.size < gm.board[change_pit.place])
				root.getChildren().add(change_pit.addPiece());
		}
		
		for (Store working_store : stores) {
			while (working_store.size > gm.board[working_store.player * numPits]) 
				root.getChildren().remove(working_store.removePiece());
			while (working_store.size < gm.board[working_store.player * numPits])
				root.getChildren().add(working_store.addPiece());
		}
	}
	
	//handle them gosh darn inputs!
	//move this to game manager probably. or don't, see if I care.
	void handle_inputs(String[] _args, String intent) {
		if (intent == "move") {
				
		} else if (intent == "info") {
			
		} else if (intent == "ack") {
			
		} else if (intent == "game_config") {
			
		}
	}
	
	class BufferListener implements ListDataListener {
		public void contentsChanged(ListDataEvent e) {
			//what happens when something more complex happens to this list?
			System.out.println("whoa");
		} 
		public void intervalAdded(ListDataEvent e) {
			//what happens when something is added to list?
			System.out.println("added");
			//pass that string along to gm.handle_input()
			//update_display()
			//remove that string from gm.handle_input()
		}
		public void intervalRemoved(ListDataEvent e) {
			//what happens when something is removed from list?
			System.out.println("removed");
		}
	}
	
}