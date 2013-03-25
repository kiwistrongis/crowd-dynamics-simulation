package assets;

import java.lang.Math;
import java.util.Observable;
import java.util.Random;
import java.util.Vector;
import org.opensourcephysics.numerics.*;

import objects.*;

public class Simulation extends Observable implements ODE {
	public double state[];
	public Box box;
	public Ball[] balls;
	public double tolerance;
	public int i, period;
	public ODESolver solver;
	public boolean paused;
	public Object pauseLock;
	
	public Simulation (){}
	public Simulation ( int period, double dt,
			double minRadius, double maxRadius,
			double minMass, double maxMass,
			double maxVelocity, int n_balls,
			double room_width, double room_height){
		this.state = new double[1 + 2*n_balls];
		this.state[0] = 0.0; //initial time
		
		//create box
		double minPos_x = -room_width/2.0;
		double maxPos_x = minPos_x + room_width;
		double minPos_y = -room_height/2.0;
		double maxPos_y = minPos_y + room_height;
		this.box = new Box(
			minPos_x, maxPos_x,
			minPos_y, maxPos_y);
		
		//generate balls
		double radius_variance = maxRadius - minRadius;
		double mass_variance = maxMass - minMass;
		Random random = new Random();
		this.balls = new Ball[n_balls];
		for( int i = 0; i < n_balls; i++){
			double v_r = maxVelocity * random.nextDouble();
			double v_theta = 2.0*Math.PI*random.nextDouble();
			balls[i] = new Ball(
				new Pointd(
					minPos_x + room_width*random.nextDouble(),
					minPos_y + room_height*random.nextDouble()),
				new Pointd(
					v_r * Math.cos(v_theta),
					v_r * Math.sin(v_theta)),
				minMass + mass_variance*random.nextDouble(),
				minRadius + radius_variance*random.nextDouble());}
						
		//write balls to state
		writeBallsToState();
		//other setup
		this.i = 0;
		this.period = period;
		this.paused = false;
		this.pauseLock = new Object();
		this.solver = new RK4(this);
		this.solver.initialize(dt);}
	
	private void writeBallsToState(){
		for( int i = 0; i < balls.length; i++){
			state[ 2*i + 1] = balls[i].p.x;
			state[ 2*i + 2] = balls[i].p.y;}}
	private void 	readBallsFromState(double[] state){
		for( int i = 0; i < balls.length; i++){
			balls[i].p.x = state[ 2*i + 1];
			balls[i].p.y = state[ 2*i + 2];}}
	/*/asdfsldfj/*/
	public double[] getState() {
		writeBallsToState();
		return state;}
	public void getRate(double[] state, double[] rate){
		readBallsFromState(state);
		
		rate[0] = 1.0;//time
		for(int i = 0; i < balls.length; i++){
			rate[ 2*i + 1] = balls[i].v.x;
			rate[ 2*i + 2] = balls[i].v.y;}}
	
	public boolean handleCollisions(){
		boolean noCollisions = true;
		Pointd dist;
		for(int i = 0; i < balls.length; i++){
			Ball ball = balls[i];
			for(Line wall : box.walls){
				dist = ball.p.distanceVector(wall);
				if(dist.mag() < ball.radius){
					Pointd push = dist.norm();
					push.x *= -ball.radius;
					push.y *= -ball.radius;
					push.add(dist);
					ball.p.add(push);
					Pointd force = ball.v.proj(dist);
					force.x *= -2.0;
					force.y *= -2.0;
					ball.v.add(force);
					//so that we know a collision happened
					noCollisions=false;}}
			for(int j = i+1; j < balls.length; j++){
				Ball other = balls[j];
				dist = ball.p.diff(other.p);
				double overlap = ball.radius + other.radius - dist.mag();
				if( overlap > 0.0){
					//push them apart
					overlap /= 2.0;
					Pointd push = dist.norm();
					push.x *= -overlap;
					push.y *= -overlap;
					ball.p.add(push);
					Pointd pushOther = dist.norm();
					pushOther.x *= overlap;
					pushOther.y *= overlap;
					other.p.add(pushOther);
					//bounce them
					double totalMass = ball.mass + other.mass;
					double ball_mc1 = ( ball.mass - other.mass) / totalMass;
					double ball_mc2 = ( other.mass * 2.0) / totalMass;
					double other_mc1 = ( other.mass - ball.mass) / totalMass;
					double other_mc2 = ( ball.mass * 2.0) / totalMass;
					
					Pointd  ball_oldv =  ball.v.proj(dist);
					Pointd other_oldv = other.v.proj(dist);
					Pointd  ball_newv =  ball_oldv.mult( ball_mc1);
					Pointd other_newv = other_oldv.mult(other_mc1);

					 ball_newv.add(other_oldv.mult( ball_mc2));
					other_newv.add( ball_oldv.mult(other_mc2));
					
					 ball.v.add( ball_oldv.mult(-1.0));
					 ball.v.add( ball_newv);
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
