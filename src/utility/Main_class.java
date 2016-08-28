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
	
	public static void main(String[] args) {
		
		Poly_library.setup();		
		
//		FileIO.writefile(user_interface.FileIO.outputfile);
		FileIO.readfile(user_interface.FileIO.inputfile);
		
		Object_manager.fixcenter();	
				
		Frame_functions.frame_setup();	
		
		Text_control.controlreader();
		Mouse_control.mcontrolreader();		
		
		if(Trajectory_optimizer.running){		
			Trajectory_optimizer.maketables();
			Trajectory_optimizer.optimize();
		}	
		
	//	elist.get(2).t.refent=0;
	//	elist.get(1).t.refent=0;
		
		for(int i=0;i<600000000;i++){						
			
			Motion.physexec();
			Graphics_engine.projector();
			Graphics_engine.lighting();
			Graphics_engine.setorder();			
			Frame_functions.displayframe.repaint();
						
			if(Motion.increment !=0 && Motion.repetition !=0){
				time += Motion.increment*Math.abs(Motion.repetition);
				runtime += Math.abs(Motion.increment*Motion.repetition);
				System.out.println(time);
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
 * port tidal forces and cable simulation (these should account for & influence rotation)
 * 
 * port trajectory optimizer
 * 
 * improve physics/graphics engine efficiency(store entities in arraylist & index (un)seen objects to remove if statements)
 * 
 * improve mouse control
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