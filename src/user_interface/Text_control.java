package user_interface;

import graphics.Frame_functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import utility.Main_class;

public class Text_control {
	
	static int state = 1; //loads by default
	
	public static void parsetext(String s){
		
		if(s.equals("exit")){ //closes program
			System.exit(0);
			
		}else if(s.equals("load")){ //prepares to load file
			
			state = 1;
			Frame_functions.inputfield.setText(null);
			Frame_functions.statefield.setText("loading from:");
			
		}else if(s.equals("save")){ //prepares to save file
			
			state = 2;
			Frame_functions.inputfield.setText(null);
			Frame_functions.statefield.setText("saving to:");
			
		}else{
			
			if(state == 1){ //loads file

				if(s.equals("default")){
					
					Main_class.running = false;
					Main_class.loading_internal = true;

				}else{
				
					Main_class.running = false;
					Main_class.loading = true;

					Main_class.loadstring = s;
				
				}
								
			}else if(state == 2){ //saves file
				
				FileIO.writefile(s);
				
			}
		}
		
	//	System.out.println(s);
	}
	
	public static void controlreader(){ //reads text input

		Frame_functions.inputfield.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent E){

				parsetext(Frame_functions.inputfield.getText());
				//System.out.println(Frame_functions.inputfield.getText());

			}

		});
	}
	
}
