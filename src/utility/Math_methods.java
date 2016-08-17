package utility;

public class Math_methods {

	public static V3 rotatepoint(V3 v, V3 r){ //make this a utility method called by both this class and graphics
		
		double sinaz=Math.sin(r.x);
		double cosaz=Math.cos(r.x);
		double sinpo=Math.sin(r.y);
		double cospo=Math.cos(r.y);
		double sinro=Math.sin(r.z);
		double cosro=Math.cos(r.z);		
		
		return new V3(
				v.x*cosaz*cospo-v.y*sinaz*cospo+v.z*sinpo,				
				v.x*(sinro*sinpo*cosaz+cosro*sinaz)+v.y*(-sinro*sinpo*sinaz+cosaz*cosro)-v.z*(sinro*cospo),
				v.x*(-cosro*sinpo*cosaz+sinro*sinaz)+v.y*(cosro*sinpo*sinaz+cosaz*sinro)+v.z*(cosro*cospo)
				);
		
	}
	
	public static V3 getrotation(V3 dp, V3 dv){ //mass is accounted for later, adds kinetic energy as a side effect
		
		V3 pv = perpendicularpart(dv.invert2(), dp); //the part of the velocity tangent to the axis of collision
			
		//Investigate why only the z values need to be negated
				
		return new V3(rotationcomp(pv.x, pv.y, dp.x, dp.y), rotationcomp(pv.x, pv.z, dp.x, dp.z), -rotationcomp(pv.z, pv.y, dp.z, dp.y));
		
	}
	
	public static double rotationcomp(double pva, double pvb, double dpa, double dpb){ //pv is perpendicular velocity, dp is delta position vector
		
		if(dpa != 0 || dpb != 0){
			return (1-Math.abs(cosdot(new V3(pva, pvb, 0), new V3(dpa, dpb, 0))))*getsign(pva, pvb, dpa, dpb)*Math.sqrt((pva*pva+pvb*pvb)/(dpa*dpa+dpb*dpb));
		}else{
			return 0;
		}
		
	}
	
	public static int getsign(double ax, double ay, double bx, double by){ //dp = b, pv = a
		
		if(bx < 0){
			return (int) Math.signum(ay - ax*by/bx);
		}else if(bx > 0){
			return (int) -Math.signum(ay - ax*by/bx);
		}else{
			return (int) Math.signum(ax);
		}	
		
	}
	
	public static double capcosine(double d){ //to fix floatingpoint errors and zero magnitude errors
		
		if(Double.isNaN(d)){
			return 0;
		}else if(d>1){
			return 1;
		}else if(d<-1){
			return -1;
		}else{			
			return d;
		}
		
	}
	
	public static V3 perpendicularpart(V3 a, V3 b){ //returns the component of a perpendicular to b
		
		return a.sub2(b.tolength2(cosdot(a, b)*a.magnitude()));
	}
	
	public static V3 parallelpart(V3 a, V3 b){ //returns the component of a parallel to b
		
		return b.tolength2(cosdot(a, b)*a.magnitude());
	}
	
/*	public static double arcsin(double x, double y){ //the two argument arcsin of x, which is a ratio (y just gives sign)  

		if(y >= 0){			
			return Math.asin(x);			
		}else{
			return Math.asin(-x) + Math.PI;
		}

	}*/
	
	public static double cosdot(V3 a, V3 b){ //cosine of the dot product of a and b
		
		return capcosine((a.x*b.x+a.y*b.y+a.z*b.z)/(a.magnitude()*b.magnitude()));
		
	}
		
}
