package objects;

import java.awt.Color;

import graphics.Graphics_engine;
import utility.Math_methods;
import utility.V3;

public class Plane {
	//graphics object representing a polygonal plane
	
	public Point corners[]; //corners of polygon, consists of pointers to point objects in an associated set of polyhedron vertices
	public V3 center; //the plane's center, used to calculate arcshort and squdistance
	public boolean arcshort; //whether the plane is within the visual field
	public V3 illumination = new V3(); //illumination at center, make a get_real_color method here to replace the one in frame_functions
	public Color c;
	public double squdistance; //square of distance to the line
		
	public Plane(Point[] p, Color cc) {	//constructs a plane with the specified corners and color
		corners=p;
		c=cc;				
		center = new V3();
	}
	
	public void setcenter(){ //computes the plane's center
		
		V3 center_buffer = new V3(0, 0, 0);
		for(Point p:corners){
			center_buffer.add(p.position);
		}		
		center.set(center_buffer.invscale2(corners.length));
		
	}
	
	public void project(V3 entpos, V3 entori, V3 campos, V3 camori){ //camori isn't currently used
		//computes angle to a face from its 3d position, used for culling of objects outside of visual field
		
		V3 v = new V3(Math_methods.rotatepoint(Math_methods.rotatepoint(center, entori).add2(entpos).sub2(campos), camori));

		squdistance = v.squmagnitude();

	}
	
	public void project3(V3 entpos, V3 campos, V3 camori){
		//computes 2d visual field position of a point from its 3d position
		
		//adds entity position to point position because polyhedron points store their locations 
		//relative to their parent entities to make rotation more computationally efficient 
		//(when rotation is 0 and the objects are moving, either storage method is equally efficient,
		//although stationary objects make this design less efficient)

		V3 v = new V3(Math_methods.rotatepoint(center.add2(entpos).sub2(campos), camori));

		squdistance = v.squmagnitude();
	//	if(v.x/(v.y*v.y + v.z*v.z)>0) {
	//		System.out.println(v.x/Math.sqrt(v.y*v.y + v.z*v.z));
	//	}
		arcshort = v.x/Math.sqrt(v.y*v.y + v.z*v.z) >= 0.1;
		
	}	

	
}
