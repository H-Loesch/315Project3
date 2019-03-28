package mancala;

import java.util.Vector;
import java.util.Scanner;

public class GameManager {
	public int[] board = new int[14];
	public int player = 0;
	public int playerWon = -1;
	Scanner scanner = new Scanner(System.in);
	private int numPits = 6;
	private int numPieces = 4;
	private int initialSetup;

	GameManager() { //initializes board
		for(int i = 1; i < 14; i++) { //skip player's kala at 0
			if(i == 7)
				i++;			//skip AI's kala at 7
			board[i] = 4;
		}
	}
	
	//TODO alternate constructor here with non-standard pit #, seed #, seed distribution (other stuff?)

	void run() {
		boolean legal = false;
		int selection = 0;
		print();
		System.out.println("Player's " + player + " turn");


		while(!legal) { //continues till legal move made
			System.out.println("\ninput move: ");
			selection = Integer.parseInt(scanner.next());   //get new input for move
			legal = legalMove(selection, player);
		}
		//legal move made
		player = move(selection, player);

		//check game over state
		if(!playerHasStones() || !computerHasStones()) {
			winner();								//game over calculate winner



		}

		run();  //continue to run
	}

	boolean legalMove(int selection, int player) {
		boolean legalMove = true;

		if(board[selection] == 0 || selection > 13) { //illegal move for any player
			legalMove = false;
		}

		if(player == 0) {
			if(selection == 0 || selection <= 7) { //illegal move for player
				legalMove = false;
			}
		}
		else {
			if(selection > 6) { //illegal move for computer
				legalMove = false;
			}
		}

		if(!legalMove) {
			System.out.println("Illegal Move!!!");
		}
		return legalMove;

	}

	int move(int selection, int player) {  //returns true after legal move made, returns false on illegal move

		if (!legalMove(selection, player)) {
			return 2;
		}
		
		int grabbed = board[selection];
		board[selection] = 0;		//remove marbles from pit

		int move = selection; //move is next pit
		int marblesWon;

		//player's move
		if(player == 0) {
			while(grabbed > 0) { //while marbles left
				move += 1; //place in next pit
				if(move == 7) {
					move += 1;			//skip AI's kala
				}
				if(move > 13)  				//start over
					move = 0;
				board[move] = board[move] + 1;
				grabbed = grabbed - 1;
			}
			System.out.println("Move ended on: " + move);
			if(move == 0) { //landed in kala, go again
				System.out.println("Go again!");
				return 0;
			}
			else {
				if(move < 7) {  //on your side
					if(board[move] == 1) { //empty pit
						int opposite = 14 - move;
						System.out.println("Won pit: " + opposite);
						marblesWon = board[14 - move] + 1; //opposing pit plus capturing marble
						board[14-move] = 0;				//set gathered pit's marble to 0
						board[move] = 0;
						board[0] += marblesWon;   		//adds marbles won to player's kala
					}
				}
			}
		}

		//AI's move
		else {
			while(grabbed > 0) { //while marbles left
				move += 1;
				if(move > 13)  				//only move on your side
					move = 1;				//skip player's kala
				board[move] = board[move] + 1;
				grabbed = grabbed - 1;
			}
			System.out.println("Move ended on: " + move);
			if(move == 7) { //landed in kala, go again
				System.out.println("Go again!");
				return 1;
			}
			else {
				if(move > 7) {  //on your side
					if(board[move] == 1) { //empty pit
						int opposite = 14 - move;
						System.out.println("Won pit: " + opposite);
						marblesWon = board[opposite] + 1; //opposing pit plus capturing marble
						board[opposite] = 0;				//set gathered pit's marble to 0
						board[move] = 0;
						board[7] += marblesWon;   		//adds marbles won to player's kala
					}
				}
			}
		}
		return 1 - player;
	}

	boolean playerHasStones() {    // need to add gather all stones
		boolean playerHasStones = false;

		for(int i = 1; i < 7; i++) {
			if(board[i] > 0)
				playerHasStones = true;
		}
		if(!playerHasStones) {
			for(int i = 8; i < 14; i++) {
				board[7] += board[i];
				board[i] = 0;
			}
		}
		return playerHasStones;
	}

	boolean computerHasStones() {  // need to add gather all stones
		boolean computerHasStones = false;

		for(int i = 8; i < 14; i++) {
			if(board[i] > 0)
				computerHasStones = true;
		}
		if(!computerHasStones) {
			for(int i = 1; i < 7; i++) {
				board[0] += board[i];
				board[i] = 0;
			}
		}
		return computerHasStones;
	}

	int winner() { //0 player 1 AI
		System.out.println("Player 1's score: " + board[0]);
		System.out.println("AI's score: " + board[7]);
		if(board[0] > board[7])		//player wins
			return 0;
		else if(board[7] > board[6])	//AI wins
			return 1;
		else							//Tie
			return 2;
	}

	void print() {
		//print top
		System.out.print("| ");
		for(int i = 13; i > 7; i--) {
			System.out.print(board[i] + " | ");
		}
		//print kalas
		System.out.println("\n" + board[0] + "                       " + board[7]);
		//print bottom
		System.out.print("| ");
		for(int i = 1; i < 7; i++) {
			System.out.print(board[i] + " | ");
		}
		System.out.println();
	}
	
	//illegal moves should still go here, and then return smth that stops the play loop
	
	//this returns a string: "OK" if move is okay, go ahead and grab the next input
	//"END" for when game is over due to regular play 
	//"ILLEGAL" if, well. if illegal lol. I'm a lil too tired to be planning out structural stuff I think.
	String handle_input(String[] input_arr) {
		//probably something that involves checking for input
		if (input_arr[0] == "INFO") {
			return "placeholder ;)";

			//do game setup stuff
			//store the initial setup stuff in the game manager itself, then use that in main to figure everything else out
			//this will also necessitate moving gm initialization out of its constructor
		} else if (input_arr[0] == "P"){
			// pie rule
			return "placeholder ;)";

			//....handle the pie rule? idk bud
		} else if (input_arr[0].matches("^[0-9]*$")) {
			System.out.println("Player's " + player + " turn");
			//if first char is a number (regular ol' move)
			for (int i = 0; i < input_arr.length; i++) {
				//move will have to be adjusted for this to work: make the error code actually work
				int result = this.move(Integer.parseInt(input_arr[i]),  player); //do that move until there are no moves remaining
				if (result == 0 || result == 1) {
					//return a player, move successful; this should also make subsequent moves from the same input return illegal
					player = result;
					return "OK";
				} else {
					//returned 2, move failed
					return "ILLEGAL";
				}
			}
		return "placeholder ;)";
		} else {
			return "placeholder ;)";
		}
	}

}
