package ca.uwaterloo.lab4_201_11;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

//Based off of the A* search algorithm
//http://www.policyalmanac.org/games/aStarTutorial.htm
public class Node {

	private float x;
	private float y;
	
	private List<Node> neighbours;
	private Node parent;
	
	
	private float g;
	private float h;
	
	public Node(float xPosition, float yPosition) {
	    x = xPosition;
	    y = yPosition;
	    neighbours = new ArrayList<Node>();
	    parent = null;
	}
	
	//adds a neighbour
	public void addNeighbour(Node n){
		neighbours.add(n);
	}
	
	//check to see if it is a neighbour, then adds it
	public boolean addNeighbour(Node n, float precision){
		//dont add same node to its own list!
		if(this.equals(n)){
			return false;
		}
		//same x, y off by 1
		if(Math.abs(x - n.getX())  <= 0.1){
			if(Math.abs(y - n.getY())  <= precision + 0.1){
				addNeighbour(n);
				return true;
			}
		}
		//same y, x off by 1
		if(Math.abs(y - n.getY())  <= 0.1){
			if(Math.abs(x - n.getX())  <= precision + 0.1){
				addNeighbour(n);
				return true;
			}
		}
		//both x and y off by exactly 1
		if(Math.abs(x - n.getX())  <= precision + 0.1){
			if(Math.abs(y - n.getY())  <= precision + 0.1){
				addNeighbour(n);
				return true;
			}
		}
		return false;
	}
	
	//resets the values, ready to search again
	public void reset(){
		g = 0;
		h = 0;
		parent = null;
	}
	
	//converts the node to a PointF
	public PointF toPointF(){
		return new PointF(x,y);
	}
	
	//returns the list of neighbours
	public List<Node> getNeighbours(){
		return neighbours;
	}
	
	//set the coordinates
	public void setCoordinates(float xPosition, float yPosition) {
	    x = xPosition;
	    y = yPosition;
	}
	
	//returns the x value
	public float getX() {
	    return x;
	}
	
	//returns the y value
	public float getY() {
	    return y;
	}
	
	//returns the parent node
	public Node getParent() {
	    return parent;
	}
	
	//sets the parent node
	public void setParent(Node prev) {
	    parent = prev;
	}
	
	//gets the F cost
	public float getF() {
	    return g + h;
	}
	
	//gets the G cost
	public float getG() {
	    return g;
	}
	
	//sets the G cost given a G cost
	private void setG(float g) {
	    this.g = g;
	}
	
	//sets the G cost given a parent node
	public void setG(Node parent, float g) {
	    setG(parent.getG() + g);
	}
	
	//gets the H cost
	public float getH() {
	    return h;
	}
	
	//sets the H cost given an H cost
	protected void setH(float h) {
	    this.h = h;
	}
	
	//sets the H cost given an end node
	public void setH(Node endNode){
		   setH((float)(Math.abs(this.getX() - endNode.getX())
	               + Math.abs(this.getY() - endNode.getY())));
	}
	
	//calculates the H cost given an end node
	public float getH(Node endNode){
		   return((float)(Math.abs(this.getX() - endNode.getX())
	               + Math.abs(this.getY() - endNode.getY())));
	}
	
	//calculates the distance between this node and a PointF
	public float getDistance(PointF p){
		return (float)Math.sqrt((p.x - x)*(p.x - x) + (p.y - y)*(p.y - y));
	}
	
	//calculates the distance between this node an another node
	public float getDistance(Node n){
		return (float)Math.sqrt((n.getX() - x)*(n.getX() - x) + (n.getY() - y)*(n.getY() - y));
	}
	
	//Overrides the equals method to do comparisons in ArrayList
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }
	    if (getClass() != obj.getClass()) {
	        return false;
	    }
	    final Node other = (Node) obj;
	    if (this.x != other.x) {
	        return false;
	    }
	    if (this.y != other.y) {
	        return false;
	    }
	    return true;
	}
}
