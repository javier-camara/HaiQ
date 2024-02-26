package org.jcm.haiq.core;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class HQRelationCollection {
	private String m_id;
	private JSONObject m_jsonObject = null;

	public HQRelationCollection(String id, JSONObject obj){
		this.m_id = id;
		this.m_jsonObject=obj;
	}

	public String getId() {
		return m_id;
	}
	
	@Override
	public String toString() {
		return "HQRelationCollection [m_id=" + m_id + ", m_jsonObject=" + m_jsonObject + "]";
	}
	
	public JSONObject getValue(String id) {
		return ((JSONObject) m_jsonObject.get(id));
	}
	
}
