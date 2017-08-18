package objects;

import java.awt.Color;
import java.util.Arrays;

import utility.V3;
import objects.Plane;
import objects.Point;

public class Polyhedron {

	public Color c; //this should probably be removed and replaced with a colormap or something

	public Plane faces[];

	public Point vertices[];

	public Polyhedron(Color col, V3 shift, V3 scale, V3 orientation, V3[] polybase, int[][] cornermapping){ //constructor		
		c=col;
		generatepoly(shift, scale, orientation, polybase, cornermapping, col);
	}

	public void generatepoly(V3 shift, V3 scale, V3 orientation, V3[] polybase, int[][] cornermapping, Color col){
		//generates a new polyhedron for the constructor
		//polybase is a set of points centered on the origin
		//each row of cornermapping: [a,b,c,d,...n,-1,w,x,...z] where a-n are non-negative integers and x-z are any integers
		
		vertices=new Point[polybase.length];
		faces=new Plane[cornermapping.length];		
		
		for(int x=0; x<polybase.length; x++){
			vertices[x]=new Point(utility.Math_methods.rotatepoint(polybase[x].dimscale2(scale), orientation).add2(shift));			
		}
		
		for(int x=0; x<cornermapping.length; x++){ //<---not to be converted to type t:<array> format
			int reallen=cornermapping[x].length;
			
			for(int y=0; y<cornermapping[x].length; y++){
				if(cornermapping[x][y]==-1){
					reallen=y;
					y=cornermapping[x].length;
				}
			}
			
			faces[x]=new Plane(mapconverter(Arrays.copyOfRange(cornermapping[x], 0, reallen)), col);
			
			faces[x].setcenter();			
			
		}
								
	}		

	public void setcenters(){ //compute face centers for lighting and distance calculations
		for(Plane p:faces){
			p.setcenter();
		}
	}
	
	public Point[] mapconverter(int[] i){ //get points at indices i to use as corners of a plane
		//returned array is intentionally shallowcopied because many points are shared between planes
		Point[] output = new Point[i.length];
		for(int x = 0; x<i.length; x++){
			output[x]=vertices[i[x]];
		}
		return output;
	}
	
}



