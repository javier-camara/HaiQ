package org.jcm.haiq.core;

public class HQAction {
	
	private String m_id;
	private String m_relation;
	
	public HQAction (String id, String relation){
		this.m_id = id;
		this.m_relation = relation;
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
	 * @return the m_relation
	 */
	public String getRelation() {
		return m_relation;
	}

	/**
	 * @param m_relation the m_relation to set
	 */
	public void setRelation(String m_relation) {
		this.m_relation = m_relation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQAction [m_id=" + m_id + ", m_relation=" + m_relation + "]";
	}
	
	
	

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
