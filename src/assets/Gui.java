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

public class Gui extends JFrame{
	// major objects
	public Simulation sim;
	public Plot plot;
	public DrawArea drawArea;
	public Overlay overlay;
	// minor fields
	public String title;
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
		//default fields
		title = "Simulation";
		window_x = 0;
		window_y = 0;
		plot.width = window_width = 1200;
		plot.height = window_height = 700;
		background_colour = new Color( 0, 0, 0, 0xff);
		background_filename = "resources/background.png";
		icon_filename = "resources/icon.png";}
	
	public void setup(){
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setTitle(title);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		window_x = ( gd.getDisplayMode().getWidth() - window_width)/2;
		window_y = ( gd.getDisplayMode().getHeight() - window_height)/2;
		setBounds( window_x, window_y, window_width, window_height);
		setBackground( background_colour);
		BufferedImage icon;
		try{
			icon = ImageIO.read(
				new File(icon_filename));
			setIconImage( icon);}
		catch(IOException e){ System.out.println(e);}
		Container frame = getContentPane();

		drawArea = new DrawArea( this);
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

		overlay = new Overlay( this);
		this.setGlassPane(overlay);
		overlay.setVisible(true);

		plot.fit(sim.boxes[0].points);
		plot.offset = plot.offset_dest;

		this.setFocusable(true);
		this.requestFocus();
		
		try{
			UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());}
		catch( Exception e){
			System.out.println(e);}

		System.out.println("Done Loading GUI");
		setVisible(true);}
	
	public void close(){
		WindowEvent wev = 
			new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().
			getSystemEventQueue().postEvent(wev);}
}