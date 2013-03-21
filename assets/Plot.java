package assets;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.RenderingHints;
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
	int x_offset;
	int y_offset;
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
		this.points = new Vector<Pointd>();
		this.origin = new Pointd(0.0, 0.0);}
	
	public void drawAxis(Graphics2D g){
		Point origin_pp = plotPoint( origin);
		//draw axis 
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
		//draw ball point
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
		
	public void fit( Vector<Pointd> points){
		Pointd sum = new Pointd(0.0,0.0);
		for(Pointd p : points)
			sum.add(p);
		x_offset = (int) Math.round( scale * sum.x / points.size());
		y_offset = (int) Math.round( scale * sum.y / points.size());}
	
	public void addTracePoint( Pointd newp){
		Pointd p = new Pointd( newp.x + 0.0, newp.y + 0.0);
		if( last.distanceSq(p) > precision ){
			points.add(p);
			last = p;}}
	
	public Point plotPoint(Pointd p){
		return new Point(
			plot_width/2 - x_offset + (int) Math.round( scale * p.x ),
			plot_height/2 + y_offset - (int) Math.round( scale * p.y ));}

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
		if( x==0.0)
			return 0.5;
		else
			return 0.5 - 
				(1.0 - Math.sqrt( 4.0 * Math.pow(x,2.0) + 1.0))/
					( 4.0 * x);}
		//f = @(x) 0.5 - (1.0 - sqrt( 4.0 * x.^2.0 + 1.0))/ ( 4.0 * x);

	public void moveView(int dx, int dy){
		x_offset += dx*0.1*plot_width;
		y_offset += dy*0.1*plot_width;}
}
