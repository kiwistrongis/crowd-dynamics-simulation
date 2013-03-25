package assets;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.ini4j.Ini;

import objects.*;

public class Configuration {
	private Ini config;
	private Ini.Section sim_config;
	private Ini.Section plot_config;
	private Ini.Section gui_config;

	public Configuration( String filename) {
		config = new Ini();
		try{ config.load(new File(filename));}
		catch(Exception e){
			System.out.println("Couldn't load configuration file.\n Stopping.");
			System.exit(0);}
		sim_config = config.get("Simulation");
		plot_config = config.get("Plot");
		gui_config = config.get("Gui");
		System.out.println("Data File: " + sim_config.get("datafile"));}

	public Simulation setupSimulation(){
		//load from data
		int period = Integer.parseInt(sim_config.get("period"));
		double dt = Double.parseDouble(sim_config.get("dt"));
		double minRadius = Double.parseDouble(sim_config.get("minRadius"));
		double maxRadius = Double.parseDouble(sim_config.get("maxRadius"));
		double minMass = Double.parseDouble(sim_config.get("minMass"));
		double maxMass = Double.parseDouble(sim_config.get("maxMass"));
		double maxVelocity = Double.parseDouble(sim_config.get("maxVelocity"));
		int n_balls = Integer.parseInt(sim_config.get("n_balls"));
		double room_width = Double.parseDouble(sim_config.get("room_width"));
		double room_height = Double.parseDouble(sim_config.get("room_height"));
		Simulation sim = new Simulation(
			period, dt,
			minRadius, maxRadius,
			minMass, maxMass,
			maxVelocity, n_balls,
			room_width, room_height);
		return sim;}
			
	public Plot setupPlot( Simulation sim){
		Plot plot = new Plot();
		//basic parmeters
		plot.scale = Double.parseDouble(plot_config.get("scale"));
		plot.scale_dest = plot.scale;
		plot.scale_original = plot.scale;
		plot.precision = Double.parseDouble(plot_config.get("precision"));
		//formatting parameters
		plot.axis_colour = parseColour(plot_config.get("axis_colour"));
		plot.bg_colour = parseColour(plot_config.get("bg_colour"));
		plot.compress_colour = parseColour(plot_config.get("compress_colour"));
		plot.decompress_colour = parseColour(plot_config.get("decompress_colour"));
		plot.line_colour = parseColour(plot_config.get("line_colour"));
		plot.ball_colour = parseColour(plot_config.get("ball_colour"));
		plot.box_colour = parseColour(plot_config.get("box_colour"));
		plot.label_colour = parseColour(plot_config.get("label_colour"));
		plot.label_font = new Font(plot_config.get("label_font"), Font.PLAIN,
			Integer.parseInt(plot_config.get("label_font_size")));
		plot.label_defaultPadding = Integer.parseInt(plot_config.get("label_defaultPadding"));
		return plot;}
		
	public Gui setupGui( Simulation sim, Plot plot){
		Gui gui = new Gui( sim, plot);
		//load configuration
		gui.title = gui_config.get("title");
		gui.window_x = Integer.parseInt(gui_config.get("window_x"));
		gui.window_y = Integer.parseInt(gui_config.get("window_y"));
		gui.window_width = Integer.parseInt(gui_config.get("window_width"));
		gui.window_height = Integer.parseInt(gui_config.get("window_height"));
		gui.background_colour = parseColour(gui_config.get("background_colour"));
		gui.background_filename = gui_config.get("background_filename");
		gui.icon_filename = gui_config.get("icon_filename");
		
		//finish setup
		gui.setup();
		return gui;}
	
	public Color parseColour(String s){
		return new Color(
			Integer.parseInt(s.substring(0,2), 16),
			Integer.parseInt(s.substring(2,4), 16),
			Integer.parseInt(s.substring(4,6), 16),
			Integer.parseInt(s.substring(6,8), 16));}
}
