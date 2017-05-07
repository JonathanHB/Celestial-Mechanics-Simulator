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

import java.io.File;

import javax.swing.JFileChooser;

public class FileIO {	

	static PrintWriter writer; //declared outside for fillfile and writefile

	static JFileChooser fileChooser = new JFileChooser();
	
	public static String getfilepath(){
	
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(null);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			//System.out.println("Selected file: " + selectedFile.getAbsolutePath());
			return selectedFile.getAbsolutePath();
		}else{
			
			return null;
		}
		
	}	
		
	public static String[] readfile(String filename){
		
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
		
	public static void loadfile(String[] inputdata){ //generates objects from an array of strings

		Main_class.elist.clear();
		Main_class.clist.clear(); //clears existing objects

		//reads in several global variables

		Motion.repbuff = 0;
		Motion.repetition = 0;

		Main_class.framewait = Integer.parseInt(inputdata[0]);

		Frame_functions.background = Parsingmethods.parsecolor(inputdata[1]);//make optional

		Graphics_engine.focusindex = Integer.parseInt(inputdata[2]); //make optional

		Motion.increment = Double.parseDouble(inputdata[3]); //make optional
		Motion.incbuff = Motion.increment; //make optional

		Key_control.phys_rep_inc = Integer.parseInt(inputdata[4]); //make optional
		Key_control.tsensitivity = Double.parseDouble(inputdata[5]); //make optional

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

		//sets camera position to that of the object whose reference frame it moves in, should be made settable with mouse
		if(Graphics_engine.focusindex!=-1){	
			Graphics_engine.focus = Main_class.elist.get(Graphics_engine.focusindex);			
		}else{			
			Graphics_engine.focus = new Entity();
			Graphics_engine.focus.position = new V3(0,0,0);
			Graphics_engine.focus.velocity = new V3(0,0,0);
		}

		Graphics_engine.viewposition.set(Graphics_engine.focus.position);
		Graphics_engine.orientation.set(new V3(0,.77,0));


		//Frame_functions.statefield.setText(Main_class.loadstring + " loaded"); //confirms file loading
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
			writer.println(Main_class.elist.indexOf(Graphics_engine.focus));	
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
		writer.print(Main_class.elist.indexOf(e.t.refent));
		
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

/*
 * public static String[] test_input1; //a string to read simulations from
	public static String[] test_input2; //a string to read simulations from
	public static String[] Sitnikov; //a string to read simulations from
	
	static Map filedirectory = new HashMap(); 
		
	
	
	
	public static void setup(){ //sets the test_input string to a hardcoded value
		
	filedirectory.put("bin", "/home/jonathan/Desktop/OMS_files/binary_stars.txt");
	
		FileIO.test_input1 = new String[]{
				"30",
				"[0;0;0]",
				"-1",
				".01",
				"1",
				"1",
				"200000000000.0,1,[400;200;110],[0;0;0],[0;0;0],[0;0;0],[0;0;0],[20;30;60],icosahedron,icosahedron,[1;1;1],[0;0;0],[30;50;40],1000,.01,-1",
				"1000000000.0,.6,[0;0;0],[10;0;.5],[0;1.1;0],[0;0;0],[0;.2;0],[20;70;30],icosahedron,icosahedron,[.5;.5;.7],[0;0;0],[20;60;40],300,1,3",
				"1000000000.0,.6,[0;0;0],[30;0;.5],[0;.6;-.1],[0;0;0],[.2;0;0],[120;20;30],icosahedron,icosahedron,[.5;.5;.5],[0;0;0],[110;30;40],600,1,-1",
				"100000000.0,.3,[0;0;0],[0;20;0],[.6;0;-.2],[0;.77;0],[.2;0;.3],[60;70;130],icosahedron,icosahedron,[.2;.2;.2],[0;0;0],[70;50;120],2500,1,-1",
				"100000000.0,.1,[0;0;0],[30;1.6;.5],[.2;.6;-.1],[0;0;0],[.2;0;0],[120;20;50],icosahedron,icosahedron,[.1;.1;.1],[0;0;0],[110;30;50],600,.2,2",
				"100000000.0,.1,[0;0;0],[30;1.5;2.5],[.2;.6;-.1],[0;0;0],[.2;0;0],[110;120;20],icosahedron,icosahedron,[.1;.1;.1],[0;0;0],[110;130;20],600,.2,2"

		};
		
		FileIO.test_input2 = new String[]{
				"30",
				"[0;0;0]",
				"-1",
				".01",
				"1",
				"1",
				"200000000000.0,1,[400;200;110],[0;0;0],[0;0;0],[0;0;0],[0;0;0],[20;30;60],cube,cube,[1;1;1],[0;0;0],[30;50;40],1000,.01,-1",
				"1000000000.0,.6,[0;0;0],[-2;0;.5],[0;1;0],[0;0;0],[0;.2;0],[20;70;30],cube,cube,[.5;.5;.7],[0;0;0],[20;60;40],300,1,-1"
				/*"1000000000.0,.6,[0;0;0],[3;0;.5],[0;0;-.1],[0;0;0],[.2;0;0],[120;20;30],cube,cube,[.5;.5;.5],[0;0;0],[110;30;40],600,1,-1",
				"100000000.0,.3,[0;0;0],[0;2;0],[0;0;-.2],[0;.77;0],[.2;0;.3],[60;70;130],cube,cube,[.2;.2;.2],[0;0;0],[70;50;120],2500,1,-1",
				"100000000.0,.1,[0;0;0],[3;1.6;.5],[.2;0;-.1],[0;0;0],[.2;0;0],[120;20;50],cube,cube,[.1;.1;.1],[0;0;0],[110;30;50],600,.2,2",
				"100000000.0,.1,[0;0;0],[3;1.5;2.5],[.2;0;-.1],[0;0;0],[.2;0;0],[110;120;20],cube,cube,[.1;.1;.1],[0;0;0],[110;130;20],600,.2,2"
				*/
	/*	};
		
		FileIO.Sitnikov = new String[]{
				"30",
				"[0;0;0]",
				"-1",
				".01",
				"1",
				"1",
				"20000000000.0,1,[400;200;110],[4;0;0],[0;-.6;0],[0;0;0],[0;0;0],[20;30;60],cube,cube,[.5;.5;.5],[0;0;0],[30;50;40],2000,.1,-1",
				"20000000000.0,.6,[0;0;0],[-4;0;0],[0;.6;0],[0;0;0],[0;0;0],[20;70;30],cube,cube,[.5;.5;.5],[0;0;0],[20;60;40],2000,.1,-1",
				"20000000000.0,.6,[0;0;0],[0;0;0],[0;0;.5],[0;0;0],[0;0;0],[20;70;30],cube,cube,[.5;.5;.5],[0;0;0],[20;60;40],1000,.5,-1"
				/*"1000000000.0,.6,[0;0;0],[3;0;.5],[0;0;-.1],[0;0;0],[.2;0;0],[120;20;30],cube,cube,[.5;.5;.5],[0;0;0],[110;30;40],600,1,-1",
				"100000000.0,.3,[0;0;0],[0;2;0],[0;0;-.2],[0;.77;0],[.2;0;.3],[60;70;130],cube,cube,[.2;.2;.2],[0;0;0],[70;50;120],2500,1,-1",
				"100000000.0,.1,[0;0;0],[3;1.6;.5],[.2;0;-.1],[0;0;0],[.2;0;0],[120;20;50],cube,cube,[.1;.1;.1],[0;0;0],[110;30;50],600,.2,2",
				"100000000.0,.1,[0;0;0],[3;1.5;2.5],[.2;0;-.1],[0;0;0],[.2;0;0],[110;120;20],cube,cube,[.1;.1;.1],[0;0;0],[110;130;20],600,.2,2"
				*/
/*		};
	}
	
	if(filename == "e"){
			//System.out.println();
			return readfile2((String)(filedirectory.get(filename)));
			
		}else if(filename.equals("d")){
			return test_input1;
		}else if(filename.equals("f")){ //TODO replace with map
			return test_input2;
		}else if(filename.equals("c")){
			return Sitnikov;
		}else{
			return readfile2(filename);
			
		} //not required because return statement terminates the method, but helps readability
		
		public static String[] readfile(String filename){ //reads in text files to generate objects

		Filetree.open();
		
		


	}*/
