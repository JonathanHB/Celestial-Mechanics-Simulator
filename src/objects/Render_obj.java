package objects;

public class Render_obj { 
	//a graphics object container used for efficient quicksorting of multiple object types

	public Plane p; //the object points to either a plane or a line
	public Line l;
	public Point po;
	public boolean plane; //true=plane, false=line
	public boolean point;
	
	//shallow copies are intentional; deep ones would waste space
	
	public Render_obj(Plane pp){ //constructs a render_obj containing a plane
		p=pp;
		plane=true;
		point = false;
	}
	
	public Render_obj(Line ll){ //constructs a render_obj containing a line
		l=ll;
		plane=false;
		point = false;
	}
	
	public Render_obj(Point popo){ //constructs a render_obj containing a line
		po=popo;
		plane=false;
		point = true;
	}
	
	public double getsqudistance(){ //gets squared distance to object for sorting
		if(plane){
			return p.squdistance;
		}else if(point == false){
			return l.squdistance;
		}else{
			return po.squdistance;
		}
		
	}
		
}
