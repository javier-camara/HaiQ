package org.jcm.haiq.core;

public class HQFormula {

	private String m_id;
	private HQExpression m_expression;
	private boolean m_defined;
	private boolean m_constant;
	private boolean m_double; // is is a double? (used for constants)
	
	public HQFormula (String id, HQExpression expression){
		this.m_id = id;
		this.m_expression = expression;
		this.m_defined = true;
		this.m_constant = false;
	}
	
	public HQFormula (String id, HQExpression expression, boolean isConstant){
		this.m_id = id;
		this.m_expression = expression;
		this.m_defined = true;
		this.m_constant = isConstant;
	}

	public void setDouble(boolean d){
		this.m_double = d;
	}
	
	public boolean isDouble(){
		return m_double;
	}

	/**
	 * @return the m_constant
	 */
	public boolean isConstant() {
		return m_constant;
	}

	/**
	 * @param m_constant the m_constant to set
	 */
	public void setConstant(boolean m_constant) {
		this.m_constant = m_constant;
	}

	public HQFormula (String id, boolean isConstant){
		this.m_id = id;
		this.m_expression =new HQExpression("");
		this.m_defined = false;
		this.m_constant = isConstant;
	}
	
	public HQFormula (String id){
		this.m_id = id;
		this.m_expression = new HQExpression("");
		this.m_defined = false;
		this.m_constant = false;
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
	 * @return the expression
	 */
	public HQExpression getExpression() {
		return m_expression;
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpression(HQExpression expression) {
		this.m_expression = expression;
	}

	/**
	 * @return the defined
	 */
	public boolean isDefined() {
		return m_defined;
	}

	/**
	 * @param defined the defined to set
	 */
	public void setDefined(boolean defined) {
		this.m_defined = defined;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQFormula [m_id=" + m_id + ", m_expression=" + m_expression + ", m_defined=" + m_defined
				+ ", m_constant=" + m_constant + ", m_double=" + m_double + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(new HQFormula("a",new HQExpression("x+5")));
		System.out.println(new HQFormula("b"));		
	}

}
