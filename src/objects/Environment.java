package objects;
//library imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import java.util.Vector;
import org.json.simple.*;

public class Environment {
	public Vector<Box> boxes;
	public Vector<Crowd> crowds;

	public Environment(){
		boxes = new Vector<Box>();
		crowds = new Vector<Crowd>();
		//general variables
		double room_width = 5.0;
		double room_height = 5.0;
		//create container box
		double minPos_x = -room_width/2.0;
		double maxPos_x = minPos_x + room_width;
		double minPos_y = -room_height/2.0;
		double maxPos_y = minPos_y + room_height;
		boxes.add( new Box(
			minPos_x, maxPos_x,
			minPos_y, maxPos_y));
		//create crowd
		crowds.add(
			new Crowd(
				//origin
				new Pointd(), 2.5,
				//destination
				new Pointd(),
				//amount
				10));}
	public Environment( String filename) throws FileNotFoundException {
		FileReader in = new FileReader( filename);
		JSONObject root = (JSONObject) JSONValue.parse( in);

		//load boxes
		boxes = new Vector<Box>();
		JSONArray boxes_data = (JSONArray) root.get("boxes");
		for( int i = 0; i < boxes_data.size(); i++){
			JSONObject box_data = (JSONObject) boxes_data.get(i);
			JSONArray x_data = (JSONArray) box_data.get("x");
			JSONArray y_data = (JSONArray) box_data.get("y");
			boxes.add( new Box(
				((Number) x_data.get(0)).doubleValue(),
				((Number) x_data.get(1)).doubleValue(),
				((Number) y_data.get(0)).doubleValue(),
				((Number) y_data.get(1)).doubleValue()));}

		//load crowds
		crowds = new Vector<Crowd>();
		JSONArray crowds_data = (JSONArray) root.get("crowds");
		for( int i = 0; i < crowds_data.size(); i++){
			JSONObject crowd_data = (JSONObject) crowds_data.get(i);
			JSONObject origin_data = (JSONObject) crowd_data.get("origin");
			JSONObject destination_data = (JSONObject) crowd_data.get("destination");
			crowds.add( new Crowd(
				new Pointd(
					((Number) origin_data.get("x")).doubleValue(),
					((Number) origin_data.get("y")).doubleValue()),
				((Number) origin_data.get("r")).doubleValue(),
				new Pointd(
					((Number) destination_data.get("x")).doubleValue(),
					((Number) destination_data.get("y")).doubleValue()),
				((Number) crowd_data.get("amount")).intValue()));}}


	public Box[] getBoxes(){
		return boxes.toArray( new Box[0]);}

	public Entity[] getEntities(){
		Vector<Entity> entities = new Vector<Entity>();
		for( Crowd crowd : crowds)
			entities.addAll( crowd.getEntities());
		return entities.toArray( new Entity[0]);}

	private class Crowd {
		Pointd origin;
		double origin_radius;
		Pointd destination;
		int amount;

		public Crowd(
				Pointd origin,
				double origin_radius,
				Pointd destination,
				int amount){
			this.origin = origin;
			this.origin_radius = origin_radius;
			this.destination = destination;
			this.amount = amount;}

		public Vector<Entity> getEntities(){
			Vector<Entity> entities = new Vector<Entity>();
			Random random = new Random();
			double minRadius = 0.1;
			double maxRadius = 0.2;
			double radius_variance = maxRadius - minRadius;
			double minMass = 1.0;
			double maxMass = 5.0;
			double mass_variance = maxMass - minMass;
			double maxVelocity = 10.0;
			double tolerance = 0.5;
	 			
			for( int i = 0; i < amount; i++){
				double radius = minRadius + radius_variance*random.nextDouble();
				double mass = minMass + mass_variance*random.nextDouble();
				double origin_distance = random.nextDouble()*origin_radius;
				double origin_theta = random.nextDouble()*2*Math.PI;
				Pointd pos = new Pointd(
						origin_distance*Math.cos( origin_theta),
						origin_distance*Math.sin( origin_theta));
				pos.add( origin);
				Entity entity = new Entity(
					pos, maxVelocity, destination, tolerance, mass, radius);
				for( Box box : boxes)
					entity.remember( box);
				entities.add( entity);}
				return entities;}
	}
}