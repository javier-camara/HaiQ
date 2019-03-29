package org.jcm.haiq.core;

import java.util.LinkedList;

public class HQGuard {

	private LinkedList<HQPredicate> m_predicates = new LinkedList<HQPredicate>();

	public void addPredicate(HQPredicate p){
		this.m_predicates.add(p);
	}
	
	public LinkedList<HQPredicate> getPredicates(){
		return m_predicates;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQGuard [m_predicates=" + m_predicates + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
