package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JButton;
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
	public static final JTextField inputfield = new JTextField(); //part of control window //probably deprecated
	public static final JTextField statefield = new JTextField(); //part of control window //probably deprecated
	public static final JTextField timefield = new JTextField(); //prints simulation time
	
	public static JButton save = new JButton("save");
	public static JButton load = new JButton("load");
	public static JButton reload = new JButton("reload");
	//JButton save = new JButton("save");
	
	public static final JTextField camspeed = new JTextField(); //part of control window
	public static final JTextField simspeed = new JTextField(); //part of control window
	public static final JTextField primary = new JTextField(); //part of control window


	
	static int height = 1000; //these change over time to track window size
	static int width = 1900;
	
	static int ctrh = height/2; //used by graphics engine
	static int ctrw = width/2;
	
	static int height2 = 200; //this value behaves oddly
	static int width2 = 200;
	
	public static Color background = new Color(0,0,0); //paint component runs once initially; this assignment prevents nullpointers
	
	public static void frame_setup(){ //sets up jframe windows
		
		statefield.setEditable(false);
		timefield.setEditable(false);
		
		displayframe.setSize(width, height);
		displayframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		displayframe.addKeyListener(new user_interface.Key_control());
		displayframe.addMouseMotionListener(new user_interface.Mouse_control());
	    displayframe.addComponentListener(new Resizehandler());

		
		displayframe.getContentPane().add(new Frame_functions(), BorderLayout.CENTER); //actually displays graphics
		
		displayframe.setVisible(true);
		
		//-----------------------------------------------------------------------------------
		
		inputbox.setSize(width2, height2);
		inputbox.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		inputbox.setLayout(null);
		
		load.setBounds(0,20,70,30);
		inputbox.add(load);
		save.setBounds(0,50,70,30);
		inputbox.add(save);
		reload.setBounds(0,80,70,30);
		inputbox.add(reload);
		camspeed.setBounds(70,20,70,30);
		inputbox.add(camspeed);
		simspeed.setBounds(70,50,70,30);
		inputbox.add(simspeed);
		primary.setBounds(70,80,70,30);
		inputbox.add(primary);
		
		inputbox.setLayout(new BorderLayout());
		
		//inputbox.add(statefield, BorderLayout.CENTER);
		inputbox.add(timefield, BorderLayout.NORTH);
		inputbox.add(inputfield, BorderLayout.SOUTH);
	
		inputbox.setVisible(true); 
		
		//Frame_functions.statefield.setText("loading from:");
		
	}
		
	public void paintComponent(Graphics g){ 
		//draws lines and planes from render_obj objects in order[] arraylist to jframe
		
		super.paintComponent(g);
		
		g.setColor(background);
		g.fillRect(0, 0, 1900, 1000); //colors background //TODO set rect dimensions to window size
		
		//System.out.println(Graphics_engine.order2.size()-1);
		for(int x = Graphics_engine.order2.size()-1; x >= 0; x--){				
			
			
			Render_obj robject = Graphics_engine.order2.get(x);
			
			//this throws a bizarre and irrational mix of null pointer and index out of bounds errors if the framewait is less than about 20ms
			//these errors aren't susceptible to usuall if/else checks for object existence and arraylist length
			//this limitation is likely based on the rendering time and probably cannot be avoided in the current rendering systen
			//20 ms has occasional errors, 10 ms constant errors, 30 is quite reliable
			
			if(robject.plane && !robject.point){ //renders planes
				
				int n = robject.p.corners.length;
				
				int xpoints[] = new int[n];
				int ypoints[] = new int[n];

				for(int z = 0; z < n; z++){
					xpoints[z] = robject.p.corners[z].projectx;
					ypoints[z] = robject.p.corners[z].projecty;
				}

				g.setColor(illuminate(robject.p.illumination, robject.p.c));				
				g.fillPolygon(xpoints, ypoints, n);
				
				g.setColor(Color.BLUE);
				
				if(Main_class.edges){
				
					for(int z = 0; z < n-1; z++){
						g.drawLine(xpoints[z], ypoints[z], xpoints[z+1], ypoints[z+1]);
					}
					g.drawLine(xpoints[0], ypoints[0], xpoints[n-1], ypoints[n-1]);
					
				}	
				
			}else if(!robject.plane && !robject.point){ //renders lines
				
				g.setColor(illuminate(robject.l.illumination, robject.l.c));				
				
				if(Main_class.traject_opt){
					g.setColor(
							new Color(
							(int) Math.round(155*robject.l.time/Main_class.runtime),
							(int) Math.round(155*robject.l.time/Main_class.runtime),
							(int) Math.round(255*robject.l.time/Main_class.runtime)
							)
							);
				}
				
				g.drawLine(
						robject.l.p1.projectx,
						robject.l.p1.projecty,
						robject.l.p2.projectx,
						robject.l.p2.projecty
						);
					
			}else{ //renders points
				
				g.setColor(Color.BLUE);
				g.drawOval(robject.po.projectx, robject.po.projecty, 1, 1);
			}
			
		}
			
//		}
			
//		}
			
	}	

	public Color illuminate(V3 light, Color c){ 
		//scales an object's color by the color and intensity of light on it to get the color of the reflected light
		return new Color(
				Misc_methods.capcolor(c.getRed()*light.x), 
				Misc_methods.capcolor(c.getGreen()*light.y), 
				Misc_methods.capcolor(c.getBlue()*light.z)
				);
		
	}
		
}
