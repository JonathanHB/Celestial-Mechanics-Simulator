package utility;

import java.util.Arrays;

public class V3 { //a 3d vector
	
	public double x;
	public double y; //vector components
	public double z;
	
	//-------------------------------------------------------------------------------
	
	public V3(){} //basic vector constructor
	
	public void set(double a, double b, double c){ //sets vector from components
		x=a;
		y=b;
		z=c;
	}
	
	public void set(V3 v){ //sets vector from another vector via deep copying
		x=v.x;
		y=v.y;
		z=v.z;
	}
	
	public V3(double a, double b, double c){ //vector constructor from components
		this.set(a,b,c);
	}
	
	public V3(V3 v){ //vector constructor from another vector via deep copying
		this.set(v);
	}
	
	//-------------------------------------------------------------------------------
	
	public V3(String s){ //vector constructor from a string
		String values[] = s.substring(1,s.length()-1).split(";");
		x=Double.parseDouble(values[0]); 
		y=Double.parseDouble(values[1]); 
		z=Double.parseDouble(values[2]);
	}
	
	public V3(String s, boolean b){ //vector constructor from a string, the boolean is for overloading purposes
		String values[] = s.split(";");
		x=Double.parseDouble(values[0]); 
		y=Double.parseDouble(values[1]); 
		z=Double.parseDouble(values[2]);
	}
	
	public String tostring(){
		return "["+x+";"+y+";"+z+"]";
	}
	
	public String tostring_short(){
		return x+";"+y+";"+z;
	}
	
	//-------------------------------------------------------------------------------

	public double magnitude(){ //vector magnitude	
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public double squmagnitude(){ //vector magnitude squared, improves computational efficiency by avoiding square roots
		return x*x+y*y+z*z;
	}
	
	public double sum(){ //sum of vector components
		return x+y+z;
	}
	
	public boolean iszero(){ //efficient check of zeroness
		return x==0 && y==0 && z==0;
	}
	
	public boolean isnonzero(){ //efficient check of zeroness
		return !this.iszero();
	}
	
	//-------------------------------------------------------------------------------
	
	public void add(double a, double b, double c){ //adds to the components of this vector
		x+=a;
		y+=b;
		z+=c;
	}
	
	public void add(V3 v){ //adds another vector to this one
		x+=v.x;
		y+=v.y;
		z+=v.z;
	}
	
	public V3 add2(double a, double b, double c){ //adds to the components of this vector
		return new V3(x+a, y+b, z+c);
	}
	
	public V3 add2(V3 v){  //adds another vector to this one
		return new V3(x+v.x, y+v.y, z+v.z);
	}
	
	//-------------------------------------------------------------------------------
	
	public void sub(double a, double b, double c){ //subtracts from the components of this vector
		x-=a;
		y-=b;
		z-=c;
	}
	
	public void sub(V3 v){ //subtracts another vector from this one
		x-=v.x;
		y-=v.y;
		z-=v.z;
	}
	
	public V3 sub2(double a, double b, double c){ //subtracts from the components of this vector
		V3 output = new V3(x-a, y-b, z-c);
		return output;
	}
	
	public V3 sub2(V3 v){ //subtracts another vector from this one
		return new V3(x-v.x, y-v.y, z-v.z);
	}
	
	//-------------------------------------------------------------------------------
	
	public void scale(double d){ //scales vector by d
		x*=d;
		y*=d;
		z*=d;
	}
	
	public V3 scale2(double d){ //returns this vector scaled by d
		return new V3(x*d,y*d,z*d);
	}
	
	public void invscale(double d){ //scales vector by d
		x/=d;
		y/=d;
		z/=d;
	}
	
	public V3 invscale2(double d){ //returns this vector scaled by d
		return new V3(x/d,y/d,z/d);
	}
	
	//-------------------------------------------------------------------------------
	//inverse versions of dimscale will be created later if they are needed
	public void dimscale(V3 v){
		x*=v.x;
		y*=v.y;
		z*=v.z;
	}
	
	public V3 dimscale2(V3 v){
		return new V3(x*v.x, y*v.y, z*v.z);
	}
	
	//-------------------------------------------------------------------------------
	
	public void invert(){ //scales vector by -1
		x*=-1;
		y*=-1;
		z*=-1;
	}
	
	public V3 invert2(){ //scales vector by d
		return new V3(-x,-y,-z);
	}
	
	public void tolength(double d){ //scales vector to length d, does nothing if vector is 0,0,0
		if(this.isnonzero()){
		this.scale(d/this.magnitude());
		}
	}
	
	public V3 tolength2(double d){ //returns this vector scaled to length d, returns a 0,0,0 vector if this vector is 0,0,0		
		if(this.isnonzero()){
			return this.scale2(d/this.magnitude());
		}else{
			return this;
		}
	}
	
	//ifs to check for 0 magnitude may be bad for runtime, consider relying on nonzero inputs to speed up method
	
	//------------------------------------------------------------------------------
	
	
	
	
	
	
}
