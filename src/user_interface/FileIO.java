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

	public static void readfile(String filename){ //reads in text files to generate objects

		try { 
			
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			Main_class.elist.clear();
			Main_class.clist.clear();
			
			Motion.repbuff=0;
			Motion.repetition=0;
									
			Main_class.framewait=Integer.parseInt(br.readLine());
			
			Frame_functions.background = parsecolor(br.readLine());
			
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
	
	public static void parsecable(String s){
		
		String cableargs[] = s.split(",");
		
		Main_class.clist.add(
				new Cable(
						Double.parseDouble(cableargs[0]),
						Double.parseDouble(cableargs[1]),
						Integer.parseInt(cableargs[2]),
						Main_class.elist.get(Integer.parseInt(cableargs[3])),
						parsecolor(cableargs[4]),
						parsecolor(cableargs[5]),
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
				parsecolor(entargs[7]),
				parsepoly(entargs[8]),
				parsemap(entargs[9]),
				new V3(entargs[10]),
				new V3(entargs[11]),
				parsecolor(entargs[12]),
				Integer.parseInt(entargs[13]),
				Double.parseDouble(entargs[14])
			)
		);		
		
	}
	
	public static Color parsecolor(String s){
		
		String values[] = s.substring(1,s.length()-1).split(";");
		return new Color(Integer.parseInt(values[0]),Integer.parseInt(values[1]),Integer.parseInt(values[2]));
		
	}
	
	public static V3[] parsepoly(String s){ //replace contents with a map?

		for(int x = 0; x<Poly_library.N; x++){
			if(s.equals(Poly_library.standard_poly_names[x])){
				return Poly_library.standard_poly_bases[x]; //poly_lib is static, so shallow copying is fine
			}
		}
		
		String[] vertices = s.substring(1,s.length()-1).split("/");
		V3[] output = new V3[vertices.length];
		for(int x=0; x<vertices.length; x++){
			output[x]=new V3(vertices[x], false);
		}
		return output;
		
	}
	
	public static int[][] parsemap(String s){ //replace contents with a map?
		
		for(int x = 0; x<Poly_library.N; x++){
			if(s.equals(Poly_library.standard_poly_names[x])){
				return Poly_library.standard_poly_maps[x]; //poly_lib is static, so shallow copying is fine
			}
		}
		
		String[] faces = s.substring(1,s.length()-1).split("/");
		int maxlength=0;
		
		for(String v:faces){
			if(v.length()*.5+.5>maxlength){
				maxlength=(int) (v.length()*.5+.5);
			}
		}
		
		int[][] output = new int[faces.length][maxlength];
		
		for(int x=0; x<faces.length; x++){
			String[] vertices = faces[x].split(";");
			for(int y=0; y<vertices.length; y++){
				output[x][y]=Integer.parseInt(vertices[y]);
			}	
		}
		
		return output;
		
	}
	
	//loading code
	//--------------------------------------------------------------------------------------------
	//saving code
	
	public static void writefile(String filename){ //get rid of argument, use outputfile

		try {

			writer = new PrintWriter(filename, "UTF-8");

			writer.println(Main_class.framewait);
			writer.println(reverseparsecolor(Frame_functions.background));
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
		writer.print(reverseparsecolor(e.p.c));
		writer.print(",");
		writer.print(reverseparsepoly(e.polybase2));
		writer.print(",");	
		writer.print(reverseparsemap(e.cornermap2));
		writer.print(",");	
		writer.print(e.scale2.tostring());
		writer.print(",");
		writer.print(e.shift2.tostring());
		writer.print(",");
		writer.print(reverseparsecolor(e.t.c));
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
			writer.println(reverseparsecolor(c.col));
			writer.print(",");
			writer.println(reverseparsecolor(c.t.c));
			writer.print(",");
			writer.println(c.t.length);
			writer.print(",");
			writer.println(c.t.resolution);

		}

	}

	public static String reverseparsecolor(Color c){
		
		return "["+c.getRed()+";"+c.getGreen()+";"+c.getBlue()+"]";
		
	}
	
	public static String reverseparsepoly(V3[] v){
		
		String output = "[";
		for(V3 vert:v){
			output+= vert.tostring_short()+"/"; //use proper string addition
		}
		return output.substring(0,output.length()-1)+ "]";
	}
	
	public static String reverseparsemap(int[][] i){
		
		String output = "[";
		
		for(int[] fmap:i){
			for(int corner:fmap){
				output += corner+";"; //use proper string addition
			}
			output = output.substring(0,output.length()-1)+ "/";
		}
		return output.substring(0,output.length()-1)+"]";
	}
	

}
