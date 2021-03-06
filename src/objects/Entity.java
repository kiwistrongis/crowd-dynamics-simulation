package objects;
import java.util.Vector;
import java.util.LinkedList;
import java.util.Queue;
public class Entity extends Ball {
	public Queue<Pointd> path;
	public Vector<Box> memory;
	public Pointd dest;
	public Pointd view;
	public double max_velocity;
	public double tolerance;

	public Entity(
			Pointd p, double max_velocity, Pointd dest,
			double tolerance, double mass, double radius){
		super( p, new Pointd(), mass, radius);
		this.dest = dest;
		this.max_velocity = max_velocity;
		this.tolerance = tolerance;
		path = new LinkedList<Pointd>();
		memory = new Vector<Box>();
		makePath();}

	public void makePath(){
		path.add( dest);
		path.add( new Pointd(p));}

	public void remember( Box box){
		if( ! memory.contains( box))
			memory.add( box);}

	public Pointd getInfluence(){
		Pointd step = p.diff( path.peek());
		double distance = step.mag();
		if( distance < radius*tolerance){
			if( path.size() > 1){
				path.remove();
				return getInfluence();}
			else return new Pointd();}
		step.normalize();
		return step.mult(max_velocity);}
}