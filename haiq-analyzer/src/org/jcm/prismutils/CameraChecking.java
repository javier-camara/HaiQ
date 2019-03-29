package org.jcm.prismutils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jcm.prismutils.PrismConnector;
import org.jcm.prismutils.PrismConnector.PrismConnectorMode;

public class CameraChecking {

	public static void main(String[] args){

	    List<Point2D> coordinates = new ArrayList<Point2D>();

	    // Lambda1 and Lambda2: from 0.01 to 0.99, step=0.01
	    
	    // rows with: lamda1, lambda2, probability
	    
		String res="result";
		String myModel = "/Users/jcamara/Dropbox/Documents/Work/misc/16/rm-bw/prism/2cam.prism";
		String myPolicy = "/Users/jcamara/Dropbox/Documents/Work/misc/16/rm-bw/prism/pol";
		String myProps = "/Users/jcamara/Dropbox/Documents/Work/misc/16/rm-bw/prism/2cam.props";
		PrismConnector pc= new PrismConnector(PrismConnectorMode.SMG,".");
		
		pc.setGenerateStrategy(true);

		String strSynth = "synth_schedule=false";
		String strEvent = "event=true";
		String strMaxFrames = "max_frames=30";
		
		String constStr = strSynth+","+strEvent+","+strMaxFrames;
		
		for (double i=0.1; i< 1; i+=0.1){
			pc.setConstants(constStr+",lambda1="+String.valueOf(i));
    		pc.setModelCheckUnderStrategy(false);
    		res = pc.modelCheckFromFileS(myModel,myProps, myPolicy,3);
    		pc.setModelCheckUnderStrategy(true);
    		res = pc.modelCheckFromFileS(myProps, myPolicy,4);      	
        	coordinates.add(new Point2D.Double(i, Double.parseDouble(res)));
        }

        for (int j=0; j< coordinates.size(); j++){
            System.out.println(" ("+String.valueOf(coordinates.get(j).getX())+", "+String.valueOf(coordinates.get(j).getY())+") ");
        }
		
	}
	
}
