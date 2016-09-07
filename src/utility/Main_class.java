package utility;

//----------package name------------------------

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

//----------external imports--------------------

import objects.Cable;
import objects.Entity;
import objects.Poly_library;
import objects.Test_mass;
import physics.Object_manager;
import physics.Motion;
import physics.Trajectory_optimizer;
import graphics.Frame_functions;
import graphics.Graphics_engine;
import user_interface.FileIO;
import user_interface.Text_control;
import user_interface.Mouse_control;

//----------internal imports--------------------

@SuppressWarnings("serial")
public class Main_class extends JPanel{
		
	public static double time = 0; //net number of seconds since the engine started
	public static double runtime = 0; //total number of seconds simulated 
		
	public static ArrayList<Entity> elist = new ArrayList<Entity>(0);

	public static ArrayList<Cable> clist = new ArrayList<Cable>(0);
	
	public static int framewait;
	
	public static boolean running;
	
	public static boolean loading;
	public static boolean saving;
	
	public static boolean loading_internal;
	
	public static String loadstring;

	static int siginc;
	
	public static void main(String[] args) {						
		
		Poly_library.setup();
		FileIO.setup();
		
		Frame_functions.frame_setup();
		
		Text_control.controlreader();
		Mouse_control.mcontrolreader();		
		
		running = false;
		
		while(true){
		
			while(running){
				Main_class.runiteration();
			}
			
			System.out.print(""); //keeps while loop from timing out and breaking
			
			if(loading){
				startsimulation(loadstring);
				running = true;
				loading = false;
			}else if(loading_internal){
				startsim2();
				running = true;
				loading_internal = false;
			}
			
		}
					
	}
	
	public static void startsimulation(String s){
		
		FileIO.readfile(s);
		
		Object_manager.fixcenter();	
							
		if(Trajectory_optimizer.running){		
			Trajectory_optimizer.maketables();
			Trajectory_optimizer.optimize();
		}	
		
		siginc = Misc_methods.sigdigs(Motion.increment);
		
		Frame_functions.timefield.setText("0.0 seconds");
		
	}
	
	public static void startsim2(){
		
		FileIO.loadtest(FileIO.test_input);
		
		Object_manager.fixcenter();	
							
		if(Trajectory_optimizer.running){		
			Trajectory_optimizer.maketables();
			Trajectory_optimizer.optimize();
		}	
		
		siginc = Misc_methods.sigdigs(Motion.increment);
		
		Frame_functions.timefield.setText("0.0 seconds"); //adjust this sometime to match siginc
		
	}
	
	public static void runiteration(){

		if(Main_class.elist.size() != 0){

			Motion.physexec();
			Graphics_engine.projector();
			Graphics_engine.lighting();
			Graphics_engine.setorder();			
			Frame_functions.displayframe.repaint();


			if(Motion.increment !=0 && Motion.repetition !=0){
				time += Motion.increment*Math.abs(Motion.repetition);
				runtime += Math.abs(Motion.increment*Motion.repetition);
				Frame_functions.timefield.setText(Misc_methods.roundto(time, siginc) + " seconds");
			//	System.out.println(time);
			}

			try {
				TimeUnit.MILLISECONDS.sleep(framewait);
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
 * fix cable physics
 * 
 * improve movement and axis angle controls(base axis angle on location relative to barycenter, or eliminate it)
 * 
 * port piloting simulation?
 * add heating simulation(via lighting engine) and lighting engine shadows
 * better collision detection with polyhedra rather than spheres
 * 
 * reduce precession of periapsis without excessive computational cost?
 * better lighting model based on spectral power distribution?
 * 
 * clean and comment code
 * 
 * 
 * 
 */
