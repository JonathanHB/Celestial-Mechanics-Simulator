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
		
	public static boolean flipping = false;	//preserves determinism when reversing direction
	
	public static double incbuff = increment; //buffers used to prevent problematic variable updates as the method runs
	public static int repbuff = repetition;		
	
	public static double G = .00000000006674; //gravitational constant
	
	public static void movement(){ //moves entities
		
		for(Entity e : Main_class.elist){
			e.move(increment); 	
		}
		
		//moves camera in sync with the entity (if any) whose frame of reference it shares
		if(Graphics_engine.focus!=-1){ 
			Graphics_engine.viewposition.add(Main_class.elist.get(Graphics_engine.focus).velocity.scale2(increment));
		}
		
	}
	
	//applies Newton's law of universal gravitation to all objects, an approximation of the N-body problem
	public static void gravitation(){ 
		
		//numerical for loops are used to avoid concurrent modification exceptions when objects split or collide
				
		double g_invrad; //G/r^3
		double distance; //r
		double squdistance; //r^2		//These variables are used stored to avoid repeating mathematical operations, speeding up calculations
		V3 dpos;
				
		for(int x = 0; x<Main_class.elist.size(); x++){ 
			
			for(int y = x+1; y<Main_class.elist.size(); y++){ 

				
				Entity e1 = Main_class.elist.get(x);
				Entity e2 = Main_class.elist.get(y);

				dpos = e1.position.sub2(e2.position); //position vector between objects
						
				squdistance = dpos.squmagnitude();
				distance = Math.sqrt(squdistance); 
				g_invrad = increment*G/(squdistance*distance); 
				//g_invrad is the the G/r^2 value used to calculate the acceleration of both objects, 
				//divided by distance for use in calculating the final acceleration vector
												
				e1.velocity.sub(dpos.scale2(e2.mass*g_invrad));  //computes acceleration of each body and updates their velocities accordingly
				e2.velocity.add(dpos.scale2(e1.mass*g_invrad));
						
				if(distance < e1.radius + e2.radius){ //checks if the objects collide
					Object_manager.add(e1, e2, true);
				}

			}
	
		}
		
	}		
	
	public static void cableforces(){ //manages cable behavior
		
		for(Cable c: Main_class.clist){ 
			
			for(Entity e: Main_class.elist){ //accelerates cables under gravity
				c.applygrav(e, increment);
			}	
			
			c.internalforces(increment);
			
		}
		
		for(int x = 0; x < Main_class.clist.size(); x++){ //checks if cables have broken and splits them accordingly
			Object_manager.splitcable(Main_class.clist.get(x), Main_class.clist.get(x).checkforbreaks());
		}	
		
	}
	
	public static void cablemove(){ //executes cable movement
		for(Cable c: Main_class.clist){
			c.move(increment);
		}
	}
	
	public static void physexec(){ //runs physics methods in an organized way
		
		//the methods are run in opposite orders when going forward and backward in time 
		//so that the physics will run in an exactly equal and opposite way, preserving determinism
		
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
	
		repetition=repbuff;  //updates increment and repetition variables from buffers
		increment=incbuff;
		
	}
	
}
