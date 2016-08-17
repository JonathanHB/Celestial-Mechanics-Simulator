package user_interface;

import graphics.Frame_functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Text_control {
	
	public static void parsetext(String s){
		System.out.println(s);
	}
	
	public static void controlreader(){

		Frame_functions.inputfield.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent E){

				parsetext(Frame_functions.inputfield.getText());
				//System.out.println(Frame_functions.inputfield.getText());

			}

		});
	}
	
}
