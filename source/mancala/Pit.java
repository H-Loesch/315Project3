package mancala;

import java.util.Random;
//extend from Node, pass
import java.util.Vector;

import javax.swing.DefaultListModel;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

//the six pits on each side of the game board (total 12)
public class Pit extends javafx.scene.shape.Circle{
//is an extension of a circle, such that eventhandlers for this can use qualities of the pit itself

	private Vector<Piece> contents;
	Pane container;
	DefaultListModel<String> buffer;
	GameManager gm;
	public int size = 0;
	public int place; //numerical place on the board
	public int player; //which player does this pit belong to?
	static Random key = new Random();
	//maybe some variable that notes if this pit has changed, and thus needs to be re-rendered?

	//construct empty pit
	public Pit(double _x_loc, double _y_loc, int _place, int _player, GameManager _gm, Pane _container, DefaultListModel<String> _buffer) {
		super(_x_loc, _y_loc, 55); //call constructor for circle
		player = _player;
		place = _place;
		container = _container;
		gm = _gm;
		buffer = _buffer;
		contents = new Vector<Piece>();

		//Event handlers for the mouse hovering over, leaving the area of, and clicking on the pits
		//These could... probably be moved to the constructor for pits, maybe?
        this.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
        	@Override public void handle(MouseEvent event) {
        		//create new stack pane for the box, since these automatically center things
        		StackPane text_box = new StackPane();
        		text_box.setLayoutX(getCenterX() - 22.5);
        		text_box.setLayoutY(getCenterY() - 90);
        		text_box.setId("temp_box");

        		Rectangle size_label = new javafx.scene.shape.Rectangle(22.5, 17.5, 45, 35);
        		size_label.setFill(Color.RED);

        		Text number = new Text(Integer.toString(gm.board[place]));

        		//Text number = new Text(Integer.toString(place));
        		number.setId("text_box_number");

        		text_box.getChildren().addAll(size_label, number);
        		size_label.setId("temp");
        		container.getChildren().add(text_box);
        	}
        });

        this.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
        	//when mouse exits pit, delete the text box created upon entry
        	@Override public void handle(MouseEvent event) {
        		javafx.scene.Node size_label = container.lookup("#temp_box");
        		container.getChildren().remove(size_label);
        		//destroy the object created when the mouse entered this pit
        	}
        });

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
        	@Override public void handle(MouseEvent event) {
        		//handle the pit being clicked on. Validate move, do a move... whatever, that's not my problem right now.
        		//check if move is legal here, or something?

        		//add that move to the buffer if the move is legal
        		if (gm.legalMove(place, gm.currentPlayer) && gm.playerInputs[gm.currentPlayer] == Source.HUMAN) {
        			//only allow pushing this to buffer if it's legal and it's actually your turn.
        			//also, let's preface it with LOCAL so that our input handler will know it's not from a remote source 
        			buffer.addElement( "LOCAL " + Integer.toString(place));
        		}
        	}
        });
	}

	//Add a piece with random color to this pit; return that piece
	public Piece addPiece() {
		double x_loc = this.getCenterX() + key.nextInt(61) - 30;
		double y_loc = this.getCenterY() + key.nextInt(61) - 30;
		//create piece
		Piece new_piece = new Piece(x_loc, y_loc);
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
