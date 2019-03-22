package mancala;
 import java.util.Vector;
import java.util.ArrayList;
import java.util.Scanner;
 public class GameManagerSimulator {
	public int[] board = new int[14];
	public int player = 0;
	public int playerWon = -1;
	ArrayList<Integer> moves = new ArrayList<Integer>();
	Scanner scanner = new Scanner(System.in);
 	GameManagerSimulator(int[] board) { //copies board over
		this.board = board;
	}
 	void move(int selection) {
		print();
		System.out.println("Player " + player + " picked " + selection);
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
			if(move != 0) {
				if(move < 7) {  //on your side
					if(board[move] == 1) { //empty pit its equal to one because you placed a stone in an empty pit)
						int opposite = 14 - move;
						System.out.println("Player 0 won pit: " + opposite);
						marblesWon = board[14 - move] + 1; //opposing pit plus capturing marble
						board[14-move] = 0;				//set gathered pit's marble to 0
						board[move] = 0;
						board[0] += marblesWon;   		//adds marbles won to player's kala
					}
				}
				player = 1;		//other player's turn
			}
			else if(move == 0){ //landed in kala, go again
				System.out.println("Go again!");
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
			if(move > 7) {  //on your side
				if(board[move] == 1) { //empty pit (its equal to one because you placed a stone in an empty pit)
					int opposite = 14 - move;
					System.out.println("Player 1 won pit: " + opposite);
					marblesWon = board[opposite] + 1; //opposing pit plus capturing marble
					board[opposite] = 0;				//set gathered pit's marble to 0
					board[move] = 0;
					board[7] += marblesWon;   		//adds marbles won to player's kala
					}
				player = 0; //other player's turn
			}
			else if(move == 7) { //landed in kala, go again
				System.out.println("Go again!");
			}
		}
 		if(!playerHasStones() || !computerHasStones()) {
			winner();								//game over, calculate winner
		}
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
 	void winner() { //0 player 1 AI
		System.out.println("Player 1's score: " + board[0]);
		System.out.println("AI's score: " + board[7]);
		if(board[0] > board[7])		//player wins
			playerWon = 0;
		else if(board[7] > board[6])	//AI wins
			playerWon = 1;
		else							//Tie
			playerWon = 2;
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