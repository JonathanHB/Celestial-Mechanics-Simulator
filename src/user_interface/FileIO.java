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
import graphics.Graphics_engine;
import physics.Motion;
import physics.Trajectory_optimizer;

public class FileIO {	

	public static String inputfile = "/home/jonathan/Desktop/cabletest2.txt"; //file to read from
	public static String outputfile = "/home/jonathan/Desktop/iotest2.txt"; //file to save to

	static PrintWriter writer; //declared outside for fillfile and writefile

	public static void readfile(String filename){ //get rid of argument, use inputfile?

		try { 
			//reads in text files to generate objects
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			Motion.repbuff=0;
			Motion.repetition=0;
						
			Main_class.framewait=Integer.parseInt(br.readLine());
			
			Motion.increment=Double.parseDouble(br.readLine());
			Motion.incbuff=Motion.increment;
			
			Key_control.phys_rep_inc=Integer.parseInt(br.readLine());
			Key_control.tsensitivity=Integer.parseInt(br.readLine());
										
			boolean reading = true;
			boolean ents = true;

			
			while(reading){		
				String s = br.readLine();
				if (s!=null){
					
				//	System.out.println(s+ ents);
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

		}catch (IOException e) {

			System.err.println("Error: " + e);

		}		
	}
	
	public static void parsecable(String s){
		
		String cableargs[] = s.split(",");
		
		Main_class.clist.add(
				new Cable(
						Double.parseDouble(cableargs[0]),
						Double.parseDouble(cableargs[1]),
						Double.parseDouble(cableargs[2]),
						Integer.parseInt(cableargs[3]),
						Integer.parseInt(cableargs[4]),
						parsecolor(cableargs[5]),
						parsecolor(cableargs[6]),
						Integer.parseInt(cableargs[7]),
						Double.parseDouble(cableargs[8])
						));
		
	}
	
/*	public static Entity[] getents(String s){
		
		String[] indices = s.substring(1, s.length()-1).split(";");
		Entity[] output = new Entity[indices.length];
		for(int x = 0; x<indices.length; x++){
			output[x] = Main_class.elist.get(Integer.parseInt(indices[x]));
		}
		
		return output;
		
	}*/
	
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
	
	//--------------------------------------------------------------------------------------------

	public static void writefile(String filename){ //get rid of argument, use outputfile

		try {

			writer = new PrintWriter(filename, "UTF-8");

			writer.println(Main_class.elist.size());
			writer.println(Main_class.framewait);			
			writer.println(Motion.increment);			
			writer.println(Key_control.phys_rep_inc);
			writer.println(Key_control.tsensitivity);

			for(int i=0; i<Main_class.elist.size(); i++){
				fillfile(i);
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
		
		writer.print(Main_class.elist.get(i).mass);	
		writer.print(",");
		writer.print(Main_class.elist.get(i).radius);
		writer.print(",");
		writer.print(Main_class.elist.get(i).luminosity.tostring());
		writer.print(",");
		writer.print(Main_class.elist.get(i).position.tostring());
		writer.print(",");
		writer.print(Main_class.elist.get(i).velocity.tostring());
		writer.print(",");
		writer.print(Main_class.elist.get(i).orientation.tostring());
		writer.print(",");
		writer.print(Main_class.elist.get(i).rotation.tostring());
		writer.print(",");
		writer.print(reverseparsecolor(Main_class.elist.get(i).p.c));
		writer.print(",");
		writer.print(reverseparsepoly(Main_class.elist.get(i).polybase2));
		writer.print(",");	
		writer.print(reverseparsemap(Main_class.elist.get(i).cornermap2));
		writer.print(",");	
		writer.print(Main_class.elist.get(i).scale2.tostring());
		writer.print(",");
		writer.print(Main_class.elist.get(i).shift2.tostring());
		writer.print(",");
		writer.print(reverseparsecolor(Main_class.elist.get(i).t.c));
		writer.print(",");
		writer.print(Main_class.elist.get(i).t.length);	
		writer.print(",");
		writer.print(Main_class.elist.get(i).t.resolution);	
		
		writer.println();

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
