/**
 * 
 */
package org.jcm.haiq.translate;

import org.jcm.alloyutils.AlloySolution;
import org.jcm.alloyutils.AlloySolution.AlloySolutionNode;
import org.jcm.haiq.core.HQCommand;
import org.jcm.haiq.core.HQECDistribution;
import org.jcm.haiq.core.HQECParameter;
import org.jcm.haiq.core.HQExpression;
import org.jcm.haiq.core.HQFormula;
import org.jcm.haiq.core.HQGuard;
import org.jcm.haiq.core.HQLabel;
import org.jcm.haiq.core.HQModel;
import org.jcm.haiq.core.HQPredicate;
import org.jcm.haiq.core.HQProcess;
import org.jcm.haiq.core.HQRewardStructure;
import org.jcm.haiq.core.HQUpdate;
import org.jcm.haiq.core.HQVariable;
import org.jcm.haiq.core.HQLabel.QuantifierType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedList;

/**
 * @author javiercamaramoreno
 *
 */
public class PrismTranslatorMDP extends PrismTranslator {

	private HashMap<String, AlloySolution> m_alloy_solutions; 
	private HQModel m_bmodel;			 // Hierarchy of behavioral types (abstract/concrete processes) and rewards
	private static final String SOL_CHOICE = "sol_choice";
	
	/**
	 * @param m
	 * @param a
	 */
	public PrismTranslatorMDP(HQModel m, HashMap<String,AlloySolution> a) {
		super (m,null);
		this.m_bmodel = m;
		this.m_alloy_solutions = a;
	}

	
	public String generateProcess(HQProcess p, String prefix, AlloySolution a, String solId){
		String res ="";

		for (Map.Entry<String, HQFormula> e: p.getFormulas().entrySet()){
				res += generateFormula (e.getValue(), prefix, solId);
		}

//		for (Map.Entry<String, HQECParameter> e: p.getECParameters().entrySet()){
//			res += generateECParameter (e.getValue(), prefix);
//		}
//		
//		for (Map.Entry<String, HQECDistribution> e: p.getECDistributions().entrySet()){
//			res += generateECDistribution (e.getValue(), prefix);
//		}

		res += "module " + solId +"_"+ prefix + "\n";
		
		for (Map.Entry<String, HQVariable> e: p.getVars().entrySet()){
			res += generateVariable (e.getValue(), prefix, solId);
		}
		
		for (HQCommand c: p.getCommands()){
			res += generateCommands (c, prefix, a, solId);
		}
		
		res += "endmodule\n\n";
		return res;
		
	}
	
	
	/**
	 * Generates prism code for a haiq variable
	 * @param v
	 * @param prefix name of the process instance to prefix the variable (to avoid collisions with other processes)
	 * @return
	 */
	public String generateVariable(HQVariable v, String prefix, String solId){
		String res = solId+"_"+prefix+"_"+v.getId() +" : ";
		String initString = v.getInit();
		if (isFormulaDeclared(v.getInit(),prefix))
			initString =solId+"_"+prefix+"_"+initString;
		switch (v.getType()){
		case INTEGER:
			String minString = v.getMin();
			String maxString = v.getMax();
			if (isFormulaDeclared(v.getMin(),prefix))
				minString = solId+"_"+prefix + "_" + minString;
			if (isFormulaDeclared(v.getMax(),prefix))
				maxString = solId+"_"+prefix + "_" + maxString;
			res += "["+minString+".."+maxString+"] init "+initString+";\n";
			break;
		case BOOLEAN:
			res += "bool init ";
			if (v.getInitBoolean())
				res += "true;\n";
			else
				res += "false;\n";
			break;
		}
		return res;
	}
	
	
	/**
	 * Generates the prism code for the set of commands resulting from a given command declaration
	 * @param c haiq command object
	 * @param prefix process instance identifier
	 * @return
	 */
	public String generateCommands(HQCommand c, String prefix, AlloySolution a, String solId){
		String relation = c.getAction().getRelation();
		if (Objects.equals(relation, "")){ // If simple action, we generate a single command
			return generateCommand(c, prefix, c.getAction().getId(), solId);
		} else if (Objects.equals(relation,"this")) { // If action is explicitly identified for process
			return generateCommand(c, prefix, prefix+"_"+c.getAction().getId(), solId);
		}
		
		// If scoped to relation, look into alloy solution relation tuples for multiple command generation
		String res="";
		String prefix_dollar = prefix.replaceAll("_", "\\$");
		for (Map.Entry<String, AlloySolutionNode> e : a.getRelated(a.getNode(prefix_dollar),relation).entrySet()){
			//System.out.println("AAA:"+String.valueOf(e.getKey())+" BBBB:"+String.valueOf(e.getValue()));
			
			String postfix = e.getValue().getId().replaceAll("\\$", "_");
			String action_expanded = "_"+c.getAction().getId()+"_";
			if (prefix.compareTo(postfix)>0) 
				action_expanded += prefix+"_"+postfix;
			else
				action_expanded += postfix+"_"+prefix;
			
			//System.out.println("ACTION EXPANDED:"+ action_expanded);
			res += generateCommand(c, prefix, action_expanded, postfix, solId);
		}
		return res;
	}
	
	
	/**
	 * Generates the prism code for an update, prefixing necessary elements with process instance identifier
	 * @param u
	 * @param prefix
	 * @return
	 */
	public String generateUpdate(HQUpdate u, String prefix, String relation_instance_prefix, String solId){
		String res ="";
		
		if (!Objects.equals(u.getProbability().getLiteral(), "")) 
			res += u.getProbability().getPrefixedLiteral(getProcessObject(prefix), prefix, relation_instance_prefix, solId, m_bmodel) + " : ";
		
		int i=0;
		for (Map.Entry<String, HQExpression> e: u.getVariableUpdates().entrySet()){
			if (i>0)
				res+=" & ";
			String update_prefix = "";
			if (isVariableDeclared(e.getKey(), prefix))
				update_prefix = solId+"_"+prefix+"_";
			res += "("+update_prefix+e.getKey()+"'="+e.getValue().getPrefixedLiteral(getProcessObject(prefix), prefix, relation_instance_prefix, solId, m_bmodel)+")";
			i++;
		}		
		return res;
	}
	
	
	
	
	/**
	 * Generates the prism code for a command, prefixing necessary elements with process instance identifier
	 * @param c
	 * @param prefix
	 * @param action_expanded action identifier (already expanded from a relation:action pair)
	 * @return
	 */
	
	public String generateCommand(HQCommand c, String prefix, String action_expanded, String solId){
		return generateCommand(c, prefix, action_expanded,"", solId);		
	}
	
	public String generateCommand(HQCommand c, String prefix, String action_expanded, String relation_instance_prefix, String solId){
		String solPrefix = Objects.equals(action_expanded,"")? "" : solId;
		String res = "\t["+solPrefix+action_expanded+"] ("+generateGuard(c.getGuard(), prefix, relation_instance_prefix, solId)+") -> ";
		
		int i=0;
		for (HQUpdate u: c.getUpdates()) {
			res += generateUpdate(u, prefix, relation_instance_prefix, solId);
			i++;
			if (i<c.getUpdates().size())
				res+=" + ";
			else 
				res+=";\n";
		}
		return res;		
	}
	
	
	/**
	 *  Generates a formula
	 */
	public String generateFormula(HQFormula f, String prefix, String solId){
		String ext_prefix =solId+"_"+prefix+"_";
		if (Objects.equals(prefix,"")){
			ext_prefix="";
			String typeStr = "";
			if (f.isConstant()){
				if (f.isDouble())
					typeStr = "double ";
				if (!f.isDefined()){
					return "const "+ typeStr + f.getId() + ";\n";
				} else {
					return "const "+ typeStr + f.getId() + " = " + f.getExpression().getLiteral() + ";\n";
				}
			}
		}
		return "formula "+ ext_prefix + f.getId() + " = " + f.getExpression().getPrefixedLiteral(getProcessObject(prefix), prefix, "", solId, m_bmodel) + ";\n";
	}
	
	/**
	 * Generates the prism code for a guard, prefixing necessary elements with process instance identifier
	 * @param g
	 * @param prefix
	 * @return
	 */
	
	public String generateGuard(HQGuard g, String solId){ // This version for guards in free reward structures
		String solGuard = "("+SOL_CHOICE+"="+solId+") & ";
		if (g.getPredicates().size()==0)
			return (solGuard + "true"); 			// Guard contains no predicate constraining the command's applicability
		String res = "";
		int count = 0;
		for (HQPredicate p : g.getPredicates()){
			if (count>0) 
				res += " & ";
			res += p.getLiteral();
			count ++;
		}
		return solGuard + res;
	}
	
	public String generateGuard (HQGuard g, String prefix, String relation_instance_prefix, String solId){
		String solGuard = "("+SOL_CHOICE+"="+solId+") & ";
		if (g.getPredicates().size()==0)
			return (solGuard+"true"); 			// Guard contains no predicate constraining the command's applicability
		String res = "";
		int count = 0;
		for (HQPredicate p : g.getPredicates()){
			if (count>0) 
				res += " & ";
			res += generatePredicate (p, prefix, relation_instance_prefix, solId);
			count ++;
		}
		return solGuard + res;
		
	}

	public String generatePredicate (HQPredicate p, String prefix, String relation_instance_prefix, String solId){
		HQPredicate.RelationType t = p.getRelation();
		HQExpression eleft = p.getLeft();
		HQExpression eright = p.getRight();

		String relationString = "";
		switch (t){
		case LEQ:
			relationString = "<=";
			break;
		case GEQ:
			relationString = ">=";
			break;			
		case NOT_EQUALS:
			relationString = "!=";
			break;
		case GREATER_THAN:
			relationString = ">";
			break;
		case LESS_THAN:
			relationString = "<";
			break;
		case EQUALS:
			relationString = "=";
			break;
		}
		String left = eleft.getPrefixedLiteral(getProcessObject(prefix), prefix, relation_instance_prefix, solId, m_bmodel);
		String right = eright.getPrefixedLiteral(getProcessObject(prefix), prefix, relation_instance_prefix, solId, m_bmodel);
		return left+relationString+right;
	}
	
	
	public String generateLabel(HQLabel l){
		String res = "formula "+l.getId()+" = ";
		String quantOpStr=" & ";
		if (l.getQuantifier() == QuantifierType.SOME)
			quantOpStr = " | ";
		
		int sc=0;
		for (Map.Entry<String, AlloySolution> sols : m_alloy_solutions.entrySet()) {
			AlloySolution a = sols.getValue();
			String ksol = sols.getKey();
			
			res += "("+SOL_CHOICE+"="+ksol+" & (";
			int i=0;
			if (a.getInstances(l.getType()).size()>0)
				for (String instance: a.getInstances(l.getType())){
					if (i>0)
						res+= quantOpStr;
					if (!Objects.equals("", instance)) {
						res += generatePredicate(l.getPredicate(), instance.replaceAll("\\$", "_"), "", ksol);
						i++;
					}
				}
			if (i==0)
				res += "false";
			i=0;
			res+="))";
			if (sc<m_alloy_solutions.size()-1)
					res += " | ";
			sc++;
		}
		

		return res +";\n";
	}
	
	public String generateChoiceModule() {
		String res= "";
		String constDefs = "// Solution choice constants\n";
		String commands = "";
		int c=0;
		for (Map.Entry<String, AlloySolution> sols : m_alloy_solutions.entrySet()) {
			constDefs += "const "+sols.getKey()+"="+c+";\n";
			commands +="\t["+sols.getKey()+"] ("+SOL_CHOICE+"=-1) -> ("+SOL_CHOICE+"'="+sols.getKey()+");\n";
			c++;
		}
		res += constDefs;
		res += "\n//Solution choice...\nmodule choice\n\t"+SOL_CHOICE+": [-1.."+m_alloy_solutions.size()+"] init -1;\n";
		res += commands;
		res += "endmodule\n\n";
		return res;
	}
	
	
	/**
	 * Generates a PRISM encoding from a given alloy structure, using processes in the behavioral model m_bmodel
	 * @return
	 */
	public String generateModelFromAlloy(){
		String res = "mdp\n\n";
		
		res += generateChoiceModule();
		
		for (int i=0; i<m_bmodel.getLiterals().size();i++) {
			res += m_bmodel.getLiterals().get(i)+"\n";
		}
		
		
		for (Map.Entry<String, AlloySolution> sols : m_alloy_solutions.entrySet()) {
			AlloySolution sol = sols.getValue();
			String ksol = sols.getKey();
			for (Map.Entry<String, AlloySolutionNode> e : sol.getNodes().entrySet()){
				String processType = e.getValue().getId().split("\\$")[0];
				//System.out.println("PROCESS TYPE:"+processType);
				String processInstance = e.getValue().getId().replaceAll("\\$", "_");
				res += generateProcess (m_bmodel.getProcesses().get(processType), processInstance, sol, ksol);
			}


			
			res+= "\n// "+ksol+" ----------------------\n\n";
		
		}
		
		for (Map.Entry<String, HQLabel> e : m_bmodel.getLabels().entrySet()){
			res += generateLabel (e.getValue());
		}
		
		for (Map.Entry<String, HQFormula> e : m_bmodel.getFormulas().entrySet()){
			res += generateFormula (e.getValue(), "");
		}
		
		for (Map.Entry<String, HQRewardStructure> e : m_bmodel.getRewardStructures().entrySet()){
			res += generateRewardStructure (e.getValue());
		}
		
//		for (Map.Entry<String, HQLabel> e : m_bmodel.getLabels().entrySet()){
//			res += generateLabel (e.getValue(), a);
//		}
		
		for (Map.Entry<String, HQECParameter> e : m_bmodel.getECParameters().entrySet()){
			res += generateECParameter (e.getValue(), "");
		}
		
		for (Map.Entry<String, HQECDistribution> e: m_bmodel.getECDistributions().entrySet()){
			res += generateECDistribution (e.getValue(), "");
		}
		
		return res;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
