package user_interface;

import graphics.Frame_functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import utility.Main_class;

public class Control_panel {

	static String input = "";
	
	public static void controlreader(){ //reads text input

		Frame_functions.inputfield.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent E){

				input = Frame_functions.inputfield.getText();
				//System.out.println(Frame_functions.inputfield.getText());

			}

		});

		Frame_functions.load.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent E){

				Main_class.running = false;
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
