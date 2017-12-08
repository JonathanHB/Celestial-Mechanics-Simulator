package user_interface;

import graphics.Frame_functions;
import graphics.Graphics_engine;
import objects.Entity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import physics.Motion;
import utility.Main_class;
import utility.V3;

public class Control_panel {

	static String input = "";

	
	public static void controlreader(){ //reads text input

		Frame_functions.inputfield.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent E){

				input = Frame_functions.inputfield.getText();
				//System.out.println(Frame_functions.inputfield.getText());

			}

		});
		
		Frame_functions.camspeed.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent E){

				String t = Frame_functions.camspeed.getText();
				
				if(t.matches("\\d+|\\d+.\\d+|.\\d+|-\\d+|-\\d+.\\d+|-.\\d+")){
				
					Key_control.tsensitivity = Double.parseDouble(t);
					
				}else{
					Frame_functions.camspeed.setText(Double.toString(Key_control.tsensitivity));
				}
				//System.out.println(Frame_functions.inputfield.getText());

			}

		});
		
		Frame_functions.simspeed.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent E){

				String t = Frame_functions.simspeed.getText();
				
				if(t.matches("\\d+|-\\d+")){
										
					Motion.repbuff = Integer.parseInt(t);
					
				}else{
					Frame_functions.simspeed.setText(Integer.toString(Motion.repbuff));
				}
				//System.out.println(Frame_functions.inputfield.getText());

			}

		});
		
		Frame_functions.primary.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent E){

				String t = Frame_functions.primary.getText();
				
				if(t.matches("\\d+|-\\d+")){
										
					int f = Integer.parseInt(t);
					
					if(f > Main_class.elist.size()-1){
						Frame_functions.primary.setText("index too large; must be below" + Main_class.elist.size());
					}else {
						if(f < 0) {
							f = -1;
							
							Graphics_engine.focus = new Entity();
							Graphics_engine.focus.position = new V3(0,0,0);
							Graphics_engine.focus.velocity = new V3(0,0,0);
							
						}else {
							
							Graphics_engine.focus = Main_class.elist.get(f);
							
						}
						
						Graphics_engine.viewposition.set(Graphics_engine.focus.position);
						Graphics_engine.orientation.set(new V3(0,.77,0));
						
						Graphics_engine.focusindex = f;
						
						
						
					}
					
					
					
					
					
				}else{
					Frame_functions.primary.setText(Integer.toString(Motion.repbuff));
				}
				//System.out.println(Frame_functions.inputfield.getText());

			}

		});

		Frame_functions.load.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent E){

				Main_class.running = false;
				Main_class.loading = true;

			}

		});
		
		Frame_functions.reload.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent E){

				Main_class.running = false;
				Main_class.reloading = true;
				Main_class.loading = true;

			}

		});
		
		Frame_functions.save.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent E){

				FileIO.writefile(FileIO.getfilepath()); //runs without affecting simulation flow

			}

		});
		
	}
	
}
