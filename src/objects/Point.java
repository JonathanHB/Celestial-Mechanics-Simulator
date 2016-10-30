package objects;

import utility.V3;

public class Point {
	//a graphical point in 3d space
	
	public V3 position; 
	
	public int projectx; //the point's 2d position in the viusal field
	public int projecty;
	
	public Point(V3 a) {
		position = new V3(a);
	}

}
