package physics;

import objects.Entity;
import objects.Plane;
import utility.Main_class;
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
		
	}

}
