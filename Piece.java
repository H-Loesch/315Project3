package mancala;

import java.util.Random;

//extend from node, pass all mouse events to the pit containing this piece
//the little stone pieces that go in the pits
public class Piece {
	static Random key = new Random();
	private static enum pieceColor {	RED, WHITE, PURPLE, BLUE, YELLOW, GREEN };
	
	//every piece has a color
	pieceColor color; 
	
	Pit container;
	
	Piece(Pit _container) {
		container = _container;
		color = pieceColor.values()[key.nextInt(6)]; //this SHOULD assign a random color to the piece. 
	}
}