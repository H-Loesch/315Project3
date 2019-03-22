package mancala;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class GameManager {
	public int[] board;
	int time = 10*1000;
	boolean legal;
	int size;
	public int player = 0;
	public boolean winner = false;
	Scanner scanner = new Scanner(System.in);
	Random rand = new Random();

	GameManager(int size, int stones, boolean random) { //initializes board, inputs must be even
		board = new int[size];
		this.size = size;
		int n;

		if(random) {			//random number of stones in each
			while(stones > 0) {
				for(int i = 1; i < size/2 ; i++) { //skip player and AI's kala
					if(stones/2 != 1)
						n = rand.nextInt(stones/2);
					else
						n = 1;
					//System.out.println("Adding " + n + " stones to location " + i + " and " + (size - i));
					board[i] = board[i] + n;						//user's side
					board[size/2 + i] = board[size/2 + i] + n;				//computer's side
					stones = stones - 2*n;				//decrease stones left to place
					//System.out.println("Stones left: " + stones);
					if(stones == 0)
						break;
				}
			}
		}
		else {			//same number of stones in each
			for(int i = 1; i < size/2 ; i++) { //skip player and AI's kala
				board[i] = stones;						//user's side
				board[size/2 + i] = stones;				//computer's side
			}
		}
	}

	void run() {
		legal = false;
		int selection = -1;
		print();
		System.out.println("Player's " + player + " turn");

		long finishTime = System.currentTimeMillis() + time;
		while(!legal && finishTime > System.currentTimeMillis()) { //continues till legal move made
			System.out.println("\ninput move: ");
			selection = Integer.parseInt(scanner.next());   //get new input for move
			legal = legalMove(selection);
		}
		if(!legal){
			System.out.println("Times up");
			System.exit(0);
		}
		//legal move made
		move(selection);

		//check game over state
		if(!playerHasStones() || !computerHasStones()) {
			winner();								//game over calculate winner
		}
		run();  //continue to run
	}


	boolean legalMove(int selection) {
		boolean legalMove = true;

		if(selection >= size || selection <= 0) {   //illegal move for any player
			legalMove = false;
		}
		else if(board[selection] == 0){				//illegal move for any player
			legalMove = false;
		}

		if(player == 0) {
			if(selection >= size/2) { //illegal move for player
				legalMove = false;
			}
		}
		else {
			if(selection <= size/2) { //illegal move for computer
				legalMove = false;
			}
		}

		if(!legalMove) {
			System.out.println("Illegal Move!!!");
		}
		return legalMove;

	}

	void move(int selection) {  //returns true after legal move made, returns false on illegal move

		int grabbed = board[selection];
		board[selection] = 0;		//remove marbles from pit

		int move = selection; //move is next pit
		int marblesWon;

		//player's move
		if(player == 0) {
			while(grabbed > 0) { //while marbles left
				move += 1; //place in next pit
				if(move == size/2) {
					move += 1;			//skip AI's kala
				}
				if(move >= size)  				//start over
					move = 0;
				board[move] = board[move] + 1;
				grabbed = grabbed - 1;
			}
			System.out.println("Move ended on: " + move);
			if(move == 0) { //landed in kala, go again
				System.out.println("Go again!");
				run();
			}
			else {
				if(move < size/2) {  //on your side
					if(board[move] == 1) { //empty pit
						int opposite = size - move;
						System.out.println("Won pit: " + opposite);
						marblesWon = board[size - move] + 1; //opposing pit plus capturing marble
						board[size-move] = 0;				//set gathered pit's marble to 0
						board[move] = 0;
						board[0] += marblesWon;   		//adds marbles won to player's kala
					}
				}
			}
			//switch players
			if(player == 0)
				player = 1;
			else
				player = 0;
		}

		//AI's move
		else {
			while(grabbed > 0) { //while marbles left
				move += 1;
				if(move >= size)  				//only move on your side
					move = 1;				//skip player's kala
				board[move] = board[move] + 1;
				grabbed = grabbed - 1;
			}
			System.out.println("Move ended on: " + move);
			if(move == size/2) { //landed in kala, go again
				System.out.println("Go again!");
				run();
			}
			else {
				if(move > size/2) {  //on your side
					if(board[move] == 1) { //empty pit
						int opposite = size - move;
						System.out.println("Won pit: " + opposite);
						marblesWon = board[opposite] + 1; //opposing pit plus capturing marble
						board[opposite] = 0;				//set gathered pit's marble to 0
						board[move] = 0;
						board[size/2] += marblesWon;   		//adds marbles won to player's kala
					}
				}
			}
		}
	}

	boolean playerHasStones() {    // need to add gather all stones
		boolean playerHasStones = false;

		for(int i = 1; i < size/2; i++) {
			if(board[i] > 0)
				playerHasStones = true;
		}
		if(!playerHasStones) {
			for(int i = size/2 + 1; i < size; i++) {
				board[7] += board[i];
				board[i] = 0;
			}
		}
		return playerHasStones;
	}

	boolean computerHasStones() {  // need to add gather all stones
		boolean computerHasStones = false;

		for(int i = size/2 + 1; i < size; i++) {
			if(board[i] > 0)
				computerHasStones = true;
		}
		if(!computerHasStones) {
			for(int i = 1; i < size/2; i++) {
				board[0] += board[i];
				board[i] = 0;
			}
		}
		return computerHasStones;
	}

	boolean winner() { //0 player 1 AI
		System.out.println("Player 1's score: " + board[0]);
		System.out.println("AI's score: " + board[7]);
		return (board[size/2] > board[0]);
	}


	void print() {
		//print top
		System.out.print(" | ");
		for(int i = size - 1; i > size/2; i--) {
			System.out.print(board[i] + " | ");
		}
		//print kalas
		System.out.print("\n" + board[0]);
		for(int i = 0; i < size; i++)
			System.out.print("  ");
		System.out.println(board[size/2]);
		//print bottom
		System.out.print(" | ");
		for(int i = 1; i < size/2; i++) {
			System.out.print(board[i] + " | ");
		}
		System.out.println();
	}
}