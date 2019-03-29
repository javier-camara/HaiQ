package org.jcm.haiq.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class HQProcess {

	private String m_id;
	private boolean m_abstract;
	private String m_extends;
	private HashMap<String, HQVariable> m_vars = new HashMap<String, HQVariable>();
	private HashMap<String, HQFormula> m_formulas = new HashMap<String, HQFormula>();
	private LinkedList<HQCommand> m_commands = new LinkedList<HQCommand>();
	private HashMap<String, HQEnum> m_enums = new HashMap<String, HQEnum>();
	// EvoChecker Extension
	private HashMap<String, HQECParameter> m_ECparams = new HashMap<String, HQECParameter>();

	/**
	 * @return the m_id
	 */
	public String getId() {
		return m_id;
	}

	/**
	 * @param m_id the m_id to set
	 */
	public void setId(String m_id) {
		this.m_id = m_id;
	}
	
	
	/**
	 * @return the m_abstract
	 */
	public boolean isAbstract() {
		return m_abstract;
	}

	/**
	 * @param m_abstract the m_abstract to set
	 */
	public void setAbstract(boolean m_abstract) {
		this.m_abstract = m_abstract;
	}
	
	
	/**
	 * @return the m_extends
	 */
	public String getExtends() {
		return m_extends;
	}

	/**
	 * @param m_extends the m_extends to set
	 */
	public void setExtends(String m_extends) {
		this.m_extends = m_extends;
	}

	/**
	 * @return the m_vars
	 */
	public HashMap<String, HQVariable> getVars() {
		return m_vars;
	}

	/**
	 * @return the m_formulas
	 */
	public HashMap<String, HQFormula> getFormulas() {
		return m_formulas;
	}
	
	/**
	 * @return the m_commands
	 */
	public LinkedList<HQCommand> getCommands() {
		return m_commands;
	}
	
	/**
	 * @return the m_ECparams
	 */
	public HashMap<String, HQECParameter> getECParameters() {
		return m_ECparams;
	}

	public HQProcess(String id){
		this.m_id = id;
		this.m_abstract = false;
		this.m_extends = "";
	}

	public HQProcess(String id, String doesExtend){
		this.m_id = id;
		this.m_abstract = false;
		this.m_extends = doesExtend;
	}

	
	public HQProcess(String id, boolean is_abstract){
		this.m_id = id;
		this.m_abstract = is_abstract;
	}

	
	
	public void addVariable(HQVariable v){
		this.m_vars.put(v.getId(), v);
	}
	
	public void addFormula(HQFormula f){
		this.m_formulas.put(f.getId(), f);
	}
	
	public void addEnum (HQEnum e){
		this.m_enums.put(e.getId(), e);
	}
	
	public void addECParameter (HQECParameter e){
		this.m_ECparams.put(e.getId(), e);
	}
	
	public void addCommand (HQCommand c){
		this.m_commands.add(c);
	}
	
	public boolean isDefinedElement(String s){
		return m_vars.containsKey(s) || m_formulas.containsKey(s);
	}
	
	/**
	 * Expands a process with the formula, variable, and command declarations of the process 
	 * @param p
	 */
	public void expand (HQProcess p){
		
		for (Map.Entry<String, HQFormula> e: p.getFormulas().entrySet()){
			if (!m_formulas.containsKey(e.getKey())){
				m_formulas.put(e.getKey(), e.getValue());
			}
		}
		
		for (Map.Entry<String, HQECParameter> e1: p.getECParameters().entrySet()){
			if (!m_ECparams.containsKey(e1.getKey())){
				m_ECparams.put(e1.getKey(), e1.getValue());
			}
		}
		
		for (Map.Entry<String, HQVariable> e2: p.getVars().entrySet()){
			if (!m_vars.containsKey(e2.getKey())){
				m_vars.put(e2.getKey(), e2.getValue());
			}
		}
		
		for (int i=0; i<p.getCommands().size();i++){
			m_commands.add(p.getCommands().get(i));
		}
		
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQProcess [m_id=" + m_id + ", m_abstract=" + m_abstract + ", m_extends=" + m_extends + ", m_vars="
				+ m_vars + ", m_formulas=" + m_formulas + ", m_commands=" + m_commands + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HQProcess m1 = new HQProcess("m1");
		m1.addVariable(new HQVariable ("x", false));
		m1.addFormula(new HQFormula ("p",new HQExpression("0.5") ));
		HQGuard g = new HQGuard();
		g.addPredicate(new HQPredicate(new HQExpression("x"), HQPredicate.RelationType.EQUALS, new HQExpression("false")));
		HQCommand c1 = new HQCommand("a",g);
//		c1.addUpdate("x", "0.2", "false");
//		c1.addUpdate("x", "0.8", "true");
		m1.addCommand(c1);
		
		System.out.println(m1.toString());

		
	}

}
