package objects;

public class Render_obj { 
	//a graphics object container used for efficient quicksorting of multiple object types

	public Plane p; //the object points to either a plane or a line
	public Line l;
	public boolean plane; //true=plane, false=line
	
	//shallow copies are intentional; deep ones would waste space
	
	public Render_obj(Plane pp){ //constructs a render_obj containing a plane
		p=pp;
		plane=true;	
	}
	
	public Render_obj(Line ll){ //constructs a render_obj containing a line
		l=ll;
		plane=false;
	}
	
	public double getsqudistance(){ //gets squared distance to object for sorting
		if(plane){
			return p.squdistance;
		}else{
			return l.squdistance;
		}
		
	}
		
}
