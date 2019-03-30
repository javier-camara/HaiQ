package org.jcm.haiq.core;

import java.util.HashMap;
import java.util.Objects;
import java.util.LinkedList;
import java.util.Map;

public class HQModel {

	public enum ModelType { DTMC, MDP, SMG, CTMC }
	
	private ModelType m_type;
	
	private HashMap<String, HQProcess> m_processes = new HashMap<String, HQProcess>();
	private HashMap<String, HQVariable> m_vars = new HashMap<String, HQVariable>();
	private HashMap<String, HQFormula> m_formulas = new HashMap<String, HQFormula>();
	private HashMap<String, HQEnum> m_enums = new HashMap<String, HQEnum>();
	private HashMap<String, HQLabel> m_labels = new HashMap<String, HQLabel>();
	private HashMap<String, HQRewardStructure> m_rewards = new HashMap<String, HQRewardStructure>();
	// EC Extension
	private HashMap<String, HQECParameter> m_ECparams = new HashMap<String, HQECParameter>();
	private HashMap<String, HQECDistribution> m_ECdistributions = new HashMap<String, HQECDistribution>();

	
	public HQModel (ModelType type){
		this.m_type = type;
	}
	
	public HQModel(){
		this.m_type = ModelType.DTMC;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQModel [m_type=" + m_type + ", m_processes=" + m_processes + ", m_vars=" + m_vars + ", m_formulas="
				+ m_formulas + ", m_enums=" + m_enums + ", m_labels=" + m_labels + ", m_rewards=" + m_rewards +
				  "m_ECparams" + m_ECparams.toString() + "m_ECdistributions" + m_ECdistributions.toString() + "]";
	}

	/**
	 * @return the m_labels
	 */
	public HashMap<String, HQLabel> getLabels() {
		return m_labels;
	}

	public void addFormula(HQFormula f){
		m_formulas.put(f.getId(), f);
	}
	
	public HashMap<String, HQFormula> getFormulas(){
		return m_formulas;
	}
	
	
	/**
	 * @return the m_enums
	 */
	public HashMap<String, HQEnum> getEnums() {
		return m_enums;
	}

	/**
	 * @param m_enums the m_enums to set
	 */
	public void addEnum(HQEnum e) {
		m_enums.put(e.getId(), e);
	}

	public boolean containsEnumItem(String s){
		for (HashMap.Entry<String, HQEnum> e: m_enums.entrySet()){
			if (e.getValue().getItems().contains(s))
				return true;
		}
		return false;
	}
	
	public int getEnumItemIndex(String s){
		for (HashMap.Entry<String, HQEnum> e: m_enums.entrySet()){
			if (e.getValue().getItems().contains(s))
				return e.getValue().getItems().indexOf(s);
		}
		return -1;
	}
	
	
	public void addLabel (HQLabel l){
		m_labels.put(l.getId(), l);
	}
	
	public ModelType getType(){
		return this.m_type;
	}

	public void setType(ModelType m_type) {
		this.m_type = m_type;
	}

	public void addVariable(HQVariable v){
		this.m_vars.put(v.getId(), v);
	}
	
	public void addProcess (String id, HQProcess p){
		this.m_processes.put(id, p);
	}
	
	public void addRewardStructure (String id, HQRewardStructure r){
		this.m_rewards.put(id, r);
	}
	
	public void addECParameter (String id, HQECParameter p){
		this.m_ECparams.put(id, p);
	}
		
	public void addECDistribution (String id, HQECDistribution d){
		this.m_ECdistributions.put(id,d);
	}
	
	/**
	 * @return the m_processes
	 */
	public HashMap<String, HQProcess> getProcesses() {
		return m_processes;
	}

	/**
	 * @return the m_globals
	 */
	public HashMap<String, HQVariable> getVariables() {
		return m_vars;
	}

	/**
	 * @return the m_rewards
	 */
	public HashMap<String, HQRewardStructure> getRewardStructures() {
		return m_rewards;
	}
	
	/**
	 * @return the m_ECparams
	 */
	public HashMap<String, HQECParameter> getECParameters() {
		return m_ECparams;
	}
	
	
	/**
	 * @return the m_ECdistributions
	 */
	public HashMap<String, HQECDistribution> getECDistributions() {
		return m_ECdistributions;
	}
	
	
	public HQProcess getAncestor (HQProcess p){
		if (!Objects.equals(p.getExtends(),null))
			return m_processes.get(p.getExtends());
		else
			return null;
	}
	
	public LinkedList<String> getAncestors (String s){
		LinkedList<String> l = new LinkedList<String>();
		HQProcess p = m_processes.get(s);
		while (!Objects.equals(p, null)){
			l.add(p.getId());
			p = getAncestor(p);
		}
		return l;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		HQProcess m1 = new HQProcess("m1");
		m1.addVariable(new HQVariable ("x", false));
		m1.addFormula(new HQFormula ("p1",new HQExpression("0.2") ));
		m1.addFormula(new HQFormula ("p2",new HQExpression("0.8") ));
		HQGuard g = new HQGuard();
		g.addPredicate(new HQPredicate(new HQExpression("x"), HQPredicate.RelationType.EQUALS,new HQExpression("false")));
//		HQCommand c1 = new HQCommand("a",g);

//		c1.addUpdate("x", "p1", "false");
//		c1.addUpdate("x", "p2", "true");
//		m1.addCommand(c1);
			
		HQProcess m2 = new HQProcess("m2");
		m2.addVariable(new HQVariable ("y", false));
		HQGuard g2 = new HQGuard();
		g2.addPredicate(new HQPredicate(new HQExpression("y"), HQPredicate.RelationType.EQUALS, new HQExpression("false")));
//		HQCommand c2 = new HQCommand("a",g2);

//		c2.addUpdate("y", "0.4", "false");
//		c2.addUpdate("y", "0.6", "true");
//		m2.addCommand(c2);

		HQModel m = new HQModel (ModelType.DTMC);
		m.addProcess(m1.getId(), m1);
		m.addProcess(m2.getId(), m2);
		
		HQRewardStructure rs = new HQRewardStructure("acount");
		rs.addReward(new HQReward(new HQAction("a","b"), new HQGuard(), new HQExpression("1"), "foo"));
		
		m.addRewardStructure(rs.getId(), rs);
		
		System.out.println(m.toString());
		
	}

}
