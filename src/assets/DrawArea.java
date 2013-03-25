package assets;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.*;
import javax.imageio.ImageIO;

import objects.*;

public class DrawArea extends JPanel {
	public static final int pointDetectRadius = 15;

	// major objects
	Gui gui;
	Plot plot;
	Simulation sim;

	// minor fields
	public boolean mouseActive;
	public Point mouse;
	public Point mouse_old;
	public int area_width;
	public int area_height;
	public BufferedImage background;
	public BufferedImage bg_original;
	public int background_width;
	public int background_height;
	public int original_width;
	public int original_height;
	public boolean bg_loaded;

	public DrawArea( Gui gui){
		this.gui = gui;
		plot = gui.plot;
		sim = gui.sim;

		setBackground( plot.bg_colour);
		mouseActive = false;
		mouse = new Point();
		
		//set up background image
		try{
			bg_original = ImageIO.read(
				new File(gui.background_filename));
			original_width = bg_original.getWidth();
			original_height = bg_original.getHeight();			
			bg_loaded = true;}
		catch(IOException e){ System.out.println(e);}
		rescaleBackground();}

	public void paintComponent(Graphics g){
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		//draw bg
		g2.drawImage(background,
			area_width/2 - background_width/2,
			area_height/2 - background_height/2, null);/**/
		//plot.drawAxis(g2);
		plot.adjustView();
		plot.drawBox(g2, sim.box);
		plot.drawBalls( g2, sim.balls, sim.box.walls);}
		//draw a black circle over the first point found
		// that is close enough to the mouse
		/*if (mouseActive){
			Point mousePoint = new Point(mouse.x,mouse.y);
			for(Point p : sim.nodes)
				if(p.distance(mousePoint) < pointDetectRadius){
					g2.setColor(Color.BLACK);
					g2.fillOval(p.x - pointRadius,
						p.y - pointRadius,
				2 * pointRadius, 2 * pointRadius);
					break;}}*/
	
	public void rescaleBackground(){
		area_width = this.getWidth();
		area_height = this.getHeight();
		plot.width = area_width;
		plot.height = area_height;
		if( bg_loaded){
			double scale = Math.max(
				Math.max(
					(double) gui.window_width / original_width,
					(double) gui.window_height / original_height),
				Math.max(
					(double) area_width / original_width,
					(double) area_height / original_height));

			background_width = (int) Math.round( scale * original_width);
			background_height = (int) Math.round( scale * original_height);

			background = new BufferedImage( background_width,
				background_height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = background.createGraphics();
			g.drawImage(bg_original, 0, 0, background_width,
				background_height, null);
			g.dispose();}}
}