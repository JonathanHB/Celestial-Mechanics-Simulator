package objects;

import utility.V3;

public class Cablenode {
	//a node in a cable; treated as having negligible mass

	public V3 position; //meters
	public V3 velocity; //meters per second
	
	public Point p; //used for rendering the point's associated links
	
	public Cablenode(V3 pos, V3 vel){ //creates a cable node with the specified pos/vel
		position = new V3(pos);
		velocity = new V3(vel);
		p = new Point(position);
	}
	
	public Cablenode(Cablenode c){ //deep copies another cablenode
		position = new V3(c.position);
		velocity = new V3(c.velocity);
		p = new Point(position);
	}
	
	public void move(double d){ //moves cablenode
		position.add(velocity.scale2(d));
		p.position.set(position);
	}
		
}
