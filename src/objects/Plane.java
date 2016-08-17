package objects;

import java.awt.Color;

import utility.V3;

public class Plane {
	
	public Point corners[];
	public V3 center;
	public boolean arcshort;
	public V3 illumination = new V3(); //illumination at center, make a get_real_color method here to replace the one in frame_functions
	public Color c;
	public double squdistance;
		
	public Plane(Point[] p, Color cc) {	

		corners=p;
		c=cc;				
		center = new V3();
	}
	
	public void setcenter(){
		
		V3 center_buffer = new V3(0, 0, 0);
		for(Point p:corners){
			center_buffer.add(p.position);
		}		
		center.set(center_buffer.invscale2(corners.length));
		
	}
	
}
