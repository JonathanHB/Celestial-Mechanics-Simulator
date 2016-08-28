package objects;

import java.awt.Color;

import physics.Motion;
import utility.Main_class;
import utility.Math_methods;
import utility.V3;

public class Cable {
	
	public double axialresponse; //maintains cable length in (m/s)/m = 1/s = hz	
	double lateralresponse; //straightens cable in (m/s)/m = 1/s = hz; unimplemented
	
	public Trail t;
	
	public double node_spacing;
	double maxlength;
	
	public Color col;

	public Cablenode[] nodes;
	
	public Line[] links;
	
	public Cable(double ax, double spacing, double maxlen, int length, int ref_ent, Color c, Color ct, int traillength, double res){
		
		col = c;
		axialresponse = ax;		
		node_spacing = spacing;		
		maxlength = maxlen;
		nodes = new Cablenode[length];
		links = new Line[length-1];
		generate(length, ref_ent, c);
		t=new Trail(
				traillength,
				res,
				center_of_mass(),
				ct
				);
		
	}
	
	public Cable(double ax, double spacing, Color c, Color ct, Cablenode[] cn, int traillength, double res){
		
		col = c;
		axialresponse = ax;
		node_spacing = spacing;
		maxlength = node_spacing*1.5;
		
		nodes = cn;
		links = new Line[cn.length-1];
		
		for(int x = 0; x<cn.length-1; x++){ //separate for loop keeps nodes[x+1] from throwing nullpointer errors		
				links[x] = new Line(cn[x].p, cn[x+1].p, c);
				links[x].setcenter();
		}
		
		t=new Trail(
				traillength,
				res,
				center_of_mass(),
				ct
				);
		
	}
	
	public void generate(int length, int ref_ent, Color c){ //check for 0 rotation
		
		double gsoradius = Math.cbrt(Motion.G*Main_class.elist.get(ref_ent).mass/Main_class.elist.get(ref_ent).rotation.squmagnitude());
		double gsovelocity = Math.sqrt(Motion.G*Main_class.elist.get(ref_ent).mass/gsoradius);
		
		for(int x = 0; x<length; x++){
			
			double altitude = gsoradius+(x-(length-1.0)/2.0)*node_spacing;
			nodes[x] = new Cablenode(
				Math_methods.rotatepoint(new V3(0, altitude, 0), Main_class.elist.get(ref_ent).rotation).add2(Main_class.elist.get(ref_ent).position),
				Math_methods.rotatepoint(new V3(0, 0, gsovelocity*altitude/gsoradius), Main_class.elist.get(ref_ent).rotation).add2(Main_class.elist.get(ref_ent).velocity)
			);
			
		}
		
		for(int x = 0; x<length; x++){ //separate for loop keeps nodes[x+1] from throwing nullpointer errors
			
			if(x!=length-1){
				links[x] = new Line(nodes[x].p, nodes[x+1].p, c);
				links[x].setcenter();
			}
		}
			
	}
	
	public void realign(V3 dp, V3 dv){
		for(Cablenode c : nodes){
			c.position.sub(dp);
			c.p.position.sub(dp);
			c.velocity.sub(dv);
		}
		for(Line l : links){
			l.center.sub(dp);
		}
		
	}
	
	public boolean[] checkforbreaks(){
		boolean anybreak = false;
		boolean[] output = new boolean[nodes.length-1];
		for(int x = 0; x<nodes.length-1; x++){			
			V3 deltapos = nodes[x+1].position.sub2(nodes[x].position);
			output[x] = deltapos.magnitude() > maxlength;
			if(output[x]){
				anybreak = true;
			}
		}
		output[nodes.length-2] = anybreak;
		return output;
	}
	
	public void move(double increment){
		
		for(Cablenode c: nodes){
			c.move(increment);
		}
		for(Line l: links){
			l.setcenter();
		}
		
		t.update(center_of_mass());
		
	}
	
	public void applygrav(Entity e, double increment){
		
		V3 dpos;
		double squdistance;
		double distance;
		double g_invrad;
		
		
		for(Cablenode c: nodes){
			
			dpos = c.position.sub2(e.position);
			
			squdistance = dpos.squmagnitude();
			distance = Math.sqrt(squdistance);
			g_invrad = increment*Motion.G/(squdistance*distance);//the last power is used for efficient scaling
											
			c.velocity.sub(dpos.scale2(e.mass*g_invrad));
			
		}
		
	}
	
	public void internalforces(double increment){
		
		for(int x = 0; x<nodes.length-1; x++){
			
			V3 deltapos = nodes[x+1].position.sub2(nodes[x].position);
			double reactionmagnitude = increment*axialresponse*(deltapos.magnitude() - node_spacing);
			
			if(Math.abs(reactionmagnitude*increment) > deltapos.magnitude()){
				reactionmagnitude = deltapos.magnitude()*Math.signum(increment);
			}
			
			nodes[x+1].velocity.sub(deltapos.tolength2(reactionmagnitude));
			nodes[x].velocity.add(deltapos.tolength2(reactionmagnitude));

		}
		
	}
	
	public V3 center_of_mass(){
		V3 sum = new V3(0,0,0);
		for(Cablenode n : nodes){
			sum.add(n.position);
		}
		return sum.invscale2(nodes.length);
	}
	
}
