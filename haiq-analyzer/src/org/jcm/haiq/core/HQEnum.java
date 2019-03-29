package org.jcm.haiq.core;

import java.util.LinkedList;

public class HQEnum {

	String m_id;
	LinkedList<String> m_items = new LinkedList<String>();
	
	
	public HQEnum (String id){
		m_id = id;
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
	 * @return the m_items
	 */
	public LinkedList<String> getItems() {
		return m_items;
	}


	/**
	 * @param m_items the m_items to set
	 */
	public void setItems(LinkedList<String> m_items) {
		this.m_items = m_items;
	}
	
	public void addItem(String item){
		m_items.add(item);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQEnum [m_id=" + m_id + ", m_items=" + m_items + "]";
	}
	
	
}
