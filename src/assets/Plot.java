package assets;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.Vector;

import objects.*;

public class Plot {
	public Vector<Pointd> points;
	public Pointd last;
	public Pointd origin;

	public int width;
	public int height;
	public double precision;
	public double scale;
	public double scale_dest;
	public Pointd offset;
	public Pointd offset_dest;
	public double lastTime;
	public double zoom_period;
	public Point zoom_staticLoc;
	public boolean zoom_staticLoc_enabled;
	public double move_period;
	public Color axis_colour;
	public Color entity_colour;
	public Color box_colour;
	public Color compress_colour;
	public Color decompress_colour;
	public Color bg_colour;
	public Color line_colour;
	public Color label_colour;
	public Font label_font;
	public int label_defaultPadding;
	public boolean drawPath;
	public boolean drawLabel;
	
	public Plot() {
		//variable initialization
		offset = new Pointd();
		offset_dest = new Pointd();
		zoom_staticLoc = new Point();
		zoom_staticLoc_enabled = false;
		points = new Vector<Pointd>();
		origin = new Pointd(0.0, 0.0);
		lastTime = (double) System.currentTimeMillis()/1000;
		//default fields
		move_period = 0.1;
		zoom_period = 0.1;
		
		scale = 100.0;
		scale_dest = scale;
		precision = 0.001;

		axis_colour = new Color( 0x00, 0x33, 0x00, 0xff);
		bg_colour = new Color( 0x00, 0x00, 0x00, 0xff);
		line_colour = new Color( 0xe0, 0xe0, 0xe0, 0xff);
		entity_colour = new Color( 0xe0, 0xe0, 0xe0, 0xff);
		box_colour = new Color( 0xe0, 0xe0, 0xe0, 0xff);
		compress_colour = new Color( 0xff, 0x00, 0x00, 0xff);
		decompress_colour = new Color( 0x00, 0x00, 0x00, 0xff);
		label_colour = new Color( 0xe0, 0xe0, 0xe0, 0xff);

		drawPath = false;
		drawLabel = false;
		label_font = new Font( "Monospace", Font.PLAIN, 11);
		label_defaultPadding = 0;}
	
	// drawing functions
	public void drawAxis(Graphics2D g){
		Point origin_pp = plotPoint( origin);
		g.setColor(axis_colour);
		g.drawLine( origin_pp.x, origin_pp.y, origin_pp.x, 0);
		g.drawLine( origin_pp.x, origin_pp.y, origin_pp.x, height);
		g.drawLine( origin_pp.x, origin_pp.y, 0, origin_pp.y);
		g.drawLine( origin_pp.x, origin_pp.y, width, origin_pp.y);}

	public void drawEntities( Graphics2D g, Entity[] entities,
			Box[] boxes){
		for( int i = 0; i < entities.length; i++)
			drawEntity( g, entities, i, boxes);}
	public void drawEntity( Graphics2D g, Entity[] entities, int entity_i,
			Box[] boxes){
		Entity entity = entities[entity_i];
		Point entity_plotPoint = plotPoint(entity.p);
		int entity_r = (int) Math.round( scale*entity.radius);
		//find compresssion
		double compression = entity.distance(entities[
			entity_i!=0 ? 0 : 1]);
		for( int i = 1; i < entities.length; i++)
			if( i != entity_i){
				double dist = entity.distance(entities[i]);
				if( dist < compression )
					compression = dist;}
		for( Box box : boxes)
			for( Line wall : box.walls){
				double dist = entity.distance(wall);
				if( dist < compression )
					compression = dist;}
		//draw entity point
		g.setComposite( AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 1));
		g.setColor( mixColours( decompress_colour, compress_colour,
				compression));
		g.fillOval( entity_plotPoint.x - entity_r,
			entity_plotPoint.y - entity_r,
			2 * entity_r, 2 * entity_r);
		//draw entity path
		if( drawPath){
			g.setComposite( AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, (float) 0.35));
			g.setColor( Color.YELLOW);
			boolean firstConnection = true;
			Point last = entity_plotPoint;
			for( Pointd step : entity.path){
				Point next = plotPoint( step);
				g.drawLine( last.x, last.y, next.x, next.y);
				last = next;
				if( firstConnection){
					firstConnection = false;
					g.setColor( Color.BLACK);}}}
		//label entity point to bottom-right of point
		if( drawLabel){
			String label = String.format("(%.2f, %.2f)",
				entity.p.x, entity.p.y);
			FontMetrics fontMetrics = g.getFontMetrics();
			int label_width = fontMetrics.stringWidth(label);
			int label_height = fontMetrics.getAscent();
			int label_padding = (int) Math.round( entity_r / 1.4); //sqrt(2) = 1.4
			g.setPaint( label_colour);
			g.drawString( label,
				entity_plotPoint.x + label_padding,
				entity_plotPoint.y + label_padding + label_height);}}

	public void drawBox( Graphics2D g, Box box){
		g.setColor( box_colour);
		for( Line line : box.walls){
			Point a = plotPoint( line.a);
			Point b = plotPoint( line.b);
			g.drawLine( a.x, a.y, b.x, b.y);}}

	// view functions
	public void adjustView(){
		double currTime = (double) System.currentTimeMillis()/1000;
		double dtime = currTime - lastTime;
		double dt = dtime/move_period;
		offset = offset.mult( 1-dt);
		offset.add( offset_dest.mult( dt));
		Pointd staticl_rp1 = inversePlotPoint( zoom_staticLoc);
		dt = dtime/zoom_period;
		scale *= (1-dt);
		scale += dt*scale_dest;
		if( zoom_staticLoc_enabled){
			Pointd staticl_rp2 = inversePlotPoint( zoom_staticLoc);
			Pointd diff = staticl_rp2.diff( staticl_rp1);
			offset.add( diff);
			offset_dest.add( diff);}
		lastTime = currTime;}

	public void moveViewDest(double dx, double dy){
		offset_dest.x += dx*0.1*width/scale;
		offset_dest.y += dy*0.1*width/scale;}

	public void zoom( double ratio){
		scale_dest = scale;
		scale_dest *= Math.pow(1.1, ratio);
		zoom_staticLoc_enabled = false;}
	public void zoom( double ratio, Point staticLoc){
		zoom( ratio);
		offset_dest = offset;
		zoom_staticLoc = staticLoc;
		zoom_staticLoc_enabled = true;}
		
	// misc
	public void fit( Vector<Pointd> points){
		Pointd sum = new Pointd(0.0,0.0);
		Pointd min = new Pointd( points.get(0));
		Pointd max = new Pointd( points.get(0));
		for(Pointd p : points){
			sum.add(p);
			if(p.x < min.x)
				min.x = p.x;
			else if( p.x > max.x)
				max.x = p.x;
			if(p.y < min.y)
				min.y = p.y;
			else if( p.y > max.y)
				max.y = p.y;}

		offset_dest.x = (int) Math.round( sum.x / points.size());
		offset_dest.y = (int) Math.round( sum.y / points.size());

		double halfWidth = width / 2.0;
		double halfHeight = height / 2.0;
		double scale_temp = Math.min(
			Math.min(
				halfWidth / ( offset_dest.x - min.x),
				halfWidth / ( max.x - offset_dest.x)),
			Math.min(
				halfHeight / ( offset_dest.y - min.y),
				halfHeight / ( max.y - offset_dest.y)));
		scale_dest = scale_temp*0.9;
		zoom_staticLoc_enabled = false;}

	
	public void addTracePoint( Pointd newp){
		Pointd p = new Pointd( newp.x + 0.0, newp.y + 0.0);
		if( last.distanceSq(p) > precision ){
			points.add(p);
			last = p;}}
	
	public Point plotPoint(Pointd p){
		return new Point(
			(int) Math.round( width/2.0 + (offset.x + p.x)*scale),
			(int) Math.round( height/2.0 + (offset.y - p.y)*scale));}
	public Pointd inversePlotPoint(Point p){
		return new Pointd(
			offset.x - ( p.x - width/2.0)/scale,
			( height/2.0 - p.y)/scale - offset.y);}

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
