package objects;

import java.awt.Color;

import utility.Main_class;
import utility.V3;

public class Trail {
	
	public int refent = -1; //index of entity relative to which trails are drawn, -1 is system barycenter
	public int length; //number of points, must be at least 3
	public double resolution;
	public Point[] nodes;
	public Line[] links;
	int nodepos=1;
	int linkpos=1;
	public Color c;
		
	public Trail(int l, double r, V3 v, Color col){
						
		c=col;
		length = l;
		resolution=r;
		nodes = new Point[length];
		links = new Line[length-1];
			
		for(int i=0; i<length-1; i++){
			nodes[i] = new Point(v);
			links[i] = new Line(nodes[0], nodes[0], col);
			links[i].center=new V3(v);
		}
		nodes[length-1] = new Point(v);
		
	}
	
	public void fixfrominit(V3 v){
		for(int i=0; i<length-1; i++){
			nodes[i].position.sub(v);
			links[i].center.sub(v);
		}	
		nodes[length-1].position.sub(v);
	}
	
	public void shiftby(V3 v){
		for(int i=0; i<length-1; i++){
			nodes[i].position.add(v);
			links[i].center.add(v);
		}	
		nodes[length-1].position.add(v);
	}

	public void update(V3 v){
		if(nodepos != 0){
			if(v.sub2(nodes[nodepos-1].position).magnitude()>=resolution){
				
				nodes[nodepos].position.set(v);								
				links[linkpos].p1=nodes[nodepos];
				links[linkpos].p2=nodes[nodepos-1];
				links[linkpos].setcenter(); //nodes[nodepos],nodes[nodepos-1]
				links[linkpos].time = Main_class.runtime;
				
				if(nodepos<length-1){
					nodepos++;
				}else{
					nodepos=0;
				}
				
				if(linkpos<length-2){
					linkpos++;
				}else{
					linkpos=0;
				}
			}			
		}else{
			if(v.sub2(nodes[length-1].position).magnitude()>=resolution){
				
				nodes[nodepos].position.set(v);		
				links[linkpos].p1=nodes[nodepos];
				links[linkpos].p2=nodes[length-1];
				links[linkpos].setcenter(); //nodes[nodepos],nodes[length-1]
				links[linkpos].time = Main_class.runtime;
		
				nodepos++;
					
				if(linkpos<length-2){
					linkpos++;
				}else{
					linkpos=0;
				}	
			}	
		}		
	}
		
}
