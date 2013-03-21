package assets;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.imageio.ImageIO;


public class Gui extends JFrame implements Observer, KeyListener{
	private Simulation sim;
	private Plot plot;
	private DrawArea drawArea;
	String title;
	public int window_x;
	public int window_y;
	public int window_width;
	public int window_height;
	public Color background_colour;
	public String background_filename;

	public Gui(Simulation sim, Plot plot){
		this.sim = sim;
		this.plot = plot;
		sim.addObserver(this);}
	
	public void setup(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/*this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("JFrame Closing");
				synchronized( sim.pauseLock){
					sim.paused = false;
					sim.pauseLock.notify();}
				e.getWindow().dispose();}});*/

		setTitle(title);
		setBounds( window_x, window_y, window_width, window_height);
		setBackground( background_colour);
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
		
	   this.setFocusable(true);
      this.requestFocus();
		addKeyListener(this);

		plot.fit(sim.box.points);
		
		System.out.println("Done Loading GUI");
		setVisible(true);}
	
	public void keyTyped( KeyEvent e){}
	public void keyPressed( KeyEvent e){
		System.out.println(String.format(
			"Key Pressed: %d",
			e.getKeyCode()));//*/
		switch(e.getKeyCode()){
			case 32:
				sim.paused = !sim.paused;
				if(!sim.paused)
					synchronized(sim.pauseLock){
						sim.pauseLock.notify();}
				break;
			case 78: //step: n
				for(int i = 0; i < sim.period; i++)
					sim.step();
				break;
			case 27://escape
			case 67://c
				close();
				break;
			case 65://left: w
				plot.moveView(-1,0);
				break;
			case 68://right: d
				plot.moveView(1,0);
				break;
			case 87://up: w
				plot.moveView(0,1);
				break;
			case 83://down: s
				plot.moveView(0,-1);
				break;}}
	public void keyReleased( KeyEvent e){}
	
	public void update(Observable o, Object arg){
		drawArea.repaint();}
	public void close(){
		WindowEvent wev = 
			new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().
			getSystemEventQueue().postEvent(wev);}

	private class DrawArea extends JPanel 
			implements MouseListener, MouseMotionListener,
				ComponentListener {
		private static final int pointRadius = 5;
		private static final int pointDetectRadius = 15;

		private boolean mouseActive;
		private int mouseX, mouseY;
		BufferedImage background;
		int area_width;
		int area_height;
		int background_width;
		int background_height;

		public DrawArea(){
			setBackground( plot.bg_colour);
			mouseActive = false;
			mouseX = mouseY = 0;
			addMouseListener(this);
			addMouseMotionListener(this);
			addComponentListener(this);
			
			//set up background image
			reloadBackground();}

		protected void paintComponent(Graphics g){
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			//area_width = this.getWidth();
			//area_height = this.getHeight();
			//System.out.println(area_width + " " + area_height);
			//draw the background
			g2.drawImage(background,
				area_width/2 - background_width/2,
				area_height/2 - background_height/2, null);/**/
			//plot.drawAxis(g2);
			plot.drawBox(g2, sim.box);
			plot.drawBalls( g2, sim.balls, sim.box.walls);}
			//draw a black circle over the first point found
			// that is close enough to the mouse
			/*if (mouseActive){
				Point mousePoint = new Point(mouseX,mouseY);
				for(Point p : sim.nodes)
					if(p.distance(mousePoint) < pointDetectRadius){
						g2.setColor(Color.BLACK);
						g2.fillOval(p.x - pointRadius,
							p.y - pointRadius,
					2 * pointRadius, 2 * pointRadius);
						break;}}*/

		public void mousePressed(MouseEvent e) {
			//System.out.println(e.getButton());
			Point mousePoint = new Point(e.getX(),e.getY());
			switch(e.getButton()){
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				default:
					break;}
			repaint();}
		public void mouseMoved(MouseEvent e){
			mouseX = e.getX();
			mouseY = e.getY();
			repaint();}
		public void mouseEntered(MouseEvent e){ 
			mouseActive = true;}
		public void mouseExited(MouseEvent e){ 
			mouseActive = false;
			repaint();}
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseDragged(MouseEvent e){}
		public void componentResized(ComponentEvent e){
			reloadBackground();}
		public void componentMoved(ComponentEvent e){}
		public void componentShown(ComponentEvent e){}
		public void componentHidden(ComponentEvent e){}
		
		public void reloadBackground(){
			area_width = this.getWidth();
			area_height = this.getHeight();
			plot.plot_width = area_width;
			plot.plot_height = area_height;
			BufferedImage temp;
			try{
				temp = ImageIO.read(
					new File(background_filename));
				int temp_width = temp.getWidth();
				int temp_height = temp.getHeight();			
				double scale = Math.max(
					Math.max(
						(double) window_width / temp_width,
						(double) window_height / temp_height),
					Math.max(
						(double) area_width / temp_width,
						(double) area_height / temp_height));
				//System.out.println(scale);
	
				background_width = (int) Math.round( scale * temp_width);
				background_height = (int) Math.round( scale * temp_height);
	
				background = new BufferedImage( background_width,
					background_height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = background.createGraphics();
				g.drawImage(temp, 0, 0, background_width,
					background_height, null);
				g.dispose();}
			catch(IOException e){ System.out.println(e);}
			repaint();}
	}
}
