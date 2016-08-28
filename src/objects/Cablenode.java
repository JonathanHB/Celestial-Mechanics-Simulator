package objects;

import utility.V3;

public class Cablenode {

	public V3 position; //meters
	public V3 velocity; //meters per second
	
	public Point p;
	
	public Cablenode(V3 pos, V3 vel){
		position = new V3(pos);
		velocity = new V3(vel);
		p = new Point(position);
	}
	
	public Cablenode(Cablenode c){
		position = new V3(c.position);
		velocity = new V3(c.velocity);
		p = new Point(position);
	}
	
	public void move(double d){
		position.add(velocity.scale2(d));
		p.position.set(position);
	}
		
}
