package objects;

import java.awt.Color;

import physics.Motion;
import utility.Main_class;
import utility.Math_methods;
import utility.V3;

public class Entity {
	
	public double mass; //kilograms //0 mass is a flag indicating the entity exists as a dummy primary for something in barycentric coordinates
	public double GMt; //G*mass*tstep
	public double radius; //meters
	public double moi; //kilogram-square meters
	
	public V3 luminosity; //watts
	public V3 position; //meters
	public V3 velocity; //meters per second
	public V3 lastvelocity; //meters per second
	public V3 acceleration; //meters per second^2
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
	
	public double maxforceproxy; //the largest force exerted by any object on this entity
	public Entity primary; //the object exerting the largest force on this entity
	public int indexbuffer; //the index of the object around which this object's trails are drawn, used to find primary in the entity array once it has been loaded
	public boolean iskeplerian;
	
	Point velpt = new Point();
	Point rotpt = new Point();
	
	public Line velvector = new Line();
	public Line rvector = new Line();
	
	//public V3 accbuff;
	
	public Entity(){} //empty constructor
	
	public Entity(boolean b){ //boolean for overloading
		position = new V3(0,0,0);
		velocity = new V3(0,0,0);
		mass = 0;
	}
	
	public Entity( //full constructor with 17 arguments
			double m, double r, V3 lum, V3 pos, V3 vel, V3 ori, V3 rot, 
			Color pcol, V3[] polybase, int[][] cornermap, V3 scale, V3 shift, 
			Color tcol, int length, double res, int refent, boolean kepler
			){		
		
		mass=m;
		GMt = .00000000006674*m*Motion.increment;
		radius=r;
		//accbuff = new V3();
		
		moi = .4*mass*radius*radius;
		
		maxforceproxy = 0;
		
		luminosity=new V3(lum);
		position=new V3(pos);
		velocity=new V3(vel);
		lastvelocity=new V3(vel);
		acceleration=new V3(0,0,1);
		orientation=new V3(ori);
		rotation=new V3(rot);
		
		polybase2 = polybase;
		cornermap2 = cornermap; //buffers for saving
		scale2 = scale;
		shift2 = shift;
		
		p=new Polyhedron(pcol, shift, scale, ori, polybase, cornermap);
		//p.translateby(position);
		
		t=new Trail(length, res, new V3(0,0,0), tcol);
		indexbuffer = refent;
		
		velpt = new Point(new V3(velocity), false);
		rotpt = new Point(new V3(rotation), false);
		
		velvector = new Line(new Point(new V3(0)), velpt, p.c);
		rvector = new Line(new Point(new V3(0)), rotpt, p.c);
		
		velvector.illumination = new V3(1,1,1);
		rvector.illumination = new V3(1,1,1);
		
		iskeplerian = kepler;
		
	}
	
	public void getreference(){
		if(indexbuffer == -1){
			primary = new Entity(true);
		}else {
			primary = Main_class.elist.get(indexbuffer);			

		}	
	}
	
	public void accelerate(V3 dpos, double a, Entity potential_ref){ //probably an unnecessary method
		
		velocity.add(dpos.scale2(a));
		
		if(Main_class.fixedreferences == false && Math.abs(a)>maxforceproxy && potential_ref.mass>mass){
			maxforceproxy = Math.abs(a);
			primary = potential_ref;
			
			
		}
		
	}
	
	public void move(double d){ //translates and rotates entity; get rid of this since it's an unnecessary method call
		
		translate(d);
		
		if(Main_class.fixedreferences == false){
			maxforceproxy = 0;
		}
	//	rotate(d);
	
	}	
	
	public void translate(double d){ //moves entity for a time increment of d seconds //maybe another needless method call
		
		//V3 dpos=velocity.scale2(d);
		position.add(velocity.scale2(d));
				
		t.shiftby(primary.velocity.scale2(d)); 

		//order of elist subtly influences trail generation but not physics here; 
		//put this code in a separate for loop to fix the above problem
			
		t.update(position);
						
	}
	
	public void rotate(double d){ //rotates the entity for a time increment of d seconds //get rid of this its an extra method call
		
		orientation.add(rotation.scale2(d)); //update polyhedron's orientation, doesn't work
		//Math_methods.rotatepoint(orientation, rotation);
		//t.update(Math_methods.rotatepoint(new V3(10,10,10), orientation).add2(position));
		
	}
	
	public V3[] getabsolutevectors(){ //digs through the tree of entities to get barycentric coordinates and velocities
		
		V3[] rootvals = new V3[2];
		
		if(indexbuffer == -1) { //if entity is already in barycentric coordinates
					
			rootvals[0] = new V3(position);
			rootvals[1] = new V3(velocity);						
			
		}else{ 
			//recursively goes down the primary tree, adding up coordinates in different reference frames
			//and eventually the barycentric frame to get the correct barycentric values
			
			rootvals = primary.getabsolutevectors();
			rootvals[0].add(position);
			rootvals[1].add(velocity);
			
		}
		
		return rootvals;
		
	}	
	
	public V3 getangles() {
		//double a = Math.atan2(velocity.y, velocity.z);
		//V3 binorm = [cross product]
		//double b = Math.atan2(velocity.x, Math.sqrt(velocity.z*velocity.z+velocity.y*velocity.y));		
		//V3 accrot = new V3(Math_methods.rotatey(Math_methods.rotatex(acceleration, a), b));
		//double c = Math.atan2(accrot.x, accrot.y);
		//System.out.println(c);
		
		return new V3(0,0,0); //want to rotate around same axes in reverse order
	}
	
	
}
