package objects;

//import graphics.Graphics_engine;

public class Render_obj {

	public Plane p;
	public Line l;
	public boolean plane; //true=plane, false=line
	
	//shallow copies are intentional; deep ones would waste space
	
	public Render_obj(Plane pp){
		p=pp;
		plane=true;	
	}
	
	public Render_obj(Line ll){
		l=ll;
		plane=false;
	}
	
	public double getsqudistance(){
		if(plane){
			return p.squdistance;
		}else{
			return l.squdistance;
		}
		
	}
		
}
