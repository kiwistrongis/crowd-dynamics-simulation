package assets;

import java.awt.*;
import java.util.Vector;
import javax.swing.*;

public class Overlay extends JComponent {
	public Gui gui;
	public boolean help_enabled;
	public double lastTime;
	public double blend;
	public double blend_period;
	public String hint;
	public Color hint_colour;
	public int hint_padding;
	public int helpBox_width;
	public int helpBox_height;
	Vector<String> helpKeys;
	Vector<String> helpValues;

	public Overlay(Gui gui) {
		this.gui = gui;
		lastTime = (double) System.currentTimeMillis()/1000;
		blend = 0.0;
		blend_period = 0.5;
		hint = "Hit F1 to show help";
		hint_colour = new Color( 0xff, 0xff, 0xff, 0xaf);
		hint_padding = 3;
		helpBox_width = 50;
		helpBox_height = 50;
		helpKeys = new Vector<String>();
		helpValues = new Vector<String>();
		helpKeys.add("Key "); helpValues.add(": Action");
		helpKeys.add("'a' "); helpValues.add(": Move left");
		helpKeys.add("'d' "); helpValues.add(": Move right");
		helpKeys.add("'w' "); helpValues.add(": Move up");
		helpKeys.add("'s' "); helpValues.add(": Move down");
		helpKeys.add("'c' "); helpValues.add(": Recenter view");
		helpKeys.add("'r' "); helpValues.add(": Reload Config");
		helpKeys.add("Esc "); helpValues.add(": Exit");}

	public void paint(Graphics g) {
		double currTime = (double) System.currentTimeMillis()/1000;
		double dtime = currTime - lastTime;
		lastTime = currTime;
		double dblend = dtime/blend_period;
		dblend *= help_enabled ? 1 : -1;
		blend += dblend;
		if( blend < 0) blend = 0;
		if( blend > 1) blend = 1;

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont( gui.plot.label_font);
		FontMetrics fontMetrics = g2.getFontMetrics();
		g2.setPaint( hint_colour);
		Point hint_p = new Point( hint_padding, hint_padding);

		// draw help hint
		g2.setComposite( AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 1 - (float) blend));
		int help_ascent = fontMetrics.getAscent();
		hint_p.y += help_ascent;

		g2.drawString( hint, hint_p.x,	hint_p.y);

		// draw help
		g2.setComposite( AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, (float) blend));

		int helpKeys_maxWidth = 0;
		Point help_p = new Point( hint_p.x, hint_p.y);
		for( String s : helpKeys){
			int s_width = fontMetrics.stringWidth(s);
			if( s_width > helpKeys_maxWidth)
				helpKeys_maxWidth = s_width;
			help_p.y += help_ascent;
			g2.drawString( s, help_p.x, help_p.y);}

		help_p = new Point( hint_p.x + helpKeys_maxWidth, hint_p.y);
		for( String s : helpValues){
			help_p.y += help_ascent;
			g2.drawString( s, help_p.x, help_p.y);}}
}