package mancala;

public class Main {
	public static void main(String[] args) {
		/*GameManager gm = new GameManager();
		gm.run();

		GameManagerSimulator gms = new GameManagerSimulator(gm.board);
		Tree tree = new Tree(gms);*/

		int[] board = new int[14];
		for(int i = 1; i < 14; i++) { //skip player's kala at 0
			if(i == 7)
				i++;			//skip AI's kala at 7
			board[i] = 4;
		}
		GameManagerSimulator gms = new GameManagerSimulator(board);
		Tree tree = new Tree(gms);
	}
}