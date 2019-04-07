package mancala;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		int size;
		int stones;
		boolean random = true;
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Size of board: ");
		size = Integer.parseInt(scanner.next());   //get size of boad
		System.out.println("Number of stones: ");
		stones = Integer.parseInt(scanner.next());   //get num stones
		
		GameManager gm = new GameManager(size, stones, random);
		//gm.run();
		
		//GameManagerSimulator gmSim = new GameManagerSimulator(gm.board);
		
		gm.print();
		Tree tree = new Tree(gm.board, gm.player);
		int bestMove;
		while(gm.playerHasStones() && gm.computerHasStones()) {
			bestMove = tree.bestNextMove();
			System.out.println("best next move: " + bestMove);
			gm.move(bestMove);
			gm.print();
			tree = new Tree(gm.board, gm.player);
		}
	}
}
