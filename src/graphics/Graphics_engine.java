package graphics;

import java.awt.Color;
import java.util.ArrayList;

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
	public static V3 axis_orientation = new V3(0,0,0); //x=azimuth, y=polar, z=roll
	public static V3 orientation = new V3(0,1.5,0); //x=azimuth, y=polar, z=roll

	public static int focus = -1;

	static double windowscale = 120;
	static double visualrange = 6;

	static double arcmax = 1.1;

	static int stoplength = 0;

	public static void projector(){

		for(int x = 0; x < Main_class.elist.size(); x++){
			for(Point p:Main_class.elist.get(x).p.vertices){
				projectpointP(p,Main_class.elist.get(x).position);

			}
			for(Point p:Main_class.elist.get(x).t.nodes){
				projectpointT(p);

			}
			//----------------------------------------------------------
			for(Plane p:Main_class.elist.get(x).p.faces){
				projectface(p);

			}
			for(Line l:Main_class.elist.get(x).t.links){
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

		double arc = visualrange*Math.atan2(hypotenuse, v.x)/hypotenuse;

		p.projectx = (int) Math.round(windowscale*(v.y*arc+Math.PI)); //y
		p.projecty = (int) Math.round(windowscale*(v.z*arc+Math.PI)); //z

	}

	public static void projectpointT(Point p){

		V3 v = new V3(utility.Math_methods.rotatepoint(utility.Math_methods.rotatepoint(p.position.sub2(viewposition), orientation), axis_orientation));

		double hypotenuse = Math.sqrt(v.z*v.z + v.y*v.y);	

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

		for(int x = 0; x < Main_class.elist.size(); x++){

			for(int y = 0; y<Main_class.elist.get(x).p.faces.length; y++){ //<---not to be converted to type t:<array> format
				if(Main_class.elist.get(x).p.faces[y].arcshort){
					order2.add(new Render_obj(Main_class.elist.get(x).p.faces[y]));					
				}
			}

			for(int y = 0; y<Main_class.elist.get(x).t.length-1; y++){ //<---not to be converted to type t:<array> format
				if(Main_class.elist.get(x).t.links[y].arcshort){
					order2.add(new Render_obj(Main_class.elist.get(x).t.links[y]));
				}
			}
		}

		order2.trimToSize();
		Misc_methods.sort2(0, order2.size()-1, order2);


	}

	public static void lighting(){

		for(int x = 0; x < Main_class.elist.size(); x++){
			for(Plane p:Main_class.elist.get(x).p.faces){
				if(p.arcshort){
					p.illumination.set(0,0,0);
				}
			}

			for(int z = 0; z<Main_class.elist.get(x).t.length-1; z++){
				if(Main_class.elist.get(x).t.links[z].arcshort){
					Main_class.elist.get(x).t.links[z].illumination.set(0,0,0);
				}
			}		
		} 	

		for(int x = 0; x < Main_class.elist.size(); x++){
			for(int y = 0; y < Main_class.elist.size(); y++){
				for(Plane p:Main_class.elist.get(y).p.faces){
					if(p.arcshort){
						p.illumination.add(Main_class.elist.get(x).luminosity.invscale2(p.center.sub2(Main_class.elist.get(x).position).squmagnitude()));
					}
				}

				for(int z = 0; z<Main_class.elist.get(y).t.length-1; z++){
					if(Main_class.elist.get(y).t.links[z].arcshort){
						Main_class.elist.get(y).t.links[z].illumination.add(Main_class.elist.get(x).luminosity.invscale2(Main_class.elist.get(y).t.links[z].center.sub2(Main_class.elist.get(x).position).squmagnitude()));
					}
				}
			} 	
		} 	
	}

}
