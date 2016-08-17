package objects;

import java.awt.Color;

import utility.V3;

public class Line {

	public Point p1;
	public Point p2;
	public V3 center;
	public boolean arcshort;
	public V3 illumination = new V3(); //illumination at midpoint, lines, unlike planes and points, are individually lit
	public double time;	
	public Color c;
	public double squdistance;
	
	public Line(Point a, Point b, Color cc) {
		p1=a;
		p2=b;
		c=cc;
	}
	
	public void setcenter(){
		center = (p1.position.add2(p2.position)).scale2(.5);		
	}
	
}
