package org.jcm.haiq.core;

import java.util.LinkedList;
import java.util.Objects;


public class HQExpression {

	private LinkedList<String> m_tokens = new LinkedList<String>();
	private String m_literal;
	
	public HQExpression (String s){
		m_literal = s;
		extractTokens(m_literal);
	}
	
	private void extractTokens(String input) {
	    input = input.replaceAll("\\(", " ( ");
	    input = input.replaceAll("\\)", " ) ");
	    input = input.replaceAll("\\+", " + ");
	    input = input.replaceAll("-", " - ");
	    input = input.replaceAll("\\*", " * ");
	    input = input.replaceAll("/", " / ");
	    input = input.replaceAll("!", " ! ");
	    input = input.replaceAll("\\?", " ? ");
	    input = input.replaceAll(":", " : ");
	    input = input.replaceAll(">", " > ");
	    input = input.replaceAll("<", " < ");


	    String[] tokens = input.trim().split("\\s+");
	    for (int i=0; i<tokens.length; i++){
	    	m_tokens.add(tokens[i]);
	    }
	}
	
	public String getLiteral(){
		return m_literal;
	}
	
	public boolean isRelationReferenceToken(String s){	
		if (s.split("\\.").length>1) {
			try{
				Double dn = Double.parseDouble(s);
			} catch (Exception e) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isSelfAttributeToken(String s){
		return Objects.equals(s.split("\\.")[0],"this");
	}
	
	public String getOrderFromPrefix(String s){
		return s.split("_")[1];
	}
	
	public String getPrefixedLiteral(HQProcess p, String prefix, String relation_instance_prefix){
		if (Objects.equals(prefix, ""))
			return m_literal;
		String pl = "";
		for (int i=0; i<m_tokens.size();i++){
			if (p.isDefinedElement(m_tokens.get(i))){ // Token refers to a formula or variable defined by this process or ancestor
				pl += prefix+"_"+m_tokens.get(i);
			} else {
				if (isRelationReferenceToken(m_tokens.get(i)) && !isSelfAttributeToken(m_tokens.get(i))){ // Token refers to a variable from a related process
					pl+= relation_instance_prefix+"_"+m_tokens.get(i).split("\\.")[1].trim();
				} else if (Objects.equals(m_tokens.get(i),"this.º")){
					pl+=getOrderFromPrefix(prefix);
				} else // Token refers to something else
					pl += m_tokens.get(i);
			}
		}
		return pl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQExpression [m_literal=" + m_literal + "]";
	}
	
	
}
