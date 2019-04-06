package mancala;

import java.util.Vector;
import java.util.Random;
import java.util.Scanner;

public class GameManager {

	public int[] board;
	
	public int currentPlayer = 0; // whose turn is it right now?
	public int localPlayer = 0; //which player is local? (it can also be both of them so whatever)

	public int winner = 0;
	Scanner scanner = new Scanner(System.in);
	public int numPits = 6; //number of pits on each side of the board
	public int numPieces = 4; //average number of pieces / pit
	public long timeLimit = 5000; //time in milliseconds for each player to make a move 
	public int moveNumber; //home many moves have been made this game.
	Source playerInputs[] = {Source.HUMAN, Source.HUMAN}; //Will contain our tw o players' sources. Defaults to 2-player local
	
	public long start_time; //time acknowledgement is received
	public long end_time;  //time move is received 
	
	Boolean acknowledge = false; //have we received an OK for the latest move
	void randomPieces() {  //used to create a random distribution of pieces, uses numPieces for average number

		Random rnd = new Random();
		int[] distro = new int[numPits];

		//insures each pit has at least 1 piece
		for(int k = 0; k < numPits; k++) {
			distro[k]=1;
		}
		for(int i = 0; i < numPits*(numPieces-1); i++) {
			distro[Math.abs(rnd.nextInt()%numPits)]++;
		}
		for(int j = 0; j < numPits; j++) {
			board[j+1]=distro[j];
			board[j+2+numPits]=distro[j];
		}
		
	}


	GameManager() { //initializes board

		 board = new int[(2*numPits)+2];
				 
		for(int i = 1; i < (2*numPits)+2; i++) { //skip player's kala at 0
			if(i == numPits+1)
				i++;			//skip AI's kala
			board[i] = numPieces;
		}
	}

	//input of negative value trigger random distribution
	//numPieces it set to absolute value of parameter
	//this value is used as the average value for the pits
	GameManager(int newPits, int newPieces) { //initializes board with values other that 6 pits and 4 pieces
		
		
		numPits = newPits;
		numPieces = Math.abs(newPieces);
		board = new int[(2*numPits)+2];

		for(int i = 1; i < (2*numPits)+2; i++) { //skip player's kala at 0
			if(i == numPits+1)
				i++;			//skip AI's kala
			board[i] = numPieces;
		}
		if(newPieces<0) {
			randomPieces();
		}
	}

	void pieRule() {
		
		for(int i = 0; i <= numPits; i++) {
			int temp = board[i];
			board[i] = board[i+numPits+1];
			board[i+numPits+1] = temp;
		}

	}
	
	
	void run() {
		
		
		boolean legal = false;
		int selection = 0;
		print();
		System.out.println("Player's " + currentPlayer + " turn");


		while(!legal) { //continues till legal move made
			System.out.println("\ninput move: ");
			selection = Integer.parseInt(scanner.next());   //get new input for move
			legal = legalMove(selection, currentPlayer);
		}
		//legal move made
		currentPlayer = move(selection, currentPlayer);

		//check game over state
		if(!playerHasStones() || !computerHasStones()) {
			findWinner();								//game over calculate winner



		}

		run();  //continue to run
	}

	boolean legalMove(int selection, int player) {
		boolean legalMove = true;

		if(board[selection] == 0 || selection > ((2*numPits)+2)-1) { //illegal move for any player (empty pit || out of bounds)
			legalMove = false;
		}

		if(selection==0 || selection == numPits+1) { //illegal move (stores)
			legalMove = false;
		}
		if(player == 0) {
			if(selection <= numPits+1) { //illegal move for player
				legalMove = false;
			}
		}
		else {
			if(selection > numPits) { //illegal move for computer
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
				if(move == numPits+1) {
					move += 1;			//skip AI's kala
				}
				if(move > ((2*numPits)+2)-1)  				//start over
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
				if(move < numPits+1) {  //on your side
					if(board[move] == 1) { //empty pit
						int opposite = (2*numPits)+2 - move;
						System.out.println("Won pit: " + opposite);
						marblesWon = board[(2*numPits)+2 - move] + 1; //opposing pit plus capturing marble
						board[(2*numPits)+2-move] = 0;				//set gathered pit's marble to 0
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
				if(move > ((2*numPits)+2)-1)  				//only move on your side
					move = 1;				//skip player's kala
				board[move] = board[move] + 1;
				grabbed = grabbed - 1;
			}
			System.out.println("Move ended on: " + move);
			if(move == numPits+1) { //landed in kala, go again
				System.out.println("Go again!");
				return 1;
			}
			else {
				if(move > numPits+1) {  //on your side
					if(board[move] == 1) { //empty pit
						int opposite = (2*numPits)+2 - move;
						System.out.println("Won pit: " + opposite);
						marblesWon = board[opposite] + 1; //opposing pit plus capturing marble
						board[opposite] = 0;				//set gathered pit's marble to 0
						board[move] = 0;
						board[numPits+1] += marblesWon;   		//adds marbles won to player's kala
					}
				}
			}
		}
		if(!playerHasStones() || !computerHasStones()) {
			return findWinner();								//game over calculate winner
		}
		return 1 - player;
	}

	boolean playerHasStones() {    // need to add gather all stones
		boolean playerHasStones = false;

		for(int i = 1; i < numPits+1; i++) {
			if(board[i] > 0)
				playerHasStones = true;
		}
		if(!playerHasStones) {
			for(int i = numPits+2; i < (2*numPits)+2; i++) {
				board[numPits+1] += board[i];
				board[i] = 0;
			}
		}
		return playerHasStones;
	}

	boolean computerHasStones() {  // need to add gather all stones
		boolean computerHasStones = false;

		for(int i = numPits+2; i < (2*numPits)+2; i++) {
			if(board[i] > 0)
				computerHasStones = true;
		}
		if(!computerHasStones) {
			for(int i = 1; i < numPits+1; i++) {
				board[0] += board[i];
				board[i] = 0;
			}
		}
		return computerHasStones;
	}

	int findWinner() { //0 player 1 AI

		System.out.println("Player 1's score: " + board[0]);
		System.out.println("AI's score: " + board[numPits+1]);
		if(board[0] > board[numPits+1])		//player wins
			return 3;
		else if(board[numPits+1] > board[0])	//AI wins
			return 4;
		else							//Tie
			return 5;
	}

	void print() {
		//print top
		System.out.print("| ");
		for(int i = (2*numPits)+2; i > numPits+1; i--) {
			System.out.print(board[i] + " | ");
		}
		//print kalas
		System.out.println("\n" + board[0] + "                       " + board[numPits+1]);
		//print bottom
		System.out.print("| ");
		for(int i = 1; i < numPits+1; i++) {
			System.out.print(board[i] + " | ");
		}
		System.out.println();
	}

}
