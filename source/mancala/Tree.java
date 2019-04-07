package mancala;
 import java.util.ArrayList;
 public class Tree {
	Node root;
	Node currentNode;
	//ArrayList<Node> children = new ArrayList<Node>();
	int depth = 4;
 	Tree(int[] board){  //constructor
		root = new Node(board, 0, depth);
		createChildren(root);
	}
 	void createChildren(Node node) {
 		currentNode = node;
		if(currentNode.playerWon == 0){  //player wins
 		}
		else if(currentNode.playerWon == 1){	//AI wins
 		}
		else {
			currentNode.validMoves();			//no winner yet, find next valid moves
 			int numMoves = currentNode.moves.size();
 			System.out.println("move size: " + numMoves);
 			//Node parent = currentNode;
			for(int i = 0; i < numMoves; i++) {
				currentNode.children.add(nextGame(currentNode.moves.get(i)));
				System.out.println("i: " + i + " num moves: " + numMoves);
				System.out.println("depth = " + currentNode.depth);
				if(currentNode.depth > 0){							//limiting depth
					System.out.println("i: " + i + " num moves: " + numMoves);
					createChildren(currentNode.children.get(i));
				}
				//sum weights here?
				System.out.println("Finished a depth search");
				System.out.println("move size: " + numMoves);
				System.out.println("currentNode child size: " + currentNode.children.size());
				//System.out.println("player 1 score: " + p1score);
				//System.out.println("player 2 score: " + p2score);
			}
			//System.out.println("move size: " + numMoves);
			//System.out.println("currentNode child size: " + currentNode.children.size());
		//	root.sumEvalsP1();
		//	root.sumEvalsP2();
		}
	}
 	
 	Node nextGame(int move) {
		Node child = new Node(currentNode.board, currentNode.player, currentNode.depth - 1);
		child.move(move);
		return child;
	}
 	
 	int bestNextMove() {
 		int bestMove = 0;
 		int bestScore;
 		
 		Node node = root;
 		
 		System.out.println("root node child size: " + node.children.size());
 		
 		if(node.player == 0) { //next move is player 1s
 			bestScore = node.children.get(0).p1Score;
	 		for(int i = 0; i < node.children.size(); i++) {
	 			System.out.println("p1 move " + node.moves.get(i) + " score is " + node.children.get(i).p1Score);
	 			if(node.children.get(i).p1Score > bestScore) {
	 				bestMove = i;
	 				bestScore = node.children.get(i).p1Score;
	 			}
	 				
	 		}
	 		
 		}
 		if(node.player == 1) { //next move is player 2s
 			bestScore = node.children.get(0).p2Score;
	 		for(int i = 0; i < node.children.size(); i++) {
	 			System.out.println("p2 move " + node.moves.get(i) + " score is " + node.children.get(i).p1Score);
	 			if(node.children.get(i).p1Score > bestScore) {
	 				bestMove = i;
	 				bestScore = node.children.get(i).p2Score;
	 			}
	 				
	 		}
	 		
 		}
 		return node.moves.get(bestMove);
 		
 	}
}