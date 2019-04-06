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
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//TODO see about deleting objects instead of just removing them from the root pane's list (memory leak?)
//TODO Add displays: current player, each player's score; probably other things too 
//TODO check on the pie rule function. maybe look into... making it cooler.
	//Probably smart to write the rest of the program in such a way that it can be done by just switching a few vars in the gm or smth.
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
	
	//remote threads
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
		} else if (warble == 2) {
			remote = new Remote(buffer, 80, InetAddress.getLocalHost().getHostName());
			config = Config.CLIENT;
		}
		
		//run our remote connection thread
		pool.execute(remote);
		// Add a listener to our buffer so it does stuff.		
		buffer.addListDataListener(new BufferListener(buffer, remote));
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
			System.out.println("added");
			Vector<String> args = new Vector<String>(Arrays.asList(target.get(0).split(" ")));
			
			Boolean isLocal = args.get(0).equals("LOCAL");
			if (isLocal) {
				args.remove(0);
			}

			handleInput(args, isLocal);
			//I mean I can't imagine why we'd be getting local acknowledgements... do we need these?
			
			//this is bugged bc java doesn't like it when we modify the GUI outside of the GUI thread
			//update_display(root, gm);
			buffer.remove(0);
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
				
				working_pit.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
		        	@Override public void handle(MouseEvent event) {
		        		//create new stack pane for the box, since these automatically center things
		        		StackPane text_box = new StackPane();
		        		text_box.setLayoutX(working_pit.getCenterX() - 22.5);
		        		text_box.setLayoutY(working_pit.getCenterY() - 90);
		        		text_box.setId("temp_box");

		        		Rectangle size_label = new javafx.scene.shape.Rectangle(22.5, 17.5, 45, 35);
		        		size_label.setFill(Color.RED);
		        		Text number = new Text(Integer.toString(gm.board[working_pit.size]));
		        		//Text number = new Text(Integer.toString(place));
		        		number.setId("text_box_number");

		        		text_box.getChildren().addAll(size_label, number);
		        		size_label.setId("temp");
		        		root.getChildren().add(text_box);
		        		update_display(root, gm);
		        	}
		        });
				
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
		System.out.println("handling inputs");
		if (args.get(0) == "WELCOME") {
			
		} else if (args.get(0) == "READY" || args.get(0).equals("OK")) {
			//this handling of things might actually be applicable to "OK" as well... 
			//might also be possible to replace Config enum with the source one? hm....
			gm.currentPlayer = 1 - gm.currentPlayer;
			if (gm.playerInputs[gm.currentPlayer] == Source.AI) {
				//Tell the AI to start movin!!!!!!!!!!!!!!!!!!!!!!!!!
			} else if (gm.playerInputs[gm.currentPlayer] == Source.REMOTECLIENT) {
				//Start keeping time TODO
			} else if (gm.playerInputs[gm.currentPlayer] == Source.REMOTESERVER) {
				//wait for the remote server to make a move 
			}
			
		}  else if (args.get(0) == "ILLEGAL") {
			if (config == Config.CLIENT) {
				//server ran out of time. Toss this in the bin, we're about to win.
			}
			
		} else if (args.get(0) == "TIME") {
			if (config == Config.CLIENT) {
				//server ran out of time. Toss this in the bin, we're about to win.
			}
			
		} else if (args.get(0) == "LOSER") {
			//end game, display loser text
			
		} else if (args.get(0) == "WINNER") {
			//end game, display winner text
			
			
		} else if (args.get(0) == "TIE") {
			//end game, display tie text
		
			
			// moves and configuration
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
			
		} else if (args.get(0) == "P"){
			// pie rule
			gm.moveNumber += 1;
			if (gm.moveNumber == 2) {
				//do a pie rule!
			} else {
				//return illegal
			}
			
			//if this was locally-generated, write it to the remote.
			if (isLocal) {
				remote.remote_writer.println("P");
			}		
			//....handle the pie rule? idk bud
			
		} else if (args.get(0).matches("^[0-9]*$")) {
			String moves = "";
			gm.moveNumber += 1;
			//if first char is a number (regular ol' move)
			for (int i = 0; i < args.size(); i++) {
				//move will have to be adjusted for this to work: make the error code actually work
				System.out.println(args.get(0));
				try {
					int result = gm.move(Integer.parseInt(args.get(i)),  gm.currentPlayer); //do that move until there are no moves remaining
					if (result == 0 || result == 1) {
						//return a player, move successful; this should also make subsequent moves from the same input return illegal
						gm.currentPlayer = result;
						moves = moves + " " + Integer.parseInt(args.get(i));
					} else {
						//returned 2, move failed
						//return "ILLEGAL";
					}
				} catch (NumberFormatException e) {
					//this ain't a number........ stop that....
				}
			}
			
			if (isLocal) {
				remote.remote_writer.println(moves);
			}		
		} else {
			//return "Failed to handle input ;;;;";
		}
		//return "This should never show up";
	return "honk";	
	}
}
