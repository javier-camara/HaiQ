package org.jcm.haiq.core;

import java.util.LinkedList;

/**
 * @author jcamara
 *
 */
public class HQECDistribution {
	
	private String m_id;
	private LinkedList<String> m_min = new LinkedList<String>();
	private LinkedList<String> m_max = new LinkedList<String>();
	
		
	public HQECDistribution(String id){
		this.m_id = id;
	}

	/** 
	 * @return number of elements in the distribution
	 */
	public int size(){
		return m_min.size();
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
	 * @return the m_min
	 */
	public String getMin(int index) {
		return m_min.get(index);
	}

	/**
	 * @param m_min the m_min to add
	 */
	public void addMin(String m_min) {
		this.m_min.add(m_min);
	}

	/**
	 * @return the m_max
	 */
	public String getMax(int index) {
		return m_max.get(index);
	}

	/**
	 * @param m_max the m_max to add
	 */
	public void addMax(String m_max) {
		this.m_max.add(m_max);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EC Distribution [m_id=" + m_id + ", m_mins=" + m_min.toString() + ", m_max=" + m_max.toString() + "]";
	}
}
