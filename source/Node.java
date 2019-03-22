package mancala;

import java.util.ArrayList;


public class Node {
	GameManagerSimulator node;
	ArrayList<Node> children;
	ArrayList<Integer> moves = new ArrayList<Integer>();

	Node(GameManagerSimulator game){
		node = game;
		children = new ArrayList<Node>();
	}
}