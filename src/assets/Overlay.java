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
	Vector<String> help;

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
		help = new Vector<String>();
		help.add("Key:Action");
		help.add("'a':Move left");
		help.add("'a':Move left");
		help.add("'d':Move right");
		help.add("'w':Move up");
		help.add("'s':Move down");
		help.add("'c':Recenter view");
		help.add("'r':Reload Configuration");
		help.add("Esc:Exit");}

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
		Point hint_p = new Point( 0, 0);

		// draw help hint
		g2.setComposite( AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 1 - (float) blend));
		int hint_width = fontMetrics.stringWidth(hint);
		int hint_height = fontMetrics.getAscent();
		g2.drawString( hint,
			hint_p.x + hint_padding,
			hint_p.y + hint_padding + hint_height);

		// draw help
		g2.setComposite( AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, (float) blend));
		for( String s : help){
			String[] split = s.split(":");
			if(split.length < 2)
				break;
			helpText_left += "" + split[0];
			helpText_right += " : " + split[1];}
		int left_width = fontMetrics.stringWidth(helpText_left);
		int left_height = fontMetrics.getAscent()*(1+help.size());
		g2.drawString( helpText_left,
			hint_p.x + hint_padding,
			hint_p.y + hint_padding + hint_height + left_height);
		int right_height = fontMetrics.getAscent()*(1+help.size());
		g2.drawString( helpText_right,
			hint_p.x + hint_padding + left_width,
			hint_p.y + hint_padding + hint_height + right_height);
	}
}