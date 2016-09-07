package user_interface;


import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import objects.Cable;
import objects.Entity;
import objects.Poly_library;
import utility.V3;
import utility.Main_class;
import graphics.Frame_functions;
import graphics.Graphics_engine;
import physics.Motion;
import physics.Trajectory_optimizer;

public class FileIO {	

	static PrintWriter writer; //declared outside for fillfile and writefile

	public static String[] test_input;
	
	public static void setup(){
		FileIO.test_input = new String[]{
				"30",
				"[0;0;0]",
				".01",
				"1",
				"1",
				"200000000000.0,1,[160;160;160],[0;0;0],[0;0;0],[0;0;0],[0;0;0],[20;30;60],cube,cube,[1;1;1],[0;0;0],[30;50;40],10,1",
				"1000000000.0,.6,[0;0;0],[10;0;.5],[0;1.1;0],[0;0;0],[0;.2;0],[20;70;30],cube,cube,[.5;.5;.7],[0;0;0],[50;30;40],600,1",
				"1000000000.0,.6,[0;0;0],[30;0;.5],[0;.6;-.1],[0;0;0],[.2;0;0],[120;70;30],cube,cube,[.5;.5;.5],[0;0;0],[20;50;90],1100,1",
				"100000000.0,.3,[0;0;0],[0;20;0],[.6;0;-.2],[0;.77;0],[.2;0;.3],[60;70;130],cube,cube,[.2;.2;.2],[0;0;0],[90;50;20],1100,1"
		};
	}
	
	public static void readfile(String filename){ //reads in text files to generate objects

		try { 
			
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			Main_class.elist.clear();
			Main_class.clist.clear();
			
			Motion.repbuff=0;
			Motion.repetition=0;
									
			Main_class.framewait=Integer.parseInt(br.readLine());
			
			Frame_functions.background = Parsingmethods.parsecolor(br.readLine());
			
			Motion.increment=Double.parseDouble(br.readLine());
			Motion.incbuff=Motion.increment;
			
			Key_control.phys_rep_inc=Integer.parseInt(br.readLine());
			Key_control.tsensitivity=Double.parseDouble(br.readLine());
										
			boolean reading = true;
			boolean ents = true;

			
			while(reading){		
				String s = br.readLine();
				if (s!=null){					
					if(ents){	
						if(s.equals("cables")){
							ents = false;
						}else{
						parseent(s);
						}
					}else{
						parsecable(s);
					}										
				}else{
					reading = false;
				}
			}
			
			if(Graphics_engine.focus!=-1){
				Graphics_engine.viewposition.set(Main_class.elist.get(Graphics_engine.focus).position);
			}
									
			br.close();
			
			Frame_functions.statefield.setText(filename + " loaded");
			Frame_functions.inputfield.setText(null);

		}catch (IOException e) {

			Frame_functions.statefield.setText("Error: " + e);			
			//System.err.println("Error: " + e);

		}		
		
	}
	
	public static void loadtest(String[] inputdata){ //reads in text files to generate objects

	//	try { 
			
		//	BufferedReader br = new BufferedReader(new FileReader(filename));
			
			Main_class.elist.clear();
			Main_class.clist.clear();
			
			Motion.repbuff=0;
			Motion.repetition=0;
									
			Main_class.framewait=Integer.parseInt(inputdata[0]);
			
			Frame_functions.background = Parsingmethods.parsecolor(inputdata[1]);
			
			Motion.increment=Double.parseDouble(inputdata[2]);
			Motion.incbuff=Motion.increment;
			
			Key_control.phys_rep_inc=Integer.parseInt(inputdata[3]);
			Key_control.tsensitivity=Double.parseDouble(inputdata[4]);
										
			boolean reading = true;
			boolean ents = true;

			
			for(int x = 5; x<inputdata.length; x++){		
			//	String s = br.readLine();
				//if (s!=null){					
					if(ents){	
						if(inputdata[x].equals("cables")){
							ents = false;
						}else{
						parseent(inputdata[x]);
						}
					}else{
						parsecable(inputdata[x]);
					}										
			//	}else{
			//		reading = false;
			//	}
			}
			
			if(Graphics_engine.focus!=-1){
				Graphics_engine.viewposition.set(Main_class.elist.get(Graphics_engine.focus).position);
			}
									
		//	br.close();
			
			Frame_functions.statefield.setText("test_input loaded");
			Frame_functions.inputfield.setText(null);

	//	}catch (IOException e) {

	//		Frame_functions.statefield.setText("Error: " + e);			
			//System.err.println("Error: " + e);

	//	}		
		
	}
	
	public static void parsecable(String s){
		
		String cableargs[] = s.split(",");
		
		Main_class.clist.add(
				new Cable(
						Double.parseDouble(cableargs[0]),
						Double.parseDouble(cableargs[1]),
						Integer.parseInt(cableargs[2]),
						Main_class.elist.get(Integer.parseInt(cableargs[3])),
						Parsingmethods.parsecolor(cableargs[4]),
						Parsingmethods.parsecolor(cableargs[5]),
						Integer.parseInt(cableargs[6]),
						Double.parseDouble(cableargs[7])
						));
		
	}
	
	public static void parseent(String s){
		
		String entargs[] = s.split(",");
		
		Main_class.elist.add(
			new Entity(
				Double.parseDouble(entargs[0]),
				Double.parseDouble(entargs[1]),	
				new V3(entargs[2]),
				new V3(entargs[3]),
				new V3(entargs[4]),
				new V3(entargs[5]),
				new V3(entargs[6]),				
				Parsingmethods.parsecolor(entargs[7]),
				Parsingmethods.parsepoly(entargs[8]),
				Parsingmethods.parsemap(entargs[9]),
				new V3(entargs[10]),
				new V3(entargs[11]),
				Parsingmethods.parsecolor(entargs[12]),
				Integer.parseInt(entargs[13]),
				Double.parseDouble(entargs[14])
			)
		);		
		
	}
	
	
	
	//loading code
	//--------------------------------------------------------------------------------------------
	//saving code
	
	public static void writefile(String filename){ //get rid of argument, use outputfile

		try {

			writer = new PrintWriter(filename, "UTF-8");

			writer.println(Main_class.framewait);
			writer.println(Parsingmethods.reverseparsecolor(Frame_functions.background));
			writer.println(Motion.increment);			
			writer.println(Key_control.phys_rep_inc);
			writer.println(Key_control.tsensitivity);

			for(int i=0; i<Main_class.elist.size(); i++){
				fillfile(i);
			}
			
			if(Main_class.elist.size()>0){
				writer.println("cables");
				
				for(int i=0; i<Main_class.clist.size(); i++){
					writecable(i);
				}
				
			}

			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

	}

	public static void fillfile(int i){ //writes into file from array
		
		Entity e = Main_class.elist.get(i);
		
		writer.print(e.mass);	
		writer.print(",");
		writer.print(e.radius);
		writer.print(",");
		writer.print(e.luminosity.tostring());
		writer.print(",");
		writer.print(e.position.tostring());
		writer.print(",");
		writer.print(e.velocity.tostring());
		writer.print(",");
		writer.print(e.orientation.tostring());
		writer.print(",");
		writer.print(e.rotation.tostring());
		writer.print(",");
		writer.print(Parsingmethods.reverseparsecolor(e.p.c));
		writer.print(",");
		writer.print(Parsingmethods.reverseparsepoly(e.polybase2));
		writer.print(",");	
		writer.print(Parsingmethods.reverseparsemap(e.cornermap2));
		writer.print(",");	
		writer.print(e.scale2.tostring());
		writer.print(",");
		writer.print(e.shift2.tostring());
		writer.print(",");
		writer.print(Parsingmethods.reverseparsecolor(e.t.c));
		writer.print(",");
		writer.print(e.t.length);	
		writer.print(",");
		writer.print(e.t.resolution);	
		
		writer.println();

	}
	
	public static void writecable(int i){

		Cable c = Main_class.clist.get(i);

		if(Main_class.elist.indexOf(c.primary_ent)!=-1){

			writer.println(c.node_spacing);
			writer.print(",");
			writer.println(c.maxlength);
			writer.print(",");
			writer.println(c.nodes.length);
			writer.print(",");
			writer.println(Main_class.elist.indexOf(c.primary_ent));
			writer.print(",");
			writer.println(Parsingmethods.reverseparsecolor(c.col));
			writer.print(",");
			writer.println(Parsingmethods.reverseparsecolor(c.t.c));
			writer.print(",");
			writer.println(c.t.length);
			writer.print(",");
			writer.println(c.t.resolution);

		}

	}

	

}
