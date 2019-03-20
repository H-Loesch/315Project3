package mancala;

//extend from Node, pass 
import java.util.Vector;
import javafx.scene.shape.Circle;

//the six pits on each side of the game board (total 12)
public class Pit extends javafx.scene.shape.Circle{
//is an extension of a circle, such that eventhandlers for this can use qualities of the pit itself
	
	private Vector<Piece> contents;
	public int place; //numerical place on the board 
	public int x_location; //x-axis location in the representation of the board 
	public int y_location; //y-axis location in... well, not "physical space", but you get what I mean.
	public int player; //which player does this pit belong to?
	//maybe some variable that notes if this pit has changed, and thus needs to be re-rendered?

	//construct empty pit 
	public Pit(int _x_loc, int _y_loc, int _place, int _player) {
		super(_x_loc, _y_loc, 55); //call constructor for circle 
		player = _player;
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
		//generate number 0 - size(), remove piece at that index. you know, that way it doesn't remove the most recently-added one every time!
	}
	
}
