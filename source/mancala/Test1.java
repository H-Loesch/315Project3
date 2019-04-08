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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;


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

	public int numPits = 4; // is this still needed?
	public int numPieces = 6;
	Config config; //are we client or server?
	public Integer local_players = 1;
	private Stage root; 
	
	private GameManager gm = new GameManager(numPits, numPieces);
	Pane game_pane; // root pane
	private DefaultListModel<String> buffer = new DefaultListModel<String>();
	
	Remote remote;
	ExecutorService pool = Executors.newFixedThreadPool(1);
	static Boolean flag;

////////////////////////////////////////////////////////////////////////////////////////////
//GUI management and main

	public static void main(String[] args) {
		launch(args);
		System.out.println("honk");
	}
	
	public void start(Stage primaryStage){
		//show the window
		root = primaryStage;
		root.show();
		// Set the stage title
		root.setTitle("Mancala!");
		buffer.addListDataListener(new BufferListener(buffer, remote));
		
		//create button control
		//Button scrabbleButton  = new Button("Scrabble");
		Button local_button = new Button("Play locally");
		Button remote_server_button = new Button("Connect to another player as a server.");
		Button remote_client_button = new Button("Connect to another player as a client.");
		
		//put the label and button in a VBox with 10 pixels of spacing.
		//VBox  vbox = new VBox(10, messageLabel, scrabbleButton, clueButton, cardButton, bankButton, valueLabel);
		VBox opener_one_vbox = new VBox(10, local_button, remote_server_button, remote_client_button);
		opener_one_vbox.setStyle("-fx-background-color: burlywood;");
		
		opener_one_vbox.setAlignment(Pos.CENTER);

		// create a Scene with the VBox as its root node
		// set the size of the scene (without this it is only as big as the label)
		Scene opener_one = new Scene(opener_one_vbox, 720, 720, Color.BURLYWOOD);
		root.setScene(opener_one);
		
////////////////////////////////////////////////////////////////////////////////////
//Opener stage 2 local
		
		local_button.setOnAction(new EventHandler<ActionEvent>() {
	 	    @Override public void handle(ActionEvent e) {
				config = Config.LOCAL;
				
				GridPane opener_two_grid = createGrid();
				
				//create 1st set of radio buttons
				final ToggleGroup player1 = createRadioButtons(opener_two_grid, 0, "Human", "Computer", "Player 0");
				final ToggleGroup player2 = createRadioButtons(opener_two_grid, 1, "Human", "Computer", "Player 1");
											
				TextField pit_number_field = createInputField(opener_two_grid, 2, "# of pits on each side");
				TextField piece_number_field = createInputField(opener_two_grid, 3, "Average # of pieces in pits");
				TextField time_limit_field = createInputField(opener_two_grid, 4, "Time limit / move ; 0 -> no limit");
				
				final ToggleGroup distribution = createRadioButtons(opener_two_grid, 5, "Random", "Standard", "Distribution of pieces");
				final ToggleGroup first = createRadioButtons(opener_two_grid, 6, "Player 0", "Player 1", "Who goes first?");
				Button play_button = new Button("Play mancala!!");
				GridPane.setConstraints(play_button, 1, 7, 1, 1);
				
				opener_two_grid.getChildren().addAll(play_button);
				
				//putting in a VBox AND HBox bc that's the easiest way I could think of to center things.
				VBox opener_two_vbox = new VBox(20, opener_two_grid);
				opener_two_vbox.setAlignment(Pos.CENTER);
				HBox opener_two_hbox = new HBox(20, opener_two_vbox);
				opener_two_hbox.setAlignment(Pos.CENTER);
				opener_two_hbox.setStyle("-fx-background-color: burlywood;");
				
				Scene opener_2 = new Scene(opener_two_hbox, 720, 720, Color.BURLYWOOD);
				root.setScene(opener_2);

				play_button.setOnAction(new EventHandler<ActionEvent>() {
			 	    @Override public void handle(ActionEvent e) {
			 	    	String message = "LOCAL INFO ";
			 	    	//grab numPits, numPieces, and time limits from the input fields. If they're not numbers, then don't bother.
			 	    	if (pit_number_field.getText().matches("\\d*")) {
			 	    		int val = Integer.parseInt(pit_number_field.getText());
			 	    		if (val > 3 && val < 10) {
			 	    			message = message + val + " ";
			 	    		} else {return;}
			 	    	} else {return;}
			 	    	if (piece_number_field.getText().matches("\\d*")) {
			 	    		int val = Integer.parseInt(piece_number_field.getText());
			 	    		if (val > 0 && val < 11) {
			 	    			message = message + val + " ";
			 	    		} else {return;}
			 	    	} else {return;}
			 	    	if (time_limit_field.getText().matches("\\d*")) {
			 	    		long val = Long.parseLong(time_limit_field.getText());
			 	    		message = message + val + " ";
			 	    	} else {return;}
			 	    	
			 	    	//radio button handling. Jesus christ why are these so terrible.
			 	    	if (((RadioButton) player1.getSelectedToggle()) == null) {
			 	    		return;
			 	    	} else if (((RadioButton) player1.getSelectedToggle()).getText().equals("Human")) {
			 	    		gm.playerInputs[0] = Source.HUMAN;
			 	    	} else if (((RadioButton) player1.getSelectedToggle()).getText().equals("Computer")) {			
			 	    		gm.playerInputs[0] = Source.AI;
			 	    	} else { return;}
			 	    	
						if (((RadioButton) player2.getSelectedToggle()) == null) {
							return;
						} else if (((RadioButton) player2.getSelectedToggle()).getText().equals("Human")) {
							gm.playerInputs[1] = Source.HUMAN;
						} else if (((RadioButton) player2.getSelectedToggle()).getText().equals("Computer")) {
							gm.playerInputs[1] = Source.AI;
						} else { return;}
						
						if (((RadioButton) first.getSelectedToggle()) == null) {
							return;
						} else if (((RadioButton) first.getSelectedToggle()).getText().equals("Player 1")) {
							message = message + "F "; //whoever's receiving this goes second
						} else if (((RadioButton) first.getSelectedToggle()).getText().equals("Player 0")) {
							message = message + "S "; //whoever's receiving this goes first
						} else { return;}
						
						if (((RadioButton) distribution.getSelectedToggle()) == null) {
							return;
						} else if (((RadioButton) distribution.getSelectedToggle()).getText().equals("Random")) {
							message = message + "R"; //random distribution (we'll let buffer handler actually make this part of the message)
						} else if (((RadioButton) distribution.getSelectedToggle()).getText().equals("Standard")) {
							message = message + "S"; //Standard distribution
						} else { return;}
						buffer.addElement(message);

						initializeGUI();
			 	    }
				});
			}
		});
				
////////////////////////////////////////////////////////////////////////////////////
//Opener stage 2 client

		remote_client_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				config = Config.CLIENT;
				gm.playerInputs[0] = Source.REMOTESERVER;
				
				GridPane opener_two_grid = createGrid();
				final ToggleGroup player2 = createRadioButtons(opener_two_grid, 0, "Human", "Computer", "Player 1");
				TextField hostname_field = createInputField(opener_two_grid, 1, "Hostname to connect to");
				TextField port_field = createInputField(opener_two_grid, 2, "Port to connect to"); // I'm unsure of how needed this is...
				//something for hostname
				//something for port
				
				Button play_button = new Button("Play mancala!!");
				GridPane.setConstraints(play_button, 1, 7, 1, 1);
				
				opener_two_grid.getChildren().addAll(play_button);
				
				//putting in a VBox AND HBox bc that's the easiest way I could think of to center things.
				VBox opener_two_vbox = new VBox(20, opener_two_grid);
				opener_two_vbox.setAlignment(Pos.CENTER);
				HBox opener_two_hbox = new HBox(20, opener_two_vbox);
				opener_two_hbox.setAlignment(Pos.CENTER);
				opener_two_hbox.setStyle("-fx-background-color: burlywood;");
				
				Scene opener_2 = new Scene(opener_two_hbox, 720, 720, Color.BURLYWOOD);
				root.setScene(opener_2);
				//then DON'T pass that to anywhere: just initialize a remote socket with that info. 
				//then wait for that remote socket to be accepted.
				//root.setScene();
				//run the other thing
				
			

				play_button.setOnAction(new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent e) {
						if (((RadioButton) player2.getSelectedToggle()) == null) {
							return;
						} else if (((RadioButton) player2.getSelectedToggle()).getText().equals("Human")) {
							gm.playerInputs[1] = Source.HUMAN;
						} else if (((RadioButton) player2.getSelectedToggle()).getText().equals("Computer")) {
							gm.playerInputs[1] = Source.AI;
						}
						
						//what port number to open our remote on: because I can't be bothered to figure out why constructing a remote with it here doesn't work.
						if (port_field.getText().matches("\\d*") && !hostname_field.getText().equals("")) {
			 	    		int port = Integer.parseInt(port_field.getText());
			 	    		String hostname = hostname_field.getText();
			 	    		remote = new Remote(buffer, port, hostname);
			 	    		
			 	    		try {
			 	    			//we SHOULD be in the GUI thread when this runs. meaning that 
			 	    			pool.execute(remote);
			 	    			Thread.sleep(2000); //wait two seconds.
			 	    			initializeGUI();
			 	    			
			 	    		} catch (InterruptedException ie) {
			 	    			//...huh. I wonder why that didn't work...?
			 	    			//just... don't do anything. If this breaks probably everything else is too.
			 	    		}
			 	    	} else {return;}
			 	    	
						try {
		 	    			//we SHOULD be in the GUI thread when this runs. meaning that 
							pool.execute(remote);
		 	    			Thread.sleep(2000); //wait two seconds.
		 	    			initializeGUI();
		 	    			
		 	    		} catch (InterruptedException ie) {
		 	    			//...huh. I wonder why that didn't work...?
		 	    			//just... don't do anything. If this breaks probably everything else is too.
		 	    		}
					}
				});
			}
		});
		
		
////////////////////////////////////////////////////////////////////////////////////
//Opener stage 2 server
		remote_server_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				config = Config.SERVER;
				gm.playerInputs[1] = Source.REMOTECLIENT;
				GridPane opener_two_grid = createGrid();
				
				//create 1st set of radio buttons
				final ToggleGroup player1 = createRadioButtons(opener_two_grid, 0, "Human", "Computer", "Player 1");
									
				TextField port_field = createInputField(opener_two_grid, 1, "Port to listen on"); // I'm unsure of how needed this is...
				TextField pit_number_field = createInputField(opener_two_grid, 2, "# of pits on each side");
				TextField piece_number_field = createInputField(opener_two_grid, 3, "Average # of pieces in pits");
				TextField time_limit_field = createInputField(opener_two_grid, 4, "Time limit / move ; 0 -> no limit");
				
				final ToggleGroup distribution = createRadioButtons(opener_two_grid, 5, "Random", "Standard", "Distribution of pieces");
				final ToggleGroup first = createRadioButtons(opener_two_grid, 6, "Client", "Server", "Who goes first?");
				Button play_button = new Button("Play mancala!!");
				GridPane.setConstraints(play_button, 1, 7, 1, 1);
				
				opener_two_grid.getChildren().addAll(play_button);
				
				//putting in a VBox AND HBox bc that's the easiest way I could think of to center things.
				VBox opener_two_vbox = new VBox(20, opener_two_grid);
				opener_two_vbox.setAlignment(Pos.CENTER);
				HBox opener_two_hbox = new HBox(20, opener_two_vbox);
				opener_two_hbox.setAlignment(Pos.CENTER);
				opener_two_hbox.setStyle("-fx-background-color: burlywood;");
				
				Scene opener_2 = new Scene(opener_two_hbox, 720, 720, Color.BURLYWOOD);
				root.setScene(opener_2);
				
				play_button.setOnAction(new EventHandler<ActionEvent>() {
			 	    @Override public void handle(ActionEvent e) {
			 	    	String message = "LOCAL INFO ";
			 	    	//grab numPits, numPieces, and time limits from the input fields. If they're not numbers, then don't bother.
			 	    	if (pit_number_field.getText().matches("\\d*")) {
			 	    		int val = Integer.parseInt(pit_number_field.getText());
			 	    		if (val > 3 && val < 10) {
			 	    			message = message + val + " ";
			 	    		} else {return;}
			 	    	} else {return;}
			 	    	if (piece_number_field.getText().matches("\\d*")) {
			 	    		int val = Integer.parseInt(piece_number_field.getText());
			 	    		if (val > 0 && val < 11) {
			 	    			message = message + val + " ";
			 	    		} else {return;}
			 	    	} else {return;}
			 	    	if (time_limit_field.getText().matches("\\d*")) {
			 	    		long val = Long.parseLong(time_limit_field.getText());
			 	    		message = message + val + " ";
			 	    	} else {return;}
			 	    	
			 	    	if (((RadioButton) player1.getSelectedToggle()) == null) {
			 	    		return;
			 	    	} else if (((RadioButton) player1.getSelectedToggle()).getText().equals("Human")) {
			 	    		gm.playerInputs[0] = Source.HUMAN;
			 	    	} else if (((RadioButton) player1.getSelectedToggle()).getText().equals("Computer")) {			
			 	    		gm.playerInputs[0] = Source.AI;
			 	    	}
			 	    	
			 	    	if (((RadioButton) first.getSelectedToggle()) == null) {
							return;
						} else if (((RadioButton) first.getSelectedToggle()).getText().equals("Server")) {
							message = message + "S "; //whoever's receiving this goes second
						} else if (((RadioButton) first.getSelectedToggle()).getText().equals("Client")) {
							message = message + "F "; //whoever's receiving this goes first
						}
						
						if (((RadioButton) distribution.getSelectedToggle()) == null) {
							return;
						} else if (((RadioButton) distribution.getSelectedToggle()).getText().equals("Random")) {
							message = message + "R"; //random distribution (we'll let buffer handler actually make this part of the message)
						} else if (((RadioButton) distribution.getSelectedToggle()).getText().equals("Standard")) {
							message = message + "S"; //Standard distribution
						}
						
						if (port_field.getText().matches("\\d*")) {
							//remote = new Remote(buffer, Integer.parseInt(port_field.getText()));
							pool.execute(remote);
							buffer.addElement(message);
							initializeGUI();
						} else {return;}
			 	    }
				});
			}
		});

	}   //end start
	
	
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

			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String message = target.get(0);
			while (message != null) {
				//split the string into an array, slot that array into a vector
				Vector<String> args = new Vector<String>(Arrays.asList(message.split(" ")));

				Boolean isLocal = args.get(0).equals("LOCAL");
				if (isLocal) {
					args.remove(0);
				}

				message = handleInput(args, isLocal);
			}

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
			for (int i = 1; i <= gm.numPits; i++) {
				Pit working_pit;
				if (j == 1) {
					// further player's pits; these are generated right-left
					working_pit = new Pit(gm.numPits * 130 + 85 - (i - 1) * 130, 260, i, 1, gm, root, buffer);
					working_pit.setFill(Color.SADDLEBROWN);
					root.getChildren().add(working_pit);
				} else {
					// closer player's pits; these are generated left-right
					working_pit = new Pit(working.get(gm.numPits - 1).getCenterX() + 130 * (i - 1), 400, gm.numPits + 1 + i, 0,
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
			double horizontal_location = (130 * Math.pow(-1, i)) + pits.get((gm.numPits - 1) * (i)).getCenterX();
			Store working = new Store(horizontal_location, vertical_location, 70, 125, i, gm, root, buffer);
			working.place = gm.numPits * i + i * 1;
			working.setFill(i == 1 ? Color.SADDLEBROWN : Color.DARKGOLDENROD);
			root.getChildren().add(working);
			working_vec.add(working);

			//add a mouse handler to update the display ; this is a horrendous workaround I know.
			working.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	        	@Override public void handle(MouseEvent event) {
	        		update_display(game_pane, gm);
	        	}
	        });
		}
		return working_vec;
	}

////////////////////////////////////////////////////////////////////////////////////////////
//GUI/Server Methods

	void update_display(Pane board, GameManager gm) {
		for (Pit change_pit : pits) {
			while (change_pit.size > gm.board[change_pit.place])
				board.getChildren().remove(change_pit.removePiece());
			while (change_pit.size < gm.board[change_pit.place])
				board.getChildren().add(change_pit.addPiece());
		}

		for (Store working_store : stores) {
			while (working_store.size > gm.board[working_store.place])
				board.getChildren().remove(working_store.removePiece());
			while (working_store.size < gm.board[working_store.place])
				board.getChildren().add(working_store.addPiece());
		}
		
		if(!gm.GUI_initialized) {initializeGUI();}
		try {
			javafx.scene.Node player_label = board.lookup("#turnlabel");
			((Text) player_label).setText("Currently player " + (gm.currentPlayer) + "'s turn.");
		} catch (NullPointerException e) {
			//I have been trying to fix this for 4 hours. I can't thnk of a workaround, so whatever.
			//do nothing!
		}
	}


////////////////////////////////////////////////////////////////////////////////////
//Buffer input Handling
	
	private String handleInput(Vector<String> args, Boolean isLocal) {
		//handle the input! A massive, sprawling if/else tree. how terrible!
		if (buffer.getSize() > 0) {
			buffer.remove(0);
		}
		if (args.get(0).equals("WELCOME")){
			System.out.println("Server connected.");
			return null;
		} else if (args.get(0).equals("READY") || args.get(0).equals("OK")) {
			//Received "READY" ack; it is now either the server's or client's turn to move.
			gm.start_time = System.currentTimeMillis();
			gm.expecting_move = true;
			if (gm.playerInputs[gm.currentPlayer] == Source.AI) {
				//make the AI do the thing.
				Tree tree = new Tree(gm.board, gm.currentPlayer, gm);
				int selection = tree.bestNextMove();	
				buffer.addElement("LOCAL " + Integer.toString(selection));
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
				//we win!
				if (config == Config.SERVER) {
					remote.remote_writer.println("LOSER");
					updateStatusLabel("YOU WIN");
				} else {
					updateStatusLabel("Player " + (gm.currentPlayer) + " WINS");
				}
			} else {
				//we lose :c
				updateStatusLabel("YOU LOSE");
			}
			return null;

		} else if (args.get(0).equals("WINNER")) {
			//end game, display winner text
			if (isLocal) {
				//we lose :c
				if (config == Config.SERVER) {
					remote.remote_writer.println("WINNER");
					updateStatusLabel("YOU LOSE");
				} else {
					updateStatusLabel("Player " + (1 - gm.currentPlayer) + "WINS");
				}
			} else {
				//we win!
				updateStatusLabel("YOU WIN");
			}
			return null;

		} else if (args.get(0).equals("TIE")) {
			//end game, display tie text
			//game ended in tie
			if (isLocal) {
				if (config == Config.SERVER) { remote.remote_writer.println("TIE");}
			}
			
			updateStatusLabel("YOU DON'T REALLY WIN OR LOSE, I GUESS");
			return null;

		} else if (args.get(0).equals("INFO")) {

			//THIS initializes our gameboard. None of the board should be initialized until this is ran.
			gm.numPits = Integer.parseInt(args.get(1));
			gm.numPieces = Integer.parseInt(args.get(2));
			gm.timeLimit = Long.parseLong(args.get(3));
			if (args.get(4).equals("F")) {
				//client goes first
				gm.currentPlayer = 1;
			} else if (args.get(4).equals("S")) {
				gm.currentPlayer = 0;
			}

			if (args.get(5).equals("S")) {
				//satndard config
				gm.board = new GameManager(gm.numPits, gm.numPieces).board;
			} else if (args.get(5).equals("R")) {
				//random config
				if (isLocal) {
					//send the config we have to the client
					gm.board = new GameManager(gm.numPits, gm.numPieces).board;
					gm.randomPieces();
					if (config == Config.SERVER) {
						for (int i = 0; i < gm.numPits; i++) {
							args.addElement(Integer.toString(gm.board[i+1]));
						}
					}
				} else {
					//set the board like the server did 
					gm.board = new GameManager(gm.numPits, gm.numPieces).board;
					for (int i = 1; i <= gm.numPits; i++) {
						gm.board[i] = Integer.parseInt(args.get(5+i));
						gm.board[i + numPits + 2] = Integer.parseInt(args.get(5+i));
					}
				}
			}
				
			if (isLocal && config != Config.LOCAL) {
				String out = "";
				for (int i = 0; i < args.size(); i++) {out = out + " " + args.elementAt(i);}
				out = out.substring(1); //remove first space
				remote.remote_writer.println(out); //send to remote once we have it up and running.
				gm.initialized = true;
				initializeGM();
				return null;
			} else if (config != Config.LOCAL) {
				remote.remote_writer.println("READY");
				if (gm.playerInputs[gm.currentPlayer] != Source.REMOTESERVER ) {
					Tree tree = new Tree(gm.board, gm.currentPlayer, gm);
					int selection = tree.bestNextMove();
					buffer.addElement("LOCAL " + Integer.toString(selection));
				}
				initializeGM();
				return null;
			} 
			
			if (gm.playerInputs[gm.currentPlayer] == Source.AI) {
				Tree tree = new Tree(gm.board, gm.currentPlayer, gm);
				int selection = tree.bestNextMove();	
				initializeGM();
				buffer.addElement("LOCAL " + Integer.toString(selection));
			}

			gm.initialized = true;
			initializeGM();
			return null;

		} else if (args.get(0).equals("P")) {
			// pie rule
			 
			if (gm.moveNumber == 1) {
				//if this is second move in game, do pie rule
				gm.moveNumber += 1;
				gm.pieRule(); // oh I don't trust this function at all.
			} else {
				if (config != Config.LOCAL) {remote.remote_writer.println("ILLEGAL");}
				if (isLocal) {
					return "LOCAL WINNER";
				} else {
					return "LOCAL LOSER";
				}
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
				try {
					int choice = Integer.parseInt(args.get(i));
					
					//our board is set up different from the requirements, so this requires adjustment
					try {
						if(!isLocal && pits.get(choice - 1 - (1*gm.currentPlayer)).player != gm.currentPlayer) { //player 1
							choice = choice + gm.numPits + 1;
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						//sometimes that happens. don't do anything with this.
					}
					
					int result = gm.move(choice, gm.currentPlayer); //do that move until there are no moves remaining
					
					//our board is set up different from the requirements, so this requires adjustment
					if (isLocal && choice > gm.numPits + 2) {
						choice = choice - gm.numPits - 1;
					}
					moves = moves + " " + Integer.toString(choice);

					if ((result == 2 || gm.illegal_flag) || (!isLocal && !gm.expecting_move)) {
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
			} else if (config == Config.CLIENT) {
				if (isLocal) {
					//Send our move out to the server.
					remote.remote_writer.println(moves);
				} else {
					//notify our AI to start its next move, otherwise nothing.
					remote.remote_writer.println("OK");
					if (gm.playerInputs[gm.currentPlayer] == Source.AI ) {
					}
				}
			} 
			
			if (gm.playerInputs[gm.currentPlayer] == Source.AI ) {
				Tree tree = new Tree(gm.board, gm.currentPlayer, gm);
				int selection = tree.bestNextMove();	
				buffer.addElement("LOCAL " + Integer.toString(selection));
			}
			
			return null;

		} else {
			return null;
			//return "Failed to handle input ;;;;";
		}
		//return "This should never show up";
	}
	
	
////////////////////////////////////////////////////////////////////////////////
//GUI setup functions, cuz jesus it's ugly without these 
	private ToggleGroup createRadioButtons(GridPane pane, int start, String name1, String name2, String label) {
		ToggleGroup group = new ToggleGroup();
		RadioButton choice1 = new RadioButton(name1);
		RadioButton choice2 = new RadioButton(name2);
		Text choiceText = new Text(label);
		choice1.setToggleGroup(group);
		choice2.setToggleGroup(group);
		GridPane.setConstraints(choice1, 1, start); //
		GridPane.setConstraints(choice2, 2, start);
		GridPane.setConstraints(choiceText, 0, start, 1, 1);
		
		pane.getChildren().addAll(choice1, choice2, choiceText);
		return group;
	}
	
	private GridPane createGrid() {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(20, 20, 20, 20)); //idk what this does
		grid.setVgap(10); //gap of 10 between things
		grid.setHgap(10);
	    grid.setPrefSize(300, 300);
	    grid.getColumnConstraints().add(new ColumnConstraints(120)); 
	    grid.getColumnConstraints().add(new ColumnConstraints(100)); 
	    grid.getColumnConstraints().add(new ColumnConstraints(100)); 
		return grid;
	}
	
	private TextField createInputField(GridPane grid, int start, String prompt ) {
		final TextField pit_number_field = new TextField();
		pit_number_field.setPromptText(prompt);
		
		GridPane.setConstraints(pit_number_field, 0, start, 2, 1);
		grid.getChildren().add(pit_number_field);
		return pit_number_field;
	}
	
	private void initializeGM() {
		game_pane = new Pane();
		pits = initializePits(game_pane, gm);
		stores = initializeStores(game_pane, gm);
	    gm.pits = pits;
	    gm.stores = stores;
	    gm.root = root;
	}
	
	private void initializeGUI() {
		if (config == Config.CLIENT) {
			root.setTitle("Mancala! - Client");
		} else if (config == Config.SERVER) {
			root.setTitle("Mancala! - Server");
		}
		
		initializeGM();
		Button pie_button = new Button("Enact: Pie Rule");
		pie_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				buffer.addElement("LOCAL P");
			}
		});
		
		Text p0label = new Text("Player 0's pits");
		Text p1label = new Text("Player 1's pits");
		Text turn_label = new Text("");
		turn_label.setId("turnlabel");
		game_pane.getChildren().add(turn_label);
		
		VBox showtime_vbox = new VBox(20, p1label, game_pane, p0label, pie_button);
		showtime_vbox.setAlignment(Pos.CENTER);
		HBox showtime_hbox = new HBox(20, showtime_vbox);
		showtime_hbox.setAlignment(Pos.CENTER);
		showtime_hbox.setStyle("-fx-background-color: burlywood;");
		Scene showtime = new Scene(showtime_hbox,  1500, 600, Color.BURLYWOOD);
		root.setScene(showtime);
		gm.GUI_initialized = true;
		update_display(game_pane, gm);
	}
	
	private void updateStatusLabel(String text) {
		try {if(!gm.GUI_initialized) {initializeGUI();}
		javafx.scene.Node player_label = game_pane.lookup("#turnlabel");
		((Text) player_label).setText(text);
		} catch (NullPointerException e) {
			game_pane.getChildren().add(new Text(text));
		}
	}

}
