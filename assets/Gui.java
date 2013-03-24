package assets;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.*;
import javax.imageio.ImageIO;

import objects.*;

public class Gui extends JFrame implements Observer{
	public Simulation sim;
	public Plot plot;
	public DrawArea drawArea;
	public Controller controller;
	String title;
	public int window_x;
	public int window_y;
	public int window_width;
	public int window_height;
	public Color background_colour;
	public String background_filename;
	public String icon_filename;

	public Gui(Simulation sim, Plot plot){
		this.sim = sim;
		this.plot = plot;
		sim.addObserver(this);}
	
	public void setup(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setTitle(title);
		setBounds( window_x, window_y, window_width, window_height);
		setBackground( background_colour);
		BufferedImage icon;
		try{
			icon = ImageIO.read(
				new File(icon_filename));
			setIconImage( icon);}
		catch(IOException e){ System.out.println(e);}
		Container frame = getContentPane();

		drawArea = new DrawArea();
		SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.NORTH, drawArea, 0,
			SpringLayout.NORTH, frame);
		layout.putConstraint(SpringLayout.WEST, drawArea, 0,
			SpringLayout.WEST, frame);
		layout.putConstraint(SpringLayout.EAST, drawArea, -0,
			SpringLayout.EAST, frame);
		layout.putConstraint(SpringLayout.SOUTH, drawArea, -0,
			SpringLayout.SOUTH, frame);
		frame.setLayout(layout);
		frame.add(drawArea);

		controller = new Controller( this);

		this.setFocusable(true);
		this.requestFocus();

		plot.fit(sim.box.points);
		
		try{ UIManager.setLookAndFeel(
			UIManager.getSystemLookAndFeelClassName());}
		catch( Exception e){
			System.out.println(e);}

		System.out.println("Done Loading GUI");
		setVisible(true);}
	
	
	public void update(Observable o, Object arg){
		drawArea.repaint();}
	public void close(){
		WindowEvent wev = 
			new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().
			getSystemEventQueue().postEvent(wev);}


	/** Draw Area sub-class
	 * handles drawing of stuff
	 **/
	private class DrawArea extends JPanel {
		private static final int pointRadius = 5;
		private static final int pointDetectRadius = 15;

		private boolean mouseActive;
		private Point mouse;
		private Point mouse_old;
		int area_width;
		int area_height;
		BufferedImage background;
		BufferedImage bg_original;
		int background_width;
		int background_height;
		boolean bg_loaded;

		public DrawArea(){
			setBackground( plot.bg_colour);
			mouseActive = false;
			mouse = new Point();
			
			//set up background image
			try{
				bg_original = ImageIO.read(
					new File(background_filename));
				bg_loaded = true;}
			catch(IOException e){ System.out.println(e);}
			reloadBackground();}

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
		
		public void reloadBackground(){
			area_width = this.getWidth();
			area_height = this.getHeight();
			plot.plot_width = area_width;
			plot.plot_height = area_height;
			if( bg_loaded){
				int original_width = bg_original.getWidth();
				int original_height = bg_original.getHeight();			
				double scale = Math.max(
					Math.max(
						(double) window_width / original_width,
						(double) window_height / original_height),
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
				g.dispose();}
			repaint();}
	}


	/** Controller sub-class
	 * handles all user input
	 **/
	private class Controller 
			implements MouseListener, MouseMotionListener,
				MouseWheelListener, KeyListener, ComponentListener {
		public boolean mouseActive;
		public boolean dragMode;
		public Point mouse;
		public Point mouse_old;
		public double zoom_ratio;

		public Controller( Gui gui){
			mouse = new Point();
			mouse_old = new Point();
			mouseActive = false;
			dragMode = false;
			zoom_ratio = 2.0;

			gui.addKeyListener(this);
			drawArea.addMouseListener(this);
			drawArea.addMouseMotionListener(this);
			drawArea.addMouseWheelListener(this);
			drawArea.addComponentListener(this);
			drawArea.mouse = mouse;
			drawArea.mouseActive = this.mouseActive;}

		//mouse handling
		public void mousePressed(MouseEvent e) {
			Point newMouse = new Point( e.getX(), e.getY());
			switch(e.getButton()){
				case 1:
					dragMode = true;
					break;
				case 2:
					break;
				case 3:
					break;
				default:
					break;}
			repaint();}
		public void mouseMoved(MouseEvent e){
			mouse_old = mouse;
			mouse.x = e.getX();
			mouse.y = e.getY();}
		public void mouseDragged(MouseEvent e){
			Point newMouse = new Point( e.getX(), e.getY());
			if( dragMode){
				Pointd mouse_rp = plot.inversePlotPoint( mouse);
				Pointd newMouse_rp = plot.inversePlotPoint( newMouse);
				plot.offset_dest.add( newMouse_rp.diff( mouse_rp));}
			mouse_old = mouse;
			mouse = newMouse;}
		public void mouseEntered(MouseEvent e){ 
			mouseActive = true;
			drawArea.mouseActive = mouseActive;}
		public void mouseExited(MouseEvent e){ 
			mouseActive = false;
			drawArea.mouseActive = mouseActive;
			repaint();}
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {
			switch(e.getButton()){
				case 1:
					dragMode = false;
					break;
				case 2:
					break;
				case 3:
					break;
				default:
					break;}}
		public void mouseWheelMoved(MouseWheelEvent e) {
			plot.zoom(-zoom_ratio*e.getWheelRotation(), mouse);}

		//component handling
		public void componentResized(ComponentEvent e){
			drawArea.reloadBackground();}
		public void componentMoved(ComponentEvent e){}
		public void componentShown(ComponentEvent e){}
		public void componentHidden(ComponentEvent e){}

		//keyboard handling
		public void keyTyped( KeyEvent e){}
		public void keyPressed( KeyEvent e){
			System.out.println(String.format(
				"Key Pressed: %d",
				e.getKeyCode()));//*/
			switch(e.getKeyCode()){
				// pause / unpause
				case 32:
					sim.paused = !sim.paused;
					if(!sim.paused)
						synchronized(sim.pauseLock){
							sim.pauseLock.notify();}
					break;
				// step
				case 78: // n
					for(int i = 0; i < sim.period; i++)
						sim.step();
					break;
				// exit
				case 27://escape
				case 67://c
					close();
					break;
				// moving around
				case 37:
				case 65://left: w
					plot.moveViewDest(-1,0);
					break;
				case 39:
				case 68://right: d
					plot.moveViewDest(1,0);
					break;
				case 38:
				case 87://up: w
					plot.moveViewDest(0,1);
					break;
				case 40:
				case 83://down: s
					plot.moveViewDest(0,-1);
					break;
				// zoom
				case 90:
				case 33://in
					plot.zoom( zoom_ratio, mouse);
					break;
				case 88:
				case 34://out
					plot.zoom( -zoom_ratio, mouse);
					break;
				//help
				case 112://f1
					break;
				default:
					break;}}
		public void keyReleased( KeyEvent e){}
	}
}
