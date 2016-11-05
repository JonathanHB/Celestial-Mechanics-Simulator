package objects;

import java.awt.Color;

import utility.Main_class;
import utility.V3;

public class Entity {
	
	public double mass; //kilograms
	public double radius; //meters
	public double moi; //kilogram-square meters
	
	public V3 luminosity; //watts
	public V3 position; //meters
	public V3 velocity; //meters per second
	public V3 orientation; //radians; x=azimuth, y=polar, z=roll
	public V3 rotation; //radians per second; x=azimuth, y=polar, z=roll
	
	public V3 scale2;
	public V3 shift2; 
	//poybase2, cornermap2, scale2, and shift2 are used to
	//generate and save the polyhedron, and process collisions
	public V3[] polybase2;
	public int[][] cornermap2; 
	
	public Polyhedron p;
	public Trail t;
	
	public Entity(){}
	
	public Entity( //full constructor with 16 arguments
			double m, double r, V3 lum, V3 pos, V3 vel, V3 ori, V3 rot, 
			Color pcol, V3[] polybase, int[][] cornermap, V3 scale, V3 shift, 
			Color tcol, int length, double res, int refent
			){		
		
		mass=m;
		radius=r;
		
		moi = .4*mass*radius*radius;
		
		luminosity=new V3(lum);
		position=new V3(pos);
		velocity=new V3(vel);
		orientation=new V3(ori);
		rotation=new V3(rot);
		
		polybase2 = polybase;
		cornermap2 = cornermap; //buffers for saving
		scale2 = scale;
		shift2 = shift;
		
		p=new Polyhedron(pcol, shift, scale, ori, polybase, cornermap);
		p.translateby(position);
		
		t=new Trail(length, res, pos, tcol, refent);
		
	}
	
	public void move(double d){ //translates and rotates entity
		
		translate(d);
		rotate(d);
	
	}	
	
	public void translate(double d){ //moves entity for a time increment of d seconds
		
		V3 dpos=velocity.scale2(d);
		position.add(dpos);
		p.translateby(dpos);
						
		if(t.refent == -1){
			t.update(position);
		}else{
			t.shiftby(Main_class.elist.get(t.refent).velocity.scale2(d)); 
			//order of elist subtly influences trail generation but not physics here; 
			//put this code in a separate for loop to fix the above problem
			
			t.update(position);
		}
		
		
	}
	
	public void rotate(double d){ //rotates the entity for a time increment of d seconds
		
		orientation.add(rotation.scale2(d)); //update cube's orientation
		p.rotatepoly(rotation.scale2(d)); //rotate the cube 
		//TODO would it be faster to rotate the polyhedron to the orientation rather than rotating it by the rotation rate??
		p.translateby(position); 
		//moves the polyhedron representing the object
		
	}
				
	
}
