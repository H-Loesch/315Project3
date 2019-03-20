package mancala;


import java.util.Vector;

//the six pits on each side of the game board (total 12)
public class Pit {

	private Vector<Piece> contents;
	public int place; //numerical place on the board 
	public int x_location; //x-axis location in the representation of the board 
	public int y_location; //y-axis location in... well, not "physical space", but you get what I mean.
	public int player; //which player does this pit belong to? 
	//maybe some variable that notes if this pit has changed, and thus needs to be re-rendered?
	
	public int size() {
		return contents.size();
	}
	
	//render the pit and all the stones in it 
	public void renderPit() {
		
	};
	
	//add a piece...? I dunno.
	public void addPiece(Piece item) {
		contents.add(item);
	};
	
	//remove a piece from this pit! whoa! 
	public void removePiece() {
		//generate number 0-size(), remove that piece. you know, that way it doesn't remove the most recently-added one every time!
	}
	
	
}
