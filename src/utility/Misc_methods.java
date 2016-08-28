package utility;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import objects.Render_obj;

public class Misc_methods { 
	//make a general version of weighted_average that uses a pair of object and weight arrays

	public static V3 weighted_average(V3 v1, V3 v2, double w1, double w2){ //v1 and v2 are V3 objects, w1 and w2 are relative, addable weights
		
		return ((v1.scale2(w1)).add2(v2.scale2(w2))).invscale2(w1+w2);
		
	}
	
	public static V3 weighted_average(V3 v1, V3 v2, double w1, double w2, double w3){ //v1 and v2 are V3 objects, w1 and w2 are relative weights, w3 is their psuedo-sum for non addable weights
		
		return ((v1.scale2(w1)).add2(v2.scale2(w2))).invscale2(w3);
		
	}
	
	public static Color coloraverage(Color a, Color b, double v1, double v2){ //a weighted average of colors
		
		return new Color(capcolor((a.getRed()*v1+b.getRed()*v2)/(v1+v2)), capcolor((a.getGreen()*v1+b.getGreen()*v2)/(v1+v2)), capcolor((a.getBlue()*v1+b.getBlue()*v2)/(v1+v2)));
		
	}
	
	public static int capcolor(double d){
		
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
	
}


/*	
//switches elements a and b
public static void replace(int a, int b, double[][] array){

	double distance = array[a][0];
	int type = (int) array[a][1];
	int ent = (int) array[a][2];       //leave these as doubles?
	int location = (int) array[a][3];

	array[a][0] = array[b][0];
	array[a][1] = array[b][1];
	array[a][2] = array[b][2];
	array[a][3] = array[b][3];

	array[b][0] = distance;		
	array[b][1] = type;
	array[b][2] = ent;
	array[b][3] = location;

}

//sorts list for interval c-d with a quicksort
public static void sort(int c, int d, double[][] array){
	//	d is the upper end of the range, c is the lower one (inclusive)
	if(d > c){

		double pivot = array[d][0];

		int n = d;

		for(int x = c; x<n;){

			if(array[x][0] > pivot){

				replace(n,n-1,array);

				if(x+1<n){
					//keeps certain switches of adjacent elements from being undone
					replace(n,x,array);	

				}	

				n--;

			}else{

				x++;

			}

		}

		sort(c,n-1,array);
		sort(n+1,d,array);		

	}

}
*/