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

//TODO make the GUI check each cycle *not* be terrible
//TODO see about deleting objects instead of just removing them from the root pane's list (memory leak?)
//TODO improve placement for pieces inside stores
//TODO Add displays: current player, each player's score; probably other things too 
//TODO add pits and stores to a pane, then put that pane centered in the root window. 
//TODO make GUI-based input check if a move is legal before sending it to the buffer.

enum Config {
	CLIENT, SERVER, LOCAL
}

enum Source {
	HUMAN, SERVER, CLIENT, AI
}

public class Test1 extends Application {
////////////////////////////////////////////////////////////////////////////////////////////
//Defining variables for our overall application. god there's so many. 
	private Vector<Pit> pits;
	private Vector<Store> stores;
	public int player = 0; // whose turn is it right now?
	public int numPits = 4; // is this still needed?
	public int numPieces = 6;
	private static Random key = new Random();
	Config config; //are we client or server?
	Source playerOne;
	Source playerTwo; //players can be either from a remote, or from a local human or AI. 
	
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
			// peel off first item, split it,
			System.out.println("added");
			String[] args = target.get(0).split(" ");
			
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
				if (config == Config.CLIENT) {
					System.out.println("Sending to remote");
					remote.remote_writer.println(args);}
				else {
					System.out.println("WHOA WE GOT A THING");
					String response = gm.handle_input(args);}
				// if first argument is a number, then this is a move; pass to game manager
			}
		

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

}