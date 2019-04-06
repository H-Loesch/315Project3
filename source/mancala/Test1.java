package mancala;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;


//TODO see about deleting objects instead of just removing them from the root pane's list (memory leak?)
//TODO Add displays: current player, each player's score; probably other things too 
//TODO check on the pie rule function. maybe look into... making it cooler.
	//Probably smart to write the rest of the program in such a way that it can be done by just switching a few vars in the gm or smth.
//TODO figure out what happens when the buffer receives a new item while already processing one 
	//use mutexes or something to make it not be a problem?
enum Config {
	CLIENT, SERVER, LOCAL
}

enum Source {
	HUMAN, REMOTESERVER, REMOTECLIENT, AI
}

public class Test1 extends Application {
////////////////////////////////////////////////////////////////////////////////////////////
//Defining variables for our overall application. god there's so many. 
	private Vector<Pit> pits;
	private Vector<Store> stores;

	public int numPits = 4; // is this still needed?
	public int numPieces = 6;
	private static Random key = new Random();
	Config config; //are we client or server?
	

	private GameManager gm = new GameManager(numPits, numPieces);
	Pane root = new Pane(); // root pane
	Pane centerPiece = new Pane(); // the gameboard itself will be stored here

	private DefaultListModel<String> buffer = new DefaultListModel<String>();
	

	static Integer configType = 0;	//remote threads

	Remote remote;
	ExecutorService pool = Executors.newFixedThreadPool(1);

////////////////////////////////////////////////////////////////////////////////////////////
//GUI management and main

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override

	  public void start(Stage primaryStage){
	    
		// Set the stage title
	    primaryStage.setTitle("Mancala!");

	    
	    //create inputs
	    TextField setPits = new TextField();
	    TextField setPieces = new TextField();
	    
	    // create a label control
	    //Label messageLabel = new Label("Virtual Game.");
	    //valueLabel = new Label("Go ahead, Click!");
	    Label pitsMessage = new Label("How many pits do you want on each side? (4-9)");
	    Label piecesMessage = new Label("How many pieces do you want on each side? (1-10) (set negative for random distributon)");
	    
	    
	    //create button control
	    //Button scrabbleButton  = new Button("Scrabble");
	    Button pvpButton = new Button("Play PvP");
		
		
		
	    
	    // Register the event Handler
	    //scrabbleButton.setOnAction(new ScrabbleButtonHandler());
	    pvpButton.setOnAction(new pvpButtonHandler());

		
	    
	    //put the label and button in a VBox with 10 pixels of spacing.
	    //VBox  vbox = new VBox(10, messageLabel, scrabbleButton, clueButton, cardButton, bankButton, valueLabel);
		VBox vbox = new VBox(10, pitsMessage,setPits,piecesMessage,setPieces,pvpButton);
		vbox.setStyle("-fx-background-color: burlywood;");
	    
	    // create a Scene with the HBox as its root node
	    // set the size of the scene (without this it is only as big as the label)
	    Scene scene = new Scene(vbox, 720, 720, Color.BURLYWOOD);
	    
	    
	    // center the Label
	    vbox.setAlignment(Pos.CENTER);

	    
	    
	    // Add the scene to the Stage
	    primaryStage.setScene(scene);

	    
	    
	    //show the window
	    primaryStage.show();
	    
	    
	  } //end start
	/*
	@Override
	public void start(Stage primary) throws Exception {
		primary.show(); // shows the scene in the newly-created application
		root.setPrefSize(1080, 720);
		root.setStyle("-fx-background-color: burlywood;");

		pits = initializePits(root, gm);
		stores = initializeStores(root, gm);
		update_display(root, gm);
		primary.setTitle("Mancala!");
		primary.setScene(new Scene(root)); // sets stage to show the scene

		// canvas.getChildren().addAll(placeInitialShapes(pits));
		primary.show(); // shows the scene in the newly-created application
		
		//placeholder input
	    Scanner uinput = new Scanner(System.in);  // Create a Scanner object
		System.out.println("hey idiot do you want to be a server? if yes type 1. client, type 2. to die, type a will saying you wanna leave me everything.");
		int warble = Integer.parseInt(uinput.nextLine());
		if (warble == 1) {
			remote = new Remote(buffer, 80);
			config = Config.SERVER;
			gm.playerInputs[1] = Source.REMOTECLIENT;
		} else if (warble == 2) {
			remote = new Remote(buffer, 80, InetAddress.getLocalHost().getHostName());
			config = Config.CLIENT;

			gm.playerInputs[1] = Source.REMOTESERVER;

		}
		
		//run our remote connection thread
		pool.execute(remote);
		// Add a listener to our buffer so it does stuff.		
		buffer.addListDataListener(new BufferListener(buffer, remote));
		
		/*String inputLine;
		while (warble == 1 && (inputLine = uinput.nextLine()) != null) {
			//read repeatedly
			remote.remote_writer.println(inputLine); //legit I built this to run off the buffer so just. write to that
		
		}*/
		
	}
	*/
	////////////////////////////////////////////////////////////////////////////////////////////
	//Buffer listener
	class BufferListener implements ListDataListener {
		DefaultListModel<String> target; // the buffer of strings that is our buffer
		Remote remote; 
		Boolean isLocal = false;
		BufferListener(DefaultListModel<String> _target, Remote _remote) {
			super();
			target = _target;
			remote = _remote;
		}

		public void contentsChanged(ListDataEvent e) {
			// what happens when something more complex happens to this list?
			System.out.println("whoa lmao");
		}

		public void intervalAdded(ListDataEvent e) {
			// what happens when something is added to list?
			// peel off first item, split it
			Vector<String> args = new Vector<String>(Arrays.asList(target.get(0).split(" ")));
			
			Boolean isLocal = args.get(0).equals("LOCAL");
			if (isLocal) {
				args.remove(0);
			}

			handleInput(args, isLocal);
			//update_display(root, gm);
			target.remove(0);
			//I mean I can't imagine why we'd be getting local acknowledgements... do we need these?
			
			//this is bugged bc java doesn't like it when we modify the GUI outside of the GUI thread
			//update_display(root, gm);
			// handling of some acknowledgments should probably go here
			// else, send them off to the game manager! woo!

			// pass that string along to gm.handle_input()
			// update_display()
			// remove that string from gm.handle_input()
		}

		public void intervalRemoved(ListDataEvent e) {
			// what happens when something is removed from list?
			//nothing lol
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////
//init Methods	
	private Vector<Pit> initializePits(Pane root, GameManager gm) {
		// initialize location, all that for the pits.
		// don't yet initialize the stones that will go in them. Or maybe do, idk.
		Vector<Pit> working = new Vector<Pit>();
		/*
		 * 7 8 9 10 11 12 1 2 3 4 5 6
		 */
		
		for (int j = 1; j <= 2; j++) {
			for (int i = 1; i <= numPits; i++) {
				Pit working_pit;
				if (j == 1) {
					// further player's pits; these are generated right-left
					working_pit = new Pit(numPits * 130 + 85 - (i - 1) * 130, 260, i, 0, gm, root, buffer);
					working_pit.setFill(Color.SADDLEBROWN);
					root.getChildren().add(working_pit);
				} else {
					// closer player's pits; these are generated left-right
					working_pit = new Pit(working.get(numPits - 1).getCenterX() + 130 * (i - 1), 400, numPits + 1 + i, 1,
							gm, root, buffer);
					working_pit.setFill(Color.DARKGOLDENROD);
					root.getChildren().add(working_pit);
				}
				
				working.add(working_pit);
			}

		}
		return working;
	}

	private Vector<Store> initializeStores(Pane root, GameManager gm) {
		Vector<Store> working_vec = new Vector<Store>();
		double vertical_location = (pits.get(0).getCenterY() + pits.lastElement().getCenterY()) / 2.0;

		for (int i = 0; i < 2; i++) {
			double horizontal_location = (130 * Math.pow(-1, i)) + pits.get((numPits - 1) * (i)).getCenterX();
			Store working = new Store(horizontal_location, vertical_location, 70, 125, i, gm, root, buffer);
			working.place = numPits * i + i * 1;
			working.setFill(i == 1 ? Color.SADDLEBROWN : Color.DARKGOLDENROD);
			root.getChildren().add(working);
			working_vec.add(working);
			
			//add a mouse handler to update the display ; this is a horrendous workaround I know. 
			working.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	        	@Override public void handle(MouseEvent event) {
	        		System.out.println("click");
	        		update_display(root, gm);
	        	}
	        });
		}
		return working_vec;
	}

////////////////////////////////////////////////////////////////////////////////////////////
//GUI/Server Methods	

	void update_display(Pane root, GameManager gm) {
		for (Pit change_pit : pits) {
			while (change_pit.size > gm.board[change_pit.place])
				root.getChildren().remove(change_pit.removePiece());
			while (change_pit.size < gm.board[change_pit.place])
				root.getChildren().add(change_pit.addPiece());
		}

		for (Store working_store : stores) {
			while (working_store.size > gm.board[working_store.place])
				root.getChildren().remove(working_store.removePiece());
			while (working_store.size < gm.board[working_store.place])
				root.getChildren().add(working_store.addPiece());
		}
	}


////////////////////////////////////////////////////////////////////////////////////
//Buffer input Handling
	private String handleInput(Vector<String> args, Boolean isLocal) {
		System.out.println(args);
		
		if (args.get(0).equals("WELCOME")){
			//yeah
		} else if (args.get(0).equals("READY") || args.get(0).equals("OK")) {
			//Received "READY" ack; it is now either the server's or client's turn to move.
			gm.acknowledge = true;
			gm.start_time = System.currentTimeMillis();
			switch(gm.playerInputs[gm.currentPlayer]) {
				case HUMAN : 
					//local player: have fun, buddy :)
					//prolly do some like. display stuff. 
					break;
				case REMOTESERVER :
					//wait for server's move 
					break;
				case AI :
					//notify AI to start making a move 
					break;
				case REMOTECLIENT : 
					//start... timing them? I don't think we really need anything here
					break;
					
			} 
			
		//I THINK OK and READY can be wrapped into the same, since whose turn it is is handled elsehwere 
/*		} else if (args.get(0) == "OK") { 
			//Received "OK" ack; it is now the other's turn. 
*/			
		} else if (args.get(0).equals("ILLEGAL")) {
			
			if (isLocal) {
				//we've written ourselves an illegal signal. neat. 
				// ....handle this, idk. 
			} else if (gm.acknowledge) {
				//the server has messed up. hee hee hoo ho
			} else {
				//if last move isn't acknowledged, that means we messed up. oh no! we lose
				//prepare to lose? (do nothing...?) 
			}
			
		} else if (args.get(0).equals("TIME")) {
			if (gm.acknowledge) {
				//If last move was acknowledged, that means server took too long
				//we win? carry out server's last move but then we win 
			} else {
				//if last move wasn't acknowledged, that means we took too long. oh no
				
			}
			
			
		} else if (args.get(0).equals("LOSER")) {
			if (isLocal) {
				System.out.println("YOU WIN");
				// we win! (cuz we're sending the remote a loser message 
			} else {
				System.out.println("YOU LOSE");
				//we lose :c
			}
			
		} else if (args.get(0).equals("WINNER")) {
			//end game, display winner text
			if (isLocal) {
				//we lose :c
				System.out.println("YOU LOSE");
			} else {
				//we win!
				System.out.println("YOU WIN");
			}
			
		} else if (args.get(0).equals("TIE")) {
			//end game, display tie text
			//game ended in tie 
			System.out.println("YOU DON'T WIN OR LOSE I GUESS");
			
		} else if (args.get(0).equals("INFO")) {
			
			//THIS initializes our gameboard. None of the board should be initialized until this is ran.
			numPits = Integer.parseInt(args.get(1));
			numPieces = Integer.parseInt(args.get(2));
			gm.timeLimit = Long.parseLong(args.get(3)); 
			if (args.get(4).equals("F")) {
				//client goes first
			} else if (args.get(4).equals("S")) {
				//server goes first
			}
			
			//probably call our initialization of the board in this block here. 
			//Call a diff one depending on if 5 is S or R 
			if (args.get(5).equals("S")) {
				//god is good. Normal configuration.
			} else if (args.get(5).equals("R")) {
				//god is not good. Random configuration. Initialize with the desired layout of pits.
				
			}
			
			//do the game setup stuff
			//if local, write the same args to the remote
			//if remote, configure game 
			
		} else if (args.get(0).equals("P")) {
			// pie rule
			if (gm.moveNumber == 1) {
				//if this is second move in game, do pie rule
				gm.moveNumber += 1;
				//do a pie rule!
			} else {
				//return illegal
			}
			
			//if this was locally-generated, write it to the remote.
			if (isLocal && (gm.playerInputs[gm.currentPlayer] == Source.REMOTESERVER || gm.playerInputs[1 - gm.currentPlayer]== Source.REMOTECLIENT )) {
				remote.remote_writer.println("P");
				gm.acknowledge = false;
			}		
			
		} else if (args.get(0).matches("^[0-9]*$")) {
			//if our first character is a number, then we do a regular ol' move.
			String moves = "";
			for (int i = 0; i < args.size(); i++) {
				//move will have to be adjusted for this to work: make the error code actually work
				try {
					int result = gm.move(Integer.parseInt(args.get(i)),  gm.currentPlayer); //do that move until there are no moves remaining
					if (result == 0 || result == 1) {
						//return a player, move successful; this should also make subsequent moves from the same input return illegal
						gm.currentPlayer = result;
						moves = moves + " " + Integer.parseInt(args.get(i));
						
						//need somethign to handle *who* wins in these situations
					} else if (result == 2) {
						//send ourselves an illegal signal.
						return "LOCAL ILLEGAL";
					
					} else if (result == 3 ) {
						//player 0 has won 
						return "LOCAL WINNER";
					} else if (result == 4 ) {
						return "LOCAL LOSER";
					} else if (result == 5 ) {
						buffer.addElement("LOCAL TIE");
					}
				} catch (NumberFormatException e) {
					//this ain't a number........ stop that....
				}
			}
			
			moves = moves.substring(1); //remove starting space
			//if we locally-generated this move, write it to the remote. 
			//if (isLocal && (gm.playerInputs[gm.currentPlayer] == Source.REMOTESERVER || gm.playerInputs[1 - gm.currentPlayer]== Source.REMOTECLIENT )) {
			if (isLocal) {	//if the other player is remote, write to remote.
				remote.remote_writer.println(moves);
				gm.acknowledge = false;
			}		
			
			//manually call the mouse click event on one of the pits
			//this is the worst possible way to trigger an event in another thread, sh.
			//Event.fireEvent(stores.get(0), new MouseEvent(MouseEvent.MOUSE_CLICKED,
			//		   stores.get(0).getCenterX(), stores.get(0).getCenterY(), stores.get(0).getCenterX(), stores.get(0).getCenterY(),
			//		   MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null));
		} else {
			//return "Failed to handle input ;;;;";
		}
		return ""; //if nothing else, return empty string.
		//return "This should never show up";
	}
}



class pvpButtonHandler implements EventHandler<ActionEvent>{

	@Override
	public void handle(ActionEvent event) {
		//Test1.configType = 2;
		//Test1.numPits = Integer.parseInt(Test1.setPits.getText());
		Button testButton = new Button("Try Me");
		testButton.setOnAction(new testHandle());
		
		VBox test = new VBox(10,testButton);
		test.setStyle("-fx-background-color: burlywood;");
		
		Scene testScene = new Scene(test,720,720);
		Stage testStage = new Stage();
		testStage.setScene(testScene);
		testStage.show();
		
		
		
		
		System.out.println("You did it");
		
	}
	

}


class testHandle implements EventHandler<ActionEvent>{
	
	@Override
	public void handle(ActionEvent event) {

		
		
		System.out.println("You did it XD XD XD");
		

	}
	

}