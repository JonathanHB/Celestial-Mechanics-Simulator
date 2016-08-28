package graphics;

import java.awt.Color;
import java.util.ArrayList;

import objects.Cable;
import objects.Cablenode;
import objects.Entity;
import objects.Line;
import objects.Plane;
import objects.Point;
import objects.Render_obj;
import utility.Main_class;
import utility.Misc_methods;
import utility.V3;

public class Graphics_engine {

	static double order[][];

	public static ArrayList <Render_obj> order2 = new ArrayList<Render_obj>(0);

	public static V3 viewposition = new V3(0,0,0);//-1000000000.0
	public static V3 axis_orientation = new V3(0,.0000000000000000000000000000000000001,0); //x=azimuth, y=polar, z=roll, small value prevents a certain undefined glitch
	public static V3 orientation = new V3(0,0,0); //x=azimuth, y=polar, z=roll

	public static int focus = -1;

	static double windowscale = 120;
	static double visualrange = 6;

	static double arcmax = 1.1;

	static int stoplength = 0;

	public static void projector(){

		for(Entity e : Main_class.elist){

			for(Point p:e.p.vertices){
				projectpointP(p,e.position);
			}

			for(Point p:e.t.nodes){
				projectpointT(p);
			}
			//----------------------------------------------------------
			for(Plane p:e.p.faces){
				projectface(p);
			}

			for(Line l:e.t.links){
				projectedge(l);				
			}

		} 

		for(Cable c : Main_class.clist){

			for(Cablenode cn:c.nodes){
				projectpointT(cn.p);
			}

			for(Line l : c.links){
				projectedge(l);				
			}
			
			for(Point p:c.t.nodes){
				projectpointT(p);
			}
			
			for(Line l:c.t.links){
				projectedge(l);				
			}

		}

	} 

	public static void projectpointP(Point p, V3 entpos){ 
		//adds entity position to point position because polyhedron points store their locations 
		//relative to their parent entities to make rotation more computationally efficient 
		//(when rotation is 0 and the objects are moving, either storage method is equally efficient,
		//although stationary objects make this design less efficient)

		V3 v = new V3(utility.Math_methods.rotatepoint(utility.Math_methods.rotatepoint(p.position.add2(entpos).sub2(viewposition), orientation), axis_orientation));

		double hypotenuse = Math.sqrt(v.z*v.z + v.y*v.y);
		
		double arc = visualrange*Math.atan2(hypotenuse, v.x)/hypotenuse; //this leads to a glitch when hypotenuse = 0;

		p.projectx = (int) Math.round(windowscale*(v.y*arc+Math.PI)); //y
		p.projecty = (int) Math.round(windowscale*(v.z*arc+Math.PI)); //z
		
	}

	public static void projectpointT(Point p){

		V3 v = new V3(utility.Math_methods.rotatepoint(utility.Math_methods.rotatepoint(p.position.sub2(viewposition), orientation), axis_orientation));

		double hypotenuse = Math.sqrt(v.z*v.z + v.y*v.y); //this leads to a glitch when hypotenuse = 0;
		
		double arc = visualrange*Math.atan2(hypotenuse, v.x)/hypotenuse;

		p.projectx = (int) Math.round(windowscale*(v.y*arc+Math.PI)); //y
		p.projecty = (int) Math.round(windowscale*(v.z*arc+Math.PI)); //z

	}

	public static void projectface(Plane p){

		V3 v = new V3(utility.Math_methods.rotatepoint(utility.Math_methods.rotatepoint(p.center.sub2(viewposition), orientation), axis_orientation));

		p.squdistance = v.z*v.z + v.y*v.y + v.x*v.x;

		p.arcshort = Math.atan2(Math.sqrt(v.z*v.z + v.y*v.y), v.x)<=arcmax;

	}

	public static void projectedge(Line l){

		V3 v = new V3(utility.Math_methods.rotatepoint(utility.Math_methods.rotatepoint(l.center.sub2(viewposition), orientation), axis_orientation));

		l.squdistance = v.z*v.z + v.y*v.y + v.x*v.x;

		l.arcshort = Math.atan2(Math.sqrt(v.z*v.z + v.y*v.y), v.x)<=arcmax;

	}

	public static void setorder(){

		order2.clear();		

		for(Entity e : Main_class.elist){

			for(Plane p : e.p.faces){
				if(p.arcshort){
					order2.add(new Render_obj(p));					
				}
			}

			for(Line l : e.t.links){
				if(l.arcshort){
					order2.add(new Render_obj(l));
				}
			}
		}

		for(Cable c : Main_class.clist){
			for(Line l : c.links){
				if(l.arcshort){
					order2.add(new Render_obj(l));
				}
			}	
			
			for(Line l : c.t.links){
				if(l.arcshort){
					order2.add(new Render_obj(l));
				}
			}	
		}

		order2.trimToSize();
		Misc_methods.sort2(0, order2.size()-1, order2);


	}

	public static void lighting(){ //turn off for traject_opt?, replace numerical for loops

		for(Entity e : Main_class.elist){
			for(Plane p : e.p.faces){
				if(p.arcshort){
					p.illumination.set(0,0,0);
				}
			}

			for(Line l : e.t.links){
				if(l.arcshort){
					l.illumination.set(0,0,0);
				}
			}

		} 	

		for(Cable c : Main_class.clist){
			for(Line l : c.links){
				if(l.arcshort){
					l.illumination.set(0,0,0);
				}
			}
			
			for(Line l : c.t.links){
				if(l.arcshort){
					l.illumination.set(0,0,0);
				}
			}
		}

		for(Entity e1 : Main_class.elist){
			if(e1.luminosity.isnonzero()){
				for(Entity e2 : Main_class.elist){
					for(Plane p : e2.p.faces){
						if(p.arcshort){
							p.illumination.add(e1.luminosity.invscale2(p.center.sub2(e1.position).squmagnitude()));
						}
					}

					for(Line l : e2.t.links){
						if(l.arcshort){
							l.illumination.add(e1.luminosity.invscale2(l.center.sub2(e1.position).squmagnitude()));
						}
					}
				} 

				for(Cable c : Main_class.clist){
					for(Line l : c.links){
						if(l.arcshort){
							l.illumination.add(e1.luminosity.invscale2(l.center.sub2(e1.position).squmagnitude()));
						}
					}
					
					for(Line l : c.t.links){
						if(l.arcshort){
							l.illumination.add(e1.luminosity.invscale2(l.center.sub2(e1.position).squmagnitude()));
						}
					}
				}

			}

		} 

	}

}
