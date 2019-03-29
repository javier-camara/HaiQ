package org.jcm.haiq.core;

import java.util.HashMap;

/**
 * @author jcamara
 *
 */
public class HQPredicate {
	public enum RelationType { UNARY, LEQ, GEQ, LESS_THAN, GREATER_THAN, EQUALS, NOT_EQUALS }
	
	private static final HashMap<RelationType, String> m_relation_tokens;
	static{
		m_relation_tokens = new HashMap<RelationType, String>();
		m_relation_tokens.put(RelationType.LEQ, "<=");
		m_relation_tokens.put(RelationType.GEQ, ">=");
		m_relation_tokens.put(RelationType.LESS_THAN, "<");
		m_relation_tokens.put(RelationType.GREATER_THAN, ">");
		m_relation_tokens.put(RelationType.EQUALS, "=");
		m_relation_tokens.put(RelationType.NOT_EQUALS, "!=");
		m_relation_tokens.put(RelationType.UNARY, ""); // * Check
	}
	
	private RelationType m_relation;
	private HQExpression m_left;
	private HQExpression m_right;
	
	public HQPredicate (HQExpression left, RelationType relation, HQExpression right){
		this.m_left = left;
		this.m_right = right;
		this.m_relation = relation;	
	}
	
	
	/**
	 * @return the original literal
	 */
	public String getLiteral() {
		return m_left.getLiteral()+ m_relation_tokens.get(m_relation) +m_right.getLiteral();
	}
	
	/**
	 * @return the m_relation
	 */
	public RelationType getRelation() {
		return m_relation;
	}
	/**
	 * @param m_relation the m_relation to set
	 */
	public void setRelation(RelationType m_relation) {
		this.m_relation = m_relation;
	}
	/**
	 * @return the m_left
	 */
	public HQExpression getLeft() {
		return m_left;
	}
	/**
	 * @param m_left the m_left to set
	 */
	public void setLeft(HQExpression m_left) {
		this.m_left = m_left;
	}
	/**
	 * @return the m_right
	 */
	public HQExpression getRight() {
		return m_right;
	}
	/**
	 * @param m_right the m_right to set
	 */
	public void setRight(HQExpression m_right) {
		this.m_right = m_right;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQPredicate [m_relation=" + m_relation + ", m_left=" + m_left + ", m_right=" + m_right + "]";
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HQPredicate p = new HQPredicate(new HQExpression("s"), RelationType.EQUALS,new HQExpression("10"));
		System.out.println(p);
	}

	
	
}
