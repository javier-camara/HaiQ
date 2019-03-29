package org.jcm.haiq.core;

/**
 * @author jcamara
 *
 */
public class HQReward {

	private HQAction m_action; // Empty String if state reward
	private HQGuard m_guard;
	private HQExpression m_expression;
	private String m_scope;
	
	
	
	public HQReward(HQAction action, HQGuard guard, HQExpression expression, String scope){
		this.m_action = action;
		this.m_guard = guard;
		this.m_expression = expression;
		this.m_scope = scope;
	}
	
	public HQReward(HQGuard guard, HQExpression expression){
		this.m_action = new HQAction("","");
		this.m_guard = guard;
		this.m_expression = expression;
	}
	
	/**
	 * @return the m_action
	 */
	public HQAction getAction() {
		return m_action;
	}




	/**
	 * @param m_action the m_action to set
	 */
	public void setAction(HQAction m_action) {
		this.m_action = m_action;
	}




	/**
	 * @return the m_guard
	 */
	public HQGuard getGuard() {
		return m_guard;
	}




	/**
	 * @param m_guard the m_guard to set
	 */
	public void setGuard(HQGuard m_guard) {
		this.m_guard = m_guard;
	}




	/**
	 * @return the m_expression
	 */
	public HQExpression getExpression() {
		return m_expression;
	}




	/**
	 * @param m_expression the m_expression to set
	 */
	public void setExpression(HQExpression m_expression) {
		this.m_expression = m_expression;
	}

	/**
	 * @return the m_scope
	 */
	public String getScope() {
		return m_scope;
	}

	/**
	 * @param m_scope the m_scope to set
	 */
	public void setScope(String m_scope) {
		this.m_scope = m_scope;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQReward [m_action=" + m_action + ", m_guard=" + m_guard + ", m_expression=" + m_expression
				+ ", m_scope=" + m_scope + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(new HQReward(new HQAction("a",""), new HQGuard(), new HQExpression("1"), "foo").toString());
		
	}

}
