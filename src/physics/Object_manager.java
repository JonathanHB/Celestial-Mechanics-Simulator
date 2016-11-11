package physics;

import java.awt.Color;
import java.util.Arrays;

import objects.Cable;
import objects.Entity;
import objects.Poly_library;
import utility.Main_class;
import utility.Math_methods;
import utility.Misc_methods;
import utility.V3;

public class Object_manager {

	public static void fixcenter(){ 
		//sets the position and velocity of the system's center of mass to 0, preventing drift
		
		double mass=0;
		V3 massdist=new V3(0,0,0); //later set to position correction factor
		V3 momentum=new V3(0,0,0); //later set to velocity correction factor
		
		for(Entity e : Main_class.elist){ //computes total mass, center of mass, and net momentum
			mass+=e.mass;
			massdist.add(e.position.scale2(e.mass));
			momentum.add(e.velocity.scale2(e.mass));
		}
		
		massdist.invscale(mass);
		momentum.invscale(mass);
		
		for(Entity e : Main_class.elist){ //adjusts center of mass and net momentum to 0
			e.position.sub(massdist);
			e.velocity.sub(momentum);
			e.t.shiftby(massdist.invert2());
			e.p.translateby(massdist.scale2(-1));
		}
		
		for(Cable c : Main_class.clist){ //adjusts center of mass and net momentum to 0
			c.t.shiftby(massdist.invert2());
			c.realign(massdist, momentum);
		}
		
	}
	
	public static void initializerefs(){
		for(Entity e : Main_class.elist){
			e.getreference();
		}
	}
	
	public static void add(Entity a, Entity b, boolean keepgeom){ 
		//fuses entities, adding their masses, volumes, angular and linear momentum, luminosity, and other properties
		//this method models changes in angular momentum resulting from non-head on collisions
		//based on the keepgeom argument, it either fuses the graphical objects representing the entities into a composite polyhedron,
		//or replaces their old geometries with a single new polyhedron, currently by default a cube.
		//entity fusion is not recorded, and so will not be undone when running simulations in reverse
		
		V3 orientation;
		V3 scale;
		
		V3[] polybase2;
		int[][] cornermap2; //variables used for graphics objects
		
				
		double vol1=a.radius*a.radius*a.radius; //compute volumes
		double vol2=b.radius*b.radius*b.radius; 
			
		double radius = Math.cbrt(vol1+vol2); //add volumes
		
		double newmass = a.mass+b.mass; //add masses
		double newmoi = .4*newmass*radius*radius; //newmoi is not the sum of the two other moi's, so the 3-weight method is used
								
		V3 luminosity = Misc_methods.weighted_average(a.luminosity, b.luminosity, vol1, vol2); //add luminosities
		
		V3 newpos = Misc_methods.weighted_average(a.position, b.position, a.mass, b.mass); //update position
		V3 newvel = Misc_methods.weighted_average(a.velocity, b.velocity, a.mass, b.mass); //update velocity	
		
		//compute relative positions and velocities for linear to angular momentum conversion
		V3 dposa = a.position.sub2(newpos); 
		V3 dposb = newpos.sub2(b.position); 		
		V3 dvela = a.velocity.sub2(newvel); 
		V3 dvelb = newvel.sub2(b.velocity);
		
		//calculate and add angular momentum based on angle and speed of collision
		V3 rotation = new V3(Misc_methods.weighted_average(a.rotation, b.rotation, a.moi, b.moi, newmoi));		
		rotation.add(Misc_methods.weighted_average(Math_methods.getrotation(dposa, dvela), Math_methods.getrotation(dposb, dvelb), a.moi, b.moi, newmoi));
		
		Color pc = Misc_methods.coloraverage(a.p.c, b.p.c, vol1, vol2); //combine colors
		Color tc = Misc_methods.coloraverage(a.t.c, b.t.c, vol1, vol2);
		
		//update graphics objects representing entities
		if(keepgeom){
			
			orientation = new V3(0,0,0);
			scale = new V3(1,1,1);
			polybase2 = Poly_library.polybaseadd(a.p.vertices, b.p.vertices, dposa, dposb);
			cornermap2 = Poly_library.cornermapadd(a.cornermap2, b.cornermap2, a.p.vertices.length);
			
		}else{
			
			orientation = Misc_methods.weighted_average(a.orientation, b.orientation, a.moi, b.moi, newmoi);
			scale = new V3(radius, radius, radius); 
			polybase2 = Poly_library.standard_poly_bases[0]; //poly_library doesn't change, so these shallow copies are fine
			cornermap2 = Poly_library.standard_poly_maps[0];
			
		}
	
		Entity reference;
		
		if(a.mass>=b.mass){
			reference = a.t.refent;
		}else{
			reference = b.t.refent;
		}
		
		//fused entity added as a new entity
		Main_class.elist.add(
				new Entity(
				newmass, radius, luminosity, newpos, newvel, orientation, rotation, 
				pc, polybase2, cornermap2, scale, new V3(0,0,0), 
				tc, a.t.length+b.t.length, (a.t.resolution+b.t.resolution)/2.0, Main_class.elist.indexOf(reference)
						));
		
		Main_class.elist.get(Main_class.elist.size()-1).getreference();
		
		check_refents(a,b);
		
		Main_class.elist.remove(a);
		Main_class.elist.remove(b);		//both original entities are removed
		Main_class.elist.trimToSize(); 		
		
	}

	public static void splitcable(Cable c, boolean[] breaks){ 
		//Splits cable objects into pieces where strain exceeds the maximum threshold 
		//These locations are given by the breaks[] array
		//segments consisting of a single cable node (i.e. those between consecutive breaks) are removed
		
			int sectionstart = 0;
			
			if(breaks[breaks.length-1]){	

				for(int x = 0; x<breaks.length-1; x++){
					if(breaks[x]){
						if(x-sectionstart>0){
							Main_class.clist.add(new Cable(c.node_spacing, c.col, c.t.c, Arrays.copyOfRange(c.nodes, sectionstart, x+1), c.t.length, c.t.resolution, c.t.refent));			
						}
						sectionstart = x+1;

					}else if(x == breaks.length-2){
						Main_class.clist.add(new Cable(c.node_spacing, c.col, c.t.c, Arrays.copyOfRange(c.nodes, sectionstart, x+2), c.t.length, c.t.resolution, c.t.refent));			
					}

				}
				
				Main_class.clist.remove(c);

			}
		
	}
	
	public static void updatetrailfoci(){
		
		if(Main_class.fixedreferences == false){
		
			for(Entity e : Main_class.elist){
			
				e.t.refent = e.primary;
				e.maxforceproxy = 0;
			
			}
		
		}
		
	}
	
	public static void check_refents(Entity a, Entity b){ //must be run between new entity generation and old entity deletion in add() method
		
		for(Entity e : Main_class.elist){
			
			if(e.t.refent == b || e.t.refent == a ){
				e.t.refent = Main_class.elist.get(Main_class.elist.size()-1);
			}
			
			if(e.t.refent == e){
				e.t.refent = new Entity();
				e.t.refent.velocity = new V3(0,0,0);				
			}
			
			e.primary = e.t.refent;
		}
		
		for(Cable c : Main_class.clist){
			
			if(c.t.refent == b || c.t.refent == a ){
				c.t.refent = Main_class.elist.get(Main_class.elist.size()-1);

			}
			
		}
	}
	
}
