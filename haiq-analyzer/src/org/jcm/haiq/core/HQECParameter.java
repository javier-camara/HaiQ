package org.jcm.haiq.core;

import java.util.Objects;

/**
 * @author jcamara
 *
 */
public class HQECParameter {
	public enum ECParameterType {INTEGER, DOUBLE};
	
	private String m_id;
	private ECParameterType m_type;
	private String m_min, m_max;
	
	
	class ECParameterRangeException extends Exception{
		public ECParameterRangeException (String msg){
			super(msg);
		}
	}
	
	public HQECParameter(String id, String type, String min, String max){
		this.m_id = id;
		if (Objects.equals("double", type)){
			this.m_type = ECParameterType.DOUBLE;
		} else if (Objects.equals("int", type)){
			this.m_type = ECParameterType.INTEGER;
		} else {
			System.out.println("Unknown EvoChecker parameter type.");
			System.exit(-1);
		}
		this.m_min = min;
		this.m_max = max;		
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
	public ECParameterType getType() {
		return m_type;
	}

	/**
	 * @param m_type the m_type to set
	 */
	public void setType(ECParameterType m_type) {
		this.m_type = m_type;
	}

	/**
	 * @return the m_min
	 */
	public String getMin() {
		return m_min;
	}

	/**
	 * @param m_min the m_min to set
	 */
	public void setMin(String m_min) {
		this.m_min = m_min;
	}

	/**
	 * @return the m_max
	 */
	public String getMax() {
		return m_max;
	}

	/**
	 * @param m_max the m_max to set
	 */
	public void setMax(String m_max) {
		this.m_max = m_max;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EC Parameter [m_id=" + m_id + ", m_type=" + m_type + ", m_min=" + m_min + ", m_max=" + m_max + "]";
	}
}
