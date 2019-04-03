package mancala;

import java.util.Random;
import java.util.Vector;

import javax.swing.DefaultListModel;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

//I think maybe this should extend from javafx.node? So that it can pull hover effects and stuff.
//It's unclear, as I'm not really sure what 

//the two larger pits on the far ends of the game board, where captured stones are stored (what a fitting name!)
public class Store extends Ellipse{
	public int player;
	public int size;
	public int place;
	private GameManager gm;
	private Pane root;
	private DefaultListModel<String> buffer;
	private static Random key = new Random();
	private Vector<Piece> contents;
	Pane container;
	
	Store(double _x_loc, double _y_loc, double _x_size, double _y_size, int _player, GameManager _gm, Pane _root, DefaultListModel<String> _buffer) {
		super(_x_loc, _y_loc, _x_size, _y_size);
		player = _player;
		contents = new Vector<Piece>();
		gm = _gm;
		root = _root;
		buffer = _buffer;
		
        addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
        	//when mouse enters the pit, create a text box with the # of pieces in that pit above it.
        	@Override public void handle(MouseEvent event) {
        		//create new stack pane for the box, since these automatically center things
        		StackPane text_box = new StackPane();
        		text_box.setLayoutX(getCenterX() - 22.5);
        		text_box.setLayoutY(getCenterY() - 90);
        		text_box.setId("temp_box");

        		Rectangle size_label = new javafx.scene.shape.Rectangle(22.5, 17.5, 45, 35);
        		size_label.setFill(Color.RED);
        		//Text number = new Text(Integer.toString(gm.board[player * (gm.numPits + 1)]));
        		Text number = new Text(Integer.toString(player));

        		text_box.getChildren().addAll(size_label, number);
        		size_label.setId("temp_box");
        		root.getChildren().add(text_box);
        		//something something create a text box above the pit when mouse is over it
        		//set the ID to something specific so that the mouse_exited item can remove it. 
        	}
        });
        
        addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
        	//when mouse exits pit, delete the text box created upon entry
        	@Override public void handle(MouseEvent event) {
        		javafx.scene.Node size_label = root.lookup("#temp_box");
        		root.getChildren().remove(size_label);
        		//destroy the object created when the mouse entered this pit
        	}
        });
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
