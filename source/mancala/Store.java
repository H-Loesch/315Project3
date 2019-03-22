package mancala;

import javafx.scene.shape.Ellipse;

//I think maybe this should extend from javafx.node? So that it can pull hover effects and stuff.
//It's unclear, as I'm not really sure what 

//the two larger pits on the far ends of the game board, where captured stones are stored (what a fitting name!)
public class Store extends Ellipse{
	int player;
	int size;
	
	Store(double _x_loc, double _y_loc, double _x_size, double _y_size, int _player) {
		super(_x_loc, _y_loc, _x_size, _y_size);
		player = _player;
	}
}
