package mancala;
import java.util.*;
 public class Node {
	GameManagerSimulator node;
	ArrayList<Node> children;
	ArrayList<Integer> moves = new ArrayList<Integer>();
	int wieght;
 	Node(GameManagerSimulator game){
		node = new GameManagerSimulator(game.board);
		children = new ArrayList<Node>();
	}
 	
 	void validMoves() {  //returns list of valid moves
		if(node.player == 0) { //player's turn
			for(int i = 1; i < 7; i++) {
				if(node.board[i] != 0)
					System.out.println("Player 0 added move: " + i);
					moves.add(i);
			}
		}
		else { //computer's turn
			for(int i = 8; i < 14; i++) {
				if(node.board[i] != 0)
					System.out.println("Player 1 added move: " + i);
					moves.add(i);
			}
		}
	}
}