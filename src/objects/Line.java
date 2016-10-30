package objects;

import java.awt.Color;

import utility.V3;

public class Line {
	//graphics object representing a line segment

	public Point p1;
	public Point p2; //the points defining the ends of the line
	
	public V3 center; //the line's midpoint, used to calculate arcshort and squdistance
	public boolean arcshort; //whether the line is within the visual field
	public V3 illumination = new V3(); //illumination at midpoint, lines, unlike planes and points, are individually lit
	public double time;	//time since line was drawn, used for time-based line shading
	public Color c;
	public double squdistance; //square of distance to the line
	
	public Line(Point a, Point b, Color cc) { //constructs a line between points a and b
		p1=a;
		p2=b;
		c=cc;
	}
	
	public void setcenter(){ //computes the midpoint
		center = (p1.position.add2(p2.position)).scale2(.5);		
	}
	
}
