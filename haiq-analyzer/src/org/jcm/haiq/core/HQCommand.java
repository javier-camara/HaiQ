package org.jcm.haiq.core;

import java.util.LinkedList;

/**
 * @author jcamara
 *
 */
public class HQCommand {

	private HQAction m_action;
	private HQGuard m_guard;
	private LinkedList<HQUpdate> m_updates = new LinkedList<HQUpdate>();

	
	
	
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
	 * @return the m_updates
	 */
	public LinkedList<HQUpdate> getUpdates() {
		return m_updates;
	}

	public HQCommand(HQAction action, HQGuard guard){
		this.m_action = action;
		this.m_guard = guard;
	}
	
	public HQCommand(String action, HQGuard guard) {
		this.m_action = new HQAction(action, "");
		this.m_guard = guard;
	}
	
	public void addUpdate (HQUpdate u){
		this.m_updates.add(u);
	}
		
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Command [m_action=" + m_action + ", m_guard=" + m_guard + ", m_updates=" + m_updates + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HQGuard g = new HQGuard();
		g.addPredicate(new HQPredicate(new HQExpression("x"), HQPredicate.RelationType.EQUALS, new HQExpression("false")));
		HQCommand c = new HQCommand("a",g);
		HQUpdate u = new HQUpdate(new HQExpression("0.2"));
//		u.addVariableUpdate("x", "false");
		c.addUpdate(u);
		HQUpdate u2 = new HQUpdate(new HQExpression("0.8"));
//		u.addVariableUpdate("x", "true");
		c.addUpdate(u2);
		System.out.println(c);
	}

}
