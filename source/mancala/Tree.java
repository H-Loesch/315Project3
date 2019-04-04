package mancala;
 import java.util.ArrayList;
/* public class Tree {
	Node root;
	ArrayList<Node> children = new ArrayList<Node>();
	ArrayList<Integer> moves = new ArrayList<Integer>();
	int depth = 3;
 	Tree(GameManagerSimulator game){  //constructor
		root = new Node(game);
		createChildren(root);
	}
 	void createChildren(Node currentNode) {
		if(currentNode.node.playerWon == 0){  //player wins
 		}
		else if(currentNode.node.playerWon == 1){	//AI wins
 		}
		else {
			validMoves(currentNode);			//no winner yet, find next valid moves
 			int numMoves = moves.size();
			for(int i = 0; i < numMoves; i++) {
				children.add(nextGame(moves.get(i)));
				if(depth > 0){							//limiting depth
					createChildren(children.get(i));
				}
				System.out.println("Finished a depth search");
			}
		}
	}
	void validMoves(Node currentNode) {  //returns list of valid moves
		if(currentNode.node.player == 0) { //player's turn
			for(int i = 1; i < 7; i++) {
				if(currentNode.node.board[i] != 0)
					System.out.println("added move: " + i);
					moves.add(i);
			}
		}
		else { //computer's turn
			for(int i = 8; i < 14; i++) {
				if(currentNode.node.board[i] != 0)
					System.out.println("added move: " + i);
					moves.add(i);
			}
		}
	}
 	Node nextGame(int move) {
		Node child = root;
		child.node.move(move);
		depth--;
		return child;
	}
}*/
