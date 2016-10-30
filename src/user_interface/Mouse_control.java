package user_interface;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import utility.Math_methods;
import utility.V3;
import graphics.Graphics_engine;

public class Mouse_control implements MouseMotionListener{
	
	public static Point lastpoint = new Point();
	
	public static double sensitivity = .006; //mouse sensitivity
	
	@Override
	public void mouseDragged(MouseEvent arg0) { //syntactically required
		// TODO Auto-generated method stub	
	}

	@Override
	public void mouseMoved(MouseEvent arg0) { //syntactically required
		// TODO Auto-generated method stub
	}
		
	public static void mcontrolreader(){ //uses mouse to point camera

		graphics.Frame_functions.displayframe.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				lastpoint = new Point(arg0.getPoint());
			}
		});
		
		graphics.Frame_functions.displayframe.addMouseMotionListener(new MouseMotionListener(){

			public void mouseMoved(MouseEvent arg0) {
				
			}
			
			public void mouseDragged(MouseEvent arg0) {
				
				graphics.Graphics_engine.orientation.z-=sensitivity*(arg0.getPoint().x-lastpoint.x);
				graphics.Graphics_engine.orientation.y+=sensitivity*(arg0.getPoint().y-lastpoint.y);
								
				lastpoint.setLocation(arg0.getPoint());	//mouse's last position to compute movement			
				
			}

		});
	}

}
