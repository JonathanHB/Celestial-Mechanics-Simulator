package graphics;

import objects.Entity;
import objects.Point;
import physics.Motion;
import utility.Main_class;
import utility.V3;

public class Equipotential_viz {
	
	public static V3 center = new V3(0,0,0);
	public static V3 apothem = new V3(30,30,30);
	public static V3 centminapo = new V3(center.sub2(apothem));
	public static double spacing = .2;
	public static double potentialvalue = -1.2;
	public static double threshold = .05;
	
	public static V3 lengths = new V3(Math.round(2*apothem.x/spacing),Math.round(2*apothem.y/spacing),Math.round(2*apothem.z/spacing));
	
	public static boolean[][][] checkedcells = new boolean[(int)(lengths.x)][(int)(lengths.y)][(int)(lengths.z)]; 
	
	public static void generatesurface(){ //deprecated; the new method is confirmed to be better
		
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
	
	public static void efficient_surface(){
		
		Main_class.equipotential.clear();
		
		for(int x = 0; x< lengths.x; x++){
			for(int y = 0; y< lengths.y; y++){
				for(int z = 0; z< lengths.z; z++){
					
					checkedcells[x][y][z] = false;
					
				}
			}
		}
		
		for(Entity e : Main_class.elist){
			for(int a = 0; a < (int)Math.round((e.position.x-centminapo.x)/spacing)-1; a++){
				
				double potential = 0;
				
				double x = a*spacing+centminapo.x;
				double y = e.position.y;
				double z = e.position.z;
				
				int b = (int)Math.round((e.position.y-centminapo.y)/spacing);
				int c = (int)Math.round((e.position.z-centminapo.z)/spacing);
				
				for(Entity ee : Main_class.elist){
					potential -= Motion.G*ee.mass/(ee.position.sub2(x, y, z)).magnitude();
				}
				
				checkedcells[a][b][c] = true;
				
				if(Math.abs(potential-potentialvalue)<= threshold){
					check_around(a,b,c);
					
				}
				
				
				
			}
		}
		
	}
	
	public static void check_around(int a, int b, int c){

		double potential = 0;
		
		double x = a*spacing+centminapo.x;
		double y = b*spacing+centminapo.y;
		double z = c*spacing+centminapo.z;
		
		for(Entity e : Main_class.elist){
			potential -= Motion.G*e.mass/(e.position.sub2(x, y, z)).magnitude();
		}
		
		checkedcells[a][b][c] = true;
		
		if(Math.abs(potential-potentialvalue)<= threshold){
			
			Main_class.equipotential.add(new Point(new V3(x,y,z)));
						
			for(int d = a-1; d<=a+1; d++){
				for(int e = b-1; e<=b+1; e++){
					for(int f = c-1; f<=c+1; f++){
						if(d >=0 && e >=0 && f >=0 && d <lengths.x && e <lengths.y && f <lengths.z && checkedcells[d][e][f] == false){
							
							check_around(d,e,f);
							
						}
					}
				}
			}
			
		}
		
	}
	
}
