package physics;

import objects.Cable;
import objects.Entity;
import graphics.Graphics_engine;
import utility.Main_class;
import utility.V3;

public class Motion {

	public static double increment; //time step, in seconds, of the physics engine
	public static int repetition; //number of times to run the physics engine between graphics engine runs
	//note: different increments produce different results, 
	//but simulations are identical and deterministic under any repetition values
	//simulation asymptotically approaches reality as increment->0, 
	//long increments relative to system size lead to issues like precession of periapsis in two body systems
		
	public static boolean flipping = false;		
	public static double incbuff = increment;
	public static int repbuff = repetition;		
	
	public static double G = .00000000006674; //gravitational constant
	
	public static void movement(){
		
		for(Entity e : Main_class.elist){
			e.move(increment); //combines movement and rotation; see entity			
		}
		if(Graphics_engine.focus!=-1){
			Graphics_engine.viewposition.add(Main_class.elist.get(Graphics_engine.focus).velocity.scale2(increment));
		}
	}
	
	public static void gravitation(){ 
		//numerical for loops are used to avoid concurrent modification exceptions when objects split or collide
				
		double g_invrad; //G/r^3
		double distance; //r
		double squdistance; //r^2
		V3 dpos;
				
		for(int x = 0; x<Main_class.elist.size(); x++){ //Entity e1 : Main_class.elist
			
			for(int y = x+1; y<Main_class.elist.size(); y++){ //Entity e2 : Main_class.elist.subList(Main_class.elist.indexOf(e1)+1, Main_class.elist.size())

				Entity e1 = Main_class.elist.get(x);
				Entity e2 = Main_class.elist.get(y);

				dpos = e1.position.sub2(e2.position);
						
				squdistance = dpos.squmagnitude();
				distance = Math.sqrt(squdistance);
				g_invrad = increment*G/(squdistance*distance);//the last power is used for efficient scaling
												
				e1.velocity.sub(dpos.scale2(e2.mass*g_invrad));
				e2.velocity.add(dpos.scale2(e1.mass*g_invrad));
						
				if(distance < e1.radius + e2.radius){
					Object_manager.add(e1, e2, true);
				}

			}
	
		}
		
	}		
	
	public static void cableforces(){
		for(Cable c: Main_class.clist){
			c.internalforces(increment);
			for(Entity e: Main_class.elist){
				c.applygrav(e, increment);
			}			
		}
		for(int x = 0; x < Main_class.clist.size(); x++){
			Object_manager.splitcable(Main_class.clist.get(x), Main_class.clist.get(x).checkforbreaks());
		}	
	}
	
	public static void cablemove(){
		for(Cable c: Main_class.clist){
			c.move(increment);
		}
	}
	
	public static void physexec(){ 
		
		if(repetition>0){
		
			for(int i=0; i<Math.abs(repetition); i++){
					
				movement();
				cablemove();
				cableforces();
				gravitation();								

			}
		
		}else{
			
			for(int i=0; i<Math.abs(repetition); i++){
					
				gravitation();
				cableforces();				
				cablemove();
				movement();

			}
			
		}
/*		
		if(flipping){
			movement();
			flipping = false;
		}
*/		
		repetition=repbuff;
		increment=incbuff;
		
	}
	
}
