package user_interface;

import java.awt.Color;

import objects.Poly_library;
import utility.V3;

public class Parsingmethods {
	//this class contains the type conversion methods used by FileIO
	
	public static Color parsecolor(String s){ //converts String to Color
		
		String values[] = s.substring(1,s.length()-1).split(";");
		return new Color(Integer.parseInt(values[0]),Integer.parseInt(values[1]),Integer.parseInt(values[2]));
		
	}
	
	public static V3[] parsepoly(String s){ //converts a String to a set of polyhedron vertices

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
	
	public static int[][] parsemap(String s){ //converts a String to a set of polyhedron faces
		
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
	
	//loading
	//-----------------------------------------------------------------------------------------
	//saving
	
	public static String reverseparsecolor(Color c){ //converts Color to String
		
		return "["+c.getRed()+";"+c.getGreen()+";"+c.getBlue()+"]";
		
	}
	
	public static String reverseparsepoly(V3[] v){ //converts a set of polyhedron vertices to String
		
		String output = "[";
		for(V3 vert:v){
			output+= vert.tostring_short()+"/"; //use proper string addition
		}
		return output.substring(0,output.length()-1)+ "]";
	}
	
	public static String reverseparsemap(int[][] i){ //converts a set of polyhedron faces to String
		
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

