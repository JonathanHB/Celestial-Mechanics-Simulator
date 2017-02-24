package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import objects.Render_obj;
import utility.Main_class;
import utility.V3;
import utility.Misc_methods;
import physics.Trajectory_optimizer;

public class Frame_functions extends JPanel{

	public static JFrame displayframe = new JFrame("OMS 2.0"); //display window	
	static JFrame inputbox = new JFrame("Enter load/save commands"); //control window
	public static final JTextField inputfield = new JTextField(); //part of control window
	public static final JTextField statefield = new JTextField(); //part of control window
	public static final JTextField timefield = new JTextField(); //prints simulation time
	
	static int height = 800; 
	static int width = 800;
	
	static int height2 = 80; //this value behaves oddly
	static int width2 = 200;
	
	public static Color background = new Color(0,0,0); //paint component runs once initially; this assignment prevents nullpointers
	
	public static void frame_setup(){ //sets up jframe windows
		
		statefield.setEditable(false);
		timefield.setEditable(false);
		
		displayframe.setSize(width, height);
		displayframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Frame_functions panel = new Frame_functions();
		
		displayframe.addKeyListener(new user_interface.Key_control());
		displayframe.addMouseMotionListener(new user_interface.Mouse_control());
		
		displayframe.getContentPane().add(panel, BorderLayout.CENTER);
		
		displayframe.setVisible(true);
					 		
		inputbox.setSize(width2, height2);
		inputbox.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		inputbox.add(timefield, BorderLayout.NORTH);		
		inputbox.add(statefield, BorderLayout.CENTER);
		inputbox.add(inputfield, BorderLayout.SOUTH);
	
		inputbox.setVisible(true); 
		
		Frame_functions.statefield.setText("loading from:");
		
	}
		
	public void paintComponent(Graphics g){ 
		//draws lines and planes from render_obj objects in order[] arraylist to jframe
		
		super.paintComponent(g);
		
		g.setColor(background);
		g.fillRect(0, 0, 800, 800); //colors background
		
		for(int x = Graphics_engine.order2.size()-1; x >= 0; x--){				
			
			Render_obj object = Graphics_engine.order2.get(x);
			
			if(object.plane && !object.point){ //renders planes
				
				int n = object.p.corners.length;
				
				int xpoints[] = new int[n];
				int ypoints[] = new int[n];

				for(int z = 0; z < n; z++){
					xpoints[z] = object.p.corners[z].projectx;
					ypoints[z] = object.p.corners[z].projecty;
				}

				g.setColor(illuminate(object.p.illumination, object.p.c));				
				g.fillPolygon(xpoints, ypoints, n);
				
				g.setColor(Color.BLUE);
				
				if(Main_class.edges){
				
					for(int z = 0; z < n-1; z++){
						g.drawLine(xpoints[z], ypoints[z], xpoints[z+1], ypoints[z+1]);
					}
					g.drawLine(xpoints[0], ypoints[0], xpoints[n-1], ypoints[n-1]);
					
				}	
				
			}else if(!object.plane && !object.point){ //renders lines
				
				g.setColor(illuminate(object.l.illumination, object.l.c));				
				
				if(Trajectory_optimizer.running){
					g.setColor(
							new Color(
							(int) Math.round(155*object.l.time/Main_class.runtime),
							(int) Math.round(155*object.l.time/Main_class.runtime),
							(int) Math.round(255*object.l.time/Main_class.runtime)
							)
							);
				}
				
				g.drawLine(
						object.l.p1.projectx,
						object.l.p1.projecty,
						object.l.p2.projectx,
						object.l.p2.projecty
						);
					
			}else{ //renders points
				
				g.setColor(Color.BLUE);
				g.drawOval(object.po.projectx, object.po.projecty, 1, 1);
			}
			
		}
			
	}	

	public Color illuminate(V3 light, Color c){ 
		//scales an object's intrinsic color by the color and intensity of light on it
		return new Color(
				Misc_methods.capcolor(c.getRed()*light.x), 
				Misc_methods.capcolor(c.getGreen()*light.y), 
				Misc_methods.capcolor(c.getBlue()*light.z)
				);
		
	}
		
}
