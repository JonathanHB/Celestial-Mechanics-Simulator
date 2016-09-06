package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Scanner;
import java.util.Random;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import objects.Render_obj;
import utility.Main_class;
import utility.V3;
import utility.Misc_methods;
import physics.Motion;
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
	
	public static Color background = new Color(0,0,0); //paint component runs once initially; this prevents nullpointers
	
	public static void frame_setup(){ //sets up jframe window
		
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
		
	}
		
	public void paintComponent(Graphics g){
		
		super.paintComponent(g);
		
		g.setColor(background);
		g.fillRect(0, 0, 800, 800); //colors background
		
		for(int x = Graphics_engine.order2.size()-1; x >= 0; x--){					
			
			Render_obj object = Graphics_engine.order2.get(x);
			
			if(object.plane){
				
				int n = object.p.corners.length;
				
				int xpoints[] = new int[n];
				int ypoints[] = new int[n];

				for(int z = 0; z < n; z++){
					xpoints[z] = object.p.corners[z].projectx;
					ypoints[z] = object.p.corners[z].projecty;
				}

				g.setColor(illuminate(object.p.illumination, object.p.c));				
				g.fillPolygon(xpoints, ypoints, n);
				
			}else{
				
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
					
			}
			
		}
			
	}	

	public Color illuminate(V3 light, Color c){
		return new Color(
				Misc_methods.capcolor(c.getRed()*light.x), 
				Misc_methods.capcolor(c.getGreen()*light.y), 
				Misc_methods.capcolor(c.getBlue()*light.z)
				);
		
	}
		
}


//from paintcomponent
//this code works nicely with small repetition values but covers the screen in bars for large ones
		/*
		int simtime = (int) Math.round(Motion.repetition*Motion.increment); //number of seconds simulated per frame
		
		g.setColor(Color.BLACK);		
		g.fillRect(8, 8, 5, Math.abs(simtime)+4);
		
		Color repgauge;
		
		if(Math.signum(simtime)>0){
			repgauge = Color.RED;
		}else{
			repgauge = Color.BLUE;
		}
		
		int count = Math.abs(simtime);
		int shift = 10;
		
		while(count > 0){
			if(count >= height-20){
				g.setColor(Color.BLACK);		
				g.fillRect(shift, 8, 5, height-16);
				
				g.setColor(repgauge);		
				g.fillRect(shift, 10, 3, height-20);
				
				shift+=4;
				count -= height-20;

			}else{
				
				g.setColor(Color.BLACK);		
				g.fillRect(shift, 8, 5, count+4);
				
				g.setColor(repgauge);		
				g.fillRect(shift, 10, 3, count);
				
				count = 0;
				
			}
		}*/
