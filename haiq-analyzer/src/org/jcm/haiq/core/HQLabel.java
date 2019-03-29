package org.jcm.haiq.core;


public class HQLabel {

	public enum QuantifierType { ALL, SOME, ONE, NONE }
	
	private QuantifierType m_quantifier;	
	private String m_id;
	private String m_type;
	private HQPredicate m_predicate;

	public HQLabel (String id, QuantifierType quantifier, String type, HQPredicate predicate){
		this.m_id = id;
		this.m_quantifier = quantifier;
		this.m_type = type;
		this.m_predicate = predicate;
	}
	
	
	
	
	/**
	 * @return the m_quantifier
	 */
	public QuantifierType getQuantifier() {
		return m_quantifier;
	}


	/**
	 * @param m_quantifier the m_quantifier to set
	 */
	public void setQuantifier(QuantifierType m_quantifier) {
		this.m_quantifier = m_quantifier;
	}


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
	 * @return the m_type
	 */
	public String getType() {
		return m_type;
	}




	/**
	 * @param m_type the m_type to set
	 */
	public void setType(String m_type) {
		this.m_type = m_type;
	}




	/**
	 * @return the m_predicate
	 */
	public HQPredicate getPredicate() {
		return m_predicate;
	}


	/**
	 * @param m_predicate the m_predicate to set
	 */
	public void setPredicate(HQPredicate m_predicate) {
		this.m_predicate = m_predicate;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQLabel [m_quantifier=" + m_quantifier + ", m_id=" + m_id + ", m_type=" + m_type + ", m_predicate="
				+ m_predicate + "]";
	}




	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
