package objects;

import java.awt.Color;
import java.util.Arrays;

import user_interface.FileIO;
import objects.Poly_library;
import physics.Motion;
import utility.Main_class;
import utility.V3;
import utility.Math_methods;
import utility.Misc_methods;

public class Entity {
	
//	public boolean used;
	public double mass; //kilograms
	public double radius; //meters
	public double moi; //kilogram-square meters
	
	public V3 luminosity; //watts
	public V3 position; //meters
	public V3 velocity; //meters per second
	public V3 orientation; //radians; x=azimuth, y=polar, z=roll
	public V3 rotation; //radians per second; x=azimuth, y=polar, z=roll
	
	public V3 scale2;
	public V3 shift2; //poybase, cornermap, scale and shift are only used by polyhedron and for saving files, and may be moved
	public V3[] polybase2;
	public int[][] cornermap2;
	
	public Polyhedron p;
	public Trail t;
	
	public Entity(){
	//	used = false;
	}
	
	public Entity( //full constructor, consider making one with fewer than 15 arguments
			double m, double r, V3 lum, V3 pos, V3 vel, V3 ori, V3 rot, 
			Color pcol, V3[] polybase, int[][] cornermap, V3 scale, V3 shift, 
			Color tcol, int length, double res
			){		
		
	//	used=true;		
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
		
		t=new Trail(length, res, pos, tcol);
		
	}
	
	public void move(double d){ //moves and rotates the entity based on time increment of d seconds
		
		V3 dpos=velocity.scale2(d);
		position.add(dpos);
		p.translateby(dpos);
						
		if(t.refent == -1){
			t.update(position);
		}else{
			t.shiftby(Main_class.elist.get(t.refent).velocity.scale2(d)); 
			//order of elist subtly influences trail generation but not physics here
			
			t.update(position);
		}
		
		
	}
	
	public void rotate(double d){ //rotates the entity based on time increment of d seconds
		
		orientation.add(rotation.scale2(d));
		p.rotatepoly(rotation.scale2(d)); //rotate the cube 
		//(would it make more sense to rotate it to the orientation rather than rotating it by the rotation rate??)
		p.translateby(position); //move the face centers, 
		//which are centered around the entity's position for efficient rotation 
		//and are reset to this frame by rotatepoly, to the correct locations
		
	}
	
	public void addwithgeom(Entity e){ //keeps entity geometry
		
		double vol1=radius*radius*radius;
		double vol2=e.radius*e.radius*e.radius;
		radius=Math.pow(vol1+vol2, 1.0/3.0);
		
		luminosity = Misc_methods.weightedaverage(luminosity, e.luminosity, vol1, vol2);
		
		V3 newpos = Misc_methods.weightedaverage(position, e.position, mass, e.mass);
		V3 newvel = Misc_methods.weightedaverage(velocity, e.velocity, mass, e.mass);		
		
		V3 dposa = position.sub2(newpos);
		V3 dposb = newpos.sub2(e.position);
		
		V3 dvela = velocity.sub2(newvel);
		V3 dvelb = newvel.sub2(e.velocity);

		scale2.set(1,1,1);
		shift2.set(0,0,0);
		orientation.set(0,0,0);
		
		Color pc = Misc_methods.coloraverage(p.c, e.p.c, vol1, vol2);
		
		polybase2 = Poly_library.polybaseadd(p.vertices, e.p.vertices, dposa, dposb);
		cornermap2 = Poly_library.cornermapadd(cornermap2, e.cornermap2, p.vertices.length);
		
		p = new Polyhedron(pc, shift2, scale2, orientation, polybase2, cornermap2);
		p.translateby(newpos);
		
		rotation.set(Misc_methods.weightedaverage(rotation, e.rotation, moi, e.moi));		
		rotation.add(Misc_methods.weightedaverage(Math_methods.getrotation(dposa, dvela), Math_methods.getrotation(dposb, dvelb), moi, e.moi));
				
		position.set(newpos);
		velocity.set(newvel);
		
		mass+=e.mass;
		moi = .4*mass*radius*radius; //mass and moi must be set last because they are used to weight other formulas
		
		Main_class.elist.remove(e);
		Main_class.elist.trimToSize();
		
	}
	
	public void addandfuse(Entity e){ //keeps graphics consistent with physics
		
		double vol1=radius*radius*radius;
		double vol2=e.radius*e.radius*e.radius;
		radius=Math.pow(vol1+vol2, 1.0/3.0);
		
		luminosity = Misc_methods.weightedaverage(luminosity, e.luminosity, vol1, vol2);
		
		V3 newpos = Misc_methods.weightedaverage(position, e.position, mass, e.mass);
		V3 newvel = Misc_methods.weightedaverage(velocity, e.velocity, mass, e.mass);	
		
		V3 dposa = position.sub2(newpos);
		V3 dposb = newpos.sub2(e.position);
		
		V3 dvela = velocity.sub2(newvel);
		V3 dvelb = newvel.sub2(e.velocity);
		
		scale2.set(radius,radius,radius);
		shift2.set(0,0,0);
		
		orientation = Misc_methods.weightedaverage(orientation, e.orientation, moi, e.moi);
		
		polybase2 = Poly_library.standard_poly_bases[0]; //poly_library is static, so these shallow copies are fine
		cornermap2 = Poly_library.standard_poly_maps[0];

		Color pc = Misc_methods.coloraverage(p.c, e.p.c, vol1, vol2);
		
		p = new Polyhedron(pc, shift2, scale2, orientation, polybase2, cornermap2);
		p.translateby(newpos);
		
		rotation.set(Misc_methods.weightedaverage(rotation, e.rotation, moi, e.moi));		
		rotation.add(Misc_methods.weightedaverage(Math_methods.getrotation(dposa, dvela), Math_methods.getrotation(dposb, dvelb), moi, e.moi));
		
		position.set(newpos);
		velocity.set(newvel);
		
		mass+=e.mass;
		moi = .4*mass*radius*radius; //mass and moi must be set last because they are used to weight other formulas
		
		Main_class.elist.remove(e);
		Main_class.elist.trimToSize();
		
	}
			
	
	
}
