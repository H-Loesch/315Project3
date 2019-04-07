package mancala;
import java.util.*;
 public class Node {
	//GameManagerSimulator node;
	int[] board;
	int player;
	int p1Score;
	int p2Score;
	int playerWon = -1;
	int depth;
	ArrayList<Node> children;
	ArrayList<Integer> moves;
	int wieght;
	int size;
 	Node(int[] board, int player, int depth){
 		this.board = new int[board.length];
 		System.arraycopy(board, 0, this.board, 0, board.length);
		children = new ArrayList<Node>();
		this.player = player;
		this.depth = depth;
		moves = new ArrayList<Integer>();
		size = board.length;
	}
 	
 	void validMoves() {  //returns list of valid moves
		if(player == 0) { //player's turn
			for(int i = 1; i < size/2; i++) {
				if(board[i] != 0)
				//	System.out.println("Player 0 added move: " + i);
					moves.add(i);
			}
		}
		else { //computer's turn
			for(int i = size/2+1; i < size; i++) {
				if(board[i] != 0)
				//	System.out.println("Player 1 added move: " + i);
					moves.add(i);
			}
		}
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
				if(move == size/2) {
					move += 1;			//skip AI's kala
				}
				if(move > size - 1)  				//start over
					move = 0;
				board[move] = board[move] + 1;
				grabbed = grabbed - 1;
			}
			if(move != 0) {
				if(move < size/2) {  //on your side
					if(board[move] == 1) { //empty pit its equal to one because you placed a stone in an empty pit)
						int opposite = size - move;
						System.out.println("Player 0 won pit: " + opposite);
						marblesWon = board[size - move] + 1; //opposing pit plus capturing marble
						board[size-move] = 0;				//set gathered pit's marble to 0
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
				if(move > size -1)  				//only move on your side
					move = 1;				//skip player's kala
				board[move] = board[move] + 1;
				grabbed = grabbed - 1;
			}
			if(move > size/2) {  //on your side
				if(board[move] == 1) { //empty pit (its equal to one because you placed a stone in an empty pit)
					int opposite = size - move;
					System.out.println("Player 1 won pit: " + opposite);
					marblesWon = board[opposite] + 1; //opposing pit plus capturing marble
					board[opposite] = 0;				//set gathered pit's marble to 0
					board[move] = 0;
					board[size/2] += marblesWon;   		//adds marbles won to player's kala
					}
				player = 0; //other player's turn
			}
			else if(move == size/2) { //landed in kala, go again
				System.out.println("Go again!");
			}
		}
 		if(!playerHasStones() || !computerHasStones()) {
			winner();								//game over, calculate winner
		}
 		System.out.println("move complete");
 		print();
 	//	evalMove();
	}
 	
 	void evalMove() {
 		int defend = 2;
 		int capture = 2;
 		
 		//player 1's board score
 		p1Score = board[0];
 		for(int i = 1; i < size/2; i++) {
 			if(board[i] == 0) { //player 1 able to capture by landing here
 				p1Score += capture;
 			}
 		}
 		for(int i = size/2+1; i < size; i++) {
 			if(board[i] > 0) { //player 2 unable to capture by landing here
 				p1Score += defend;
 			}
 		}
 		
 		//player 2's board score
 		p2Score = board[size/2];
 		for(int i = 1; i < size/2; i++) {
 			if(board[i] > 0) { //player 1 unable to capture by landing here
 				p2Score += defend;
 			}
 		}
 		for(int i = size/2+1; i < size; i++) {
 			if(board[i] > 0) { //player 2 able to capture by landing here
 				p2Score += capture;
 			}
 		}
 		
 		if(playerWon == 1) { //player 1 won
 			p1Score = 500;
 			p2Score = -500;
 		}
 		else if(playerWon == 2) { //player 2 won
 			p1Score = -500;
 			p2Score = 500;
 		}
 	}
 	
 	int sumEvalsP1() {
 		for(int i = 0; i < children.size(); i++) {
 			p1Score += children.get(i).sumEvalsP1();
 		}
 		return p1Score;
 	}
 	
 	int sumEvalsP2() {
 		for(int i = 0; i < children.size(); i++) {
 			p2Score += children.get(i).sumEvalsP1();
 		}
 		return p2Score;
 	}
 	
 	boolean playerHasStones() {    // need to add gather all stones
		boolean playerHasStones = false;
 		for(int i = 1; i < size/2; i++) {
			if(board[i] > 0)
				playerHasStones = true;
		}
		if(!playerHasStones) {
			for(int i = size/2+1; i < size; i++) {
				board[size/2] += board[i];
				board[i] = 0;
			}
		}
		return playerHasStones;
	}
 	boolean computerHasStones() {  // need to add gather all stones
		boolean computerHasStones = false;
 		for(int i = size/2+1; i < size; i++) {
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
 	void winner() { //0 player 1 AI
		System.out.println("Player 1's score: " + board[0]);
		System.out.println("AI's score: " + board[size/2]);
		if(board[0] > board[size/2])		//player wins
			playerWon = 0;
		else if(board[size/2] > board[0])	//AI wins
			playerWon = 1;
		else							//Tie
			playerWon = 2;
	}
 	void print() {
		//print top
		System.out.print("| ");
		for(int i = size-1; i > size/2; i--) {
			System.out.print(board[i] + " | ");
		}
		//print kalas
		System.out.println("\n" + board[0] + "                       " + board[size/2]);
		//print bottom
		System.out.print("| ");
		for(int i = 1; i < size/2; i++) {
			System.out.print(board[i] + " | ");
		}
		System.out.println();
	}
}