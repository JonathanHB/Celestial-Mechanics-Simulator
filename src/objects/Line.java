package objects;

import java.awt.Color;

import utility.Math_methods;
import utility.V3;

public class Line {
	//graphics object representing a line segment

	public Point p1;
	public Point p2; //the points defining the ends of the line
	
	public V3 center; //the line's midpoint, used to calculate arcshort and squdistance
	//public boolean arcshort; //whether the line is within the visual field
	public V3 illumination = new V3(); //illumination at midpoint, lines, unlike planes and points, are individually lit
	public double time;	//time since line was drawn, used for time-based line shading
	public Color c;
	public double squdistance; //square of distance to the line
	
	public Line(){}
	
	public Line(Point a, Point b, Color cc) { //constructs a line between points a and b
		p1=a;
		p2=b;
		c=cc;
	}
	
	public void setcenter(){ //computes the midpoint
		center = (p1.position.add2(p2.position)).scale2(.5);		
	}
	
	public void project(V3 campos, V3 camori){ 
		//computes angle to a line from its 3d position, used for culling of objects outside of visual field
		
		V3 v = new V3(Math_methods.rotatepoint(center.sub2(campos), camori));

		squdistance = v.squmagnitude();

	}
	
	//-------------------------------------------------
	
	public void project2(V3 campos, V3 camori, V3 axisori){ //project with a second rotation
		//computes angle to a line from its 3d position, used for culling of objects outside of visual field
		
		V3 v = new V3(Math_methods.rotatepoint(Math_methods.reverserotatepoint(center.sub2(campos), axisori), camori));

		squdistance = v.squmagnitude();

	}
	
}
