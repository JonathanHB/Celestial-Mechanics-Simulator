package physics;

import java.awt.Color;
import java.util.Arrays;

import objects.Cable;
import objects.Entity;
import objects.Plane;
import objects.Poly_library;
import objects.Polyhedron;
import utility.Main_class;
import utility.Math_methods;
import utility.Misc_methods;
import utility.V3;

public class Object_manager {

	public static void fixcenter(){
		
		double mass=0;
		V3 massdist=new V3(0,0,0); //later set to position correction factor
		V3 momentum=new V3(0,0,0); //later set to velocity correction factor
		
		for(Entity e : Main_class.elist){
			mass+=e.mass;
			massdist.add(e.position.scale2(e.mass));
			momentum.add(e.velocity.scale2(e.mass));
		}
		
		massdist.invscale(mass);
		momentum.invscale(mass);
		
		for(Entity e : Main_class.elist){	
			e.position.sub(massdist);
			e.velocity.sub(momentum);
			e.t.fixfrominit(massdist);
			e.p.translateby(massdist.scale2(-1));
		}
		
		for(Cable c : Main_class.clist){	
			c.t.fixfrominit(massdist);
			c.realign(massdist, momentum);
		}
		
	}
	
	public static void add(Entity a, Entity b, boolean keepgeom){
		
		V3 orientation;
		V3 scale;
		
		V3[] polybase2;
		int[][] cornermap2;
		
				
		double vol1=a.radius*a.radius*a.radius;
		double vol2=b.radius*b.radius*b.radius;
			
		double radius = Math.cbrt(vol1+vol2);
		
		double newmass = a.mass+b.mass;
		double newmoi = .4*newmass*radius*radius; //newmoi is not the sum of the two other moi's, so the 3-weight method is used
								
		V3 luminosity = Misc_methods.weighted_average(a.luminosity, b.luminosity, vol1, vol2);
		
		V3 newpos = Misc_methods.weighted_average(a.position, b.position, a.mass, b.mass);
		V3 newvel = Misc_methods.weighted_average(a.velocity, b.velocity, a.mass, b.mass);		
		
		V3 dposa = a.position.sub2(newpos);
		V3 dposb = newpos.sub2(b.position);
		
		V3 dvela = a.velocity.sub2(newvel);
		V3 dvelb = newvel.sub2(b.velocity);
		
		Color pc = Misc_methods.coloraverage(a.p.c, b.p.c, vol1, vol2);
		Color tc = Misc_methods.coloraverage(a.t.c, b.t.c, vol1, vol2);
		
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
	
		V3 rotation = new V3(Misc_methods.weighted_average(a.rotation, b.rotation, a.moi, b.moi, newmoi));		
		rotation.add(Misc_methods.weighted_average(Math_methods.getrotation(dposa, dvela), Math_methods.getrotation(dposb, dvelb), a.moi, b.moi, newmoi));
		
		Main_class.elist.add(
				new Entity(
				newmass, radius, luminosity, newpos, newvel, orientation, rotation, 
				pc, polybase2, cornermap2, scale, new V3(0,0,0), 
				tc, a.t.length+b.t.length, (a.t.resolution+b.t.resolution)/2.0
						));
		
		Main_class.elist.remove(a);
		Main_class.elist.remove(b);
		Main_class.elist.trimToSize();
		
	}

	public static void splitcable(Cable c, boolean[] breaks){
		
			int sectionstart = 0;
			
			if(breaks[breaks.length-1]){	

				for(int x = 0; x<breaks.length-1; x++){
					if(breaks[x]){
						if(x-sectionstart>0){
							Main_class.clist.add(new Cable(c.axialresponse, c.node_spacing, c.col, c.t.c, Arrays.copyOfRange(c.nodes, sectionstart, x+1), c.t.length, c.t.resolution));			
						}
						sectionstart = x+1;

					}else if(x == breaks.length-2){
						Main_class.clist.add(new Cable(c.axialresponse, c.node_spacing, c.col, c.t.c, Arrays.copyOfRange(c.nodes, sectionstart, x+2), c.t.length, c.t.resolution));			
					}

				}
				
				Main_class.clist.remove(c);

			}
		
	}
	
}
