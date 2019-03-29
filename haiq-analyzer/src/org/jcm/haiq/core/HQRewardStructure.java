package org.jcm.haiq.core;

import java.util.LinkedList;

public class HQRewardStructure {

	private String m_id;
	private LinkedList<HQReward> m_rewards = new LinkedList<HQReward>();
	
	public HQRewardStructure(String id){
		this.m_id = id;
	}
	
	public void addReward (HQReward r){
		this.m_rewards.add(r);
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
	 * @return the m_rewards
	 */
	public LinkedList<HQReward> getRewards() {
		return m_rewards;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQRewardStructure [m_id=" + m_id + ", m_rewards=" + m_rewards + "]";
	}

}
