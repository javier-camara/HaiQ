package org.jcm.prismutils;


import org.jcm.prismutils.PrismConnector;
import org.jcm.prismutils.PrismConnector.PrismConnectorMode;

public class RosReconfigurationTest {

	public static void main(String[] args){
	    
		String res="result";
		String myModel = "/Users/jcamara/Dropbox/Documents/work/projects/BRASS/brassarchstylephase2/haiq/simpleconftest1.prism";
		String myPolicy = "/Users/jcamara/Dropbox/Documents/work/projects/BRASS/brassarchstylephase2/haiq/pol";
		String myProps = "/Users/jcamara/Dropbox/Documents/work/projects/BRASS/brassarchstylephase2/haiq/simpletest1.props";
		PrismConnector pc= new PrismConnector(PrismConnectorMode.MDP,"/Users/jcamara/Dropbox/Documents/work/projects/BRASS/brassarchstylephase2/haiq/");
		
		pc.setGenerateStrategy(true);


		
		String constStr = "";
		
			pc.setConstants(constStr);
			pc.setGenerateStrategy(true);
    		pc.setModelCheckUnderStrategy(false);
    		res = pc.modelCheckFromFileS(myModel,myProps, myPolicy,0);
		System.out.println("Done. Result:" + res);
	}
	
}
