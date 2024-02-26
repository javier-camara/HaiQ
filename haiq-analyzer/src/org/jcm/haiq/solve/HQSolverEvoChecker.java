package org.jcm.haiq.solve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jcm.alloyutils.AlloyConnector;
import org.jcm.alloyutils.AlloySolution;
import org.jcm.haiq.core.HQModel;
import org.jcm.haiq.core.HQProperty;
import org.jcm.haiq.parse.ParserMark1.PropertyEntry;
import org.jcm.haiq.solve.HQSolverIterative.PolicyExport;
import org.jcm.haiq.solve.HQSolverIterative.StructureExport;
import org.jcm.haiq.translate.PrismTranslator;
import org.jcm.prismutils.PrismConnector;
import org.jcm.prismutils.PrismConnector.PrismConnectorMode;
import org.jcm.util.TextFileHandler;

import evochecker.EvoChecker;

public class HQSolverEvoChecker implements HQSolver {
	
	public void setConstantDefinitions(String m_consts_string) {}
	public void exportConfigurations(String m_configurationsJSON_output, StructureExport json) {}
	public void setExportModels(boolean b) {}
	public void setExportPolicies(boolean b) {}
	public int getTotalSolutions(){return m_solutions.size();}
	public void exportPolicies(String m_policies_output, PolicyExport plaintext, ArrayList<String> m_policies_export_params) {}



	

	protected HashMap<String, String> m_solutions = new HashMap<String, String>();
	protected HashMap<String, String> m_structures = new HashMap<String, String>();
	protected AlloyConnector m_ac;
	protected EvoChecker m_ec;
	protected boolean m_debug=false;
	protected ScoreBoard m_scoreboard = new ScoreBoard();
	protected HashMap<String, ConstantDefinition> m_constant_definitions = new HashMap<String, ConstantDefinition>();
	protected static String m_tmp_path;
	protected int m_total_commands_generated=0;
	protected int m_total_lines_generated=0;
	protected int m_total_formulas_generated=0;
	protected int m_total_modules_generated=0;
	
	
	public HQSolverEvoChecker(String tmp_path) {
		// TODO Auto-generated constructor stub
		this.m_tmp_path=tmp_path;
		m_ac = new AlloyConnector();
	}

	/**
	 * Set the maximum number of configurations to generate
	 */	
	public void setMaxConfigs (int n){
		m_ac.setMaxSolutions(n);
	}

	public ScoreBoard getScoreBoard(){
		return m_scoreboard;
	}
	
	public HashMap<String, String> getConfigurations(){
		return null; // Dummy method
	}
	
	public static void generateConfigFile() {
		
	}
	
	/*
	 *  Counts number of lines in a string
	 */
	private static int countLines(String str){
		   String[] lines = str.split("\r\n|\r|\n");
		   return  lines.length;
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
			String currentSolStr = pt.generateModelFromAlloy();
			m_solutions.put(strSolId, currentSolStr);
			m_total_formulas_generated += pt.getM_formulas_generated();
			m_total_commands_generated += pt.getM_commands_generated();
			m_total_modules_generated += pt.getM_modules_generated();
			m_total_lines_generated +=  countLines(currentSolStr);
			
			if (m_debug)
				System.out.println("\n\n\n SOLUTION "+String.valueOf(i)+" ----------------------\n\n\n" + m_solutions.get(strSolId));
		}
		return m_ac.getSolutions().size();
	}
		
	public int getTotalLinesGenerated() {
		return m_total_lines_generated;
	}

	public int getTotalCommandsGenerated() {
		return m_total_commands_generated;
	}

	public int getTotalFormulasGenerated() {
		return m_total_formulas_generated;
	}
	
	public int getTotalModulesGenerated() {
		return m_total_modules_generated;
	}

	
	public String runExperiment(PropertyEntry pe, HQModel bModel){
		String res="";
		
		if (m_solutions.entrySet().size()==0) { // If no structures satisfying constraints have been found, exit
			System.out.println("\n[S-Set empty]: No structures satisfying the constraints have been found. Exiting...");
			System.exit(-1);
		}		
		return checkSolution(pe, bModel);
	}

	public String getEClibsdir () {
		Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            if (Objects.equals(envName, "EC_LIB_PATH")){
            	return env.get(envName);       
            }
        }
        return "";
	}
	
	public String generateECConfigCode(String problem, String model_template, String properties_file) {
		String res="PROBLEM = "+problem+"\n"
				+ "\tMODEL_TEMPLATE_FILE = "+ model_template+"0"+"\n"
				+ "\tPROPERTIES_FILE = "+properties_file+"\n"
				+ "\tALGORITHM = NSGAII\n"
				+ "\tPOPULATION_SIZE = 50\n"
				+ "\tMAX_EVALUATIONS = 1000\n"
				+ "\tPROCESSORS = 1\n"
				+ "\tVERBOSE = false\n"
				+ "\tMODEL_CHECKING_ENGINE = "+getEClibsdir()+"/PrismExecutor.jar\n"
				+ "\tMODEL_CHECKING_ENGINE_LIBS_DIRECTORY = "+getEClibsdir()+"/runtime\n";
		return res;		
	}
	
	public String checkSolution(PropertyEntry pe, HQModel bModel){
		String res ="-";
		String property = pe.getLiteral();
		String property_alias = pe.getId();
		String final_result =res;
		

		int propertyIndex = 0;
		String myModel = m_tmp_path+"/temp.prism";
		String propertiesFile = m_tmp_path+"/temp.props";
		String ECpropertiesFile = m_tmp_path+"/ECtemp.props"; // For EvoChecker props
		String ECConfigFile = m_tmp_path+"/ECconfig.properties";

	
		TextFileHandler thp = new TextFileHandler(propertiesFile);
		TextFileHandler thp2 = new TextFileHandler(ECpropertiesFile);
		TextFileHandler thp3 = new TextFileHandler(ECConfigFile);
		
		//System.out.println("Prop:" + property);
		//System.out.println("Alias:" + property_alias);
		
		thp3.exportFile(generateECConfigCode("test", myModel, ECpropertiesFile));
		
		HQProperty prop = new HQProperty(property);
		thp2.exportFile(prop.getECAnnotation()+"\n"+prop.getPCTLTranslation()); // For EvoChecker props - extend to multiple ones

		
		int modelIndex = 0;
		for (Map.Entry<String, String> e: m_solutions.entrySet()){
			TextFileHandler th = new TextFileHandler(myModel+Integer.toString(modelIndex));
			th.exportFile(e.getValue());
			modelIndex++;
		}
		
		m_ec = new EvoChecker();
		m_ec.setConfigurationFile(ECConfigFile);
		m_ec.start();
		
		
		return res;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
	}
	
}
