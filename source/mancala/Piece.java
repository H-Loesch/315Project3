package mancala;

import java.util.Random;

import javafx.scene.paint.Color;

//extend from node, pass all mouse events to the pit containing this piece
//the little stone pieces that go in the pits
public class Piece extends javafx.scene.shape.Circle {
	static Random key = new Random();
	
	Pit container;
	
	//create a piece with random color 
	//TODO make version that specifies color
	//TODO add more potential colors
	Piece(double _x_loc, double _y_loc) {
		super(_x_loc, _y_loc, 20);
		this.setStroke(Color.BLACK);
		this.setMouseTransparent(true);
		
		int color_key = key.nextInt(3);
		switch(color_key) {
		case (0):
			this.setFill(Color.HOTPINK);
			break;
		case(1):
			this.setFill(Color.WHITE);
			break;
		case(2):
			this.setFill(Color.DEEPSKYBLUE);
			break;
		}	
	}
}
