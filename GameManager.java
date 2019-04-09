package mancala;

import java.util.Vector;
import java.util.Scanner;

public class GameManager {
	int[] board = new int[14];
	int player = 0;
	boolean winner = false;
	Scanner scanner = new Scanner(System.in);

	GameManager() { //initializes board
		for(int i = 1; i < 14; i++) { //skip player's kala
			if(i == 7)
				i++;			//skip AI's kala
			board[i] = 4;
		}
	}

	void run() {
		boolean legal = false;
		int selection = 0;
		print();


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

			//*****exit game / start new game

		}

		//switch players
		if(player == 0)
			player = 1;
		else
			player = 0;

		run();  //continue to run
	}

	boolean legalMove(int selection, int player) {
		boolean legalMove = true;

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

		if(selection > 13) //too high index
			legalMove = false;

		if(!legalMove) {
			System.out.println("Illegal Move!!!");
		}
		return legalMove;

	}

	void move(int selection, int player) {  //returns true after legal move made, returns false on illegal move

		int grabbed = board[selection];
		board[selection] = 0;		//remove marbles from pit

		int move = selection + 1; //move is next pit
		int marblesWon;

		//player's move
		if(player == 0) {
			while(grabbed > 0) { //while marbles left
				if(move == 7) {
					move += 1;			//skip AI's kala
				}
				if(move > 13)  				//start over
					move = 0;
				board[move] = board[move] + 1;
				grabbed = grabbed - 1;
				move += 1;
			}
			if(move == 0) { //landed in kala, go again
				run();
			}
			else {
				if(move < 7) {  //on your side
					if(board[move] == 0) { //empty pit
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
				if(move > 13)  				//only move on your side
					move = 1;				//skip player's kala
				board[move] = board[move] + 1;
				grabbed = grabbed - 1;
				move += 1;
			}
			if(move == 7) { //landed in kala, go again
				run();
			}
			else {
				if(move > 7) {  //on your side
					if(board[move] == 0) { //empty pit
						marblesWon = board[14 - move] + 1; //opposing pit plus capturing marble
						board[14-move] = 0;				//set gathered pit's marble to 0
						board[move] = 0;
						board[0] += marblesWon;   		//adds marbles won to player's kala
					}
				}
			}
		}
	}

	boolean playerHasStones() {    // need to add gather all stones
		boolean playerHasStones = false;

		for(int i = 1; i < 7; i++) {
			if(board[i] > 0)
				playerHasStones = true;
		}
		if(!playerHasStones) {
			for(int i = 8; i < 14; i++)
				board[7] += board[i];
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
			for(int i = 1; i < 7; i++)
				board[0] += board[i];
		}
		return computerHasStones;
	}

	boolean winner() { //0 player 1 AI
		System.out.println("player's score: " + board[0]);
		System.out.println("AI's score: " + board[7]);
		return (board[7] > board[0]);
	}

	void print() {
		//print top
		for(int i = 13; i > 7; i--) {
			System.out.print(board[i] + " ");
		}
		//print kalas
		System.out.println("\n" + board[0] + "         " + board[7]);
		//print bottom
		for(int i = 1; i < 7; i++) {
			System.out.print(board[i] + " ");
		}
		System.out.println();
	}
}
