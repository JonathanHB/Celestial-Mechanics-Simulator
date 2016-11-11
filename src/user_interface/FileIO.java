package user_interface;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import objects.Cable;
import objects.Entity;
import utility.V3;
import utility.Main_class;
import graphics.Frame_functions;
import graphics.Graphics_engine;
import physics.Motion;

public class FileIO {	

	static PrintWriter writer; //declared outside for fillfile and writefile

	public static String[] test_input; //a string to read simulations from
	
	public static void setup(){ //sets the test_input string to a hardcoded value
		FileIO.test_input = new String[]{
				"30",
				"[0;0;0]",
				"-1",
				".01",
				"1",
				"1",
				"200000000000.0,1,[400;200;110],[0;0;0],[0;0;0],[0;0;0],[0;0;0],[20;30;60],cube,cube,[1;1;1],[0;0;0],[30;50;40],1000,.01,-1",
				"1000000000.0,.6,[0;0;0],[10;0;.5],[0;1.1;0],[0;0;0],[0;.2;0],[20;70;30],cube,cube,[.5;.5;.7],[0;0;0],[20;60;40],300,1,-1",
				"1000000000.0,.6,[0;0;0],[30;0;.5],[0;.6;-.1],[0;0;0],[.2;0;0],[120;20;30],cube,cube,[.5;.5;.5],[0;0;0],[110;30;40],600,1,-1",
				"100000000.0,.3,[0;0;0],[0;20;0],[.6;0;-.2],[0;.77;0],[.2;0;.3],[60;70;130],cube,cube,[.2;.2;.2],[0;0;0],[70;50;120],2500,1,-1",
				"100000000.0,.1,[0;0;0],[30;1.6;.5],[.2;.6;-.1],[0;0;0],[.2;0;0],[120;20;50],cube,cube,[.1;.1;.1],[0;0;0],[110;30;50],600,.2,2",
				"100000000.0,.1,[0;0;0],[30;1.5;2.5],[.2;.6;-.1],[0;0;0],[.2;0;0],[110;120;20],cube,cube,[.1;.1;.1],[0;0;0],[110;130;20],600,.2,2"

		};
	}
	
	public static String[] readfile(String filename){ //reads in text files to generate objects

		if(filename.equals("d")){
			return test_input;
		}else{ //not required because return statement terminates the method, but helps readability

			ArrayList<String> filecontents = new ArrayList<String>(0);

			try { 	
				
				BufferedReader br = new BufferedReader(new FileReader(filename));

				boolean reading = true;

				while(reading){	

					String s = br.readLine();

					if (s!=null){		//checks for end of file			
						filecontents.add(s);

					}else{
						reading = false;

					}

				}

			}catch (IOException e) {

				Frame_functions.statefield.setText("Error: " + e);			

			}	

			return (String[]) filecontents.toArray(new String[filecontents.size()]);

		}

	}
		
	public static void loadfile(String[] inputdata){ //generates objects from an array of strings

		Main_class.elist.clear();
		Main_class.clist.clear(); //clears existing objects

		//reads in several global variables
		
		Motion.repbuff = 0;
		Motion.repetition = 0;

		Main_class.framewait = Integer.parseInt(inputdata[0]);

		Frame_functions.background = Parsingmethods.parsecolor(inputdata[1]);

		Graphics_engine.focus = Integer.parseInt(inputdata[2]);
		
		Motion.increment = Double.parseDouble(inputdata[3]);
		Motion.incbuff = Motion.increment;

		Key_control.phys_rep_inc = Integer.parseInt(inputdata[4]);
		Key_control.tsensitivity = Double.parseDouble(inputdata[5]);
		
		boolean ents = true;

		for(int x = 6; x<inputdata.length; x++){	//reads in entities, then cables	
			
			if(ents){	
				if(inputdata[x].equals("cables")){
					ents = false;
				}else{
					parseent(inputdata[x]);
				}
			}else{
				parsecable(inputdata[x]);
			}										

		}
		
		//sets camera position to that of the object whose reference frame it moves in
		if(Graphics_engine.focus!=-1){			
			Graphics_engine.viewposition.set(Main_class.elist.get(Graphics_engine.focus).position);
			Graphics_engine.orientation.set(new V3(0,0,0));

		}else{
			Graphics_engine.viewposition.set(new V3(0,0,0));
			Graphics_engine.orientation.set(new V3(0,.77,0));
		}


		Frame_functions.statefield.setText(Main_class.loadstring + " loaded"); //confirms file loading
		//Frame_functions.inputfield.setText(null); //clears input

	}
	
	public static void parsecable(String s){ //generates cable from string, see cable constructors
		
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
						Double.parseDouble(cableargs[7]),
						Integer.parseInt(cableargs[8])
						));
		
	}
	
	public static void parseent(String s){ //generates ent from string, see cable constructors
		
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
				Double.parseDouble(entargs[14]),
				Integer.parseInt(entargs[15])
			)
		);		
		
	}
	
		
	//loading code
	//--------------------------------------------------------------------------------------------
	//saving code
	
	public static void writefile(String filename){ //writes current state of the simulation to a text file

		try {

			writer = new PrintWriter(filename, "UTF-8");

			//writes global variables			
			writer.println(Main_class.framewait);
			writer.println(Parsingmethods.reverseparsecolor(Frame_functions.background));
			writer.println(Graphics_engine.focus);	
			writer.println(Motion.increment);			
			writer.println(Key_control.phys_rep_inc);
			writer.println(Key_control.tsensitivity);

			//saves entities, then cables
			
			for(int i=0; i<Main_class.elist.size(); i++){
				fillfile(i);
			}
			
			if(Main_class.clist.size()>0){
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

	public static void fillfile(int i){ //saves the entity at index i
		
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
		writer.print(",");
		writer.print(e.t.refent);
		
		writer.println();

	}
	
	public static void writecable(int i){ //saves the cable at index i

		Cable c = Main_class.clist.get(i);

		if(Main_class.elist.indexOf(c.primary_ent)!=-1){

			writer.print(c.node_spacing);
			writer.print(",");
			writer.print(c.maxlength);
			writer.print(",");
			writer.print(c.nodes.length);
			writer.print(",");
			writer.print(Main_class.elist.indexOf(c.primary_ent));
			writer.print(",");
			writer.print(Parsingmethods.reverseparsecolor(c.col));
			writer.print(",");
			writer.print(Parsingmethods.reverseparsecolor(c.t.c));
			writer.print(",");
			writer.print(c.t.length);
			writer.print(",");
			writer.print(c.t.resolution);
			writer.print(",");
			writer.print(c.t.refent);
			
			writer.println();
			
		}

	}

	

}
