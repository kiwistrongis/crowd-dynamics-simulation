package assets;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.Vector;

import objects.*;

public class Plot {
	Vector<Pointd> points;
	Pointd last;
	Pointd origin;

	int plot_width;
	int plot_height;
	double precision;
	double scale;
	double scale_dest;
	Pointd offset;
	Pointd offset_dest;
	double lastTime;
	double zoom_period;
	Point zoom_staticLoc;
	double move_period;
	Color axis_colour;
	Color ball_colour;
	Color box_colour;
	Color compress_colour;
	Color decompress_colour;
	Color bg_colour;
	Color line_colour;
	Color label_colour;
	Font label_font;
	int label_defaultPadding;
	
	public Plot() {
		offset = new Pointd();
		move_period = 0.1;
		zoom_period = 0.1;
		offset_dest = new Pointd();
		zoom_staticLoc = new Point();
		this.points = new Vector<Pointd>();
		this.origin = new Pointd(0.0, 0.0);
		lastTime = System.currentTimeMillis()/1000.0;}
	
	// drawing functions
	public void drawAxis(Graphics2D g){
		Point origin_pp = plotPoint( origin);
		g.setColor(axis_colour);
		g.drawLine( origin_pp.x, origin_pp.y, origin_pp.x, 0);
		g.drawLine( origin_pp.x, origin_pp.y, origin_pp.x, plot_height);
		g.drawLine( origin_pp.x, origin_pp.y, 0, origin_pp.y);
		g.drawLine( origin_pp.x, origin_pp.y, plot_width, origin_pp.y);}

	public void drawBalls( Graphics2D g, Ball[] balls,
			Vector<Line> walls){
		for( int i = 0; i < balls.length; i++)
			drawBall( g, balls, i, walls);}
	public void drawBall( Graphics2D g, Ball[] balls, int ball_i,
			Vector<Line> walls){
		Ball ball = balls[ball_i];
		Point ball_plotPoint = plotPoint(ball.p);
		int ball_r = (int) Math.round( scale*ball.radius);
		//find compresssion
		double compression = ball.distance(balls[
			ball_i!=0 ? 0 : 1]);
		for( int i = 1; i < balls.length; i++)
			if( i != ball_i){
				double dist = ball.distance(balls[i]);
				if( dist < compression )
					compression = dist;}
		for( Line wall : walls){
			double dist = ball.distance(wall);
			if( dist < compression )
				compression = dist;}
		//draw ball point
		g.setColor( mixColours( decompress_colour, compress_colour,
				compression));
		g.fillOval( ball_plotPoint.x - ball_r,
			ball_plotPoint.y - ball_r,
			2 * ball_r, 2 * ball_r);
		//label ball point to bottom-right of point
		String label = String.format("(%.2f, %.2f)",
			ball.p.x, ball.p.y);
		FontMetrics fontMetrics = g.getFontMetrics();
		int label_width = fontMetrics.stringWidth(label);
		int label_height = fontMetrics.getAscent();
		int label_padding = (int) Math.round( ball_r / 1.4); //sqrt(2) = 1.4
		g.setPaint( label_colour);
		g.drawString( label,
			ball_plotPoint.x + label_padding,
			ball_plotPoint.y + label_padding + label_height);}

	public void drawBox( Graphics2D g, Box box){
		g.setColor( box_colour);
		for( Line line : box.walls){
			Point a = plotPoint( line.a);
			Point b = plotPoint( line.b);
			g.drawLine( a.x, a.y, b.x, b.y);}}

	// view functions
	public void adjustView(){
		double currTime = System.currentTimeMillis()/1000.0;
		double dtime = currTime - lastTime;
		double dt = dtime/move_period;
		offset = offset.mult( 1-dt);
		offset.add( offset_dest.mult( dt));
		Pointd staticl_rp1 = inversePlotPoint( zoom_staticLoc);
		dt = dtime/zoom_period;
		scale *= (1-dt);
		scale += dt*scale_dest;
		Pointd staticl_rp2 = inversePlotPoint( zoom_staticLoc);
		Pointd diff = staticl_rp2.diff( staticl_rp1);
		offset.add( diff);
		offset_dest.add( diff);
		lastTime = currTime;}

	public void moveViewDest(double dx, double dy){
		offset_dest.x += dx*0.1*plot_width/scale;
		offset_dest.y += dy*0.1*plot_width/scale;}

	public void zoom( double ratio){
		zoom( ratio, 0,0);}
	public void zoom( double ratio, int mouse_x, int mouse_y){
		zoom( ratio, new Point( mouse_x, mouse_y));}
	public void zoom( double ratio, Point staticLoc){
		scale_dest = scale;
		scale_dest *= Math.pow(1.1, ratio);
		offset_dest = offset;
		zoom_staticLoc = staticLoc;}
		
	// misc
	public void fit( Vector<Pointd> points){
		Pointd sum = new Pointd(0.0,0.0);
		for(Pointd p : points)
			sum.add(p);
		offset.x = (int) Math.round( sum.x / points.size());
		offset.y = (int) Math.round( sum.y / points.size());}
	
	public void addTracePoint( Pointd newp){
		Pointd p = new Pointd( newp.x + 0.0, newp.y + 0.0);
		if( last.distanceSq(p) > precision ){
			points.add(p);
			last = p;}}
	
	public Point plotPoint(Pointd p){
		return new Point(
			(int) Math.round( plot_width/2.0 - (offset.x + p.x)*scale),
			(int) Math.round( plot_height/2.0 + (offset.y - p.y)*scale));}
	public Pointd inversePlotPoint(Point p){
		return new Pointd(
			( p.x - plot_width/2.0)/scale + offset.x,
			( plot_height/2.0 - p.y)/scale - offset.y);}

	public Color mixColours( Color d, Color e, double value){
		double ratio = squisher(value);
		double invRatio = 1.0 - ratio;
		int r = (int) Math.round(
			ratio * d.getRed() + invRatio * e.getRed());
		int g = (int) Math.round(
			ratio * d.getGreen() + invRatio * e.getGreen());
		int b = (int) Math.round(
			ratio * d.getBlue() + invRatio * e.getBlue());
		int a = (int) Math.round(
			ratio * d.getAlpha() + invRatio * e.getAlpha());
		return new Color( r % 256, g % 256, b % 256, a % 256);}
	public double squisher(double x){
		return x == 0.0 ?
			0.5 : 0.5 - 
				(1.0 - Math.sqrt( 4.0 * Math.pow(x,2.0) + 1.0))/
					( 4.0 * x);}
}
