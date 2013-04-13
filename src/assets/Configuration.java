package assets;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import org.ini4j.Ini;

import objects.*;

public class Configuration {
	private String filename;
	private Ini config;
	private Ini.Section sim_config;
	private Ini.Section plot_config;
	private Ini.Section gui_config;

	public Configuration( String filename) {
		this.filename = filename;
		loadFile();}

	public void loadFile(){
		config = new Ini();
		try{ config.load(new File(filename));}
		catch(IOException e){
			System.out.println(
				"Couldn't load configuration file.");}
		sim_config = config.get("Simulation");
		plot_config = config.get("Plot");
		gui_config = config.get("Gui");}

	public void loadSimulation( Simulation sim){
		if( sim_config == null) return;
		String datafile = new String();
		datafile = sim_config.get("datafile");
		if( datafile != null)
			try{ sim.load( new Environment( datafile));}
			catch( FileNotFoundException e){
				System.out.println("Datafile not found");}}
			
	public void loadPlot( Plot plot){
		if( plot_config == null) return;
		String value = new String();
		//basic parmeters
		value = plot_config.get("precision");
		if( value != null)
			plot.precision = Double.parseDouble(value);
		value = plot_config.get("drawPath");
		if( value != null)
			plot.drawPath = Boolean.parseBoolean(value);
		value = plot_config.get("drawLabel");
		if( value != null)
			plot.drawLabel = Boolean.parseBoolean(value);
		//formatting parameters
		value = plot_config.get("axis_colour");
		if( value != null)
			plot.axis_colour = parseColour(value);
		value = plot_config.get("bg_colour");
		if( value != null)
			plot.bg_colour = parseColour(value);
		value = plot_config.get("compress_colour");
		if( value != null)
			plot.compress_colour = parseColour(value);
		value = plot_config.get("decompress_colour");
		if( value != null)
			plot.decompress_colour = parseColour(value);
		value = plot_config.get("line_colour");
		if( value != null)
			plot.line_colour = parseColour(value);
		value = plot_config.get("entity_colour");
		if( value != null)
			plot.entity_colour = parseColour(value);
		value = plot_config.get("box_colour");
		if( value != null)
			plot.box_colour = parseColour(value);
		value = plot_config.get("label_colour");
		if( value != null)
			plot.label_colour = parseColour(value);
		value = plot_config.get("label_font");
		String value2 = plot_config.get("label_font_size");
		if( value != null && value2 != null)
			try{ plot.label_font = new Font(value, Font.PLAIN, Integer.parseInt( value2));}
			catch( NumberFormatException e){
				System.out.println("Font Size parse faild");}
		value = plot_config.get( "label_defaultPadding");
		if( value != null)
			plot.label_defaultPadding = Integer.parseInt(value);}
		
	public void loadGui( Gui gui){
		if( gui_config == null) return;
		String value = new String();
		//load configuration
		value = gui_config.get("title");
		if( value != null)
			gui.title = value;
		value = gui_config.get("window_width");
		if( value != null)
			gui.window_width = Integer.parseInt(value);
		value = gui_config.get("window_height");
		if( value != null)
			gui.window_height = Integer.parseInt(value);
		value = gui_config.get("background_colour");
		if( value != null)
			gui.background_colour = parseColour(value);
		value = gui_config.get("background_filename");
		if( value != null)
			gui.background_filename = value;
		value = gui_config.get("icon_filename");
		if( value != null)
			gui.icon_filename = value;}
	
	public Color parseColour(String s){
		return new Color(
			Integer.parseInt(s.substring(0,2), 16),
			Integer.parseInt(s.substring(2,4), 16),
			Integer.parseInt(s.substring(4,6), 16),
			Integer.parseInt(s.substring(6,8), 16));}
}
