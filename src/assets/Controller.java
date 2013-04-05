package assets;

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

import objects.*;

/** Controller sub-class
 * handles all user input
 **/
public class Controller 
		implements MouseListener, MouseMotionListener, Observer,
			MouseWheelListener, KeyListener, ComponentListener {
	// major objects
	Gui gui;
	Simulation sim;
	// minor fields
	public boolean mouseActive;
	public boolean dragMode;
	public Point mouse;
	public Point mouse_old;
	public double zoom_ratio;
	public double time_old;
	public double time;
	public int second;
	public int fps_count;
	public double drawPeriod;

	public Controller(){

		mouse = new Point();
		mouse_old = new Point();
		mouseActive = false;
		dragMode = false;
		zoom_ratio = 2.0;

		time = (double) System.currentTimeMillis()/1000;
		drawPeriod = (double) 1/60;
		second = (int) time;
		fps_count = 0;}

	public void setup( Gui gui, Simulation sim){
		this.gui = gui;
		this.sim = sim;

		gui.addKeyListener(this);
		gui.drawArea.addMouseListener(this);
		gui.drawArea.addMouseMotionListener(this);
		gui.drawArea.addMouseWheelListener(this);
		gui.drawArea.addComponentListener(this);
		gui.drawArea.mouse = mouse;
		gui.drawArea.mouseActive = this.mouseActive;
		sim.addObserver(this);

		time = (double) System.currentTimeMillis()/1000;}
	
	//redraw requests handling
	public void update(Observable o, Object arg){
		time = (double) System.currentTimeMillis()/1000;
		int newSecond = (int) time;
		if ( newSecond != second){
			second = newSecond;
			//System.out.printf( "FPS: %d\n", fps_count);
			fps_count = 0;}
		if( (time - time_old) > drawPeriod){
			gui.drawArea.repaint();
			//overlay.repaint();
			fps_count++;
			time_old = time;}}

	//mouse handling
	public void mousePressed(MouseEvent e) {
		Point newMouse = new Point( e.getX(), e.getY());
		//System.out.printf( "MB Pressed: %d\n", e.getButton()));
		switch(e.getButton()){
			case 1:
				dragMode = true;
				gui.overlay.setCursor( new Cursor( Cursor.MOVE_CURSOR));
				break;
			case 2:
				break;
			case 3:
				break;
			default:
				break;}}
	public void mouseMoved(MouseEvent e){
		mouse_old = mouse;
		mouse.x = e.getX();
		mouse.y = e.getY();}
	public void mouseDragged(MouseEvent e){
		Point newMouse = new Point( e.getX(), e.getY());
		if( dragMode){
			Pointd mouse_rp = gui.plot.inversePlotPoint( mouse);
			Pointd newMouse_rp = gui.plot.inversePlotPoint( newMouse);
			gui.plot.offset_dest.add( newMouse_rp.diff( mouse_rp));}
		mouse_old = mouse;
		mouse = newMouse;}
	public void mouseEntered(MouseEvent e){ 
		mouseActive = true;
		gui.drawArea.mouseActive = mouseActive;}
	public void mouseExited(MouseEvent e){ 
		mouseActive = false;
		gui.drawArea.mouseActive = mouseActive;}
	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
		switch(e.getButton()){
			case 1:
				dragMode = false;
				gui.overlay.setCursor(
					new Cursor( Cursor.DEFAULT_CURSOR));
				break;
			case 2:
				break;
			case 3:
				break;
			default:
				break;}}
	public void mouseWheelMoved(MouseWheelEvent e) {
		gui.plot.zoom(-zoom_ratio*e.getWheelRotation(), mouse);}

	//component handling
	public void componentResized(ComponentEvent e){
		gui.drawArea.rescaleBackground();}
	public void componentMoved(ComponentEvent e){}
	public void componentShown(ComponentEvent e){}
	public void componentHidden(ComponentEvent e){}

	//keyboard handling
	public void keyTyped( KeyEvent e){}
	public void keyPressed( KeyEvent e){
		/*System.out.printf(
			"Key Pressed: %d\n", e.getKeyCode());//*/
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
			// recenter and rezoom
			case 67://c
				gui.plot.fit(sim.boxes[0].points);
				break;
			// exit
			case 27://escape
				gui.close();
				break;
			// moving around
			case 37:
			case 65://left: w
				gui.plot.moveViewDest(-1,0);
				break;
			case 39:
			case 68://right: d
				gui.plot.moveViewDest(1,0);
				break;
			case 38:
			case 87://up: w
				gui.plot.moveViewDest(0,1);
				break;
			case 40:
			case 83://down: s
				gui.plot.moveViewDest(0,-1);
				break;
			// zoom
			case 90:
			case 33://in
				gui.plot.zoom( zoom_ratio);
				break;
			case 88:
			case 34://out
				gui.plot.zoom( -zoom_ratio);
				break;
			//help
			case 112://f1
				gui.overlay.help_enabled = ! gui.overlay.help_enabled;
				break;
			default:
				break;}}
	public void keyReleased( KeyEvent e){}
}
