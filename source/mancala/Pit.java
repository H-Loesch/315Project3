package mancala;

import java.util.Random;
//extend from Node, pass 
import java.util.Vector;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

//the six pits on each side of the game board (total 12)
public class Pit extends javafx.scene.shape.Circle{
//is an extension of a circle, such that eventhandlers for this can use qualities of the pit itself
	
	private Vector<Piece> contents;
	public int size;
	public int place; //numerical place on the board 
	public int player; //which player does this pit belong to?
	static Random key = new Random();
	//maybe some variable that notes if this pit has changed, and thus needs to be re-rendered?

	//construct empty pit 
	public Pit(int _x_loc, int _y_loc, int _place, int _player) {
		super(_x_loc, _y_loc, 55); //call constructor for circle 
		player = _player;
		place = _place;
		contents = new Vector<Piece>();
	}
	
	public Pit(int _x_loc, int _y_loc, int _place, int _player, Vector<Piece> _pieces) {
		super(_x_loc, _y_loc, 55); //call constructor for circle 
		player = _player;
		place = _place;
		contents = _pieces;
	}
	
	//render the pit and all the stones in it 
	public void renderPit() {
		//do something, idk.
	};
	
	//Add a piece with random color to this pit; return that piece 
	public Piece addPiece() {
		double x_loc = this.getCenterX() + key.nextInt(61) - 30;
		double y_loc = this.getCenterY() + key.nextInt(61) - 30;
		//create piece
		Piece new_piece = new Piece(x_loc, y_loc, this);
		new_piece.setStroke(Color.BLACK);
		contents.add(new_piece);
		size += 1;
		
		//set location of piece such that it is within this pit on the GUI.
		
		return new_piece;
	};
	
	//remove a piece from this pit! whoa! 
	public Piece removePiece() {
		//generate number 0 - size(), remove piece at that index. you know, that way it doesn't remove the most recently-added one every time!
		Piece temp = contents.remove(size - 1);
		size -= 1;
		return temp;
	}
	
}
