package assets;

import java.lang.Math;
import java.util.Observable;
import java.util.Random;
import java.util.Vector;
import org.opensourcephysics.numerics.*;

import objects.*;

public class Simulation extends Observable implements ODE {
	//
	public ODESolver solver;
	public double state[];
	public Box[] boxes;
	public Entity[] entities;
	public boolean paused;
	public Object pauseLock;
	//sim parameters
	public int i;
	public int period;
	public double dt;
	
	public Simulation (){
		period = 20;
		paused = false;
		pauseLock = new Object();
		dt = 0.0001;
		//do a default environment setup
		load( new Environment());}

	public void load( Environment env){
		boxes = env.getBoxes();
		entities = env.getEntities();}

	public void setup(){
		state = new double[1 + 4*entities.length];
		//initial time
		state[0] = 0.0;

		//write entities to state
		writeEntitysToState();
		
		//other setup
		this.i = 0;
		this.solver = new RK4(this);
		this.solver.initialize(dt);}
	
	private void writeEntitysToState(){
		for( int i = 0; i < entities.length; i++){
			state[ 2*i + 1] = entities[i].p.x;
			state[ 2*i + 2] = entities[i].p.y;}}

	private void readEntitysFromState(double[] state){
		for( int i = 0; i < entities.length; i++){
			entities[i].p.x = state[ 2*i + 1];
			entities[i].p.y = state[ 2*i + 2];}}

	public double[] getState() {
		writeEntitysToState();
		return state;}

	public void getRate(double[] state, double[] rate){
		readEntitysFromState(state);
		rate[0] = 1.0;//time
		for(int i = 0; i < entities.length; i++){
			Pointd v = entities[i].getInfluence();
			rate[ 2*i + 1] = v.x;
			rate[ 2*i + 2] = v.y;}}
	
	public boolean handleCollisions(){
		boolean noCollisions = true;
		Pointd dist;
		for(int i = 0; i < entities.length; i++){
			Entity entity = entities[i];
			for( Box box : boxes){
				for(Line wall : box.walls){
					dist = entity.p.distanceVector(wall);
					if(dist.mag() < entity.radius){
						Pointd push = dist.norm();
						push.x *= -entity.radius;
						push.y *= -entity.radius;
						push.add(dist);
						entity.p.add(push);
						Pointd force = entity.v.proj(dist);
						force.x *= -2.0;
						force.y *= -2.0;
						entity.v.add(force);
						//so that we know a collision happened
						noCollisions=false;}}}
			for(int j = i+1; j < entities.length; j++){
				Entity other = entities[j];
				dist = entity.p.diff(other.p);
				double overlap = entity.radius + other.radius - dist.mag();
				if( overlap > 0.0){
					//push them apart
					overlap /= 2.0;
					Pointd push = dist.norm();
					push.x *= -overlap;
					push.y *= -overlap;
					entity.p.add(push);
					Pointd pushOther = dist.norm();
					pushOther.x *= overlap;
					pushOther.y *= overlap;
					other.p.add(pushOther);
					//bounce them
					double totalMass = entity.mass + other.mass;
					double entity_mc1 = ( entity.mass - other.mass) / totalMass;
					double entity_mc2 = ( other.mass * 2.0) / totalMass;
					double other_mc1 = ( other.mass - entity.mass) / totalMass;
					double other_mc2 = ( entity.mass * 2.0) / totalMass;
					
					Pointd  entity_oldv =  entity.v.proj(dist);
					Pointd other_oldv = other.v.proj(dist);
					Pointd  entity_newv =  entity_oldv.mult( entity_mc1);
					Pointd other_newv = other_oldv.mult(other_mc1);

					entity_newv.add(other_oldv.mult( entity_mc2));
					other_newv.add( entity_oldv.mult(other_mc2));
					
					 entity.v.add( entity_oldv.mult(-1.0));
					 entity.v.add( entity_newv);
					other.v.add(other_oldv.mult(-1.0));
					other.v.add(other_newv);
					
					//so that we know a collision happened
					noCollisions=false;}}}
		return noCollisions;}

	public void step (){
		i++;
		solver.step();
		handleCollisions();
		setChanged();
		notifyObservers();}
}
