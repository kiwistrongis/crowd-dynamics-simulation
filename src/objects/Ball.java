package objects;
public class Ball {
	public double radius;
	public double mass;
	public Pointd p;
	public Pointd v;
	
	public Ball( Pointd p, Pointd v, double mass, double radius){
		this.p = p;
		this.v = v;
		this.mass = mass;
		this.radius = radius;}
	
	public Pointd momentum(){
		return v.mult(mass);}
	
	public double distance( Ball b){
		return p.distance(b.p) - b.radius - this.radius;}
	public double distance( Line line){
		return p.distance(line) - radius;}
}
