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

	//construct empty pit 
	public Pit(int _player, int _x_loc, int _y_loc, int _place) {
		player = _player;
		x_location = _x_loc; 
		y_location = _y_loc; 
		place = _place;
		contents = new Vector<Piece>();
	}
	
	public Pit(int _player, int _x_loc, int _y_loc, int _place, Vector<Piece> _pieces) {
		player = _player;
		x_location = _x_loc; 
		y_location = _y_loc; 
		place = _place;
		contents = _pieces;
	}
	
	public int size() {
		return contents.size();
	}
	
	//render the pit and all the stones in it 
	public void renderPit() {
		//do something, idk.
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
