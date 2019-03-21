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
		int move = selection + 1; //move is next pit
		
		//player's move
		if(player == 0) {
			board[selection] = 0;		//remove marbles from pit
			
			while(grabbed < 0) { //while marbles left
				if(move > 6)  				//only move on your side
					move = 0;
				board[move] = board[move] + 1;
				grabbed = grabbed - 1;
			}
			if(move == 0) { //user moves again landed in kala
				
			}
			else {
				if(board[move] == 0) { //empty pit, take opposing team's pit
					
				}
			}
		}
		
		//AI's move
		else {
			board[selection] = 0;		//remove marbles from pit
			
			while(grabbed < 0) { //while marbles left
				if(move > 13)  				//only move on your side
					move = 7;
				board[move] = board[move] + 1;
				grabbed = grabbed - 1;
			}
			if(move == 0) { //user moves again landed in kala
				
			}
			else {
				if(board[move] == 0) { //empty pit, take opposing team's pit
					
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
