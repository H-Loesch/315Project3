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

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

//TODO see about deleting objects instead of just removing them from the root pane's list (memory leak?)
//TODO Add displays: current player, each player's score; probably other things too 
//TODO check on the pie rule function. maybe look into... making it cooler.
	//Probably smart to write the rest of the program in such a way that it can be done by just switching a few vars in the gm or smth.
//TODO figure out what happens when the buffer receives a new item while already processing one 
	//use mutexes or something to make it not be a problem?	
	//this may not actually be a problem, cuz the remote or GUI thread (whichever trigger buffer change) are the ones that go thru that function
//TODO some sort of Bool to track whether we are currently accepting a move. 
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

	public int player = 0; // is this still needed?
	static int numPits = 6; // is this still needed? YES? need a variable to pass into game manager
	static int numPieces = 4;
	//public int configType = 0;

	private Color player1Color = Color.SADDLEBROWN;
	private Color player2Color = Color.DARKGOLDENROD;
	Config config; //are we client or server?

	static TextField setPits;
	static TextField setPieces;

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
	public void start(Stage primary) throws Exception {
		primary.show(); // shows the scene in the newly-created application
		//root.setPrefSize(135*(numPits+2), 720);
		root.setPrefSize(1080, 640);
		root.setStyle("-fx-background-color: burlywood;");
		primary.setTitle("Mancala!");
		
		setPits = new TextField();
		setPits.setPromptText("How Many Pits?");
		setPits.relocate((720/2)-75,50);
		root.getChildren().add(setPits);
		
		setPieces = new TextField();
		setPieces.setPromptText("How Many Pieces?");
		setPieces.relocate((720/2)-75,125);
		root.getChildren().add(setPieces);
		
		//Button play = new Button("Play");
		//root.getChildren().add(play);
		
		//update_display(root,gm);
				
		//root.getChildren().remove(setPits);
				
		pits = initializePits(root, gm);
		stores = initializeStores(root, gm);
		update_display(root, gm);
		
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
			gm.playerInputs[0] = Source.HUMAN;
			gm.playerInputs[1] = Source.REMOTECLIENT;
		} else if (warble == 2) {
			remote = new Remote(buffer, 80, InetAddress.getLocalHost().getHostName());
			config = Config.CLIENT;
			gm.playerInputs[1] = Source.HUMAN;
			gm.playerInputs[0] = Source.REMOTESERVER;
		}
		
		//run our remote connection thread
		pool.execute(remote);
		// Add a listener to our buffer so it does stuff.		
		buffer.addListDataListener(new BufferListener(buffer, remote));
		
		/*
		String inputLine;
		while (warble == 1 && (inputLine = uinput.nextLine()) != null) {
			//read repeatedly
			remote.remote_writer.println(inputLine); //legit I built this to run off the buffer so just. write to that
		
		}*/
		
	}

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

			String message = target.get(0);
			while (message != null) {
				//split the string into an array, slot that array into a vector
				System.out.println(message);
				Vector<String> args = new Vector<String>(Arrays.asList(message.split(" ")));
				
				Boolean isLocal = args.get(0).equals("LOCAL");
				if (isLocal) {
					args.remove(0);
				}
				
				message = handleInput(args, isLocal);
			}
			buffer.remove(0);
			
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
					working_pit.setFill(player1Color);
					root.getChildren().add(working_pit);
				} else {
					// closer player's pits; these are generated left-right
					working_pit = new Pit(working.get(numPits - 1).getCenterX() + 130 * (i - 1), 400, numPits + 1 + i, 1,
							gm, root, buffer);
					working_pit.setFill(player2Color);
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
			working.setFill(i == 1 ? player1Color : player2Color);
			root.getChildren().add(working);
			working_vec.add(working);
			
			//add a mouse handler to update the display ; this is a horrendous workaround I know. 
			working.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	        	@Override public void handle(MouseEvent event) {
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
		//handle the input! A massive, sprawling if/else tree. how terrible!
		
		if (args.get(0).equals("WELCOME")){
			System.out.println("Server connected.");
			return null;
		} else if (args.get(0).equals("READY") || args.get(0).equals("OK")) {
			//Received "READY" ack; it is now either the server's or client's turn to move.
			gm.start_time = System.currentTimeMillis();
			gm.expecting_move = true;
			if (gm.playerInputs[gm.currentPlayer] == Source.AI) {
				//If our AI is the current player, notify it to start doing things. 
				//this SHOULD only come up if we receive READY and are first.
			}
			return null;
			
		} else if (args.get(0).equals("ILLEGAL")) {
			//nothing really needs to be done with this, I think. Server should send LOSE or WIN.
			return null;
			
		} else if (args.get(0).equals("TIME")) {
			//nothing really needs to be done with this, I think. Server should send LOSE or WIN.
			return null;
			
		} else if (args.get(0).equals("LOSER")) {
			if (isLocal) {
				// we win! (cuz we're sending the remote a loser message 
				remote.remote_writer.println("LOSER");
				System.out.println("YOU WIN");
			} else {
				System.out.println("YOU LOSE");
				//we lose :c
			}
			return null;
			
		} else if (args.get(0).equals("WINNER")) {
			//end game, display winner text
			if (isLocal) {
				//we lose :c
				remote.remote_writer.println("WINNER");
				System.out.println("YOU LOSE");
			} else {
				//we win!
				System.out.println("YOU WIN");
			}
			return null;
			
		} else if (args.get(0).equals("TIE")) {
			//end game, display tie text
			//game ended in tie 
			if (isLocal) {
				remote.remote_writer.println("TIE");
			}
			System.out.println("YOU DON'T WIN OR LOSE I GUESS");
			return null;
			
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
			
			gm.initialized = true;
			//do the game setup stuff
			//if local, write the same args to the remote
			
			if (isLocal) {
				String out = "";
				for (int i = 0; i < args.size(); i++) {out = out + args.get(i) + " ";}
				remote.remote_writer.println(out);
			} else {
				//we received this from server, so we should.... initialize that stuff, huh?
			}
			return null;
			
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
				gm.acknowledged = false;
			}		
			return null;
			
		} else if (args.get(0).matches("^[0-9]*$")) {
			//if our first character is a number, then we do a regular ol' move.
			String moves = "";
			
			gm.end_time = System.currentTimeMillis();
			
			for (int i = 0; i < args.size(); i++) {
				//move will have to be adjusted for this to work: make the error code actually work
				try {
					int choice = Integer.parseInt(args.get(i));
					int result = gm.move(choice, gm.currentPlayer); //do that move until there are no moves remaining
					moves = moves + " " + args.get(i);
					
					if ((result == 2 || gm.illegal_flag) || (!isLocal && gm.expecting_move)) {
						//if that's an illegal move, OR we weren't expecting a remote input, return illegal.
						gm.illegal_flag = true;
					
					} else if ( result == 0 || result == 1) {
						//return a player, move successful; this should also make subsequent moves from the same input return illegal
						if (gm.currentPlayer != result) {
							gm.expecting_move = false;
							gm.currentPlayer = result;
						}
						 
					} else if (result == 3 ) {
						//player 0 has won 
						return "LOCAL WINNER";
					} else if (result == 4 ) {
						return "LOCAL LOSER";
					} else if (result == 5 ) {
						return "LOCAL TIE";
					}
				} catch (NumberFormatException e) {
					//this ain't a number........ stop that....
				}
			}
			
			gm.moveNumber += 1;
			moves = moves.substring(1); //remove starting space
			//if (isLocal && (gm.playerInputs[gm.currentPlayer] == Source.REMOTESERVER || gm.playerInputs[1 - gm.currentPlayer]== Source.REMOTECLIENT )) {
			
			long time_elapsed = gm.end_time - gm.start_time;
			gm.end_time = 0;
		
			gm.start_time = System.currentTimeMillis();
			if (config == Config.SERVER) {
				if (isLocal) {
					//Move from us. Scrutinize, send to client.
					if (gm.illegal_flag) {
						remote.remote_writer.println(moves);
						remote.remote_writer.println("ILLEGAL");
						return "LOCAL WINNER";
					} else if (time_elapsed > gm.timeLimit && gm.timeLimit != 0) {
						remote.remote_writer.println("TIME");
						remote.remote_writer.println(moves);
						return "LOCAL WINNER";
					} else {
						//move went fine. yay. pass it off to client.
						remote.remote_writer.println(moves);
					}
				
				} else {
					//move from client. scrutinize, then move on.
					if (gm.illegal_flag) {
						remote.remote_writer.println("ILLEGAL");
						return "LOCAL LOSER";
					} else if (time_elapsed > gm.timeLimit && gm.timeLimit != 0) {
						remote.remote_writer.println("TIME");
						return "LOCAL LOSER"; 
					} else {
						remote.remote_writer.println("OK");
					}
				}
			}
			
			if (config == Config.CLIENT) {
				if (isLocal) {
					//Send our move out to the server.
					remote.remote_writer.println(moves);
				} else {
					//notify our AI to start its next move, otherwise nothing.
					remote.remote_writer.println("OK");
				}
			}
			
			if (gm.playerInputs[gm.currentPlayer] == Source.AI) {
				//if the current player is our AI, signal the AI to move.
			}
			
			return null;
			
			
			//manually call the mouse click event on one of the pits
			//this is the worst possible way to trigger an event in another thread, sh.
			//Event.fireEvent(stores.get(0), new MouseEvent(MouseEvent.MOUSE_CLICKED,
			//		   stores.get(0).getCenterX(), stores.get(0).getCenterY(), stores.get(0).getCenterX(), stores.get(0).getCenterY(),
			//		   MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null));
		} else {
			return null;
			//return "Failed to handle input ;;;;";
		}
		//return "This should never show up";
	}
	
}


class playButtonHandler implements EventHandler<ActionEvent>{
	
	@Override
	public void handle(ActionEvent event) {
		Test1.configType = 2;
		Test1.numPits = Integer.parseInt(Test1.setPits.getText());
		Test1.configType.notify();
	}
	

}
