package org.jcm.haiq.translate;

import org.jcm.haiq.core.*;
import org.jcm.haiq.core.HQECParameter.ECParameterType;
import org.jcm.haiq.core.HQLabel.QuantifierType;
import org.jcm.haiq.core.HQModel.ModelType;
import org.jcm.alloyutils.AlloySolution.AlloySolutionNode;
import org.jcm.alloyutils.AlloySolution;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Map;

// TODO: Reward structures: to be defined within processes themselves? 

public class PrismTranslator {

	private AlloySolution m_alloy_solution; // Alloy solution that captures the general structure of the model to translate
	private HQModel m_bmodel;			 // Hierarchy of behavioral types (abstract/concrete processes) and rewards
	
	public PrismTranslator(HQModel m, AlloySolution a){
		this.m_bmodel = m;
		this.m_alloy_solution = a;
	}
	
	/**
	 * Generates prism code for a haiq variable
	 * @param v
	 * @param prefix name of the process instance to prefix the variable (to avoid collisions with other processes)
	 * @return
	 */
	public String generateVariable(HQVariable v, String prefix){
		String res = prefix+"_"+v.getId() +" : ";
		String initString = v.getInit();
		if (isFormulaDeclared(v.getInit(),prefix))
			initString =prefix+"_"+initString;
		switch (v.getType()){
		case INTEGER:
			String minString = v.getMin();
			String maxString = v.getMax();
			if (isFormulaDeclared(v.getMin(),prefix))
				minString = prefix + "_" + minString;
			if (isFormulaDeclared(v.getMax(),prefix))
				maxString = prefix + "_" + maxString;
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
	 * Determines if a variable has been declared in a given process
	 * @param varName variable name
	 * @param procId  process id for declaration scope
	 * @return
	 */
	public boolean isVariableDeclared (String varName, String procId){
		return getProcessObject(procId).getVars().containsKey(varName);			
	}
	
	/**
	 * Helper function that gets process object definition from a given instance id
	 * @param procId
	 * @return
	 */
	public HQProcess getProcessObject(String procId){
		String procType = procId.split("_")[0];
		return m_bmodel.getProcesses().get(procType);		
	}
	
	/**
	 * Determines if a formula has been declared in a given process
	 * @param formName formula name
	 * @param procId  process id for declaration scope
	 * @return
	 */
	public boolean isFormulaDeclared (String formName, String procId){
		String procType = procId.split("_")[0];
		return m_bmodel.getProcesses().get(procType).getFormulas().containsKey(formName);			
	}

	
	/**
	 * Generates the prism code for the set of commands resulting from a given command declaration
	 * @param c haiq command object
	 * @param prefix process instance identifier
	 * @return
	 */
	public String generateCommands(HQCommand c, String prefix){
		String relation = c.getAction().getRelation();
		if (Objects.equals(relation, "")){ // If simple action, we generate a single command
			return generateCommand(c, prefix, c.getAction().getId());
		} else if (Objects.equals(relation,"this")) { // If action is explicitly identified for process
			return generateCommand(c, prefix, prefix+"_"+c.getAction().getId());
		}
		
		// If scoped to relation, look into alloy solution relation tuples for multiple command generation
		String res="";
		String prefix_dollar = prefix.replaceAll("_", "\\$");
		for (Map.Entry<String, AlloySolutionNode> e : m_alloy_solution.getRelated(m_alloy_solution.getNode(prefix_dollar),relation).entrySet()){
			//System.out.println("AAA:"+String.valueOf(e.getKey())+" BBBB:"+String.valueOf(e.getValue()));
			
			String postfix = e.getValue().getId().replaceAll("\\$", "_");
			String action_expanded = "_"+c.getAction().getId()+"_";
			if (prefix.compareTo(postfix)>0) 
				action_expanded += prefix+"_"+postfix;
			else
				action_expanded += postfix+"_"+prefix;
			
			//System.out.println("ACTION EXPANDED:"+ action_expanded);
			res += generateCommand(c, prefix, action_expanded, postfix);
		}
		return res;
	}
	
	/**
	 * Determines if a string encodes a numeric literal
	 * @param str
	 * @return
	 */
	public boolean isNumeric(String str){
	  try  
	  {  
	    Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	
	/**
	 * Determines if a string encodes a boolean literal
	 * @param str
	 * @return
	 */
	public boolean isBoolean(String str){
		return (Objects.equals(str,"true") || Objects.equals(str, "false"));
	}
	
	
	
	/**
	 * Prefixes expression literal with process identifier if it is not a numeric or boolean literal
	 * (assumes it is a formula) TODO: This should be improved to actually parse arbitrary expressions
	 * @param u
	 * @param prefix
	 * @return
	 */
	public String prefixUpdateExpression(String e, String prefix){
		if (isNumeric(e) || isBoolean(e))
			return e;
		if(isFormulaDeclared(e, prefix)){
			return prefix+"_"+e;
		}
		return e;
	}
	
	/**
	 * Prefixes update probability literal with process identifier if it is not a numeric or boolean literal
	 * (assumes it is a formula) TODO: This should be improved to actually parse arbitrary expressions
	 * @param u
	 * @param prefix
	 * @return
	 */
	public String prefixUpdateProbability(HQUpdate u, String prefix){
		String e = u.getProbability().getLiteral();
		if (isNumeric(e))
			return e;
		else
			return prefix+"_"+e;		
	}
	

	public String generatePredicate (HQPredicate p, String prefix, String relation_instance_prefix){
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
		String left = eleft.getPrefixedLiteral(getProcessObject(prefix), prefix, relation_instance_prefix);
		String right = eright.getPrefixedLiteral(getProcessObject(prefix), prefix, relation_instance_prefix);
		return left+relationString+right;
	}

	/**
	 * Generates the prism code for a guard, prefixing necessary elements with process instance identifier
	 * @param g
	 * @param prefix
	 * @return
	 */
	
	public String generateGuard(HQGuard g){ // This version for guards in free reward structures
		if (g.getPredicates().size()==0)
			return ("true"); 			// Guard contains no predicate constraining the command's applicability
		String res = "";
		int count = 0;
		for (HQPredicate p : g.getPredicates()){
			if (count>0) 
				res += " & ";
			res += p.getLiteral();
			count ++;
		}
		return res;
	}
	
	public String generateGuard (HQGuard g, String prefix, String relation_instance_prefix){
		if (g.getPredicates().size()==0)
			return ("true"); 			// Guard contains no predicate constraining the command's applicability
		String res = "";
		int count = 0;
		for (HQPredicate p : g.getPredicates()){
			if (count>0) 
				res += " & ";
			res += generatePredicate (p, prefix, relation_instance_prefix);
			count ++;
		}
		return res;
		
	}
	
	/**
	 * Generates the prism code for an update, prefixing necessary elements with process instance identifier
	 * @param u
	 * @param prefix
	 * @return
	 */
	public String generateUpdate(HQUpdate u, String prefix, String relation_instance_prefix){
		String res ="";
		
		if (!Objects.equals(u.getProbability().getLiteral(), "")) 
			res += u.getProbability().getPrefixedLiteral(getProcessObject(prefix), prefix, relation_instance_prefix) + " : ";
		
		int i=0;
		for (Map.Entry<String, HQExpression> e: u.getVariableUpdates().entrySet()){
			if (i>0)
				res+=" & ";
			String update_prefix = "";
			if (isVariableDeclared(e.getKey(), prefix))
				update_prefix = prefix+"_";
			res += "("+update_prefix+e.getKey()+"'="+e.getValue().getPrefixedLiteral(getProcessObject(prefix), prefix, relation_instance_prefix)+")";
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
	
	public String generateCommand(HQCommand c, String prefix, String action_expanded){
		return generateCommand(c, prefix, action_expanded,"");		
	}
	
	public String generateCommand(HQCommand c, String prefix, String action_expanded, String relation_instance_prefix){
		String res = "\t["+action_expanded+"] ("+generateGuard(c.getGuard(), prefix, relation_instance_prefix)+") -> ";
		
		int i=0;
		for (HQUpdate u: c.getUpdates()) {
			res += generateUpdate(u, prefix, relation_instance_prefix);
			i++;
			if (i<c.getUpdates().size())
				res+=" + ";
			else 
				res+=";\n";
		}
		return res;		
	}
	
	public String generateFormula(HQFormula f, String prefix){
		String ext_prefix =prefix+"_";
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
		return "formula "+ ext_prefix + f.getId() + " = " + f.getExpression().getPrefixedLiteral(getProcessObject(prefix), prefix, "") + ";\n";
	}
	
	public String generateECParameter(HQECParameter p, String prefix){
		String ext_prefix =prefix+"_";
		if (Objects.equals(prefix, "")){
			ext_prefix="";
		}
		String typeStr = "int";
		if (p.getType()==ECParameterType.DOUBLE) typeStr = "double";
		return "evolve "+typeStr+" "+ext_prefix+p.getId()+"["+p.getMin()+".."+p.getMax()+"];\n";
	}
	
	public String generateECDistribution(HQECDistribution d, String prefix){
		String ext_prefix=prefix+"_";
		if (Objects.equals(prefix, "")){
			ext_prefix="";
		}
		String res = "evolve distribution "+ext_prefix+d.getId();
		for (int i=0; i<d.size();i++){
			res += "["+d.getMin(i)+".."+d.getMax(i)+"]";
		}
		return res+";\n";
	}
	
	public String generateProcess(HQProcess p){
		return generateProcess(p, "");
	}

	public String generateProcess(HQProcess p, String prefix){
		String res ="";

		for (Map.Entry<String, HQFormula> e: p.getFormulas().entrySet()){
			res += generateFormula (e.getValue(), prefix);
		}

		for (Map.Entry<String, HQECParameter> e: p.getECParameters().entrySet()){
			res += generateECParameter (e.getValue(), prefix);
		}
		
		for (Map.Entry<String, HQECDistribution> e: p.getECDistributions().entrySet()){
			res += generateECDistribution (e.getValue(), prefix);
		}

		res += "module " + prefix + "\n";
		
		for (Map.Entry<String, HQVariable> e: p.getVars().entrySet()){
			res += generateVariable (e.getValue(), prefix);
		}
		
		for (HQCommand c: p.getCommands()){
			res += generateCommands (c, prefix);
		}
		
		res += "endmodule\n\n";
		return res;
		
	}
	
	
	public String generateReward(HQReward r){
		String res = "";		
		LinkedList<String> expandedL = new LinkedList<String>(); // Employed to avoid duplicate action entries in reward structure
		
		if (Objects.equals(r.getScope(),"")){ // Free reward (not within the scope of a process)
			res +="\t";
			if (!Objects.equals(r.getAction().getId(), "")){ // Action reward
				res += "["+r.getAction().getId()+"] ";
			}
			res += generateGuard(r.getGuard()) + " : " + r.getExpression().getLiteral() + ";\n";	
			return res;
		}
		
		
		for (Map.Entry<String, AlloySolutionNode> e : m_alloy_solution.getNodes().entrySet()){ // Reward within process
			String processType = e.getValue().getId().split("\\$")[0];
			String processInstance = e.getValue().getId().replaceAll("\\$", "_");
		
			String prefix_dollar =e.getValue().getId();
			if (!Objects.equals(r.getAction().getId(), "")){ // Action reward
				
				if (m_bmodel.getAncestors(processType).contains(r.getScope())){
					if (Objects.equals(r.getAction().getRelation(), "")){
						res += "\t["+r.getAction().getId()+"] ";
						res += generateGuard(r.getGuard(), processInstance, "") + " : " + r.getExpression().getPrefixedLiteral(m_bmodel.getProcesses().get(processType), processInstance, "") + ";\n";						
					} else {
						for (Map.Entry<String, AlloySolutionNode> re : m_alloy_solution.getRelated(m_alloy_solution.getNode(prefix_dollar),r.getAction().getRelation()).entrySet()){
							String postfix = re.getValue().getId().replaceAll("\\$", "_");
							String action_expanded = "_"+r.getAction().getId()+"_";
							if (processInstance.compareTo(postfix)>0) 
								action_expanded += processInstance+"_"+postfix;
							else
								action_expanded += postfix+"_"+processInstance;
							if (!expandedL.contains(action_expanded)){												
								res += "\t["+action_expanded+"] ";
								res += generateGuard(r.getGuard(), processInstance, postfix) + " : " + r.getExpression().getPrefixedLiteral(m_bmodel.getProcesses().get(processType), processInstance, postfix) + ";\n";
								expandedL.add(action_expanded);
							}
						}
					}
				}
				
				
			} else { // State reward
				if (Objects.equals(processType, r.getScope())){
					res += "\t"+generateGuard(r.getGuard(), processInstance, "") + " : " + r.getExpression().getPrefixedLiteral(m_bmodel.getProcesses().get(processType), processInstance, "") + ";\n";
			}
			}
		}
		return res;
	}
	
	
	
	public String generateRewardStructure(HQRewardStructure r){
		String res = "rewards " + "\""+r.getId()+"\"\n"; 
		for (HQReward e: r.getRewards()){
			res += generateReward(e);
		}
		res += "endrewards\n\n";
		return res;
	}
	
	public String generateLabel(HQLabel l){
		String res = "formula "+l.getId()+" = ";
		
		int i=0;
		String quantOpStr=" & ";
		if (l.getQuantifier() == QuantifierType.SOME)
			quantOpStr = " | ";
		if (m_alloy_solution.getInstances(l.getType()).size()>0)
			for (String instance: m_alloy_solution.getInstances(l.getType())){
				if (i>0)
					res+= quantOpStr;
				if (!Objects.equals("", instance)) {
					res += generatePredicate(l.getPredicate(), instance.replaceAll("\\$", "_"), "");
					i++;
				}
			}
		if (i==0)
			res += "false";
		return res +";\n";
	}
	
	
	/**
	 * DEPRECATED
	 * @param m
	 * @return
	 */
	public String generateModel(HQModel m){
		String res = "";
		switch(m.getType()){
		case DTMC:
			res +="dtmc\n\n";
			break;
		case MDP:
			res+="mdp\n\n";
			break;
		case SMG:
			res+="smg\n\n";
			break;
		case CTMC:
			res+="ctmc\n\n";
			break;
		}
		
		for (Map.Entry<String, HQProcess> e : m.getProcesses().entrySet()){
			if (!e.getValue().isAbstract())
				res += generateProcess (e.getValue());
		}
		

		for (Map.Entry<String, HQRewardStructure> e : m.getRewardStructures().entrySet()){
			res += generateRewardStructure (e.getValue());
		}
		
		return res;
	}
	
	
	/**
	 * Generates a PRISM encoding from a given alloy structure, using processes in the behavioral model m_bmodel
	 * @return
	 */
	public String generateModelFromAlloy(){
		String res = "";
		switch(m_bmodel.getType()){
		case DTMC:
			res +="dtmc\n\n";
			break;
		case MDP:
			res+="mdp\n\n";
			break;
		case SMG:
			res+="smg\n\n";
			break;
		case CTMC:
			res+="ctmc\n\n";
			break;
		}
		
		for (int i=0; i<m_bmodel.getLiterals().size();i++) {
			res += m_bmodel.getLiterals().get(i)+"\n";
		}
		
		for (Map.Entry<String, AlloySolutionNode> e : m_alloy_solution.getNodes().entrySet()){
			String processType = e.getValue().getId().split("\\$")[0];
			//System.out.println("PROCESS TYPE:"+processType);
			String processInstance = e.getValue().getId().replaceAll("\\$", "_");
			res += generateProcess (m_bmodel.getProcesses().get(processType), processInstance);
		}
		
		for (Map.Entry<String, HQFormula> e : m_bmodel.getFormulas().entrySet()){
			res += generateFormula (e.getValue(), "");
		}
		
		for (Map.Entry<String, HQRewardStructure> e : m_bmodel.getRewardStructures().entrySet()){
			res += generateRewardStructure (e.getValue());
		}
		
		for (Map.Entry<String, HQLabel> e : m_bmodel.getLabels().entrySet()){
			res += generateLabel (e.getValue());
		}
		
		for (Map.Entry<String, HQECParameter> e : m_bmodel.getECParameters().entrySet()){
			res += generateECParameter (e.getValue(), "");
		}
		
		for (Map.Entry<String, HQECDistribution> e: m_bmodel.getECDistributions().entrySet()){
			res += generateECDistribution (e.getValue(), "");
		}
		
		return res;
	}
	
	
	public void setBehavioralModel(HQModel m){
		m_bmodel = m;
	}
	
	public void setAlloySolution(AlloySolution a){
		m_alloy_solution = a;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HQProcess m1 = new HQProcess("m1");
		m1.addVariable(new HQVariable ("x", false));
		m1.addFormula(new HQFormula ("p1",new HQExpression("0.2")));
		m1.addFormula(new HQFormula ("p2",new HQExpression("0.8") ));
		HQGuard g = new HQGuard();
		g.addPredicate(new HQPredicate(new HQExpression("x"), HQPredicate.RelationType.EQUALS, new HQExpression("false")));
		HQCommand c1 = new HQCommand("a",g);

//		c1.addUpdate("x", "p1", "false");
//		c1.addUpdate("x", "p2", "true");
		m1.addCommand(c1);
			
		HQProcess m2 = new HQProcess("m2");
		m2.addVariable(new HQVariable ("y", false));
		HQGuard g2 = new HQGuard();
		g2.addPredicate(new HQPredicate(new HQExpression("y"), HQPredicate.RelationType.EQUALS, new HQExpression("false")));
		HQCommand c2 = new HQCommand("a",g2);

//		c2.addUpdate("y", "0.4", "false");
//		c2.addUpdate("y", "0.6", "true");
		m2.addCommand(c2);

		HQModel m = new HQModel (ModelType.DTMC);
		m.addProcess(m1.getId(), m1);
		m.addProcess(m2.getId(), m2);
		
		HQRewardStructure rs = new HQRewardStructure("acount");
		rs.addReward(new HQReward(new HQAction("a","rel"), new HQGuard(), new HQExpression("1"), "foo"));
		
		m.addRewardStructure(rs.getId(), rs);
		
	}

}
