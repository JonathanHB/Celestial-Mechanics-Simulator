package graphics;

import java.util.ArrayList;

import objects.Cable;
import objects.Cablenode;
import objects.Entity;
import objects.Line;
import objects.Plane;
import objects.Point;
import objects.Render_obj;
import physics.Motion;
import user_interface.Key_control;
import utility.Main_class;
import utility.Math_methods;
import utility.Misc_methods;
import utility.V3;

public class Graphics_engine {

	public static ArrayList <Render_obj> order2 = new ArrayList<Render_obj>(0);
	//used to hold all graphics objects in order of distance for rendering purposes

	public static V3 viewposition = new V3(0,0,0);
	public static V3 orientation = new V3(0,0,0); //x=azimuth, y=polar, z=roll

	public static Entity focus;
	public static int focusindex;
	
	static double windowscale = 800;
	//static double visualrange = 6; //used in stereographic projections

	static double arcmax = 1.5;

	public static ArrayList <Line> rvectors = new ArrayList<Line>(0);
	
	public static void projector(){ //converts 3d object positions to 2d positions in visual field

		rvectors.clear();
		
		for(Entity e : Main_class.elist){
			//System.out.println(e.position.tostring());

			for(Point p:e.p.vertices){
				p.project3(e.position, viewposition, orientation);
			}

			for(Point p:e.t.nodes){
				p.projectabs(viewposition, orientation);
			}
			//----------------------------------------------------------
			for(Plane p:e.p.faces){
				p.project3(e.position, viewposition, orientation);
			}

			for(Line l:e.t.links){
				l.project(viewposition, orientation);		
			}
			/*
			e.rvector.p1.position.set(Math_methods.rotationaxis(e.rotation).scale2(5));
			e.rvector.p2.position.set(0);

			
			e.rvector.setcenter();			
			e.rvector.p1.project(e.position, new V3(0,0,0), viewposition, orientation);
			e.rvector.p2.project(e.position, new V3(0,0,0), viewposition, orientation);
			e.rvector.project(viewposition, orientation);
			
			//System.out.println(e.rotation.tostring());
			
			rvectors.add(e.rvector);
			*/
			/*
			e.velvector.setcenter();			
			projectpointP(e.velvector.p1, e.position, e.orientation);
			projectpointP(e.velvector.p2, e.position, e.orientation);
			projectedge(e.velvector);
			rvectors.add(e.velvector);*/	
			
		} 

		for(Cable c : Main_class.clist){

			for(Cablenode cn:c.nodes){
			//	projectpointT(cn.p);
				cn.p.project3(cn.position, viewposition, orientation);
			}

			for(Line l : c.links){
			//	projectedge(l);		
				l.project(viewposition, orientation);
			}
			
			for(Point p:c.t.nodes){
			//	projectpointT(p);
				p.projectabs(viewposition, orientation);
			}
			
			for(Line l:c.t.links){
			//	projectedge(l);	
				l.project(viewposition, orientation);
			}

		}
		
		for(Point p : Main_class.equipotential){			
		//	projectpointT(p);		
			p.projectabs(viewposition, orientation);
		}

	} 
/*
	public static void projectpointP(Point p, V3 entpos, V3 entori){ //combine with T version?
		//computes 2d visual field position of a point from its 3d position
		//used for points in polyhedra
		
		//adds entity position to point position because polyhedron points store their locations 
		//relative to their parent entities to make rotation more computationally efficient 
		//(when rotation is 0 and the objects are moving, either storage method is equally efficient,
		//although stationary objects make this design less efficient)

		V3 v = new V3(Math_methods.rotatepoint(Math_methods.rotatepoint(p.position, entori).add2(entpos).sub2(viewposition), orientation));

		//double hypotenuse = Math.sqrt(v.z*v.z + v.y*v.y);
		
		//double arc = visualrange*Math.atan2(hypotenuse, v.x)/hypotenuse; //this leads to a glitch when hypotenuse = 0;

		int[] projection = planarprojection(v);
		p.projectx = projection[0];
		p.projecty = projection[1];

		
		//p.projectx = (int) Math.round(windowscale*(v.y*arc)+Frame_functions.ctrw); //y
		//p.projecty = (int) Math.round(windowscale*(v.z*arc)+Frame_functions.ctrh); //z
		
	}

	public static void projectpointT(Point p){
		//computes 2d visual field position of a point from its 3d position
		//used for points in trails
		
		V3 v = new V3(Math_methods.rotatepoint(p.position.sub2(viewposition), orientation));

		//double hypotenuse = Math.sqrt(v.z*v.z + v.y*v.y); //this leads to a glitch when hypotenuse = 0;
		
		//double arc = visualrange*Math.atan2(hypotenuse, v.x)/hypotenuse;

		//p.squdistance = v.squmagnitude(); //used for accurate overlap
		
		int[] projection = planarprojection(v);
		p.projectx = projection[0];
		p.projecty = projection[1];
		
		//p.projectx = (int) Math.round(windowscale*(v.y*arc)+Frame_functions.ctrw); //y
		//p.projecty = (int) Math.round(windowscale*(v.z*arc)+Frame_functions.ctrh); //z

	}

	public static void projectface(Plane p, V3 entpos, V3 entori){
		//computes angle to a face from its 3d position, used for culling of objects outside of visual field
		
		V3 v = new V3(Math_methods.rotatepoint(Math_methods.rotatepoint(p.center, entori).add2(entpos).sub2(viewposition), orientation));

		p.squdistance = v.squmagnitude();

		//p.arcshort = Math.atan2(Math.sqrt(v.z*v.z + v.y*v.y), v.x)<=arcmax; //has negligible effect on runtime

	}

	public static void projectedge(Line l){
		//computes angle to a line from its 3d position, used for culling of objects outside of visual field
		
		V3 v = new V3(Math_methods.rotatepoint(l.center.sub2(viewposition), orientation));

		l.squdistance = v.squmagnitude();

		//l.arcshort = Math.atan2(Math.sqrt(v.z*v.z + v.y*v.y), v.x)<=arcmax;

	}*/

	public static void setorder(){ 
		//sets up graphics object array to be sorted
		//sorting is used to draw nearer objects in front of further ones
		//culls objects outside of visual field
		
		order2.clear();		

		for(Entity e : Main_class.elist){

			for(Plane p : e.p.faces){
				if(p.arcshort){
					order2.add(new Render_obj(p));					
				}
			}

			for(Line l : e.t.links){
			//	if(l.arcshort){
					order2.add(new Render_obj(l));
			//	}
			}
		}

		for(Cable c : Main_class.clist){
			
			for(Line l : c.links){
			//	if(l.arcshort){
					order2.add(new Render_obj(l));
			//	}
			}	
			
			for(Line l : c.t.links){
			//	if(l.arcshort){
					order2.add(new Render_obj(l));
			//	}
			}	
		}
		
		for(Point p : Main_class.equipotential){
			order2.add(new Render_obj(p));
		}
		
		for(Line l : rvectors){
		//	if(l.arcshort){
				order2.add(new Render_obj(l));
		//	}
		}	

		order2.trimToSize();
		Misc_methods.sort2(0, order2.size()-1, order2); //sort objects by distance


	}

	public static void lighting(){ 
		//provides dynamic lighting for objects and trails, but doesn't simulate shadowing

		for(Entity e : Main_class.elist){
			for(Plane p : e.p.faces){
			//	if(p.arcshort){
					p.illumination.set(0,0,0);
			//	}
			}

			for(Line l : e.t.links){
			//	if(l.arcshort){
					l.illumination.set(0,0,0);
			//	}
			}

		} 	
		
		if(Main_class.stressvisualization == false){
			for(Cable c : Main_class.clist){
				for(Line l : c.links){
				//	if(l.arcshort){
						l.illumination.set(0,0,0);
				//	}
				}
			}
		}	
		
		for(Cable c : Main_class.clist){
			for(Line l : c.t.links){
			//	if(l.arcshort){
					l.illumination.set(0,0,0);
			//	}
			}
		}

		for(Entity e1 : Main_class.elist){
			if(e1.luminosity.isnonzero()){
				for(Entity e2 : Main_class.elist){
					for(Plane p : e2.p.faces){
					//	if(p.arcshort){
							p.illumination.add(e1.luminosity.invscale2(Math_methods.rotatepoint(p.center, e2.orientation).add2(e2.position).sub2(e1.position).squmagnitude()));
					//	}
					}

					for(Line l : e2.t.links){
					//	if(l.arcshort){
							l.illumination.add(e1.luminosity.invscale2(l.center.sub2(e1.position).squmagnitude()));
					//	}
					}
				} 
				
				if(Main_class.stressvisualization == false){ //illuminates cables
					for(Cable c : Main_class.clist){
						for(Line l : c.links){
						//	if(l.arcshort){
								l.illumination.add(e1.luminosity.invscale2(l.center.sub2(e1.position).squmagnitude()));
						//	}
						}					
					}
				}
				
				for(Cable c : Main_class.clist){ //illuminates cable trails
					for(Line l : c.t.links){
					//	if(l.arcshort){
							l.illumination.add(e1.luminosity.invscale2(l.center.sub2(e1.position).squmagnitude()));
					//	}
					}
				}
			}

		} 

	}

	public static void getfocus(){
		
		if(!Main_class.fixedreferences){
			
			Entity potentialref = new Entity();
			double maxforce = 0;
		//	double capturerad = 0;
			
			for(Entity e : Main_class.elist){
				
				double force = e.mass/(e.position.sub2(viewposition).squmagnitude()); 
				//actually missing G; not a real force
				
				if(force>maxforce){
					maxforce = force;
					potentialref = e;
				//	capturerad = e.position.sub2(viewposition).squmagnitude();
				}
				
			}
			
			focus = potentialref;
			
		//	Key_control.tsensitivity = capturerad/10;
			
		}
		
	}
	
	public static void camtofocus() {
		if(focusindex != -1) {
			viewposition = focus.position.add2(0,0,2*focus.radius);
		}	
	}
	
	public static int[] planarprojection(V3 v) {
		
		int[] xy = new int[2];
		
		if(v.x > 0) {
			xy[0] = (int) (Math.round(windowscale*v.y/v.x)+Frame_functions.ctrw);
			xy[1] =	(int) (Math.round(windowscale*v.z/v.x)+Frame_functions.ctrh);	
		}else {
			xy[0] = (int) (Math.round(9999*v.y));
			xy[1] = (int) (Math.round(9999*v.z));
		}
		
		return xy;
		
	}
	
}
