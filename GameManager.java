package mancala;

import java.util.Vector;

public class GameManager {
	int[] board = new int[14];
	
	public void run() {
		int selection, player;
		initialize(); //initialize board
		player = 0;   //player goes 1st
		
		selection = -1;  //to always fail at start
		
		while(!legalMove(selection, player)) { //continues till legal move made
			selection = 1;   //need to get new input for move
		}
		
	}
	
	boolean legalMove(int selection, int player) {
		boolean legalMove = true;
		
		if(player == 0) {
			if(selection == 0 || selection > 6) { //illegal move for player
				return false;
			}
		}
		else {
			if(selection <= 7) { //illegal move for computer
				return false;
			}
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
			}
			if(move == 0) { //landed in kala, go again
				
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
			}
			if(move == 7) { //landed in kala, go again
				
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
	
	boolean playerNoStones() {    // need to add gather all stones
		boolean playerHasStones = false;
		
		for(int i = 1; i <= 3; i++) {
			if(board[i] >= 0)
				playerHasStones = true;
		}
		for(int i = 11; i <= 13; i++) {
			if(board[i] >= 0)
				playerHasStones = true;
		}
	
		return playerHasStones;
	}
	
	boolean computerNoStones() {  // need to add gather all stones
		boolean computerHasStones = false;
		
		for(int i = 4; i <= 6; i++) {
			if(board[i] >= 0)
				computerHasStones = true;
		}
		for(int i = 8; i <= 10; i++) {
			if(board[i] >= 0)
				computerHasStones = true;
		}
	
		return computerHasStones;
	}
	
	boolean winner() { //0 player 1 AI
		return (board[7] > board[0]);
	}

	void initialize() {
		
	}
}
