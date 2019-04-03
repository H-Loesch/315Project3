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

public class Test1 extends Application {
////////////////////////////////////////////////////////////////////////////////////////////
//Defining variables for our overall application. god there's so many. 
	private Vector<Pit> pits;
	private Vector<Store> stores;
	// this buffer alerts an eventlistener every time it is changed.
	public int player = 0; // is this still needed?
	public int numPits = 4; // is this still needed?
	public int numPieces = 6;
	private static Random key = new Random();

	private GameManager gm = new GameManager(numPits, numPieces);
	Pane root = new Pane(); // root pane
	Pane centerPiece = new Pane(); // the gameboard itself will be stored here

	private DefaultListModel<String> buffer = new DefaultListModel<String>();
	static String config;

	RemoteTask server_write;
	RemoteTask server_read;
	RemoteTask server_general;
	ExecutorService pool = Executors.newFixedThreadPool(2);

////////////////////////////////////////////////////////////////////////////////////////////
//GUI management and main

	public static void main(String[] args) {
		try {
			config = args[2]; // are we client or server?
		} catch (ArrayIndexOutOfBoundsException e) {
			config = "server";
		}
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

		////////////////////////////////////////////////////////////////////////////////////////////
		// Different bits: the client and the server app are different here

		if (config == "server") {
			server_write = new RemoteTask(buffer, "write", "server");
			server_read = new RemoteTask(buffer, "read", "server");
			server_general = new RemoteTask(buffer, "", "server");
			server_initialize(80);
		} else if (config == "client") {
			server_write = new RemoteTask(buffer, "write", "client");
			server_read = new RemoteTask(buffer, "read", "client");
			server_general = new RemoteTask(buffer, "", "client");
			server_initialize(80, InetAddress.getLocalHost().getHostName());
		}

		// this buffer vector updates whenever
		buffer.addListDataListener(new BufferListener(buffer));
	}

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

			if (true) {
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
					// if first argument is a number, then this is a move; pass to game manager
				}
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
//Shared init Methods	
	private Vector<Pit> initializePits(Pane root, GameManager gm) {
		// initialize location, all that for the pits.
		// don't yet initialize the stones that will go in them. Or maybe do, idk.
		Vector<Pit> working = new Vector<Pit>();
		/*
		 * 7 8 9 10 11 12 1 2 3 4 5 6
		 */
		for (int j = 1; j < numPits; j++) {
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
					root.getChildren().add(working_pit);
					working_pit.setFill(Color.DARKGOLDENROD);
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
//Shared GUI/Server Methods	

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
		System.out.println("HOOOOOOOOOOOOOOOOONK");
	}

	// these are all... so terrible.
	// they use the remoteTask.in field to pass input to the task, then run that
	// task from the pool.
	// I guess there are probably... worse ways to do this? probably?

	void write_to_remote(String _in) {
		server_write.in = _in;
		pool.execute(server_write);
	}

	void read_from_remote() {
		pool.execute(server_read);
	}

	void server_initialize(int port) {
		server_general.in_int = port;
		server_general.in = "initialize";
		server_general.in2 = "mancalaServer18242";
		pool.execute(server_general);
	}

	void server_initialize(int port, String hostname) {
		server_general.in_int = port;
		server_general.in = "initialize";
		server_general.in2 = hostname;
		pool.execute(server_general);
	}

	void server_close() {
		server_general.in = "close";
		pool.execute(server_general);
	}

}