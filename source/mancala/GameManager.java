package mancala;

import java.util.Vector;

public class GameManager {
	int[] board = new int[14];

	public void move(int selection, int player) {
		int grabbed = board[selection];
		board[selection] = 0;
		for(int i = 1; i <= grabbed; i++) { //does this need to be <= or <
			board[selection + i] = board[selection + i] + 1;
			
			if(i == grabbed){ //last marble placed
				if(player == 0) { //user's move
					if(selection + i == 0) { //user moves again
					
					}
					else if(selection + i <= 3 || selection + i >= 11) {
						if(board[selection + i] == 0) { //empty pit, take opposing team's pit
							
						}
					}
				}
				else { //AI's move
					if(selection + i == 7) { //AI moves again
					
					}
					else if(selection + i > 3 && selection + i < 11) {
						if(board[selection + i] == 0) { //empty pit, take opposing team's pit
							
						}
					}
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
