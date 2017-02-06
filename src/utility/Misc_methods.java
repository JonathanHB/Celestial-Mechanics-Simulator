package utility;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import objects.Entity;
import objects.Render_obj;

public class Misc_methods {
		
	public static V3 weighted_average(V3 v1, V3 v2, double w1, double w2){ 
		//computes the weighted average of the vectors
		//v1 and v2 are V3 objects, w1 and w2 are relative weights
		
		return ((v1.scale2(w1)).add2(v2.scale2(w2))).invscale2(w1+w2);
		
	}
	
	public static V3 weighted_average(V3 v1, V3 v2, double w1, double w2, double w3){ 
		//computes the weighted average of the vectors
		//v1 and v2 are V3 objects, w1 and w2 are relative weights, w3 is their psuedo-sum for non addable weights(such as moments of inertia)
		
		return ((v1.scale2(w1)).add2(v2.scale2(w2))).invscale2(w3);
		
	}
	
	public static V3 weighted_average(V3 v1, V3 v2, V3 w1, V3 w2, double w3){ 
		//computes the weighted average of the vectors
		//v1 and v2 are V3 objects, w1 and w2 are relative weights, w3 is their psuedo-sum for non addable weights(such as moments of inertia)
		
		return ((v1.dimscale2(w1)).add2(v2.dimscale2(w2))).invscale2(w3);
		
	}
	
	public static Color coloraverage(Color a, Color b, double v1, double v2){ //a weighted average of two colors
		
		return new Color(capcolor((a.getRed()*v1+b.getRed()*v2)/(v1+v2)), capcolor((a.getGreen()*v1+b.getGreen()*v2)/(v1+v2)), capcolor((a.getBlue()*v1+b.getBlue()*v2)/(v1+v2)));
		
	}
	
	public static int capcolor(double d){ //makes sure d is a legal color component
		
		if(d<=255){
			return (int) Math.round(d);
		}else{
			return 255;
		}
		
	}

	//sorts arraylist for interval c-d with a quicksort
	public static void sort2(int c, int d, ArrayList<Render_obj> array){
		//	d is the upper end of the range, c is the lower one (inclusive)
		if(d > c){
			
			double pivot = array.get(d).getsqudistance();
		
			int n = d;

			for(int x = c; x<n;){

				if(array.get(x).getsqudistance() > pivot){
				
					Collections.swap(array, n, n-1);
									
					if(x+1<n){
						//keeps certain switches of adjacent elements from being undone
						Collections.swap(array, n, x);
					}	

					n--;

				}else{

					x++;

				}

			}

			sort2(c,n-1,array);
			sort2(n+1,d,array);		

		}

	}
	
	public static int sigdigs(double d){ //computes the number of decimal places required to express d
		String s = Double.toString(d);
		return s.length()-s.indexOf(".")-1;
	}
	
	public static double roundto(double d, int r){ //rounds d to r decimal places
		return Math.round(d*Math.pow(10, r))/Math.pow(10, r);
	}
	
	public static void revolve(Entity e){
		double angle = .35;
		double altitude = e.position.x;
		e.position.x = altitude*Math.cos(angle);
		e.position.y = altitude*Math.sin(angle);
		double speed = e.velocity.y;
		e.velocity.x = speed*Math.sin(-angle);
		e.velocity.y = speed*Math.cos(-angle);
		
	}
	
}
