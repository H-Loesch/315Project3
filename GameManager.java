package mancala;

import java.util.Vector;

public class GameManager {
	int[] board = new int[14];

	public void move(int selection, int player) {
		
		//***check if legal move?***
		
		int grabbed = board[selection];
		board[selection] = 0;
		int move = selection + 1; //move is next pit
		
		//player's move
		if(player == 0) {
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
	
	public boolean playerNoStones() {
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
	
	public boolean computerNoStones() {
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
	
	public boolean winner() { //0 player 1 AI
		return (board[7] > board[0]);
	}
}
