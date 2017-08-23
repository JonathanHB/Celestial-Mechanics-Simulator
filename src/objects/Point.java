package objects;

import graphics.Graphics_engine;
import utility.Math_methods;
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
	
	public void project(V3 entpos, V3 entori, V3 campos, V3 camori){
		//computes 2d visual field position of a point from its 3d position
		
		//adds entity position to point position because polyhedron points store their locations 
		//relative to their parent entities to make rotation more computationally efficient 
		//(when rotation is 0 and the objects are moving, either storage method is equally efficient,
		//although stationary objects make this design less efficient)

		V3 v = new V3(Math_methods.rotatepoint(Math_methods.rotatepoint(position, entori).add2(entpos).sub2(campos), camori));

		int[] projection = Graphics_engine.planarprojection(v);
		projectx = projection[0];
		projecty = projection[1];
		
	}	
	
	public void projectabs(V3 campos, V3 camori){
		//computes 2d visual field position of a point from its 3d position
		
		//adds entity position to point position because polyhedron points store their locations 
		//relative to their parent entities to make rotation more computationally efficient 
		//(when rotation is 0 and the objects are moving, either storage method is equally efficient,
		//although stationary objects make this design less efficient)

		V3 v = new V3(Math_methods.rotatepoint(position.sub2(campos), camori));

		int[] projection = Graphics_engine.planarprojection(v);
		projectx = projection[0];
		projecty = projection[1];
		
	}
	
	public void project3(V3 entpos, V3 campos, V3 camori){
		//computes 2d visual field position of a point from its 3d position
		
		//adds entity position to point position because polyhedron points store their locations 
		//relative to their parent entities to make rotation more computationally efficient 
		//(when rotation is 0 and the objects are moving, either storage method is equally efficient,
		//although stationary objects make this design less efficient)

		V3 v = new V3(Math_methods.rotatepoint(position.add2(entpos).sub2(campos), camori));

		int[] projection = Graphics_engine.planarprojection(v);
		projectx = projection[0];
		projecty = projection[1];
		
	}	

}
