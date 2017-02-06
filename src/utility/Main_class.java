package utility;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import objects.Cable;
import objects.Entity;
import objects.Point;
import objects.Poly_library;
import physics.Object_manager;
import physics.Motion;
import physics.Trajectory_optimizer;
import graphics.Equipotential_viz;
import graphics.Frame_functions;
import graphics.Graphics_engine;
import user_interface.FileIO;
import user_interface.Text_control;
import user_interface.Mouse_control;

@SuppressWarnings("serial")
public class Main_class extends JPanel{
		
	public static double time = 0; //net number of seconds simulated; physical time in simulation
	public static double runtime = 0; //total number of seconds simulated; positive and negative time are added
	//time variables reset when the simulation restarts	
	
	static int siginc; 
	//the number of significant digits of the physics simulation's time increment,
	//used to truncate floating point errors from the simulation time for display
	
	public static ArrayList<Point> equipotential = new ArrayList<Point>(0); //equipotential surface points
	
	public static ArrayList<Entity> elist = new ArrayList<Entity>(0); //All planets/stars/moons

	public static ArrayList<Cable> clist = new ArrayList<Cable>(0); //Space elevator cables
	
	public static int framewait; //time wait in milliseconds between rendering frames
	
	public static boolean stressvisualization = true;
	public static boolean fixedreferences = true;
	public static boolean equipotentialviz = true;
	
	public static boolean running; 
	public static boolean loading; 	//state variables used for loading, running, and saving	simulations
	public static boolean saving;
	
	public static boolean loading_internal; //whether the simulation is the hardcoded demo or a text file
	
	public static String loadstring; //location of input data file
		
	public static void main(String[] args) {						
		
		//one time setup and initialization of various objects and variables
		Poly_library.setup();
		FileIO.setup();
		
		Frame_functions.frame_setup();
		
		Text_control.controlreader();
		Mouse_control.mcontrolreader();		
		
		running = false; //ensures that the simulation waits for something to load before running
		
		
		while(true){ //indefinite loop, terminates only on exit command or program window closure
		
			while(running){
				Main_class.runiteration(); //runs the physics and graphics engines
			}
			
			System.out.print(""); //keeps while loop from timing out and breaking
			
			if(loading){ //loads simulation from a file specified by the variable loadstring
				startsimulation(loadstring);
				running = true;
				loading = false;
			}
			
		}
					
	}
	
	public static void startsimulation(String s){ //loads simulation from file and sets it up

		FileIO.loadfile(FileIO.readfile(s));
		
		Object_manager.initializerefs();
		
		Object_manager.fixcenter();
							
		if(Trajectory_optimizer.running){ //runs trajectory optimizer	
			Trajectory_optimizer.maketables();
			Trajectory_optimizer.optimize();
		}	
		
		siginc = Misc_methods.sigdigs(Motion.increment); 
		
		Frame_functions.timefield.setText("0.0 seconds");
		
	}
	
	public static void runiteration(){ //runs the physics and graphics engines

		if(Main_class.elist.size() != 0){ //checks for empty simulation

			Motion.physexec(); //physics engine
			
			Graphics_engine.getfocus();
			Equipotential_viz.generatesurface();
			
			Graphics_engine.projector();
			Graphics_engine.lighting();   //graphics engine
			Graphics_engine.setorder();			
			
			Frame_functions.displayframe.repaint(); //updates jframe


			if(Motion.increment !=0 && Motion.repetition !=0){ //checks that simulation isn't paused
				time += Motion.increment*Math.abs(Motion.repetition);      //update time and runtime
				runtime += Math.abs(Motion.increment*Motion.repetition); 
				Frame_functions.timefield.setText(Misc_methods.roundto(time, siginc) + " seconds"); //display the time
			}

			try {
				TimeUnit.MILLISECONDS.sleep(framewait); //wait to control simulation speed
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();					
			}

		}

	}

}

//TODO:
/*
 * 
 * 
 * 
 * port tidal forces (these should account for & influence rotation)
 * 
 * 
 * improve movement and axis angle controls(base axis angle on location relative to barycenter, or eliminate it)
 * 
 * port piloting simulation?
 * add heating simulation(via lighting engine) and lighting engine shadows
 * better collision detection with polyhedra rather than spheres 
 * so that graphical representations of entities are physically meaningful?
 * 
 * reduce precession of periapsis without excessive computational cost?
 * better lighting model based on spectral power distribution?
 * 
 * clean and comment code
 * 
 * 
 *\/home/jonathan/workspace/CMS_2.0/cabledemo.txt
 *\/home/jonathan/workspace/CMS_2.0/basicdemo.txt
 *\/home/jonathan/workspace/CMS_2.0/Earth_Moon.txt
 *\/home/jonathan/Desktop/binary_stars.txt
 */
