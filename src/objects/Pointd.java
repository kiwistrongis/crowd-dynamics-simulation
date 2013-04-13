package objects;
public class Pointd extends java.awt.geom.Point2D{
	public double x;
	public double y;
	
	public Pointd(){
		this.x = 0;
		this.y = 0;}
	public Pointd( Pointd other){
		this.x = other.x;
		this.y = other.y;}
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
		Pointd pa = this.diff(line.a);
		Pointd l = line.a.diff(line.b);
		double pa_l = pa.dot( l);
		/*System.out.printf(
			"x: %f, y: %f pa_l: %f, l.mag(): %f\n",
			x, y, pa_l, l.mag());//*/
		if( pa_l > 0)
			return pa;
		else if( pa_l < -l.dot(l))
			return this.diff(line.b);
		else
			return pa.proj( l.perp());}
	
	public Pointd proj(Pointd p){
		Pointd p_norm = p.norm();
		return p_norm.mult(p_norm.dot(this));}
	public double dot(Pointd p){
		return x*p.x + y*p.y;}
	public double mag(){
		return this.distance( 0.0, 0.0);}
	public Pointd norm(){
		double m = mag();
		if( m != 0)
			return new Pointd( x/m, y/m);
		else
			return new Pointd();}
	public void normalize(){
		double m = mag();
		if( m!= 0){
			x /= m;
			y /= m;}}
	public Pointd perp(){
		return new Pointd( y, -x);}

	public boolean equals( Pointd other){
		return
			this.x == other.x &&
			this.y == other.y;}
}
