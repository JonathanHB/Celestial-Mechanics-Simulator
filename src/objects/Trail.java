package objects;

import java.awt.Color;

import utility.Main_class;
import utility.V3;

public class Trail {
	
	public int refent = -1; //index of entity relative to which trails are drawn, -1 is system barycenter
	public int length; //number of points, equals the number of segments plus 1
	public double resolution; //actually the resolution squared to save computing power
	public double squres; //square of the distance between nodes, computationally better than the actual distance
	public Point[] nodes; //nodes along the trail
	public Line[] links; //links along the trail
	int nodepos=0; //array position of node to be modified
	int linkpos=0; //array position of link to be modified
	public Color c;
		
	public Trail(int l, double r, V3 v, Color col){ //constructs a new trail

		c=col;
		length = l+1;
		resolution=r;
		squres = r*r;
		nodes = new Point[length];
		links = new Line[length-1];

		for(int i=0; i<length-1; i++){
			nodes[i] = new Point(v);
			links[i] = new Line(nodes[0], nodes[0], col);
			links[i].center = new V3(v);
		}
		nodes[length-1] = new Point(v);

	}
	
	public void shiftby(V3 v){ //moves the entire trail, 
		//used to keep it in the frame of reference of another object and
		//realign it when the barycenter is set to 0,0,0
		for(int i=0; i<length-1; i++){
			nodes[i].position.add(v);
			links[i].center.add(v);
		}	
		nodes[length-1].position.add(v);
	}

	public void update(V3 v){ //add a new trail segment if the object has moved far enough
		if(nodepos != 0){
			if(v.sub2(nodes[nodepos-1].position).squmagnitude()>=squres){
				
				nodes[nodepos].position.set(v);								
				links[linkpos].p1=nodes[nodepos];
				links[linkpos].p2=nodes[nodepos-1];
				links[linkpos].setcenter();
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
			if(v.sub2(nodes[length-1].position).squmagnitude()>=squres){

				nodes[nodepos].position.set(v);		
				links[linkpos].p1=nodes[nodepos];
				links[linkpos].p2=nodes[length-1];
				links[linkpos].setcenter();
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
