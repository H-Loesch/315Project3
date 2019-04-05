package mancala;
 import java.util.ArrayList;
 public class Tree {
	Node root;
	Node currentNode;
	//ArrayList<Node> children = new ArrayList<Node>();
	int depth = 3;
 	Tree(GameManagerSimulator game){  //constructor
		root = currentNode = new Node(game);
		createChildren();
	}
 	void createChildren() {
		if(currentNode.node.playerWon == 0){  //player wins
 		}
		else if(currentNode.node.playerWon == 1){	//AI wins
 		}
		else {
			currentNode.validMoves();			//no winner yet, find next valid moves
 			int numMoves = currentNode.moves.size();
 			System.out.println("move size: " + numMoves);
			for(int i = 0; i < numMoves; i++) {
				currentNode.children.add(nextGame(currentNode.moves.get(i)));
				if(depth > 0){							//limiting depth
					createChildren();
				}
				System.out.println("Finished a depth search");
			}
		}
	}
 	/*
	void validMoves(Node currentNode) {  //returns list of valid moves
		if(currentNode.node.player == 0) { //player's turn
			for(int i = 1; i < 7; i++) {
				if(currentNode.node.board[i] != 0)
				//	System.out.println("added move: " + i);
					moves.add(i);
			}
		}
		else { //computer's turn
			for(int i = 8; i < 14; i++) {
				if(currentNode.node.board[i] != 0)
				//	System.out.println("added move: " + i);
					moves.add(i);
			}
		}
	}
	*/
 	
 	Node nextGame(int move) {
		Node child = new Node(currentNode.node);
		child.node.move(move);
		depth--;
		return child;
	}
}