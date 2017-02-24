package objects;

import utility.V3;

public class Poly_library {
	//a library of polyhedra used when loading objects so that the user 
	//does not have to manually define common objects
	//currently only contains a cube object
	
	//note: integer arrays deep copy automatically, so some code here should be updated
	
	public static int N = 2; //number of polyhedra
	
	public static String[] standard_poly_names; 
	public static V3[][] standard_poly_bases;
	public static int[][][] standard_poly_maps;
	
	public static void setup(){ 
		//sets up library with hardcoded objects, no objects are added during runtime
		
		standard_poly_names = new String[N];	
		
		standard_poly_names[0]="cube";		
		standard_poly_names[1]="icosahedron";
		
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
		
		double phi = (1+Math.sqrt(5))/2; //=1.618
		
		standard_poly_bases[1] = new V3[12];
		
		standard_poly_bases[1][0]=new V3(0,1,phi);
		standard_poly_bases[1][1]=new V3(0,1,-phi);
		standard_poly_bases[1][2]=new V3(0,-1,phi);
		standard_poly_bases[1][3]=new V3(0,-1,-phi);
		standard_poly_bases[1][4]=new V3(1,phi,0);
		standard_poly_bases[1][5]=new V3(1,-phi,0);
		standard_poly_bases[1][6]=new V3(-1,phi,0);
		standard_poly_bases[1][7]=new V3(-1,-phi,0);
		standard_poly_bases[1][8]=new V3(phi,0,1);
		standard_poly_bases[1][9]=new V3(-phi,0,1);
		standard_poly_bases[1][10]=new V3(phi,0,-1);
		standard_poly_bases[1][11]=new V3(-phi,0,-1);
		
		//
		
		standard_poly_maps = new int [N][][];
		
		standard_poly_maps[0] = new int[][]{
				{0,1,3,2},
				{4,5,7,6},
				{0,2,6,4},
				{1,3,7,5},
				{2,3,7,6},
				{0,1,5,4}};
		
		standard_poly_maps[1] = new int[][]{
				{0,4,8},				
				{0,4,6},				
				{0,2,8},
				{0,2,9},
				{0,6,9},
				{3,7,11},
				{3,11,1},
				{3,7,5},
				{3,1,10},
				{3,5,10},
				{1,4,6},
				{1,4,10},
				{10,8,5},
				{6,11,9},
				{4,10,8},
				{1,6,11},
				{7,2,5},
				{5,8,2},
				{7,9,2},
				{7,9,11}};
		
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
