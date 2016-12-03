package utility;

public class Math_methods {

	public static V3 rotatepoint(V3 v, V3 r){ //rotates V3 v around the origin by the angle r
		
		double sinro=Math.sin(r.x);
		double cosro=Math.cos(r.x);	
		double sinpo=Math.sin(r.y);
		double cospo=Math.cos(r.y);		
		double sinaz=Math.sin(r.z);
		double cosaz=Math.cos(r.z);
		
		return new V3(
				v.x*cosaz*cospo-v.y*sinaz*cospo+v.z*sinpo,				
				v.x*(sinro*sinpo*cosaz+cosro*sinaz)+v.y*(-sinro*sinpo*sinaz+cosaz*cosro)-v.z*(sinro*cospo),
				v.x*(-cosro*sinpo*cosaz+sinro*sinaz)+v.y*(cosro*sinpo*sinaz+cosaz*sinro)+v.z*(cosro*cospo)
				);
		
	}
	
	public static V3 getrotation(V3 dp, V3 dv){ //returns the angular momentum resulting from a collision
		
		V3 pv = perpendicularpart(dv, dp); //the part of the relative velocity perpendicular to the axis of collision
							
		return new V3(rotationcomp(pv.z, pv.y, dp.z, dp.y), rotationcomp(pv.x, pv.z, dp.x, dp.z), rotationcomp(pv.y, pv.x, dp.y, dp.x));
		
	}
	
	public static double rotationcomp(double pva, double pvb, double dpa, double dpb){ 
		//finds the component of rotation around a given axis resulting from a collision
		//pv is perpendicular velocity, dp is delta position vector
		
		if(dpa != 0 || dpb != 0){
			//return (1-Math.abs(cosdot(new V3(pva, pvb, 0), new V3(dpa, dpb, 0))))*getsign(pva, pvb, dpa, dpb)*Math.sqrt((pva*pva+pvb*pvb)/(dpa*dpa+dpb*dpb));
			//System.out.println(cosdot(new V3(pva, pvb, 0), new V3(dpa, dpb, 0)));
			
			V3 dv = new V3(pva, pvb, 0);
			V3 dp = new V3(dpa, dpb, 0);
			
			return perpendicularpart(dv,dp).magnitude()*dp.magnitude()*getsign(pva, pvb, dpa, dpb);
			//the return value is in units of m^2/s
			
		}else{
			return 0;
		}
		
	}
	
	public static int getsign(double ax, double ay, double bx, double by){ //dp = b, pv = a
		//gets sign for rotation calculations
		if(bx < 0){
			return (int) Math.signum(ay - ax*by/bx);
		}else if(bx > 0){
			return (int) -Math.signum(ay - ax*by/bx);
		}else{
			return (int) Math.signum(ax);
		}	
		
	}
	
	public static double capcosine(double d){ 
		//fixes floating point errors NAN errors resulting from using the dot product to compute cosines
		
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
	
	public static double cosdot(V3 a, V3 b){ //cosine of the angle between a and b, obtained by dividing the dot product by the magnitudes
		
		return capcosine((a.x*b.x+a.y*b.y+a.z*b.z)/(a.magnitude()*b.magnitude()));
		
	}
	
	public static V3 randomangle(){ //each component is in the range of +-PI
		
		return new V3(Math.random(), Math.random(), Math.random()).sub2(.5, .5, .5).scale2(2*Math.PI);
				
	}
		
}
