package mancala;

import java.util.Random;
import java.util.Vector;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

//I think maybe this should extend from javafx.node? So that it can pull hover effects and stuff.
//It's unclear, as I'm not really sure what 

//the two larger pits on the far ends of the game board, where captured stones are stored (what a fitting name!)
public class Store extends Ellipse{
	public int player;
	public int size;
	private GameManager gm;
	private Pane root;
	private static Random key = new Random();
	private Vector<Piece> contents;
	
	Store(double _x_loc, double _y_loc, double _x_size, double _y_size, int _player) {
		super(_x_loc, _y_loc, _x_size, _y_size);
		player = _player;
		contents = new Vector<Piece>();
	}
	
	public Piece addPiece() {
		//location will be some place such that this will be actually inside the pit. 
		double x_loc = this.getCenterX() + key.nextInt((int) this.getRadiusX() + 1 - 15) - (this.getRadiusX() / 2);
		double y_loc = this.getCenterY() + key.nextInt((int) this.getRadiusY() + 1) - (this.getRadiusY() / 2);
		
		Piece new_piece = new Piece(x_loc, y_loc);
		contents.add(new_piece);
		size += 1;
		
		return new_piece;
	}
	
	public Piece addPiece(Piece _in) {
		contents.add(_in);
		size += 1;
		return _in;
	}
	
	//remove a piece from this pit! whoa! 
	public Piece removePiece() {
		//generate number 0 - size(), remove piece at that index. you know, that way it doesn't remove the most recently-added one every time!
		Piece temp = contents.remove(size - 1);
		size -= 1;
		return temp;
	}
}
