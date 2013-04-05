import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.Math;
import javax.imageio.ImageIO;
import org.opensourcephysics.numerics.*;

import assets.*;
import objects.*;

public class Driver {
	private static Controller controller;
	private static Object lock;
	private static Simulation sim;
	private static Gui gui;
	public static void main(String args[]) {
		System.out.println("Loading config file");
		Configuration config =
			new Configuration( args.length > 0 ? args[0] : "default.ini");
			
		System.out.println("Loading Simulation");
		sim = new Simulation();
		config.loadSimulation( sim);
		sim.setup();
		
		System.out.println("Loading Plot");
		Plot plot = new Plot();
		config.loadPlot( plot);
		
		System.out.println("Loading Gui");
		gui = new Gui( sim, plot);
		config.loadGui( gui);
		gui.setup();
		gui.drawArea.rescaleBackground();
		
		System.out.println("Loading Controller");
		controller = new Controller();
		controller.setup( gui, sim);
		
		lock = new Object();
		Thread stepThread = new Thread(){
			long time_pre, time_post;
			public void run(){
				synchronized(sim.pauseLock){
					while(!gui.isVisible());
					while(gui.isVisible()){
						time_pre = System.nanoTime();
						if(sim.paused){
							System.out.println("Simulation Paused");
							try { sim.pauseLock.wait();}
							catch( InterruptedException e){}
							System.out.println("Simulation Unpaused");}
						else
							sim.step();
						time_post = System.nanoTime();
						try{ Thread.sleep(1);}
						catch (InterruptedException e){}}}
				System.out.println("Stepping stopped");}};

		Thread waitThread = new Thread(){
			public void run(){
				synchronized( lock){
					while(!gui.isVisible());
					while(gui.isVisible())
						try { lock.wait();}
						catch (InterruptedException e){}
					System.out.println("Closing");}}};
		waitThread.start();

		gui.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				gui.setVisible( false);
				e.getWindow().dispose();
				synchronized( sim.pauseLock){
					sim.paused = false;
					sim.pauseLock.notify();}
				synchronized( lock){
					lock.notify();}}});
		
		System.out.println("Beginning Simulation");
		stepThread.start();
		
		System.out.println("Waiting for GUI exit");
		try { waitThread.join();}
		catch (InterruptedException e){}}
}
