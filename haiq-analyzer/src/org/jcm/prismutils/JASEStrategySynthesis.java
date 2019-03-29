package org.jcm.prismutils;

import java.awt.geom.Point2D;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Objects;

import org.jcm.prismutils.PrismConnector;
import org.jcm.prismutils.PrismConnector.PrismConnectorMode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JASEStrategySynthesis {

	public List<Point2D> ipoints = new ArrayList<Point2D>();

	
	public void loadPointsFromFile(String mapFile){
        JSONParser parser = new JSONParser();
        NumberFormat format = NumberFormat.getInstance();

        Object obj=null;
        try{
            obj = parser.parse(new FileReader(mapFile)); 
        } catch (Exception e) {
            System.out.println("Could not load Map File");
        }

        JSONObject jsonObject = (JSONObject) obj;
        JSONArray nodes = (JSONArray) jsonObject.get("initial-points");

        for (Object node : nodes) {
            JSONObject jsonNode = (JSONObject) node;
            double src_x=0, src_y=0;
            try{
                src_x = Double.parseDouble(String.valueOf(jsonNode.get("x")));
                src_y = Double.parseDouble(String.valueOf(jsonNode.get("y")));
                ipoints.add(new Point2D.Double(src_x, src_y));
            } catch (Exception e){
                System.out.println("Error parsing coordinates in location.");
            }
        }
    }

	
	public static void main(String[] args){

	    JASEStrategySynthesis synth = new JASEStrategySynthesis();
	    synth.loadPointsFromFile("/Users/jcamara/Dropbox/Documents/work/projects/BRASS/mapeditor.git/trunk/datapoints.json");
	    System.out.println("Initial location grid in map:");
	    for (int j=0; j< synth.ipoints.size(); j++){
            System.out.println(" ("+String.valueOf(synth.ipoints.get(j).getX())+", "+String.valueOf(synth.ipoints.get(j).getY())+") ");
        }
	}
	
}
