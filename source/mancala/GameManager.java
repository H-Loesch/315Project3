package mancala;

import java.util.Vector;
import java.util.Scanner;

public class GameManager {
	public int[] board = new int[14];
	public int player = 0;
	public boolean winner = false;
	Scanner scanner = new Scanner(System.in);
	
	GameManager() { //initializes board
		for(int i = 1; i < 14; i++) { //skip player's kala
			if(i != 7)
				board[i] = 4;			//skip AI's kala
		}
	}
	
	/*void run() {
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
		move(selection, player);
		
		//check game over state
		if(!playerHasStones() || !computerHasStones()) {
			winner();								//game over calculate winner
			
			
			
		}
		
		//switch players
		if(player == 0)
			player = 1;
		else
			player = 0;
		
		run();  //continue to run
	}*/
	
	boolean legalMove(int selection, int player) {
		boolean legalMove = true;
		
		if(board[selection] == 0 || selection > 13) { //illegal move for any player
			legalMove = false;
		}
		
		if(player == 0) {
			if(selection == 0 || selection > 6) { //illegal move for player
				legalMove = false;
			}
		}
		else {
			if(selection <= 7) { //illegal move for computer
				legalMove = false;
			}
		}
		
		if(!legalMove) {
			System.out.println("Illegal Move!!!");
		}
		return legalMove;
		
	}
	
	int move(int selection, int player) {  //returns true after legal move made, returns false on illegal move
		
		int grabbed = board[selection];
		board[selection] = 0;		//remove marbles from pit
		
		int move = selection; //move is next pit
		int marblesWon;
		
		if (!legalMove(selection, player)) {
			return 2;
		}
		
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
	
	boolean winner() { //0 player 1 AI
		System.out.println("Player 1's score: " + board[0]);
		System.out.println("AI's score: " + board[7]);
		return (board[7] > board[0]);
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

}
