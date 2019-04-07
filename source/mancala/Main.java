package mancala;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		int size;
		int stones;
		boolean random = false;
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Size of board: ");
		size = Integer.parseInt(scanner.next());   //get size of boad
		System.out.println("Number of stones: ");
		stones = Integer.parseInt(scanner.next());   //get num stones
		
		GameManager gm = new GameManager(size, stones, random);
		//gm.run();
		
		//GameManagerSimulator gmSim = new GameManagerSimulator(gm.board);
		Tree tree = new Tree(gm.board);
		//System.out.println("best next move: " + tree.bestNextMove());
	}
}
