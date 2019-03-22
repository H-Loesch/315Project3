package mancala;

import java.util.ArrayList;

public class Tree {
	Node root;
	ArrayList<Node> children;
	ArrayList<Integer> moves = new ArrayList<Integer>();

	Tree(GameManager game){  //constructor
		root = new Node(game);
	}

	void createChildren(Node currentNode) {
		validMoves(currentNode);

		int numMoves = moves.size();
		for(int i = 0; i < numMoves; i++) {
			children.add(nextGame(moves.get(i)));
			createChildren(children.get(i));
		}
	}
	void validMoves(Node currentNode) {  //returns list of valid moves
		if(currentNode.node.player == 0) { //player's turn
			for(int i = 1; i < 7; i++) {
				if(currentNode.node.board[i] != 0)
					moves.add(i);
			}
		}
		else { //computer's turn
			for(int i = 8; i < 14; i++) {
				if(currentNode.node.board[i] != 0)
					moves.add(i);
			}
		}
	}

	Node nextGame(int move) {
		Node child = root;
		child.node.move(move);
		return child;
	}
}
