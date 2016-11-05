package objects;

import java.awt.Color;

import physics.Motion;
import utility.Main_class;
import utility.Math_methods;
import utility.V3;

public class Cable {
	
	public Trail t; //the trail drawn by the cable's center of mass
	
	public double node_spacing; //spacing between cable nodes
	public double maxlength; //maximum strain between cable nodes before the cable breaks
	
	public Color col; //cable color

	public Cablenode[] nodes; //nodes used to simulate cable
	
	public Line[] links; //links which generate forces between nodes
	
	public Entity primary_ent; //entity above which the cable is in geostationary orbit
		
	public Cable(double spacing, double maxlen, int length, Entity ref_ent, Color c, Color ct, int traillength, double res, int trail_refent){
		//constructor to create a cable in geostationary orbit over the ref_ent entity
		
		col = c;
		node_spacing = spacing;		
		maxlength = maxlen;
		nodes = new Cablenode[length];
		links = new Line[length-1];
		primary_ent = ref_ent;
		
		generate(length, ref_ent, c);
		
		t = new Trail(
				traillength,
				res,
				center_of_mass(),
				ct,
				trail_refent
				);
		
	}
	
	public Cable(double spacing, Color c, Color ct, Cablenode[] cn, int traillength, double res, int trail_refent){
		//constructor to create a cable when an existing cable breaks
		
		col = c;
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
				ct,
				trail_refent
				);
		
	}
	
	public void generate(int length, Entity primary, Color c){
		//generates a cable in geostationary orbit over "primary" entity
		//the method generates a cable at an arbitrary altitude and then uses bisection search to align it to
		//within cablebalance Newtons of geostationary orbit
		//the cable begins arbitrarily at the ascending or descending node of its orbit		
		
		double gsoradius = Math.cbrt(Motion.G*primary.mass/primary.rotation.squmagnitude());
		double gsospeed = Math.sqrt(Motion.G*primary.mass/gsoradius);
		
		boolean adjusting = true;
		double cablebalance = .000000000001; //maximum allowed imbalance in force on cable, in Newtons
		
		double[][] adjustedcable = new double[length][2];
		
		double deltarad = 1E12;
		double totaldr = 0;
					
		while(adjusting){ //adjusts cable altitude to balance forces
						
			double nf = netforce(cableframegen(gsoradius, gsospeed, totaldr), primary.mass);
		//	System.out.println(nf);

			if(Math.abs(nf) <= cablebalance){
				adjustedcable = cableframegen(gsoradius, gsospeed, totaldr);
				adjusting = false;
		//		System.out.println("done");
			}else if(nf<0){
				deltarad*=.7;
				deltarad = Math.abs(deltarad);

			}else{
				deltarad*=.7;
				deltarad = -Math.abs(deltarad);

			}
			
			totaldr += deltarad;
			
		}
							
		for(int x = 0; x<length; x++){ //generates cable once altitude is determined
						
			V3 node_long = new V3(0,0,Math.atan2(primary.rotation.y, primary.rotation.x));
			V3 inclination = new V3(0,-Math.atan2(primary.rotation.z, Math.sqrt(primary.rotation.x*primary.rotation.x+primary.rotation.y*primary.rotation.y)),0);
			
			nodes[x] = new Cablenode(
				Math_methods.rotatepoint(new V3(0, adjustedcable[x][0], 0), node_long).add2(primary.position),
				Math_methods.rotatepoint(Math_methods.rotatepoint(new V3(0, 0, adjustedcable[x][1]), inclination), node_long).add2(primary.velocity)
			);
						
		}
		
		for(int x = 0; x<length; x++){ //separate for loop keeps nodes[x+1] from throwing nullpointer errors
			
			if(x!=length-1){
				links[x] = new Line(nodes[x].p, nodes[x+1].p, c);
				links[x].setcenter();
			}
		}
			
	}
	
	public void realign(V3 dp, V3 dv){ //part of the altitude calculation code
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
		//finds breaks in cable where strain exceeds maxlength, passes these breaks
		//to object_manager.splitcable() to actually break the cable
		
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
	
	public void move(double increment){ //moves cable and draws trail
		
		for(Cablenode c: nodes){
			c.move(increment);
		}
		for(Line l: links){
			l.setcenter();
		}
		
		t.update(center_of_mass());
		
	}
	
	public void applygrav(Entity e, double increment){
		//apply Newton's law of gravitation to the cable; see the physics.movement.gravitation()
		
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
	
	public void internalforces(double increment){ //calculates and applies forces between nodes
		
		for(int x = 0; x<nodes.length-1; x++){
			
			V3 deltapos = nodes[x+1].position.sub2(nodes[x].position);			
 
			double reactionmagnitude = .5*(deltapos.magnitude() - node_spacing)*Math.signum(increment);
			V3 reactionvector = deltapos.tolength2(reactionmagnitude);
			
			nodes[x+1].velocity.sub(reactionvector);
			nodes[x].velocity.add(reactionvector);	
			
		//	System.out.println(reactionmagnitude*100000);	
			
			if(Main_class.stressvisualization){
				links[x].illumination.set(reactionmagnitude*1000);
			}
			
		}
		
	}
	
	public V3 center_of_mass(){ //calculates center of mass for trail drawing
		V3 sum = new V3(0,0,0);
		for(Cablenode n : nodes){
			sum.add(n.position);
		}
		return sum.invscale2(nodes.length);
	}
	
	public double netforce(double[][] nodedata, double mass){ 
		//for second index in nodedata: 0=position, 1=velocity
		//calculates net force on cable
		
		double sum = 0;
		
		for(double[] d : nodedata){
			sum += d[1]*d[1]/d[0] - Motion.G*mass/(d[0]*d[0]);
		}
		
		return sum;		
	}
	
	public double[][] cableframegen(double gsoradius, double gsospeed, double shift){
		//generates new node position set to calculate force on with netforce()
		
		double output[][] = new double[nodes.length][2];

		for(int x = 0; x<nodes.length; x++){
			
			output[x][0] = gsoradius+(x-(nodes.length-1.0)/2.0)*node_spacing+shift;
			output[x][1] = gsospeed*output[x][0]/gsoradius;
			
		}
		
		return output;
		
	}
	
}
