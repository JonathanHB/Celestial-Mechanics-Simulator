package objects;

import physics.Trajectory_optimizer;
import utility.Main_class;
import utility.Math_methods;
import utility.V3;

public class Test_mass {
	
	public V3 initorbit; //parameters (longitude, inclination, phase) of initial orbit
	
	public V3 position; //physics properties for simulation
	public V3 velocity;
	
	public double minsquaredistance = Double.MAX_VALUE; 
	//tracks this object's closest approach to the target
	//squared distance is used instead of distance to
	//save computing power that would be involved in calculating sqrt
	
	public Test_mass(Entity e){ //used for table generation

		position = new V3(e.position);
		velocity = new V3(e.velocity);
	
		minsquaredistance = Double.MAX_VALUE;
		
	}
	
	public Test_mass(Test_mass t){ //deep copies another Test_mass
		
		position = new V3(t.position);
		velocity = new V3(t.velocity);

		initorbit = new V3(t.initorbit);
		
		minsquaredistance = t.minsquaredistance;
		
	}
	
	public Test_mass(){ //creates an empty Test_mass as a data buffer for the closest() method
		
		position = new V3();
		velocity = new V3();
		
		initorbit = new V3(0,0,0);
	
		minsquaredistance = Double.MAX_VALUE;
	}
	
	public Test_mass(Test_mass best_test, boolean b){ 
		//creates a test mass in a pseudorandom orbit; b is for constructor overloading
		//the orbit is in a distribution centered on the orbit of the argument best_test

		V3 orbit = new V3(Math.random(), Math.random(), Math.random()).sub2(new V3(.5,.5,.5)).scale2(2*Math.PI).add2(best_test.initorbit); 
		//parameters for a circular orbit of fixed radius; longitude, inclination, roll

		velocity = Math_methods.rotatepoint(new V3(0,0,Trajectory_optimizer.basevelocity), orbit).add2(Main_class.elist.get(Trajectory_optimizer.start).velocity);
		position = Math_methods.rotatepoint(new V3(0, Trajectory_optimizer.altitude+Main_class.elist.get(Trajectory_optimizer.start).radius, 0), orbit).add2(Main_class.elist.get(Trajectory_optimizer.start).position);

		initorbit = orbit;
		
		minsquaredistance = Double.MAX_VALUE;
		
	}
	
	public Test_mass(V3[] data){
		//creates a test mass in a pseudorandom orbit
		
		V3 orbit = Math_methods.randomangle().dimscale2(new V3(1,1,1).sub2(data[1])).add2(data[0]);
		//data[0] is the average best orbit, data[1] is the spread of the orbit distribution 
		//(see trajectory_optimizer for details on how this is generated
		
		velocity = Math_methods.rotatepoint(new V3(0,0,Trajectory_optimizer.basevelocity), orbit).add2(Main_class.elist.get(Trajectory_optimizer.start).velocity);
		position = Math_methods.rotatepoint(new V3(0, Trajectory_optimizer.altitude+Main_class.elist.get(Trajectory_optimizer.start).radius, 0), orbit).add2(Main_class.elist.get(Trajectory_optimizer.start).position);

		System.out.println(velocity.tostring());
		
		initorbit = orbit;
		
		minsquaredistance = Double.MAX_VALUE; //resets distance tracking variable
		
	}
	
	public void move(double d){ //moves Test_mass		
		position.add(velocity.scale2(d));
	}
	
	public void update_distance(double d){ //updates minsquaredistance
		if(d<minsquaredistance){
			minsquaredistance = d;
		}
	}
	
}
