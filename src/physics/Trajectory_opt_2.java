package physics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import objects.Entity;
import objects.Poly_library;
import objects.Test_mass;
import objects.Test_mass_2;
import utility.Main_class;
import utility.Math_methods;
import utility.Misc_methods;
import utility.V3;

public class Trajectory_opt_2 {

	public boolean sailing = false; //whether the craft has a solar sail

	public static int init_index;
	public static Entity init_pointer; //index and pointer of entity around which the craft begins
	
	public static int target_index;
	//public Entity target_pointer; //index and pointer of target entity
	//static double targetGM; //G*m of target entity to calculate potential energy
	static double target_mass; //target entity mass to calculate potential energy
	
	//parameters for solar sailing:
	//there is assumed to be a single light source with a radius much smaller than the distance to the craft
	
	int light_ent; //the entity emitting the light used for sailing (assumed to be only one)
	Entity l_ent; //pointer to the above entity
	double lightpower; //power emitted by light source (assuming spherically symmetric distribution)
	double area; //area of solar sail
	double c = 2.998e8; //speed of light, used to determine photon pressure
	double m; //craft mass
	
	V3 F; //force applied by sail divided by distance from light source squared, in the plane of the radial and velocity vectors

	//general parameters:
	//massive objects are assumed not to collide
	
	static int initprobes; //number of probes for initial search
	static int repeatprobes; //number of probes for subsequent steps
	
	static double maxtime; //31557600;  //number of seconds to simulate for (1 year = 31557600 seconds) 
	static int ticknum; //number of ticks to simulate for 	
	static int tickrange; //the range of ticks over which probes launch [are added from buffer array]
	static int T; //tick counting variable, used in multiple methods so it's defined globally

	static int repetitions; //number of times to run optimization steps
	static int i;
	static byte metric; //which metric to use for optimization (i.e. distance vs energy)
	static boolean direction; //whether to minimize or maximize the metric TODO implement this!
	
	static boolean hit = false; //did a probe hit the target? //TODO or reach the desired metric threshold
	
	//double radius; //initial probe radius from starting entity  
	//double velocity; //initial probe velocity
	//allows for starting objects in parabolic or hyperbolic orbits with infinite semimajor axis
	
	static boolean whichparams[]; //which orbital parameters to optimize	
	static V3 orbitparams;
	static V3 planeparams;
	static double startparam;
	static double ranges[]; //range of orbital parameters to test for each parameter being optimized. Values are total range widths; ranges are centered on 0.

	static int pnum; //number of planets, assumed to be constant
	
	static ArrayList<Test_mass_2> probes = new ArrayList<Test_mass_2>(0);
	static ArrayList<Test_mass_2> probes2 = new ArrayList<Test_mass_2>(0);

	static Test_mass_2 best; //the buffer holding the probe with the best trajectory for use as a seed

	static Test_mass_2[] planets;
	
	static boolean bb = false;
	
	//initialize parameter values and planet array
	static void setup() {
		
		init_index = 1;
		init_pointer = Main_class.elist.get(init_index);
		target_index = 2;
		target_mass = Main_class.elist.get(target_index).GMt/(Motion.increment*Motion.G);
		metric = 0; //distance is currently metric 0, energy is metric 1
		
		pnum = Main_class.elist.size(); //number of planets/moons/other massive bodies
		
		planets = new Test_mass_2[pnum];
		
		for(int x = 0; x < pnum; x++) {
			
			planets[x] = new Test_mass_2(Main_class.elist.get(x));
			
		}
		
		whichparams = new boolean[] {false,false,false,true,true,true,true};
		//the first three parameters are phase/inclination/major axis, the fourth through sixth are orbital angles, and the seventh parameter is launch time
		//launch time is probably redundant with phase where it's very small but not in general; phase may not be useful or realistic
		
		ranges = new double[] {0, 0, 0, 2*Math.PI, Math.PI, 2*Math.PI, 2147376}; //one of the values only needs to be Pi, not 2Pi, since the coordinate system has an extra half-degree of freedom.	
		orbitparams = new V3(6571000,11000, 0); 
		planeparams = new V3(0);
		startparam = 0;
		
		initprobes = 4000;
		repeatprobes = 1000;
		maxtime = 864000;
		repetitions = 1;
		
		//6571000 = radius of 200 km LEO in m
		//11000 = approx. LEO escape velocity in m/s
					
		ticknum = (int) Math.round(maxtime/Motion.increment);
		tickrange = (int) Math.round(ranges[6]/Motion.increment);
					
		
	}
	
	//the main method of this class; it runs all other necessary ones
	public static void optimize() {
		
		setup();
		
		i = 0;
		
		System.out.println("start");
						
		//generate a probe set in unweighted random orbits
		for (int x = 0; x < initprobes; x ++) {
			probes2.add(new Test_mass_2(orbitparams, planeparams, startparam, whichparams, ranges, init_pointer));
		}
	
		iteration();
		System.out.println("Round " + i + "; Closest approach: " + best.minmetric);
		
		//main optimizer loop
		for(i = i; i < repetitions; i++) { //i is defined as a global variable for use outside the loop
			//generate a new set of probes with the best one from the last test as a seed
			
			for (int x = 0; x < repeatprobes; x ++) {			
				probes2.add(new Test_mass_2(best, whichparams, ranges, init_pointer)); 
			}

			iteration();
			System.out.println("Round " + i + "; Closest approach: " + best.minmetric);
		}
		

		Motion.repetition=best.launchtick;
		Motion.repbuff=Motion.repetition;
		Motion.physexec();
		Motion.repetition=0;
		Motion.repbuff=0;
			
		Main_class.time = best.launchtick*Motion.increment;
		Main_class.runtime = Math.abs(Main_class.time);
			
		add_best(best);
		
		System.out.println("-------------------------");
		
		System.out.println("Orbit dimensions (): " + best.initorbit.tostring());
		System.out.println("Orbital plane: " + best.initplane.tostring());
		System.out.println("Intercept time (s): " + best.launchtick*Motion.increment);
		System.out.println("Closest approach (m): " + best.minmetric);
		
		System.out.println("-------------------------");

		System.out.println("done");
		
	}
	
	//runs one iteration of the optimization code
	public static void iteration() {
		
		for(T = -tickrange; T < 0; T++) { //runs backwards to start of launch window
			inverseoptgrav();
		}
		
		//run through the buffer array and add probes at the appropriate times
		for(T = -tickrange; T <= tickrange; T++) { //-tickrange
							
			//add all probes that launch at a given tick from the buffer array
			for(int x = 0; x<probes2.size(); x++) { 
			
					if(probes2.get(x).launchtick == T) { //adds probes to be launched at current time to the simulation
						probes.add(new Test_mass_2(probes2.get(x)));
						probes.get(probes.size()-1).position.add(planets[init_index].position);
						probes.get(probes.size()-1).velocity.add(planets[init_index].velocity); //corrects for movement of planets since simulation start
						probes2.remove(x); //removes the launched probe from the buffer arraylist
						probes2.trimToSize();
						x--;

					}
			}	
			
			optgrav();			

		}

		//run the rest of the simulation, beginning at T=tickrange where the probe-adding initialization loop left off
		for(T = T; T < ticknum; T++){ 

			optgrav();
			
		}
		
		resetplanets();

		//find the probe that got closest to the target to seed the trajectories of the next set of probes
		if(!hit){
			best = new Test_mass_2(closest(probes));
		}

		//shrink ranges to focus search
		for(int x = 0; x<7; x++) {
			ranges[x] *= 0.05;//this parameter should be a variable, and the launch window range may need to shrink faster when the optimal time is near one end of the range		
		}

		//clear arraylists
		probes2.clear();
		probes2.trimToSize();
		probes.clear();
		probes.trimToSize();

	}
	
	public static void inverseoptgrav(){
		//applies Newton's law of universal gravitation from planets
		//see the gravitation method in the Motion class for details
		
		double cuberad; //G/r^3
		double distance; //r
		double squdistance; //r^2
		V3 dpos;
		
		//calculate inter-planet interactions		
		for(int x=0; x < pnum; x++){
			for(int y=x+1; y < pnum; y++){
				
				dpos = planets[x].position.sub2(planets[y].position); //position vector between objects
				
				squdistance = dpos.squmagnitude();
				//distance = Math.sqrt(squdistance); 
				cuberad = squdistance*Math.sqrt(squdistance); 
				//g_invrad is the the G/r^2 value used to calculate the acceleration of both objects, 
				//divided by distance for use in calculating the final acceleration vector
												
				planets[x].velocity.add(dpos.scale2(Main_class.elist.get(y).GMt/cuberad));  //computes acceleration of each body and updates their velocities accordingly
				planets[y].velocity.sub(dpos.scale2(Main_class.elist.get(x).GMt/cuberad));				
				
			}
			
		}
		
		for(int x = 0; x < pnum; x++){
			planets[x].move(-Motion.increment);
		}
		
	}	

	public static void optgrav(){
		//applies Newton's law of universal gravitation from planets to probes, which are considered massless
		//see the gravitation method in the Motion class for details
		
		for(int x = 0; x < probes.size(); x++) {
			probes.get(x).move(Motion.increment);
		}
		
		for(int x = 0; x < pnum; x++){
			planets[x].move(Motion.increment);
		}		
		
		double cuberad; //G/r^3
		double distance; //r
		double squdistance; //r^2
		V3 dpos;
		
		//calculate inter-planet interactions		
		for(int x=0; x < pnum; x++){
			for(int y=x+1; y < pnum; y++){
				
				dpos = planets[x].position.sub2(planets[y].position); //position vector between objects
				
				squdistance = dpos.squmagnitude();
				//distance = Math.sqrt(squdistance); 
				cuberad = squdistance*Math.sqrt(squdistance); 
				//g_invrad is the the G/r^2 value used to calculate the acceleration of both objects, 
				//divided by distance for use in calculating the final acceleration vector
												
				planets[x].velocity.sub(dpos.scale2(Main_class.elist.get(y).GMt/cuberad));  //computes acceleration of each body and updates their velocities accordingly
				planets[y].velocity.add(dpos.scale2(Main_class.elist.get(x).GMt/cuberad));
				
			}
			
		}
		
		//calculate gravitation on and movement of probes
		
		for(int x=0; x < pnum; x++){
			if(x!=target_index){
				for(int y=0; y<probes.size(); y++){

					dpos=probes.get(y).position.sub2(planets[x].position);

					squdistance = dpos.squmagnitude();
					distance = Math.sqrt(squdistance);
					cuberad = squdistance*distance;//the last power is used for efficient scaling

					probes.get(y).velocity.sub(dpos.scale2(Main_class.elist.get(x).GMt/cuberad));

					//removes probes which hit entities other than the target
					if(distance <= Main_class.elist.get(x).radius*1.01){ //scaling for safety/floating point mitigation

						probes.remove(y);
						probes.trimToSize();

					}
					
				}

			}else{
				for(int y=0; y<probes.size(); y++){

					dpos=probes.get(y).position.sub2(planets[x].position);

					squdistance = dpos.squmagnitude();
					distance = Math.sqrt(squdistance);
					cuberad = squdistance*distance;

					probes.get(y).velocity.sub(dpos.scale2(Main_class.elist.get(x).GMt/cuberad));

					//immediately terminates optimizer if a probe hits the target
					if(distance <= Main_class.elist.get(x).radius && metric == 0){					
						
						best = new Test_mass_2(probes.get(y));
						hit=true;						
						System.out.println("hit!");
						
						x = pnum;
						y = probes.size();
						
						T = Integer.MAX_VALUE-10;
						i = repetitions;														
						
						break;
					}else {
						if(metric == 0) {				
							probes.get(y).update(distance);
						}else if(metric == 1){ //calculates energy per kg of probe (i.e. energy of a 1 kg probe)
							probes.get(y).update(-Motion.G*target_mass/(distance)+(planets[target_index].velocity.sub2(probes.get(y).velocity)).squmagnitude()/2);
						}

					}
						
				}

			}

		}

	}
	
	//resets planet positions for each round of optimization	
	public static void resetplanets() {
		for(int x = 0; x < pnum; x++) {
			planets[x].position.set(Main_class.elist.get(x).position);
			planets[x].velocity.set(Main_class.elist.get(x).velocity);
		}
	}
	
	//finds the probe with the closest approach to the target
	public static Test_mass_2 closest(ArrayList<Test_mass_2> t){		
		
		Test_mass_2 current_min = new Test_mass_2(); 

		for(Test_mass_2 m:t){
			if(m.minmetric < current_min.minmetric){		
				current_min = m;	//shallowcopying is okay here			
			}

		}

		return new Test_mass_2(current_min);
		
	}
	
	public static void add_best(Test_mass_2 t) {
		
		V3[] cartparams = Math_methods.esctrajectory(t.initorbit.x, t.initorbit.y, t.initplane.x, t.initplane.y, t.initplane.z);
		
		//System.out.println(cartparams[0].tostring() + "," + cartparams[1].tostring());		
		//System.out.println(init_index);
		
		Main_class.elist.add(
			new Entity(
				Double.MIN_VALUE,Double.MIN_VALUE,new V3(0,0,0),
				cartparams[0], cartparams[1],
				new V3(0,0,0),new V3(0,0,0),
				new Color(100,100,100),Poly_library.standard_poly_bases[0],Poly_library.standard_poly_maps[0],new V3(0,0,0),new V3(0,0,0),
				new Color(100,100,100),2000,.2, init_index, false
			)
		);
		
		Entity e = Main_class.elist.get(pnum); //pointer to the new entity representing the most successful probe
		
		e.primary = Main_class.elist.get(init_index);
		e.position.set(e.getabsolutevectors()[0]);
		e.velocity.set(e.getabsolutevectors()[1]);
		e.indexbuffer = -1;			
		e.t.shiftby(e.getabsolutevectors()[0]);
				
		hit = true;
		
	}
	

	
}
