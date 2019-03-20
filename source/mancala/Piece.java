package mancala;

import java.util.Random;

//the little stone pieces that go in the pits
public class Piece {
	static Random key = new Random();
	private static enum Color {	RED, WHITE, PURPLE, BLUE, YELLOW, GREEN };
	
	//every piece has a color
	Color color; 
	
	Pit container;
	
	Piece(Pit _container) {
		container = _container;
		color = Color.values()[key.nextInt(6)]; //this SHOULD assign a random color to the piece. 
	}
}
