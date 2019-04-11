package org.jcm.haiq.solve;

import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

import org.jcm.haiq.core.HQModel;
import org.jcm.haiq.core.HQProperty;
import org.jcm.alloyutils.AlloyConnector;
import org.jcm.alloyutils.AlloySolution;
import org.jcm.haiq.parse.*;
import org.jcm.haiq.parse.ParserMark1.PropertyEntry;
import org.jcm.haiq.translate.PrismTranslator;
import org.jcm.prismutils.PrismConnector;
import org.jcm.prismutils.PrismConnector.PrismConnectorEngine;
import org.jcm.prismutils.PrismConnector.PrismConnectorMode;
import org.jcm.util.*;
import prism.PrismCL;



/**
 * @author jcamara
 *
 */
public class HQSolver {

	public enum StructureExport {JSON, TIKZ};
	private HashMap<String, String> m_solutions = new HashMap<String, String>();
	private HashMap<String, String> m_structures = new HashMap<String, String>();
	private HashMap<String, String> m_structures_tikz = new HashMap<String, String>();
	private AlloyConnector m_ac;
	private PrismConnector m_pc;
	private boolean m_debug=false;
	private ScoreBoard m_scoreboard = new ScoreBoard();
	private HashMap<String, ConstantDefinition> m_constant_definitions = new HashMap<String, ConstantDefinition>();
	private static String m_tmp_path;
	private static Boolean m_sim_enabled=false;
	private static PrismCL m_prismcl=new PrismCL();
	
	
	private static final HashMap<String, PrismConnector.PrismConnectorEngine> m_engines;
	static{
		m_engines = new HashMap<String, PrismConnector.PrismConnectorEngine>();
		m_engines.put("explicit", PrismConnector.PrismConnectorEngine.EXPLICIT);
		m_engines.put("hybrid", PrismConnector.PrismConnectorEngine.HYBRID);
	}
	
	public HQSolver(String tmp_path){
		this(tmp_path, "explicit");
	}
	
	public HQSolver(String tmp_path, String engine){
		m_tmp_path = tmp_path;
		m_ac = new AlloyConnector();
		if (!m_engines.containsKey(engine)){
			System.out.println("Error: Unknown model checking engine.");
			System.exit(-1);
		}
		m_pc = new PrismConnector(PrismConnectorMode.DTMC, m_engines.get(engine), tmp_path);
	}
	
	/**
	 * Set the maximum number of configurations to generate
	 */	
	public void setMaxConfigs (int n){
		m_ac.setMaxSolutions(n);
	}
	
	/**
	 * Unpacks a string of constant definitions into the dictionary of ConstantDefinitions for the solver
	 * @param s String in format "const1id=min1:max1:step1;const2id=val2; ..." 
	 */
	public void setConstantDefinitions(String s){
		String[] constantDefStrs = s.split(",");
		for (int i=0; i<constantDefStrs.length; i++){
			String defStr = constantDefStrs[i].trim();
			String def_id = defStr.split("=")[0].trim();
			String def_tail = defStr.split("=")[1].trim();
			String[] defParts = def_tail.split(":");
			if (defParts.length>1){ // If constant defined over a range min:max:step
				m_constant_definitions.put(def_id, new ConstantDefinition(def_id, defParts[0], defParts[1], defParts[2]));
			} else { // simple value
				m_constant_definitions.put(def_id, new ConstantDefinition(def_id, defParts[0], defParts[0], "0"));
			}
		}
		System.out.println(m_constant_definitions.toString());
	}
	
	public HashMap<String, ConstantDefinition> getConstantDefinitions(){
		return m_constant_definitions;
	}
	
	public ScoreBoard getScoreBoard(){
		return m_scoreboard;
	}
	
	/**
	 * Generates set of structures and their corresponding behavioral translations
	 * @param modelFile Alloy model file extracted from haiq specification
	 * @param bModel HQModel object containing the hierarchy of processes parsed from haiq spec
	 * @return number of solutions generated
	 */
	public int generateSolutions(String modelFile, HQModel bModel){
		m_ac.generateSolutions(modelFile);
				
		for (int i=0; i<m_ac.getSolutions().size();i++){
			String strSolId = AlloyConnector.SOLUTION_STRING+String.valueOf(i);
			String strSol = m_ac.getSolution(strSolId);
			AlloySolution sol = new AlloySolution();
			sol.loadFromString(strSol);
			PrismTranslator pt = new PrismTranslator(bModel,sol);
//			m_structures.put(strSolId, sol.toString());
//			m_structures.put(strSolId, strSolId+"-"+sol.toPrismPred());
			m_structures.put(strSolId, sol.toJSON(strSolId));
			m_structures_tikz.put(strSolId, sol.toTikz(strSolId));
			m_solutions.put(strSolId, pt.generateModelFromAlloy() );
			if (m_debug)
				System.out.println("\n\n\n SOLUTION "+String.valueOf(i)+" ----------------------\n\n\n" + m_solutions.get(strSolId));
		}
		return m_ac.getSolutions().size();
	}
	
	
	public String runExperiment(PropertyEntry pe, HQModel bModel){
		String res="";
		
		if (m_solutions.entrySet().size()==0) { // If no structures satisfying constraints have been found, exit
			System.out.println("\n[S-Set empty]: No structures satisfying the constraints have been found. Exiting...");
			System.exit(-1);
		}		
		if (m_constant_definitions.size()>0){
			SpaceIterator si = new SpaceIterator(m_constant_definitions);
			for (String constStr: si.getCombinations()){
				res +="Constants: ["+constStr+"], Result: "+checkSolution(pe, bModel, constStr)+"\n";
			}
		} else {
			return checkSolution(pe, bModel);
		}
		return res;
	}
	
	public String checkSolution(PropertyEntry pe, HQModel bModel){
		return checkSolution (pe, bModel, "");
	}
	
	public String checkSolution(PropertyEntry pe, HQModel bModel, String constStr){
		String res ="-";
		String property = pe.getLiteral();
		String property_alias = pe.getId();
		String final_result =res;
		
		double m_maxp = 0.0;
		double m_minp = 1.0;
		double m_maxr = 0.0;
		double m_minr = Double.MAX_VALUE;
		boolean bool_res = false;

		int propertyIndex = 0;
		String myModel = m_tmp_path+"/temp.prism";
		String propertiesFile = m_tmp_path+"/temp.props";
		String ECpropertiesFile = m_tmp_path+"/ECtemp.props"; // For EvoChecker props
		String myPolicy = m_tmp_path+"/pol";
		
		TextFileHandler thp = new TextFileHandler(propertiesFile);
		TextFileHandler thp2 = new TextFileHandler(ECpropertiesFile);
		//System.out.println("Prop:" + property);
		//System.out.println("Alias:" + property_alias);
		
		HQProperty prop = new HQProperty(property);
		thp.exportFile(prop.getPCTLTranslation());
		thp2.exportFile(prop.getECAnnotation()+"\n"+prop.getPCTLTranslation()); // For EvoChecker props - extend to multiple ones
		
		
		if (bModel.getType()==HQModel.ModelType.DTMC && 
			(prop.isMaximizationQuantifier(prop.getQtype()) || prop.isMinimizationQuantifier(prop.getQtype()) ) ){
				System.out.println("\n[Incompatible Property-Model]: Only P and Q-based quantifiers allowed for DTMC models (no Pmax/Pmin, Rmax/Rmin-based). Exiting...");
				System.exit(-1);
		}
		
		switch(prop.getQtype()){
		case MAX_P: case MAX_P_MIN: case MAX_P_MAX:
			final_result = String.valueOf(m_maxp);
			break;
		case MIN_P: case MIN_P_MIN: case MIN_P_MAX:
			final_result = String.valueOf(m_minp);
			break;
		case MAX_R: case MAX_R_MAX: case MAX_R_MIN:
			final_result = String.valueOf(m_maxr);
			break;
		case MIN_R: case MIN_R_MIN: case MIN_R_MAX:
			final_result = String.valueOf("Infinity");
			break;
		case RANGE_P: case RANGE_P_MAX: case RANGE_P_MIN:
			final_result = "["+String.valueOf(m_minp)+","+String.valueOf(m_maxp)+"]";
			break;
		case RANGE_R: case RANGE_R_MAX: case RANGE_R_MIN:
			final_result = "[0.0, Infinity]";
			break;
		case ALL_R: case ALL_R_MAX: case ALL_R_MIN:
		case ALL_P: case ALL_P_MAX: case ALL_P_MIN:
			bool_res = true;
			final_result = String.valueOf(bool_res);
			break;
		case SOME_R: case SOME_R_MAX: case SOME_R_MIN:
		case SOME_P: case SOME_P_MAX: case SOME_P_MIN:
			bool_res = false;
			final_result = String.valueOf(bool_res);
			break;
		case NO_R: case NO_R_MAX: case NO_R_MIN:
		case NO_P: case NO_P_MAX: case NO_P_MIN:
			bool_res = true;
			final_result = String.valueOf(bool_res);
			break;
		case S_SOME_P: case S_SOME_P_MAX: case S_SOME_P_MIN:
		case S_MAX_P: case S_MAX_P_MAX: case S_MAX_P_MIN: 
		case S_MIN_P: case S_MIN_P_MAX: case S_MIN_P_MIN:
		case S_SOME_R: case S_SOME_R_MAX: case S_SOME_R_MIN:
		case S_MAX_R: case S_MAX_R_MAX: case S_MAX_R_MIN:
		case S_MIN_R: case S_MIN_R_MAX: case S_MIN_R_MIN:
			final_result = "[]";
			break;			
		}		
		
		TextFileHandler th = new TextFileHandler(myModel);
		m_pc.setModelCheckUnderStrategy(false);		
		ProgressBar b = new ProgressBar(m_solutions.entrySet().size(), 0.5);
		for (Map.Entry<String, String> e: m_solutions.entrySet()){
			th.exportFile(e.getValue());
			m_pc.setConstants(constStr);
			m_pc.setGenerateStrategy(false);
			m_pc.setModelCheckUnderStrategy(false);
			if (!m_sim_enabled){
			res = m_pc.modelCheckFromFileS(myModel, propertiesFile, myPolicy, propertyIndex);
			m_scoreboard.addSolutionforProperty(e.getKey(), property_alias, res);
			} else {
				// Simulation-based statistical model checking
			}
			b.tick();
			b.print();
			if (m_debug)
				System.out.println("Result for "+e.getKey()+": "+ res);
		
			double prob;
			switch(prop.getQtype()){
			case MAX_P: case MAX_P_MAX: case MAX_P_MIN:
				prob = Double.parseDouble(res);
				if (prob>m_maxp){
					m_maxp = prob;
					final_result = String.valueOf(m_maxp);
				}
				break;
			case MIN_P: case MIN_P_MAX: case MIN_P_MIN:
				prob = Double.parseDouble(res);
				if (prob<m_minp){
					m_minp = prob;
					final_result = String.valueOf(m_minp);
				}
				break;
			case RANGE_P: case RANGE_P_MAX: case RANGE_P_MIN:
				prob = Double.parseDouble(res);
				if (prob<m_minp){
					m_minp = prob;
				}
				if (prob>m_maxp){
					m_maxp = prob;
				}
				final_result = "["+String.valueOf(m_minp)+","+String.valueOf(m_maxp)+"]";
				break;
			case MAX_R: case MAX_R_MAX: case MAX_R_MIN:
				prob = Double.parseDouble(res);
				if (prob>m_maxr || Objects.equals(res, "Infinity")){
					m_maxr = prob;
					final_result = res;
				}
				break;
			case MIN_R: case MIN_R_MAX: case MIN_R_MIN:
				prob = Double.parseDouble(res);
				if (prob<m_minr){
					m_minr = prob;
					final_result = String.valueOf(m_minr);
				}
				break;
			
			case RANGE_R: case RANGE_R_MAX: case RANGE_R_MIN: // TODO: modify
				prob = Double.parseDouble(res);
				if (prob<m_minr){
					m_minr = prob;
				}
				if (prob>m_maxr){
					m_maxr = prob;
				}
				final_result = "["+String.valueOf(m_minr)+","+String.valueOf(m_maxr)+"]";
				break;
				
			case ALL_R: case ALL_R_MAX: case ALL_R_MIN:
			case ALL_P: case ALL_P_MAX: case ALL_P_MIN:
				if (!prop.evalAgainstBound(res)){
					return String.valueOf(false);
				}
				break;
			case SOME_R: case SOME_R_MAX: case SOME_R_MIN:
			case SOME_P: case SOME_P_MAX: case SOME_P_MIN:
				if (prop.evalAgainstBound(res)){
					return String.valueOf(true);
				}
				break;
			case NO_R: case NO_R_MAX: case NO_R_MIN:
			case NO_P: case NO_P_MAX: case NO_P_MIN:
				if (prop.evalAgainstBound(res)){
					return String.valueOf(false);
				}
				break;	
			case S_SOME_P: case S_SOME_P_MAX: case S_SOME_P_MIN:
				if (prop.evalAgainstBound(res)){
					return "["+String.valueOf(m_structures.get(e.getKey()))+"]";
				}
				break;
			case S_MAX_P: case S_MAX_P_MAX: case S_MAX_P_MIN:
				prob = Double.parseDouble(res);
				if (prob>m_maxp || Objects.equals(final_result,"[]")){
					m_maxp = prob;
					final_result = "["+String.valueOf(m_structures.get(e.getKey()))+"]";
				}
				break;
			case S_MIN_P: case S_MIN_P_MAX: case S_MIN_P_MIN:
				prob = Double.parseDouble(res);
				if (prob<m_minp || Objects.equals(final_result,"[]")){
					m_minp = prob;
					final_result = "["+String.valueOf(m_structures.get(e.getKey()))+"]";
				}
				break;
			case S_SOME_R: case S_SOME_R_MAX: case S_SOME_R_MIN:
				if (prop.evalAgainstBound(res)){
					return "["+String.valueOf(m_structures.get(e.getKey()))+"]";
				}
				break;
			case S_MAX_R: case S_MAX_R_MAX: case S_MAX_R_MIN:
				prob = Double.parseDouble(res);
				if (prob>m_maxr || Objects.equals(final_result,"[]")){
					m_maxr = prob;
					final_result = "["+String.valueOf(m_structures.get(e.getKey()))+"]";
				}
				break;
			case S_MIN_R: case S_MIN_R_MAX: case S_MIN_R_MIN:
				prob = Double.parseDouble(res);
				if (prob<m_minr || Objects.equals(final_result,"[]")){
					m_minr = prob;
					final_result = "["+String.valueOf(m_structures.get(e.getKey()))+"]";
				}
				break;
			}			
		}
		return final_result;				
	}
	
	public void exportConfigurations(String filepath, HQSolver.StructureExport export_mode){
		if (export_mode==StructureExport.JSON)
			for (Map.Entry<String, String> e: m_structures.entrySet()){
				TextFileHandler fhdata = new TextFileHandler(filepath+"/"+e.getKey()+".json");
				fhdata.exportFile(e.getValue());
			}
		if (export_mode==StructureExport.TIKZ)
			for (Map.Entry<String, String> e: m_structures_tikz.entrySet()){
				TextFileHandler fhdata = new TextFileHandler(filepath+"/"+e.getKey()+".tex");
				fhdata.exportFile(e.getValue());
			}
	}
	
	
//	/**
//	 * Process the simulation-related command-line options and generate
//	 * a SimulationMethod object to be used for approximate model checking.
//	 * @param expr The property to be checked (note: constants may not be defined)
//	 * @throws PrismException if there are problems with the specified options
//	 */
//	private SimulationMethod processSimulationOptions(Expression expr) throws PrismException
//	{
//		SimulationMethod aSimMethod = null;
//
//		// See if property to be checked is a reward (R) operator
//		boolean isReward = (expr instanceof ExpressionReward);
//
//		// See if property to be checked is quantitative (=?)
//		boolean isQuant = Expression.isQuantitative(expr);
//
//		// Pick defaults for simulation settings not set from command-line
//		if (!simApproxGiven)
//			simApprox = prism.getSettings().getDouble(PrismSettings.SIMULATOR_DEFAULT_APPROX);
//		if (!simConfidenceGiven)
//			simConfidence = prism.getSettings().getDouble(PrismSettings.SIMULATOR_DEFAULT_CONFIDENCE);
//		if (!simNumSamplesGiven)
//			simNumSamples = prism.getSettings().getInteger(PrismSettings.SIMULATOR_DEFAULT_NUM_SAMPLES);
//		if (!simWidthGiven)
//			simWidth = prism.getSettings().getDouble(PrismSettings.SIMULATOR_DEFAULT_WIDTH);
//
//		if (!reqIterToConcludeGiven)
//			reqIterToConclude = prism.getSettings().getInteger(PrismSettings.SIMULATOR_DECIDE);
//		if (!simMaxRewardGiven)
//			simMaxReward = prism.getSettings().getDouble(PrismSettings.SIMULATOR_MAX_REWARD);
//		if (!simMaxPathGiven)
//			simMaxPath = prism.getSettings().getLong(PrismSettings.SIMULATOR_DEFAULT_MAX_PATH);
//
//		// Pick a default method, if not specified
//		// (CI for quantitative, SPRT for bounded)
//		if (simMethodName == null) {
//			simMethodName = isQuant ? "ci" : "sprt";
//		}
//
//		// CI
//		if (simMethodName.equals("ci")) {
//			if (simWidthGiven && simConfidenceGiven && simNumSamplesGiven) {
//				throw new PrismException("Cannot specify all three parameters (width/confidence/samples) for CI method");
//			}
//			if (!simWidthGiven) {
//				// Default (unless width specified) is to leave width unknown
//				aSimMethod = new CIwidth(simConfidence, simNumSamples);
//			} else if (!simNumSamplesGiven) {
//				// Next preferred option (unless specified) is unknown samples
//				if (simManual)
//					aSimMethod = new CIiterations(simConfidence, simWidth, reqIterToConclude);
//				else
//					aSimMethod = (isReward ? new CIiterations(simConfidence, simWidth, simMaxReward) : new CIiterations(simConfidence, simWidth));
//			} else {
//				// Otherwise confidence unknown
//				aSimMethod = new CIconfidence(simWidth, simNumSamples);
//			}
//			if (simApproxGiven) {
//				mainLog.printWarning("Option -simapprox is not used for the CI method and is being ignored");
//			}
//		}
//		// ACI
//		else if (simMethodName.equals("aci")) {
//			if (simWidthGiven && simConfidenceGiven && simNumSamplesGiven) {
//				throw new PrismException("Cannot specify all three parameters (width/confidence/samples) for ACI method");
//			}
//			if (!simWidthGiven) {
//				// Default (unless width specified) is to leave width unknown
//				aSimMethod = new ACIwidth(simConfidence, simNumSamples);
//			} else if (!simNumSamplesGiven) {
//				// Next preferred option (unless specified) is unknown samples
//				if (simManual)
//					aSimMethod = new ACIiterations(simConfidence, simWidth, reqIterToConclude);
//				else
//					aSimMethod = (isReward ? new ACIiterations(simConfidence, simWidth, simMaxReward) : new CIiterations(simConfidence, simWidth));
//			} else {
//				// Otherwise confidence unknown
//				aSimMethod = new ACIconfidence(simWidth, simNumSamples);
//			}
//			if (simApproxGiven) {
//				mainLog.printWarning("Option -simapprox is not used for the ACI method and is being ignored");
//			}
//		}
//		// APMC
//		else if (simMethodName.equals("apmc")) {
//			if (isReward) {
//				throw new PrismException("Cannot use the APMC method on reward properties; try CI (switch -simci) instead");
//			}
//			if (simApproxGiven && simConfidenceGiven && simNumSamplesGiven) {
//				throw new PrismException("Cannot specify all three parameters (approximation/confidence/samples) for APMC method");
//			}
//			if (!simApproxGiven) {
//				// Default (unless width specified) is to leave approximation unknown
//				aSimMethod = new APMCapproximation(simConfidence, simNumSamples);
//			} else if (!simNumSamplesGiven) {
//				// Next preferred option (unless specified) is unknown samples
//				aSimMethod = new APMCiterations(simConfidence, simApprox);
//			} else {
//				// Otherwise confidence unknown
//				aSimMethod = new APMCconfidence(simApprox, simNumSamples);
//			}
//			if (simWidthGiven) {
//				mainLog.printWarning("Option -simwidth is not used for the APMC method and is being ignored");
//			}
//		}
//		// SPRT
//		else if (simMethodName.equals("sprt")) {
//			if (isQuant) {
//				throw new PrismException("Cannot use SPRT on a quantitative (=?) property");
//			}
//			aSimMethod = new SPRTMethod(simConfidence, simConfidence, simWidth);
//			if (simApproxGiven) {
//				mainLog.printWarning("Option -simapprox is not used for the SPRT method and is being ignored");
//			}
//			if (simNumSamplesGiven) {
//				mainLog.printWarning("Option -simsamples is not used for the SPRT method and is being ignored");
//			}
//		} else
//			throw new PrismException("Unknown simulation method \"" + simMethodName + "\"");
//
//		return aSimMethod;
//	}
	
	public static void main(String[] args){
		
		
	}
}
