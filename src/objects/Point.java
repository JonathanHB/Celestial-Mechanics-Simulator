package objects;

import utility.V3;

public class Point {
	//a graphical point in 3d space
	
	public V3 position; 
	
	public int projectx; //the point's 2d position in the viusal field
	public int projecty;

	public double squdistance;
	
	public Point(){}
	
	public Point(V3 a, boolean b) { //shallowcopy constructor
		position = a;
	}
	
	public Point(V3 a) { //deep copy constructor
		position = new V3(a);
	}

}
