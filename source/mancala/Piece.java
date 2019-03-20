package mancala;


//the little stone pieces that go in the pits
public class Piece {
	private static enum Color {	RED, WHITE, PURPLE, BLUE, YELLOW, GREEN };
	
	//every piece has a color
	Color color; 
	
	Pit container;
}
