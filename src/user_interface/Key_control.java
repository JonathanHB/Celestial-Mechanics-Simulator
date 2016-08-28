package user_interface;

import graphics.Frame_functions;
import graphics.Graphics_engine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import physics.Motion;
import utility.Math_methods;
import utility.V3;

public class Key_control implements KeyListener{

	static double tsensitivity; //translational	
	static double rsensitivity=.05; //rotational

//	static double phys_inc_inc=.1; //increment of physics engine incbuff adjustment
	static int phys_rep_inc; //increment of physics engine repbuff adjustment

	static double mx; //unused
	static double my;

	static int lastrep = 0;

	public static void keyparser(KeyEvent e){
		char c = e.getKeyChar();
		//System.out.println(e.getKeyCode());
		//find a decent data structure to use for this method rather than elif chains
		
		if(c=='a'){
			Graphics_engine.viewposition.x-=tsensitivity*Math.sin(Graphics_engine.orientation.z);
			Graphics_engine.viewposition.y-=tsensitivity*Math.cos(Graphics_engine.orientation.z);//.sub(Math_methods.rotatepoint(new V3(tsensitivity,0,0), Graphics_engine.orientation));
		}else if(c=='d'){
			Graphics_engine.viewposition.x+=tsensitivity*Math.sin(Graphics_engine.orientation.z);
			Graphics_engine.viewposition.y+=tsensitivity*Math.cos(Graphics_engine.orientation.z);//.add(Math_methods.rotatepoint(new V3(tsensitivity,0,0), Graphics_engine.orientation));
		}else if(c=='s'){
			Graphics_engine.viewposition.x-=tsensitivity*Math.cos(Graphics_engine.orientation.z);
			Graphics_engine.viewposition.y+=tsensitivity*Math.sin(Graphics_engine.orientation.z);//.sub(Math_methods.rotatepoint(new V3(0,tsensitivity,0), Graphics_engine.orientation));
		}else if(c=='w'){
			Graphics_engine.viewposition.x+=tsensitivity*Math.cos(Graphics_engine.orientation.z);
			Graphics_engine.viewposition.y-=tsensitivity*Math.sin(Graphics_engine.orientation.z);//.add(Math_methods.rotatepoint(new V3(0,tsensitivity,0), Graphics_engine.orientation));
		}else if(c=='q'){
			Graphics_engine.viewposition.z-=tsensitivity;//.sub(Math_methods.rotatepoint(new V3(0,0,tsensitivity), Graphics_engine.orientation));
		}else if(c=='e'){
			Graphics_engine.viewposition.z+=tsensitivity;//.add(Math_methods.rotatepoint(new V3(0,0,tsensitivity), Graphics_engine.orientation));
		}

/*		if(c=='z'){ //code for altering physics increment, something which should be automatic or fixed
			Motion.incbuff+=phys_inc_inc;
		}else if(c=='x'){
			if(Motion.incbuff>=phys_inc_inc){
				Motion.incbuff-=phys_inc_inc;
			}
		}else*/ 
		
		if(c=='c'){

			reversal(lastrep, Motion.repbuff, Motion.repbuff+phys_rep_inc);
			lastrep = Motion.repbuff;
			Motion.repbuff+=phys_rep_inc;	

		}else if(c=='v'){

			reversal(lastrep, Motion.repbuff, Motion.repbuff-phys_rep_inc);
			lastrep = Motion.repbuff;
			Motion.repbuff-=phys_rep_inc;	

		}else if(c=='b'){
			lastrep = Motion.repbuff;
			Motion.repbuff = 0;
		}

	}

	public static void reversal(int a, int b, int c){ //a = previous repetition rate, b = current rate, c = new rate

		if(b*c<0){
			Motion.incbuff*=-1;
			Motion.flipping = true;
		
		}else if(b == 0 && a*c<0){
			Motion.incbuff*=-1;
			Motion.flipping = true;
		
		}else if(a == 0 && b == 0 && c<0){
			Motion.incbuff*=-1;
			Motion.flipping = true;
		
		}


	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		keyparser(arg0);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {}
	@Override
	public void keyReleased(KeyEvent arg0) {}

}
