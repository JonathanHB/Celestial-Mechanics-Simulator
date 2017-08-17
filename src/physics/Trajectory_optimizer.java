package physics;

import java.awt.Color;
import java.util.ArrayList;

import objects.Entity;
import objects.Poly_library;
import objects.Test_mass;
import utility.Main_class;
import utility.Math_methods;
import utility.V3;

public class Trajectory_optimizer {

	//public static boolean running = true; //whether to run the trajectory optimizer

	public static int start = 0;    //locations in array of start and target entities
	public static int target = 1;

	public static double altitude = 4000000; //distance above start entity surface to place test objects

	public static int start_num = 1000;     // number of particles used for the first repetition //6000
	public static int rep_num = 400;		 // number of particles used for each repetition     //400

	public static int particle_num;

	public static int repetitions = 4;        //number of repetitions //16
	public static double maxtime = 31557600;  //number of seconds to simulate for (1 year = 31557600 seconds) 
	public static int ticknum;                //number of ticks to simulate for 

	public static double basevelocity;        		   //velocity for a circular orbit
	public static double deltav = 3.01E3;           //maximum initial delta v in m/s
	
	public static int i = 0;
	public static int ii = 0; //used to immediately quit optimizer once a good trajectory is found

	public static V3 postable[][]; //planet positions, trades memory for computing power savings (exceeds VM capacity and fails, may take too much memory)

	public static Test_mass[] virtualents; //used to generate postable

	static ArrayList<Test_mass> probes = new ArrayList<Test_mass>(0);

	public static void maketables(){

		ticknum = (int) Math.ceil(maxtime/Motion.increment); 
		//number of ticks to simulate each iteration for
		basevelocity = deltav + Math.sqrt(Motion.G*Main_class.elist.get(start).mass/(Main_class.elist.get(start).radius+altitude));	
		//velocity for a circular orbit	+ deltav
		postable = new V3[Main_class.elist.size()][ticknum]; 
		//a table of positions of entities at every tick; 
		//data intensive but avoids the need to repeatedly simulate the motion of the entities
		//also avoids the need to modify and then reset entities
		virtualents = new Test_mass[Main_class.elist.size()];
		//an array of duplicate entities used to generate postable
		
		
		//the code below generates virtualents and uses them to generate the postable
		
		for(Entity e : Main_class.elist){

			virtualents[Main_class.elist.indexOf(e)] = new Test_mass(e);

		}

		for(int x = 0; x<ticknum; x++){

			tablegenwrite(x);
			tablegenmove(virtualents);
			tablegengrav();

		}

	}	

	public static void optimize(){

		//the program begins by testing a set of randomly distributed orbits
		
		particle_num = start_num;
		
		for(int x = 0; x<particle_num; x++){

			probes.add(new Test_mass(new Test_mass(), false));
		}

		for(ii = 0; ii<ticknum; ii++){

			move(probes);
			optgrav(ii);

		}

		//Starting with the results of its initial orbit distribution, 
		//the program tests increasingly concentrated sets of orbits,
		//approaching a single orbital plane and phase. 
		//The orbital parameters from each round of testing are weighted 
		//by how close the probe came to the target, and this weighted average is then
		//used as the center of a new orbit distribution. The size of the distribution shrinks 
		//for each round of testing based on how much the closest approach to the target improved	
		
		particle_num = rep_num;
		
		for(i=i; i<repetitions; i++){ //i=i preserves i value, which is either 0 or repetitions(used to cause the loop to exit immediately)

			V3[] traject_mod = shrinkrange();

			probes.clear();
			
			for(int x = 0; x<particle_num-1; x++){
				
				probes.add(new Test_mass(traject_mod));
				
			}

			probes.trimToSize();
			
			for(ii = 0; ii<ticknum; ii++){
			
				move(probes);
				optgrav(ii);

			}

		}

		Test_mass best_traject = closest(probes);
		
		//System.out.println(Math.sqrt(best_traject.minsquaredistance));
		//System.out.println(best_traject.initorbit.scale2(180/Math.PI).tostring()+": longitude of [a/de]scending node, inclination, phase"); //orbital parameters in degrees
		
		//The system adds an entity in the best orbit found by the calculator to display the results
		Main_class.elist.add(
			new Entity(
				0,1,new V3(0,0,0),
				Math_methods.rotatepoint(new V3(0, altitude+Main_class.elist.get(start).radius, 0), best_traject.initorbit).add2(Main_class.elist.get(start).position),
				Math_methods.rotatepoint(new V3(0,0,basevelocity+deltav), best_traject.initorbit).add2(Main_class.elist.get(start).velocity),
				new V3(0,0,0),new V3(0,0,0),
				new Color(0,0,0),Poly_library.standard_poly_bases[0],Poly_library.standard_poly_maps[0],new V3(2E6,2E6,2E6),new V3(0,0,0),
				new Color(0,0,0),2000,17000000, -1, false
			)
		);

	}	

	public static void tablegenmove(Test_mass[] t){ //moves virtualents for postable generation
		for(Test_mass m : t){		
			m.move(Motion.increment);	
		}
	}
	
	public static void tablegengrav(){ //applies gravitation to virtualents for postable generation; see gravitation() in Motion class

		double g_invrad; //G/r^3
		double distance; //r
		double squdistance; //r^2
		V3 dpos;
		
		for(int x = 0; x<virtualents.length; x++){
			
			for(int y = x+1; y<virtualents.length; y++){

				dpos=virtualents[x].position.sub2(virtualents[y].position);

				squdistance=dpos.squmagnitude();
				distance=Math.sqrt(squdistance);
				g_invrad=Motion.increment*Motion.G/(squdistance*distance);//the last power is used for efficient scaling

				virtualents[x].velocity.sub(dpos.scale2(Main_class.elist.get(y).mass*g_invrad));
				virtualents[y].velocity.add(dpos.scale2(Main_class.elist.get(x).mass*g_invrad));

				if(distance<Main_class.elist.get(x).radius+Main_class.elist.get(y).radius){
					System.out.println("Error, entity collision inhibits correct table generation.");
				}
			}
		}

	}

	public static void tablegenwrite(int time){ //records virtualent positions to generate postable
		for(int x=0; x<Main_class.elist.size(); x++){					
			postable[x][time]=new V3(virtualents[x].position);								
		}
	}
	
	public static void optgrav(int time){
		//applies Newton's law of universal gravitation from planets to probes, which are considered massless
		//see the gravitation method in the Motion class for details
		
		double g_invrad; //G/r^3
		double distance; //r
		double squdistance; //r^2
		V3 dpos;

		for(int x=0; x<Main_class.elist.size(); x++){
			if(x!=target){
				for(int y=x+1; y<probes.size(); y++){

					dpos=probes.get(y).position.sub2(postable[x][time]);

					squdistance=dpos.squmagnitude();
					distance=Math.sqrt(squdistance);
					g_invrad=Motion.increment*Motion.G/(squdistance*distance);//the last power is used for efficient scaling

					probes.get(y).velocity.sub(dpos.scale2(Main_class.elist.get(x).mass*g_invrad));

					//removes probes which hit entities other than the target
					if(distance<=Main_class.elist.get(x).radius){

						probes.remove(y);
						probes.trimToSize();

					}
					
				}

			}else{

				for(int y=x+1; y<probes.size(); y++){

					dpos=probes.get(y).position.sub2(postable[x][time]);

					squdistance=dpos.squmagnitude();
					distance=Math.sqrt(squdistance);
					g_invrad=Motion.increment*Motion.G/(squdistance*distance);//the last power is used for efficient scaling

					probes.get(y).velocity.sub(dpos.scale2(Main_class.elist.get(x).mass*g_invrad));

					probes.get(y).update_distance(squdistance);

					//immediately terminates optimizer if a probe hits the target
					if(distance<=Main_class.elist.get(x).radius){

						System.out.println(time*Motion.increment + " seconds");
						i = repetitions;
						ii = ticknum;
						y = particle_num;
						x = Main_class.elist.size();

					}

				}

			}

		}

	}

	public static Test_mass closest(ArrayList<Test_mass> t){
		//finds each probe's closest approach to the target
		
		Test_mass current_min = new Test_mass(); 

		for(Test_mass m:t){
			if(m.minsquaredistance<current_min.minsquaredistance){		
				current_min = m;	//shallowcopying is okay here			
			}

		}

		return current_min;
		
	}
	
	public static void move(ArrayList<Test_mass> t){ //moves probes
		for(Test_mass m:t){		
			m.move(Motion.increment);	
		}
	}
	
	public static V3[] shrinkrange(){ 
		//converts the orbital parameters (inclination, longitude of nodes, and phase, stored in a V3 object)
		//to Cartesian coordinates, weights them by probe's closest approach to the target,
		//and adds the coordinates up to get the average best orbit and the concentration of angles around it
		//the average best orbit is used as a seed to generate new test orbits, while the concentration indicates
		//how wide their distribution should be.
		
		V3 coscompsums = new V3(0,0,0);
		V3 sincompsums = new V3(0,0,0);
		
		double invsum = 0;
	
		for(Test_mass t : probes){
			
			coscompsums.add(t.initorbit.cosines().invscale2(t.minsquaredistance));
			sincompsums.add(t.initorbit.sines().invscale2(t.minsquaredistance));
			invsum += 1.0/t.minsquaredistance;
			
		}
		
		sincompsums.invscale(probes.size()*invsum);
		coscompsums.invscale(probes.size()*invsum);
		
		V3 best_angles = new V3(sincompsums, coscompsums);
		V3 concentrations = new V3(sincompsums, coscompsums, false);
		
		V3[] v = {best_angles, concentrations};
		
		return v;
		
	}
	
}


