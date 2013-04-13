package objects;
import java.util.Vector;
public class Box {
	public Vector<Pointd> points;
	public Pointd a, b, c, d;
	public Vector<Line> walls;
	public Line ab, bc, cd, da;
	
	public Box( double x1, double x2, double y1, double y2){
		points = new Vector<Pointd>();
		points.add( this.a = new Pointd( x1, y1));
		points.add( this.b = new Pointd( x2, y1));
		points.add( this.c = new Pointd( x2, y2));
		points.add( this.d = new Pointd( x1, y2));
		walls = new Vector<Line>();
		walls.add( ab = new Line( a, b));
		walls.add( bc = new Line( b, c));
		walls.add( cd = new Line( c, d));
		walls.add( da = new Line( d, a));}

	public boolean equals( Box other){
		return 
			this.a.equals(other.a) &&
			this.b.equals(other.b) &&
			this.c.equals(other.c) &&
			this.d.equals(other.d);}

	public String toString(){
		return String.format(
			"{\"x\":[%f,%f],\"y\":[%f,%f]}",
			a.x, c.x, a.y, c.y);}
}
