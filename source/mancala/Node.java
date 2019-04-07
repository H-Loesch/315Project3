package mancala;
import java.util.*;

public class Node {
	GameManager root;
	int player; //which player we are. 
	ArrayList<GameManager> children;
	ArrayList<Integer> moves = new ArrayList<Integer>();
	
	Node(GameManager game){
		root = game;
		children = new ArrayList<GameManager>();
		player = game.currentPlayer;
	}
	void createChildren() {
		validMoves();
		
		int numMoves = moves.size();
		for(int i = 0; i < numMoves; i++) {
			children.add(nextGame(moves.get(i)));
		}
	}
	void validMoves() {  //returns list of valid moves
		if(root.currentPlayer == 0) { //player's turn
			for(int i = 1; i < 7; i++) {
				if(root.board[i] != 0)
					moves.add(i);
			}
		}
		else { //computer's turn
			for(int i = 8; i < 14; i++) {
				if(root.board[i] != 0)
					moves.add(i);
			}	
		}
	}

	GameManager nextGame(int move) {
		GameManager child = root;
		child.move(move, player, null);
		return child;
	}
}
