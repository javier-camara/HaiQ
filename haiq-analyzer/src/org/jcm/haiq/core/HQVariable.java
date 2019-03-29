package org.jcm.haiq.core;

import java.util.Objects;

/**
 * @author jcamara
 *
 */
public class HQVariable {
	public enum VariableType {INTEGER, BOOLEAN};
	
	private String m_id;
	private VariableType m_type;
	private String m_min, m_max, m_init;
	
	
	class VariableRangeException extends Exception{
		public VariableRangeException (String msg){
			super(msg);
		}
	}
	
	public HQVariable(String id, String min, String max, String init){
		this.m_id = id;
		this.m_type = VariableType.INTEGER;
//		try{
//			if (min>max | init>max | init<min){
//				throw new VariableRangeException("Variable \'"+m_id+"\' initialization range incorrect. Init value should be within range.");
//			} else {
				this.m_min = min;
				this.m_max = max;
				this.m_init = init;
//			}
//		} catch (VariableRangeException e){
//			System.out.println(e.getMessage());
//		}
		
	}

	public HQVariable (String id, boolean init){
		this.m_id = id;
		this.m_type = VariableType.BOOLEAN;
		if (init) 
			this.m_init = "1";
		else
			this.m_init = "0";		
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
	public VariableType getType() {
		return m_type;
	}

	/**
	 * @param m_type the m_type to set
	 */
	public void setType(VariableType m_type) {
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

	/**
	 * @return the m_init
	 */
	public String getInit() {
		return m_init;
	}

	/**
	 * @param m_init the m_init to set
	 */
	public void setInit(String m_init) {
		this.m_init = m_init;
	}
	
	/**
	 * @return the m_init (for boolean var)
	 */
	public boolean getInitBoolean() {
		if (Objects.equals(m_init, "0"))
			return false;
		else return true;
	}

	/**
	 * @param m_init the m_init to set (for boolean var)
	 */
	public void setInitBoolean(boolean m_init) {
		if (m_init)
			this.m_init = "1";
		else this.m_init = "0";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Variable [m_id=" + m_id + ", m_type=" + m_type + ", m_min=" + m_min + ", m_max=" + m_max + ", m_init="
				+ m_init + "]";
	}
	
	public static void main(String[] args) {
		HQVariable bvar = new HQVariable ("boolvar", false);
		System.out.println(bvar.toString());
		HQVariable ivar = new HQVariable ("intvar", "10", "100", "3");
		System.out.println(ivar.toString());

	}

	

}
