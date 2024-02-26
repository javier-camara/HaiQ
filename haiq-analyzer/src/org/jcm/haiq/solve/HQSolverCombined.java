package org.jcm.haiq.solve;

import org.jcm.alloyutils.AlloyConnector;
import org.jcm.alloyutils.AlloySolution;
import org.jcm.haiq.core.HQModel;
import org.jcm.haiq.core.HQProperty;
import org.jcm.haiq.parse.ParserMark1.PropertyEntry;
import org.jcm.haiq.translate.PrismTranslatorMDP;
import org.jcm.prismutils.PrismPolicyReader;
import org.jcm.util.ProgressBar;
import org.jcm.util.TextFileHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HQSolverCombined extends HQSolverIterative {

	public HQSolverCombined(String tmp_path) {
		super(tmp_path);
		// TODO Auto-generated constructor stub
	}

	public HQSolverCombined(String tmp_path, String engine) {
		super(tmp_path, engine);
		// TODO Auto-generated constructor stub
	}
	
	
	public int generateSolutions(String modelFile, HQModel bModel){
		m_ac.generateSolutions(modelFile);
		HashMap<String, AlloySolution> sols = new HashMap<String,AlloySolution>();
		
		for (int i=0; i<m_ac.getSolutions().size();i++){
			String strSolId = AlloyConnector.SOLUTION_STRING+String.valueOf(i);
			String strSol = m_ac.getSolution(strSolId);
			AlloySolution sol = new AlloySolution();
			sol.loadFromString(strSol);
			sols.put(strSolId, sol);
			m_structures.put(strSolId, sol.toJSON(strSolId));
		}
		PrismTranslatorMDP pt = new PrismTranslatorMDP(bModel,sols);
		m_solutions.put("sol_0", pt.generateModelFromAlloy());
		return m_ac.getSolutions().size();
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
		
		
//		if (bModel.getType()==HQModel.ModelType.DTMC && 
//			(prop.isMaximizationQuantifier(prop.getQtype()) || prop.isMinimizationQuantifier(prop.getQtype()) ) ){
//				System.out.println("\n[Incompatible Property-Model]: Only P and Q-based quantifiers allowed for DTMC models (no Pmax/Pmin, Rmax/Rmin-based). Exiting...");
//				System.exit(-1);
//		}
		
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
		
//		m_pc.setModelCheckUnderStrategy(false);		
		ProgressBar b = new ProgressBar(m_solutions.entrySet().size(), 0.5);
		int modelIndex = 0;
		for (Map.Entry<String, String> e: m_solutions.entrySet()){
			if (m_export_models) {
				TextFileHandler th = new TextFileHandler(myModel+Integer.toString(modelIndex));
				th.exportFile(e.getValue());
			}
			modelIndex++;
			m_pc.setConstants(constStr);
			m_pc.setGenerateStrategy(true, myPolicy);
			//m_pc.setModelCheckUnderStrategy(false);
			if (!m_sim_enabled){
				res = m_pc.modelCheckFromStrings(e.getValue(), prop.getPCTLTranslation(), myPolicy, propertyIndex);
				//res = m_pc.modelCheckFromFileS(myModel, propertiesFile, myPolicy, propertyIndex);
				PrismPolicyReader pr = new PrismPolicyReader(myPolicy);
				pr.readPolicy();
				m_policies.put(e.getKey(), pr.getPlan());
				m_scoreboard.addSolutionforProperty(e.getKey(), property_alias, res);
				if (m_debug)
					System.out.println("Strategy for "+e.getKey()+": "+m_policies.get(e.getKey()).toString());
			} else {
				// Simulation-based statistical model checking * NOT IMPLEMENTED YET *
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
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
