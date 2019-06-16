package objects;

import physics.Motion;
import physics.Trajectory_optimizer;
import utility.Main_class;
import utility.Math_methods;
import utility.V3;

public class Test_mass_2 {
	
	public V3 initorbit; //semimajor axis, eccentricity, and true anomaly
	public V3 initplane; //argument of periapsis, inclination, longitude of ascending node
	public double launchtime; //s
	public int launchtick; //which tick to launch at; derived from launchtime
	
	public V3 position; //values for simulation
	public V3 velocity;
	
	public double minmetric = Double.MAX_VALUE; //actually the minimum metric value
	//tracks this object's closest approach to the target value of the metric
	//Distance: distance is already calculated instead of squared distance as part of gravitational calculation, so there is no reason to use the squared distance instead
	//Energy: tracks this object's minimum energy wrt target. Squaring it doesn't improve computational efficiency due to the kinetic energy term
	
	public Test_mass_2(Entity e){ //used for planet initialization

		position = new V3(e.position);
		velocity = new V3(e.velocity);
		
	}
	
	public Test_mass_2(Test_mass_2 t){ //deep copies another Test_mass
		
		position = new V3(t.position);
		velocity = new V3(t.velocity);

		initorbit = new V3(t.initorbit);
		initplane = new V3(t.initplane);
		launchtime = t.launchtime;
		launchtick = t.launchtick;
		
		minmetric = t.minmetric;
		
	}
	
	public Test_mass_2(){ //creates an empty Test_mass as a data buffer for the closest() method
		
		position = new V3();
		velocity = new V3();
		
		initorbit = new V3(0,0,0);
		initplane = new V3(0,0,0);
	
	}
	
	public Test_mass_2(V3 orbit, V3 plane, double t0, boolean[] whichparams, double[] ranges, Entity primary){ //generates a test mass in a weighted random orbit
		
		double variations[] = new double[] {0,0,0,0,0,0,0}; //how much to permute each initial value
		
		for(int i = 0; i<7; i++) {			
			if(whichparams[i]) {				
				variations[i] = (Math.random()-0.5)*ranges[i]; //generates permutations to add to values			
			}
		}		
						
		initorbit = new V3(orbit.add2(variations[0], variations[1], variations[2]));
		initplane = new V3(plane.add2(variations[3], variations[4], variations[5]));
		
		launchtime = t0 + variations[6];
		
		launchtick = (int) Math.round(launchtime/Motion.increment);
		
		V3[] cartparams = Math_methods.esctrajectory(initorbit.x, initorbit.y, initplane.x, initplane.y, initplane.z);
		
		position = cartparams[0];
		velocity = cartparams[1];
		
	}
	
	public Test_mass_2(Test_mass_2 t, boolean[] whichparams, double[] ranges, Entity primary){ //generates a test mass in a weighted random orbit
		
		double variations[] = new double[] {0,0,0,0,0,0,0}; //how much to permute each initial value
		
		for(int i = 0; i<7; i++) {			
			if(whichparams[i]) {				
				variations[i] = (Math.random()-0.5)*ranges[i]; //generates permutations to add to values			
			}
		}
		
		
		initorbit = new V3(t.initorbit.add2(variations[0], variations[1], variations[2]));
		initplane = new V3(t.initplane.add2(variations[3], variations[4], variations[5]));
		
		launchtime = t.launchtime + variations[6];
		
		launchtick = (int) Math.round(launchtime/Motion.increment);
		
		V3[] cartparams = Math_methods.esctrajectory(initorbit.x, initorbit.y, initplane.x, initplane.y, initplane.z);
		
				
		position = cartparams[0];
		velocity = cartparams[1];
		
	}
	
	public void move(double d){ //moves Test_mass		
		position.add(velocity.scale2(d));
	}
	
	public void update(double d){ //updates mindistance
		if(d<minmetric){
			minmetric = d;
		}

	}
	
}
