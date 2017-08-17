package utility;

public class V3 { //a 3d vector
	
	public double x;
	public double y; //vector components
	public double z;
	
	//-------------------------------------------------------------------------------
	
	public V3(){} //basic vector constructor
	
	public V3(double a){ //vector constructor from components
		this.set(a,a,a);
	}
	
	public V3(double a, double b, double c){ //vector constructor from components
		this.set(a,b,c);
	}
	
	public V3(V3 v){ //vector constructor from another vector via deep copying
		this.set(v);
	}
	
	//-------------------------------------------------------------------------------
	
	public void set(double a){ //sets vector from components
		x=a;
		y=a;
		z=a;
	}
	
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
	
	//-------------------------------------------------------------------------------
	
	//these methods are used for loading/saving V3 data to/from text files
	
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
	
	public String tostring(){ //converts V3 to a human readable string
		return "["+x+";"+y+";"+z+"]";
	}
	
	public String tostring_short(){ //converts V3 to a human readable string
		return x+";"+y+";"+z;
	}
	
	//-------------------------------------------------------------------------------
	
	public double squmagnitude(){ //vector magnitude squared, improves computational efficiency by avoiding square roots
		return x*x + y*y + z*z;
	}
	
	public double magnitude(){ //vector magnitude	
		return Math.sqrt(this.squmagnitude());
	}
	
	public double magpow(double pow){ //returns magnitude to an arbitrary power, using squmagnitude is efficient
		return Math.pow(this.squmagnitude(), pow/2.0);
	}
	
	public double sum(){ //sum of vector components
		return x+y+z;
	}
	
	public boolean iszero(){ //efficient check of zeroness
		return x==0 && y==0 && z==0;
	}
	
	public boolean isnonzero(){ //efficient check of nonzeroness
		return !this.iszero();
	}
	
	//-------------------------------------------------------------------------------		
	
	public void add(double a, double b, double c){ //adds to the components of this vector
		x += a;
		y += b;
		z += c;
	}
	
	public void add(double a){ //adds to the components of this vector
		this.add(a,a,a);
	}
	
	public void add(V3 v){ //adds another vector to this one
		this.add(v.x, v.y, v.z);
	}
		
	public V3 add2(double a, double b, double c){ //adds to the components of this vector
		return new V3(x+a, y+b, z+c);
	}
	
	public V3 add2(double a){ //adds to the components of this vector
		return this.add2(a,a,a);
	}
	
	public V3 add2(V3 v){  //adds another vector to this one
		return this.add2(v.x, v.y, v.z);
	}
	
	//-------------------------------------------------------------------------------
	
	public void sub(double a, double b, double c){ //subtracts from the components of this vector
		x -= a;
		y -= b;
		z -= c;
	}
	
	public void sub(double a){ //adds to the components of this vector
		this.sub(a,a,a);
	}
	
	public void sub(V3 v){ //adds another vector to this one
		this.sub(v.x, v.y, v.z);
	}
	
	public V3 sub2(double a, double b, double c){ //subtracts from the components of this vector
		return new V3(x-a, y-b, z-c);
	}
	
	public V3 sub2(double a){ //subtracts from the components of this vector
		return this.sub2(a,a,a);
	}
	
	public V3 sub2(V3 v){ //subtracts another vector from this one
		return this.sub2(v.x, v.y, v.z);
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
	
	public void dimscale(V3 v){ //multiplies the components of 2 vectors
		x*=v.x;
		y*=v.y;
		z*=v.z;
	}
	
	public V3 dimscale2(V3 v){ //multiplies the components of 2 vectors
		return new V3(x*v.x, y*v.y, z*v.z);
	}
	
	public void invdimscale(V3 v){ //divides the components one vector by another
		x/=v.x;
		y/=v.y;
		z/=v.z;
	}
	
	public V3 invdimscale2(V3 v){ //divides the components one vector by another
		return new V3(x/v.x, y/v.y, z/v.z);
	}
	
	//-------------------------------------------------------------------------------
	
	public void invert(){ //scales vector by -1
		x*=-1;
		y*=-1;
		z*=-1;
	}
	
	public V3 invert2(){ //returns vector scaled by -1
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
	
	//methods used to process vectors containing angles
	
	public V3 cosines(){
		return new V3(Math.cos(x), Math.cos(y), Math.cos(z));
	}
	
	public V3 sines(){
		return new V3(Math.sin(x), Math.sin(y), Math.sin(z));
	}
	
	//used to find the average best orbital parameters using the mean sines and cosines
	//this constructor makes a vector containing 3 angles from vectors containing coordinates
	public V3(V3 sin, V3 cos){ 
		this.set(Math.atan2(sin.x, cos.x), Math.atan2(sin.y, cos.y), Math.atan2(sin.z, cos.z));
	}
		
	//used to find how concentrated the test orbits are around the average best ones using the mean sines and cosines
	//this constructor makes a vector containing 3 magnitudes from vectors containing coordinates
	public V3(V3 a, V3 b, boolean c){ //the boolean is for overloading purposes
		this.set(Math.sqrt(a.x*a.x+b.x*b.x), Math.sqrt(a.y*a.y+b.y*b.y), Math.sqrt(a.z*a.z+b.z*b.z));
	}
	
	
}
