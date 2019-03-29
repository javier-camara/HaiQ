package org.jcm.haiq.core;

import java.util.HashMap;

public class HQUpdate {
	private HQExpression m_probability;
	private HashMap<String, HQExpression> m_var_updates = new HashMap<String, HQExpression>();
		
	public HQUpdate (HQExpression probability){
		m_probability = probability;
	}
	
	public HashMap<String, HQExpression> getVariableUpdates(){
		return m_var_updates;
	}
	
	public void addVariableUpdate (String variable, HQExpression expression){
		m_var_updates.put(variable, expression);
	}
	
	public HQExpression getVariableUpdate(String variable){
		return m_var_updates.get(variable);
	}


	/**
	 * @return the m_probability
	 */
	public HQExpression getProbability() {
		return m_probability;
	}

	/**
	 * @param m_probability the m_probability to set
	 */
	public void setProbability(HQExpression m_probability) {
		this.m_probability = m_probability;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQUpdate [m_probability=" + m_probability + ", m_var_updates=" + m_var_updates + "]";
	}

	
	
}
