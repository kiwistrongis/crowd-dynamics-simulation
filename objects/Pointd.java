package objects;
public class Pointd extends java.awt.geom.Point2D{
	public double x;
	public double y;
	public Pointd( double x, double y){
		this.x = x;
		this.y = y;}
	public void setLocation( double x, double y){
		this.x = x;
		this.y = y;}
	public double getX(){
		return x;}
	public double getY(){
		return y;}
	
	public void add(Pointd p){
		x+=p.x;
		y+=p.y;}
	public Pointd diff(Pointd p){
		return new Pointd( p.x-x, p.y-y);}
	public Pointd mult(double d){
		return new Pointd( x*d, y*d);}
	public double distance(Line line){
		return this.distanceVector( line).mag();}
	public Pointd distanceVector( Line line){
		return this.diff(line.a).proj( 
			line.a.diff(line.b).perp());}
	
	public Pointd proj(Pointd p){
		Pointd p_norm = p.norm();
		return p_norm.mult(p_norm.dot(this));}
	public double dot(Pointd p){
		return x*p.x + y*p.y;}
	public double mag(){
		return this.distance( 0.0, 0.0);}
	public Pointd norm(){
		double m = mag();
		return new Pointd( x/m, y/m);}
	public void normalize(){
		double m = mag();
		x /= m;
		y /= m;}
	public Pointd perp(){
		return new Pointd( y, -x);}
}
