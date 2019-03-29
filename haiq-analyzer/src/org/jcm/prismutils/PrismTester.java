package org.jcm.prismutils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Objects;

import org.jcm.prismutils.PrismConnector;
import org.jcm.prismutils.PrismConnector.PrismConnectorMode;

public class PrismTester {

	public static void main(String[] args){

	    List<Point2D> coordinates = new ArrayList<Point2D>();

	    
		String res="result";
//		String myModel = "/Users/jcamara/Dropbox/Documents/Work/Projects/ROVER/tmpres/uncertainty-simple-v1.4.smg";
		String myModel = "/Users/jcamara/Dropbox/Documents/Work/Projects/ROVER/tmpres/znn-uncertainty-v7-urt.smg";
		String myPolicy = "/Users/jcamara/Dropbox/Documents/Work/Projects/ROVER/tmpres/pol";
//		String myProps = "/Users/jcamara/Dropbox/Documents/Work/Projects/ROVER/tmpres/uncertainty-simple-v1.props";
		String myProps = "/Users/jcamara/Dropbox/Documents/Work/Projects/ROVER/tmpres/znn-uncertainty.props";
		PrismConnector pc= new PrismConnector(PrismConnectorMode.SMG, "/Users/jcamara/Dropbox/Documents/Work/Projects/ROVER/tmpres/");
		
//		res = pc.modelCheckFromFileS(myModel,"R{\"time\"}min=? [ F goal ]", myPolicy);
//		res = pc.modelCheckFromFileS(myModel,myProps, myPolicy,0);
//		res = pc.modelCheckFromFileS(myModel,myProps, myPolicy,1);
//		res = pc.modelCheckFromFileS(myModel,myProps, myPolicy,2);		
		pc.setGenerateStrategy(true);

		
		for (int i=0; i< 101; i+=1){
//		for (int i=0; i<= 20; i+=1){
			pc.setConstants("INIT_MC="+String.valueOf(i)+",dos_threshold=60,INIT_STD_MC=20");
//			pc.setConstants("INIT_X="+String.valueOf(i)+",error=3,step=3");
    		pc.setModelCheckUnderStrategy(false);
    		res = pc.modelCheckFromFileS(myModel,myProps, myPolicy,0);
    		pc.setModelCheckUnderStrategy(true);
    		res = pc.modelCheckFromFileS(myProps, myPolicy,1);      	
        	coordinates.add(new Point2D.Double(i, Double.parseDouble(res)));
        }

        for (int j=0; j< coordinates.size(); j++){
            System.out.println(" ("+String.valueOf(coordinates.get(j).getX())+", "+String.valueOf(coordinates.get(j).getY())+") ");
        }
		
	}
	
	
//	public static void main(String[] args){
//
//	    // Lambda1 and Lambda2: from 0.01 to 0.99, step=0.01
//	    
//	    // rows with: lamda1, lambda2, probability
//	    
//		String res="result";
//		String myModel = "/Users/jcamara/Dropbox/Documents/Work/misc/16/rm-bw/prism/2cam.prism";
//		String myPolicy = "/Users/jcamara/Dropbox/Documents/Work/misc/16/rm-bw/prism/pol";
//		String myProps = "/Users/jcamara/Dropbox/Documents/Work/misc/16/rm-bw/prism/2cam.props";
//		PrismConnector pc= new PrismConnector(PrismConnectorMode.SMG,".");
//		
//		pc.setGenerateStrategy(true);
//
//		String strSynth = "synth_schedule=true";
//		String strMaxFrames = "max_frames=30";
//		
//		String constStr = strSynth+","+strMaxFrames;
//		
//		LinkedList<String> list = new LinkedList<String>();
//		
//		for (double i=0.1; i< 1; i+=0.1){
//			for (double j=0.1; j< 1; j+=0.1){
//				pc.setConstants(constStr+",lambda1="+String.valueOf(i)+",lambda2="+String.valueOf(j));
//	    		pc.setModelCheckUnderStrategy(false);
//	    		res = pc.modelCheckFromFileS(myModel,myProps, myPolicy,2);
//	    		pc.setModelCheckUnderStrategy(true);
//	    		res = pc.modelCheckFromFileS(myProps, myPolicy,3);      	
//	    		list.add(String.valueOf(i)+", "+String.valueOf(j)+", "+String.valueOf(res)+"\n");
//	    		if (!Objects.equals(res,"1.0")){
//	    			System.out.println("FAIL!!!!!!!!!");
//	    			System.exit(-1);
//	    		}
//	    		//coordinates.add(new Point2D.Double(i, Double.parseDouble(res)));
//	        }
//		}
//
//        for (int j=0; j< list.size(); j++){
//            System.out.println(list.get(j));
//        }
//		
//	}
	
	
	
}
