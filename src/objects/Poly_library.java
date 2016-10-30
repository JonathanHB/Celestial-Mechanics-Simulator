package objects;

import user_interface.FileIO;
import utility.V3;

public class Poly_library {
	//a library of polyhedra used when loading objects so that the user 
	//does not have to manually define common objects
	
	//note: integer arrays deep copy automatically, so some code here should be updated
	
	public static int N = 1; //number of polyhedra
	
	public static String[] standard_poly_names; 
	public static V3[][] standard_poly_bases;
	public static int[][][] standard_poly_maps;
	
	public static void setup(){ 
		//sets up library with hardcoded objects, no objects are added during runtime
		
		standard_poly_names = new String[N];	
		
		standard_poly_names[0]="cube";
		
		standard_poly_bases = new V3[N][];
		
		standard_poly_bases[0] = new V3[8];		
		standard_poly_bases[0][0]=new V3(-1,-1,-1);
		standard_poly_bases[0][1]=new V3(-1,-1,1);
		standard_poly_bases[0][2]=new V3(-1,1,-1);
		standard_poly_bases[0][3]=new V3(-1,1,1);
		standard_poly_bases[0][4]=new V3(1,-1,-1);
		standard_poly_bases[0][5]=new V3(1,-1,1);
		standard_poly_bases[0][6]=new V3(1,1,-1);
		standard_poly_bases[0][7]=new V3(1,1,1);
		
		standard_poly_maps = new int [N][][];
		
		standard_poly_maps[0] = new int[][]{
				{0,1,3,2},
				{4,5,7,6},
				{0,2,6,4},
				{1,3,7,5},
				{2,3,7,6},
				{0,1,5,4}};
		
	}
	
	public static V3[] polybaseadd(Point[] a, Point[] b, V3 da, V3 db){ 
		//fuses two polybases, used to preserve graphics in entity collisions
		
		V3[] output = new V3[a.length+b.length];
		int count = 0;
		for(Point p:a){
			output[count] = new V3(p.position.add2(da));
			count++;
		}
		for(Point p:b){
			output[count] = new V3(p.position.sub2(db));
			count++;
		}

		return output;
	}
	
	public static int[][] cornermapadd(int[][] a, int[][] b, int lendiff){ 
		//fuses two polymaps, used to preserve graphics in entity collisions

		int[][] output = new int[a.length+b.length][];
		int count = 0;
		for(int[] i:a){
			output[count] = new int[i.length];
			for(int x = 0; x<i.length; x++){
				output[count][x] = i[x];
			}
			count++;
		}
		for(int[] i:b){
			output[count] = new int[i.length];
			for(int x = 0; x<i.length; x++){
				output[count][x] = i[x]+lendiff;
			}
			count++;
		}
		return output;
	}
	
}
