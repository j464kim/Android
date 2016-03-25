package ca.uwaterloo.lab4_201_11;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

//Based off of the A* search algorithm
//http://www.policyalmanac.org/games/aStarTutorial.htm
//Converts the map to an ArrayList of nodes
//Does not draw nodes where it thinks there is a wall
//Assumptions: 
//all double lines indicate switching between walkable/unwalkable
//all triple lines indicate still unwalkable
//all single lines are part of double or triple lines
//all images start in unwalkable territory
public class Graph {
	Node head;
	Node user;
	Node dest;
	static float precision = 0.75f;//node density
	List<Node> yNodes;
	List<Node> xNodes;
	List<Node> nodes;
	boolean inWall;
	int wallCounter;
	
	public Graph(){
		head = new Node(0,0);
		yNodes = new ArrayList<Node>();
		xNodes = new ArrayList<Node>();
		nodes = new ArrayList<Node>();
		user = head;
		dest = head;
		inWall = false;
	}
	
	//adds the map and converts it to an ArrayList of nodes
	public void addMap(Mapper map){
		
		//adds nodes with respect to the Y direction
		yNodes = new ArrayList<Node>();
		
		for(float i = 0; i < map.getWidthMetres(); i += precision){//samples and makes nodes at a specific x interval denoted by precision
			inWall = true;//assumes that the initial territory is unwalkable
			for(float j = 0; j < map.getHeightMetres(); j += precision){//samples and makes nodes at a specific y interval denoted by precision
				wallCounter += map.calculateIntersections(new PointF(i, j), new PointF(i, j + precision)).size();//checks for intersection between nodes
				//toggles between walkable/unwalkable ONLY if it encounters a double line
				if(wallCounter == 2){
					toggle();
					wallCounter = 0;
				}else if(wallCounter >= 3){
					wallCounter = 0;
				}
				//adds a node only if the location is walkable
				if(!inWall){
					yNodes.add(new Node(i, j + precision));
				}
			}
		}
		
		//adds nodes with respect to the X direction, same logic as above
		xNodes = new ArrayList<Node>();
		for(float j = 0; j < map.getWidthMetres(); j += precision){
			inWall = true;
			for(float i = 0; i < map.getHeightMetres(); i += precision){
				wallCounter += map.calculateIntersections(new PointF(i, j), new PointF(i + precision, j)).size();
				if(wallCounter == 2){
					toggle();
					wallCounter = 0;
				}else if(wallCounter >= 3){
					wallCounter = 0;
				}
				if(!inWall){
					xNodes.add(new Node(i + precision, j));
				}
			}
		}
		
		//intersect the two directions' results, they both must agree for me to add it
		//there are cases where nodes are sampling the small white-space between double lines, this reduces that error
		nodes = new ArrayList<Node>();
		for(Node n : xNodes){
			if(yNodes.contains(n)){
				nodes.add(n);
			}
		}	
		//adds the neighbours to each node
		addNeighbours();
	}
	
	//clears values for more path planning
	public void reset(){
		for(Node n : nodes){
			n.reset();
		}
		user = head;
		dest = head;
	}
	
	//wipes all, not ready for path planning until another map is loaded
	public void hardReset(){
		nodes = new ArrayList<Node>();
		user = head;
		dest = head;
	}
	
	//helper method, adds neighbours for all nodes, only used once unfortunately
	private void addNeighbours(){
		for(Node n: nodes){
			for(Node b : nodes){
				n.addNeighbour(b, precision);
			}
		}
	}
	
	//finds the path between two points using the A* algorithm
	public List<PointF> findPath(PointF start, PointF end){
		//list initializations, start and end point initializations
		List<PointF> path = new ArrayList<PointF>();
		path.add(end);
		setUser(start);
		setDestination(end);
		
		List<Node> open = new ArrayList<Node>();
		List<Node> close = new ArrayList<Node>();
		open.add(user);
		Node q;
		//loops until there are no more open nodes or the destination is reached
		while(!open.isEmpty()){
			q = findLowestF(open); //find lowest f value node in open
			open.remove(q);//remove it from open
			//pop open the surroundings around q
			for(Node n : q.getNeighbours()){
				//if its been opened before, change values potentially
				if(!open.contains(n)){//not in open
					if(!close.contains(n)){//not in closed, change value
						n.setG(q, q.getDistance(n));
						n.setH(dest);
						n.setParent(q);
						open.add(n);
					}else if(n.getF() > q.getG() + q.getDistance(n) + n.getH(dest)){//in closed, but old value higher, change it
						n.setG(q, q.getDistance(n));
						n.setH(dest);
						n.setParent(q);
						close.remove(n);
						open.add(n);
					}
				}else if(n.getF() > q.getG() + q.getDistance(n) + n.getH(dest)){//in open, but old value higher, change it
					n.setG(q, q.getDistance(n));
					n.setH(dest);
					n.setParent(q);
				}
				//found it!Write the path
				if(n.equals(dest)){
					q = dest;
					while(q != null){
						path.add(q.toPointF());
						q = q.getParent();
					}
					path.add(start);
					return path;
				}
			}
			//add the node to the close list
			close.add(q);
		}
		//this sometimes happens when there is no good path
		return path;
	}
	
	//helper method, finds the lowest F cost in a list
	private Node findLowestF(List<Node> list){
		Node ptr = new Node(0,0);
		if(!list.isEmpty()){
			ptr = list.get(0);
			for(Node n: list){
				if(n.getF()<ptr.getF()){
					ptr = n;
				}
			}
		}
		return ptr;
	}
	
	//obvious
	public void removeNode(Node n){
		nodes.remove(n);
	}
	//obvious
	public void removeNodes(List<Node> list){
		for(Node n:list){
			nodes.remove(n);
		}
	}
	//set the end node given a PointF
	public Node setDestination(PointF p){
		if(!nodes.isEmpty()){
			float initial = nodes.get(0).getDistance(p);
			for(Node n : nodes){
				if(n.getDistance(p) < initial){
					initial = n.getDistance(p);
					dest = n;
				}
			}
		}
		return dest;
	}
	
	//set the start node given a PointF
	public Node setUser(PointF p){
		if(!nodes.isEmpty()){
			float initial = nodes.get(0).getDistance(p);
			for(Node n : nodes){
				if(n.getDistance(p) < initial){
					initial = n.getDistance(p);
					user = n;
				}
			}
		}
		return user;
	}
	
	//changes the node density
	public void setPrecision(float f){
		precision = f;
	}
	
	//switches between walkable and unwalkable, helper method
	private void toggle(){
		if(inWall == true){
			inWall = false;
		}else{
			inWall = true;
		}
	}
	
	//obvious
	public List<Node> getYNodes(){
		return yNodes;
	}
	
	//obvious
	public List<Node> getXNodes(){
		return xNodes;
	}
	
	//obvious
	public List<Node> getNodes(){
		return nodes;
	}
	
}
