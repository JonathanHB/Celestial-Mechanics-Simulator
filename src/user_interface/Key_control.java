package user_interface;

import graphics.Frame_functions;
import graphics.Graphics_engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import physics.Motion;

public class Key_control implements KeyListener{

	public static double tsensitivity; //translational	sensitivity
	static double rsensitivity = .05; //rotational sensitivity

	static int phys_rep_inc; //increment of physics engine repetition adjustment
//	static int lastnonzero = 1;
	
	
	static double mx; //unused
	static double my;

//	static int lastrep = 0;

	public static void keyparser(KeyEvent e){ //controls camera movement with keyboard input
		
		char c = e.getKeyChar();

		//makes camera's lateral movement slow down when it points up or down				
		double cosy_scaling = tsensitivity*Math.abs(Math.cos(Graphics_engine.orientation.y));
		
		//computes magnitudes of lateral x and y movement
		double cosz = Math.cos(Graphics_engine.orientation.z)*cosy_scaling;
		double sinz = Math.sin(Graphics_engine.orientation.z)*cosy_scaling;
		
		if(c=='a'){
			Graphics_engine.viewposition.x-=sinz;
			Graphics_engine.viewposition.y-=cosz;
		}else if(c=='d'){
			Graphics_engine.viewposition.x+=sinz;
			Graphics_engine.viewposition.y+=cosz;
		}else if(c=='s'){
			Graphics_engine.viewposition.x-=cosz;
			Graphics_engine.viewposition.y+=sinz;
		}else if(c=='w'){
			Graphics_engine.viewposition.x+=cosz;
			Graphics_engine.viewposition.y-=sinz;
		}else if(c=='q'){
			Graphics_engine.viewposition.z-=tsensitivity;
		}else if(c=='e'){
			Graphics_engine.viewposition.z+=tsensitivity;
		}
		
		if(c=='c'){ //speeds up simulation

		//	reversal(lastrep, Motion.repbuff, Motion.repbuff+phys_rep_inc);
		//	lastrep = Motion.repbuff;
			Motion.repbuff+=phys_rep_inc;
			Frame_functions.simspeed.setText(Integer.toString(Motion.repbuff));

		}else if(c=='v'){ 
			//slows down simulation (including reversing it and increasing the magnitude of backwards simulation)

		//	reversal(lastrep, Motion.repbuff, Motion.repbuff-phys_rep_inc);
		//	lastrep = Motion.repbuff;
			Motion.repbuff-=phys_rep_inc;
			Frame_functions.simspeed.setText(Integer.toString(Motion.repbuff));

		}else if(c=='b'){ //pauses simulation
		//	lastrep = Motion.repbuff;
			Motion.repbuff = 0;
			Frame_functions.simspeed.setText("0.0");
		}

		
		
	}

/*	public static void reversal(int a, int b, int c){ //a = previous repetition rate, b = current rate, c = new rate
		//checks to reverse physics engine when repetition changes sign
		//also called when the repetition rate is reversed by text entry
		
		if(b*c<0 || (b == 0 && a*c<0) || (a == 0 && b == 0 && c<0)){
			Motion.incbuff*=-1;
			Motion.flipping = true;
		}

	}*/

	@Override
	public void keyTyped(KeyEvent arg0) {} //syntactically required

	@Override
	public void keyPressed(KeyEvent arg0) { //reads keyboard input
		keyparser(arg0);
	}
	@Override
	public void keyReleased(KeyEvent arg0) {} //syntactically required

}
