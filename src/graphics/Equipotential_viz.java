package graphics;

import objects.Entity;
import objects.Point;
import physics.Motion;
import utility.Main_class;
import utility.V3;

public class Equipotential_viz {
	
	public static V3 center = new V3(0,0,0);
	public static V3 apothem = new V3(20,20,20);
	public static double spacing = .25;
	public static double potentialvalue = -1.2;
	public static double threshold = .05;
	
	public static void generatesurface(){
		
		if(Main_class.equipotentialviz){

			Main_class.equipotential.clear();
			
			for(double x = center.x-apothem.x; x<center.x+apothem.x; x+= spacing){
				for(double y = center.y-apothem.y; y<center.y+apothem.y; y+= spacing){
					for(double z = center.z-apothem.z; z<center.z+apothem.z; z+= spacing){
						
						double potential = 0;
						
						for(Entity e : Main_class.elist){
							potential -= Motion.G*e.mass/(e.position.sub2(x, y, z)).magnitude();
						}
						
						if(Math.abs(potential-potentialvalue)<= threshold){
							Main_class.equipotential.add(new Point(new V3(x,y,z)));
						}
						
					}
				}
			}

		}

	}
	
	
}
