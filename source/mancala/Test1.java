package mancala;

import java.net.InetAddress;


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

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

//TODO make the GUI check each cycle *not* be terrible
//TODO see about deleting objects instead of just removing them from the root pane's list (memory leak?)
//TODO improve placement for pieces inside stores
//TODO Add displays: current player, each player's score; probably other things too 
//TODO add pits and stores to a pane, then put that pane centered in the root window. 
//TODO make GUI-based input check if a move is legal before sending it to the buffer.

enum Config {
	CLIENT, SERVER, LOCAL
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
	private static Random key = new Random();
	private Color player1Color = Color.SADDLEBROWN;
	private Color player2Color = Color.DARKGOLDENROD;
	Config config; //are we client or server?

	static TextField setPits;
	static TextField setPieces;
	
	
	private GameManager gm = new GameManager(numPits, numPieces);
	Pane root = new Pane(); // root pane
	Pane centerPiece = new Pane(); // the gameboard itself will be stored here

	private DefaultListModel<String> buffer = new DefaultListModel<String>();
	
	
	static int configType = 0;
	//remote threads
	RemoteTask server_write;
	RemoteTask server_read;
	RemoteTask server_general;
	ExecutorService pool = Executors.newFixedThreadPool(2);

////////////////////////////////////////////////////////////////////////////////////////////
//GUI management and main

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primary) throws Exception {
		primary.show(); // shows the scene in the newly-created application
		//root.setPrefSize(135*(numPits+2), 720);
		root.setPrefSize(720, 720);
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
		
		
		
		Button play = new Button("Play");
		root.getChildren().add(play);
		
		
		//primary.show();
		//update_display(root,gm);
		
		
		primary.setScene(new Scene(root));
		
		
		while(configType==0) {
			int x = 3;
			
		}
		
		
		//root.getChildren().remove(setPits);
		
		
		//pits = initializePits(root, gm);
		//stores = initializeStores(root, gm);
		//update_display(root, gm);
		
		//primary.setScene(new Scene(root)); // sets stage to show the scene
		
		// canvas.getChildren().addAll(placeInitialShapes(pits));
		primary.show(); // shows the scene in the newly-created application
		
		//placeholder input
	    Scanner uinput = new Scanner(System.in);  // Create a Scanner object
		System.out.println("hey idiot do you want to be a server? if yes type 1. client, type 2. to die, type a will saying you wanna leave me everything.");
		//int config = Integer.parseInt(uinput.nextLine());
		//int config = 2;
		if (configType == 1) {
			initializeAsServer(80);
		} else if (configType == 2) {
			initializeAsClient(80, InetAddress.getLocalHost().getHostName());
		}
		// Add a listener to our buffer so it does stuff.		
		buffer.addListDataListener(new BufferListener(buffer));
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	//Buffer listener
	class BufferListener implements ListDataListener {
		DefaultListModel<String> target; // the buffer of strings that is our buffer
		RemoteTask remote;
		
		BufferListener(DefaultListModel<String> _target) {
			super();
			target = _target;
			
		}

		public void contentsChanged(ListDataEvent e) {
			// what happens when something more complex happens to this list?
			System.out.println("whoa lmao");
		}

		public void intervalAdded(ListDataEvent e) {
			// what happens when something is added to list?
			// peel off first item, split it,
			System.out.println("added");
			String[] args = target.get(0).split(" ");
			
			/*
			if (config == "server") {
				// we're a server
				// acknowledgements handling
				if (args[0] == "WELCOME") {

				} else if (args[0] == "READY") {

				} else if (args[0] == "OK") {

				} else if (args[0] == "ILLEGAL") {

				} else if (args[0] == "TIME") {

				} else if (args[0] == "LOSER") {

				} else if (args[0] == "WINNER") {

				} else if (args[0] == "TIE") {

					// moves and configuration
				} else {
					String response = gm.handle_input(args);
					// if first argument is a number, then this is a move; pass to game manager
				}
			} else {
				writeToRemote(args[0]);
				// we're a client
				if (args[0] == "WELCOME") {

				} else if (args[0] == "READY") {

				} else if (args[0] == "OK") {

				} else if (args[0] == "ILLEGAL") {

				} else if (args[0] == "TIME") {

				} else if (args[0] == "LOSER") {

				} else if (args[0] == "WINNER") {

				} else if (args[0] == "TIE") {

					// moves and configuration
				} else {
					String response = gm.handle_input(args);
					writeToRemote(response);
					// if first argument is a number, then this is a move; pass to game manager
				}
			}*/

			update_display(root, gm);
			buffer.remove(0);
			// handling of some acknowledgments should probably go here
			// else, send them off to the game manager! woo!

			// pass that string along to gm.handle_input()
			// update_display()
			// remove that string from gm.handle_input()
		}

		public void intervalRemoved(ListDataEvent e) {
			// what happens when something is removed from list?
			System.out.println("removed");
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

	// these are all... so terrible.
	// they use the remoteTask.in field to pass input to the task, then run that
	// task from the pool.
	// I guess there are probably... worse ways to do this? probably?

	void writeToRemote(String _in) {
		server_write.in = _in;
		pool.execute(server_write);
	}

	void readFromRemote() {
		pool.execute(server_read);
	}

	void initializeAsServer(int port) {
		server_write = new RemoteTask(buffer, Task.WRITE, Config.SERVER);
		server_read = new RemoteTask(buffer, Task.READ, Config.SERVER);
		server_general = new RemoteTask(buffer, Task.GENERAL, Config.SERVER);
		
		server_general.in = Integer.toString(port);
		server_general.in2 = null;
		pool.execute(server_general);
	}
	
	void initializeAsClient(int port, String hostname) {
		//hostname =  InetAddress.getLocalHost().getHostName(); // set hostname as self
		server_write = new RemoteTask(buffer, Task.WRITE, Config.CLIENT);
		server_read = new RemoteTask(buffer, Task.READ, Config.CLIENT);
		server_general = new RemoteTask(buffer, Task.GENERAL, Config.CLIENT);
		
		server_general.in = Integer.toString(port);
		server_general.in2 = hostname;
		pool.execute(server_general);
	}

	void closeRemoteConnection() {
		server_general.in = "close";
		pool.execute(server_general);
	}
	

}


class playButtonHandler implements EventHandler<ActionEvent>{
	
	@Override
	public void handle(ActionEvent event) {
		Test1.configType = 2;
		Test1.numPits = Integer.parseInt(Test1.setPits.getText());
		
	}
	
	
	
}
