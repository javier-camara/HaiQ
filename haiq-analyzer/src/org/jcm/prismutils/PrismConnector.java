package org.jcm.prismutils;


import prism.Prism;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismSettings;
import prism.Result;
import prism.UndefinedConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Objects;
import java.util.HashMap;

import parser.*;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import parser.ast.Property;

/**
 * @author jcamara
 *
 */
public class PrismConnector {

	public enum PrismConnectorMode {MDP, SMG, DTMC};
	public enum PrismConnectorEngine {EXPLICIT, HYBRID};
	
	public static final int ALL_PROPS=-1;
	
	public static PrismConnectorMode m_mode;
	public static PrismConnectorEngine m_engine;
	public static PrismFileLog m_log;
	public static Prism m_prism;
	public static ModulesFile m_modulesFile;
	public static PropertiesFile m_propertiesFile;
	public static Result m_result;
	public static UndefinedConstants m_undefinedMFConstants;
	public static Values m_definedMFConstants;
	public static Values m_definedPFConstants;
	public static UndefinedConstants m_undefinedConstants[];
	public static ArrayList<Property> m_propertiesToCheck;
	public static String m_constSwitch;
	public static boolean m_debug=false;
	private static String m_tmp_path;
	

	/**
	 *  Constructor defaults to MDP mode and Explicit engine
	 */
	public PrismConnector(String tmp_path) {
		this(PrismConnectorMode.MDP,  PrismConnectorEngine.EXPLICIT ,tmp_path);
		m_tmp_path = tmp_path;
	}
	
	/**
	 *  Constructor defaults Explicit engine
	 */
	public PrismConnector(PrismConnectorMode mode, String tmp_path) {
		this(mode, PrismConnectorEngine.EXPLICIT ,tmp_path);
		m_tmp_path = tmp_path;
	}
	
//	public PrismConnector(PrismConnectorMode mode) {
//		return PrismConnector(mode, "stdout");
//	}
	
	public PrismConnector(PrismConnectorMode mode, PrismConnectorEngine engine, String tmp_path) {
		m_mode = mode;
		m_engine = engine;
		m_tmp_path = tmp_path;
		m_log = new PrismFileLog(m_tmp_path+"/log.txt");
		m_prism = new Prism(m_log);
		m_propertiesToCheck = new ArrayList<Property>();
		m_prism.setGenStrat(true);
	
		

		try{ // Initialize Prism (defaults to explicit engine both for MDP and SMG modes)
			if (m_debug) 
				System.out.println("Initializing PRISM");
			m_prism.initialise();
			switch(engine){
			case EXPLICIT:
				m_prism.setEngine(Prism.EXPLICIT); 
				break;
			case HYBRID:
				m_prism.setEngine(Prism.HYBRID);
				break;
			}
			if (m_debug)
				System.out.println("Initialized - ENGINE: "+ String.valueOf(m_prism.getEngine()));
		}catch (PrismException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}
		
		try{ // Set strategy export mode
			m_prism.setExportAdv(Prism.EXPORT_ADV_MDP);
			if (m_mode==PrismConnectorMode.SMG)
				m_prism.setExportAdv(Prism.EXPORT_PLAIN);
		} catch (PrismException e){
			System.out.println("Could not change strategy export mode to MDP");
		}
	}
		
	/**
	 * Sets undefined constants for model checking
	 * @param constantStr String of the form "constantname1=constantvalue1,constantname2=constantvalue2,..."
	 */
	public static void setConstants(String constantStr){
		m_constSwitch = constantStr;
	}
	
	
	/**
	 * Enables/Disables model checking under strategy mode (only when in SMG mode)
	 * @param enabled
	 */
	public static void setModelCheckUnderStrategy(boolean enabled){
		try {
			m_prism.getSettings().set(PrismSettings.PRISM_IMPLEMENT_STRATEGY, enabled);
		} catch (PrismException error) {
			error.printStackTrace();
		}
		if (m_prism.getSettings().getBoolean(PrismSettings.PRISM_IMPLEMENT_STRATEGY) && m_prism.getStrategy() == null){ 
				System.out.println("Warning: no strategy in memory.");
		}

	}

	/**
	 * Enables/Disables strategy generation when model checking
	 * @param enabled
	 */
	public static void setGenerateStrategy(boolean enabled, String outFile){
		try {
//			m_prism.getSettings().set(PrismSettings.PRISM_GENERATE_STRATEGY, enabled);
			m_prism.getSettings().set(PrismSettings.PRISM_EXPORT_ADV, "MDP");
			m_prism.getSettings().set(PrismSettings.PRISM_EXPORT_ADV_FILENAME, outFile);
		} catch (PrismException error) {
			error.printStackTrace();
		}
	}
	
	
	/**
	 * Loads model from file
	 * @param modelFileName
	 */
	public static void loadModel(String modelFileName){
		try { // PRISM model parsing		
			m_modulesFile = m_prism.parseModelFile(new File(modelFileName));
			m_prism.loadPRISMModel(m_modulesFile);
			
		}
		catch (FileNotFoundException e) {
			System.out.println("Error FNE: " + e.getMessage());
			System.exit(1);
		}
		catch (PrismException e) {
			System.out.println("Error PE1: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static void loadModelFromString(String modelCode){
		try { // PRISM model parsing		
			m_modulesFile = m_prism.parseModelString(modelCode);
			m_prism.loadPRISMModel(m_modulesFile);
			
		}
		catch (PrismException e) {
			System.out.println("Error PE1: " + e.getMessage());
			System.exit(1);
		}
	}
	
	/**
	 * Loads properties from file
	 * @param propertiesFileName
	 */
	public static void loadProperties(String propertiesFileName){
		try { // PRISM property parsing						
			//m_propertiesFile = m_prism.parsePropertiesString(m_modulesFile, property);
			m_propertiesFile = m_prism.parsePropertiesFile(m_modulesFile, new File(propertiesFileName));
			
		
		}
		catch (FileNotFoundException e) {
			System.out.println("Error FNE: " + e.getMessage());
			System.exit(1);
		}
		catch (PrismException e) {
			System.out.println("Error PE1: " + e.getMessage());
			System.exit(1);
		}
	}
	
	
	public static void loadPropertyFromString(String propertyString){
		try { // PRISM property parsing						
			//m_propertiesFile = m_prism.parsePropertiesString(m_modulesFile, property);
			m_propertiesFile = m_prism.parsePropertiesString(m_modulesFile, propertyString);
					
		}
		catch (PrismException e) {
			System.out.println("Error PE1: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Model checks properties from files (all of them)
	 * @param modelFileName
	 * @param propertiesFileName
	 * @param strategyFileName
	 * @return String result of model checking
	 */
	public static String modelCheckFromFileS(String modelFileName, String propertiesFileName, String strategyFileName){
		return modelCheckFromFileS(modelFileName, propertiesFileName, strategyFileName, ALL_PROPS);
	}
	
	
	/**
	 * Model checks a specific property from file (on current model, does not load a new model)
	 * @param propertiesFileName
	 * @param strategyFileName
	 * @param propertyToCheck int property index in file to model check (begins in 0). ALL_PROPS for checking all properties in file
	 * @return String result of model checking
	 */
	public static String modelCheckFromFileS(String propertiesFileName, String strategyFileName, int propertyToCheck){
		return modelCheckFromFileS(null, propertiesFileName, strategyFileName, propertyToCheck);
	}
	
	
	public static String modelCheckFromStrings(String modelString, String propertyString, String strategyFileName, int propertyToCheck) {
		m_propertiesToCheck.clear();
		loadModelFromString(modelString);
		loadPropertyFromString(propertyString);
		return modelCheck(strategyFileName, propertyToCheck);		
	}
	
	
	public static String modelCheckFromStrings(String modelString, String propertyString, String strategyFileName){
		return modelCheckFromStrings(modelString, propertyString, strategyFileName, ALL_PROPS);
	}
	
	/**
	 * Model checks properties from files
	 * @param modelFileName String model file to load. If null, no model loaded and model check performed on current model
	 * @param propertiesFileName
	 * @param strategyFileName
	 * @param propertyToCheck int property index in file to model check (begins in 0). ALL_PROPS for checking all properties in file
	 * @return String result of model checking
	 */
	public static String modelCheckFromFileS(String modelFileName, String propertiesFileName, String strategyFileName, int propertyToCheck){
		m_propertiesToCheck.clear();		
		return modelCheck(strategyFileName, propertyToCheck);
	}
	
	/**
	 * Model checks properties from files
	 * @param modelFileName String model file to load. If null, no model loaded and model check performed on current model
	 * @param propertiesFileName
	 * @param strategyFileName
	 * @param propertyToCheck int property index in file to model check (begins in 0). ALL_PROPS for checking all properties in file
	 * @return String result of model checking
	 */
	public static String modelCheck(String strategyFileName, int propertyToCheck){
		int numPropertiesToCheck=0;
		int i;
//		
//		m_propertiesToCheck.clear();
//		
		String res="";
//		
//		if (modelFileName!=null){
//			loadModel(modelFileName);
//		}
//		loadProperties(propertiesFileName);	
		
		// no properties to check
		if (m_propertiesFile == null) {
			numPropertiesToCheck = 0;
		}
		// unless specified, verify all properties
		else if (propertyToCheck == ALL_PROPS) {
			numPropertiesToCheck = m_propertiesFile.getNumProperties();
			for (i = 0; i < numPropertiesToCheck; i++) {
				m_propertiesToCheck.add(m_propertiesFile.getPropertyObject(i));
			}
		} else {
			m_propertiesToCheck.add(m_propertiesFile.getPropertyObject(propertyToCheck));
			numPropertiesToCheck=1;
		}
		
		// process info about undefined constants
		
		
		// first, see which constants are undefined
		// (one set of info for model, and one set of info for each property)
		m_undefinedMFConstants = new UndefinedConstants(m_modulesFile, null);
		m_undefinedConstants = new UndefinedConstants[numPropertiesToCheck];
		for (i = 0; i < numPropertiesToCheck; i++) {
			m_undefinedConstants[i] = new UndefinedConstants(m_modulesFile, m_propertiesFile, m_propertiesToCheck.get(i));
		}
		
		
		try {
			// then set up value using const switch definitions
			m_undefinedMFConstants.defineUsingConstSwitch(m_constSwitch);
			for (i = 0; i < numPropertiesToCheck; i++) {
				m_undefinedConstants[i].defineUsingConstSwitch(m_constSwitch);
			}	
		}
		catch (PrismException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			m_definedMFConstants = m_undefinedMFConstants.getMFConstantValues();
			m_prism.setPRISMModelConstants(m_definedMFConstants);
		} catch (PrismException e) {
			System.out.println(e.getMessage());
		}
		
		try { // Model check
			
			if (m_propertiesFile != null) {
				m_definedPFConstants = m_undefinedConstants[0].getPFConstantValues();
				if (m_debug)
					System.out.println(String.valueOf( m_undefinedConstants[0].getPFConstantValues()));
				m_propertiesFile.setSomeUndefinedConstants(m_definedPFConstants);	
			}
			
			m_result = m_prism.modelCheck(m_propertiesFile, m_propertiesToCheck.get(0));
			if (m_debug)
				System.out.println(m_result.getResult());
			res = m_result.getResult().toString();
		} 
		catch (PrismException e) {
			System.out.println("Error PE2: " + e.getMessage());
			System.exit(1);
		}
		
		
		// Export strategy if generated - Not needed anymore. Automatically done by Prism.
//		if (m_result.getStrategy() != null) {
//			System.out.println("*** Exporting Strategy "+strategyFileName);
//			try {
//				m_prism.exportStrategy(m_result.getStrategy(), Prism.StrategyExportType.ACTIONS, strategyFileName.equals("stdout") ? null : new File(strategyFileName+".act"));
//				m_prism.exportStrategy(m_result.getStrategy(), Prism.StrategyExportType.INDUCED_MODEL, strategyFileName.equals("stdout") ? null : new File(strategyFileName+".ind"));
//				mergeActionsInducedModelIntoAdversary(strategyFileName+".act", strategyFileName+".ind", strategyFileName+".adv");
//			}
//			// in case of error, report it and proceed
//			catch (FileNotFoundException e) {
//				System.out.println("Couldn't open file \"" + strategyFileName + "\" for output");
//			} catch (PrismException e) {
//				System.out.println(e.getMessage());
//			}
//			try {
//				m_prism.getStrategy().buildProduct(m_prism.getBuiltModelExplicit()).exportToPrismExplicitTra(strategyFileName+".sad");
//			} catch (Exception e1) {
//				System.out.println(e1.getLocalizedMessage()+"--"+e1.getMessage());
//			}
//		} else { 
//			if (m_debug)
//				System.out.println("*** No Strategy generated.");
//		}
		
		//m_prism.closeDown();
		return res;
	}	
	
	/**
     * Exports a String to a text file
     * @param f String filename
     * @param text String text to be exported
     */
    public static void exportTextToFile(String f, String text){
        try {
            BufferedWriter out = new BufferedWriter (new FileWriter(f));
            out.write(text);
            out.close();
        }
        catch (IOException e){
            System.out.println("Error exporting text");
        }
    }
    
	/**
	 * Merges actions and induced models files into an adversary file (for MDP strategy export)
	 * @param actionsFileName
	 * @param inducedModelFileName
	 * @param stratFileName String output MDP strategy file
	 */
	public static void mergeActionsInducedModelIntoAdversary(String actionsFileName, String inducedModelFileName, String stratFileName){
		HashMap<String, String> actions = new HashMap<String, String>();
		Scanner sc=null;
		try { 
			sc = new Scanner(new File(actionsFileName));
		}
		catch (FileNotFoundException e){
			System.out.println("Error merging actions and induced model into policy. File not found "+actionsFileName);
		}
		
		while (sc.hasNextLine()) {
		  String[] pairs = sc.nextLine().split(":");
		  actions.put(pairs[0], pairs[1]);
		}	
		sc.close();
		
		try { 
			sc = new Scanner(new File(inducedModelFileName));
		}
		catch (FileNotFoundException e){
			System.out.println("Error merging actions and induced model into policy. File not found "+inducedModelFileName);
		}
		
		String mergedStrat = "";
		while (sc.hasNextLine()) {
			  String transferLine = sc.nextLine();
			  String[] chunks = transferLine.split(" ");
			  String action = actions.get(chunks[0]);
			  if (!Objects.equals(action, "null") && chunks.length>2)
				  transferLine = transferLine +" "+action;
			  mergedStrat = mergedStrat+ transferLine + "\n";
		}	
		sc.close();		
		exportTextToFile(stratFileName, mergedStrat);
	}
	
	
}

