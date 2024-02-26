package org.jcm.haiq.core;

import java.util.LinkedList;
import java.util.Objects;
import org.json.simple.JSONObject;

public class HQExpression {

	private LinkedList<String> m_tokens = new LinkedList<String>();
	protected String m_literal;
	
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
		if ((s.split("\\.").length>1) && !isRelationCollectionReference(s)) {
			try{
				Double.parseDouble(s);
			} catch (Exception e) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isRelationCollectionReference(String s) {
		return s.startsWith("#");
	}
	
	public boolean isSelfAttributeToken(String s){
		return Objects.equals(s.split("\\.")[0],"this") && !isRelationCollectionReference(s);
	}
	
	public String getOrderFromPrefix(String s){
		return s.split("_")[1];
	}
	
	// This is used for the PrismTranslateMDP scheme...
	public String getPrefixedLiteral(HQProcess p, String prefix, String relation_instance_prefix){
		return getPrefixedLiteral(p, prefix, relation_instance_prefix, "", null);
	}
		
	
	public String getLiteralRelationCollection(String token_literal, String prefix, String relation_instance_prefix, HQModel m) {
		String relationName = token_literal.split("\\.")[0].replace("#", "");
		String label = token_literal.split("\\.")[1];
		String sthis = prefix.split("_")[0];
		String sthat = relation_instance_prefix.split("_")[0];
		System.out.println("Relation Name: "+ relationName + " -- Label: " + label + " -- This: " + sthis + " -- That: " + sthat);
		JSONObject src = m.getRelationCollection(relationName).getValue(label);
		JSONObject src2 = (JSONObject) src.get(sthis);
		String res = src2.get(sthat).toString();
		System.out.println(res);
		return res;
	}
	
	
	public String getPrefixedLiteral(HQProcess p, String prefix, String relation_instance_prefix, String solId, HQModel m){
		
		
		String sep_char="_";
		if (Objects.equals(solId,"")) {
			sep_char="";
		}
		if (Objects.equals(prefix, ""))
			return m_literal;
		String pl = "";
//		System.out.println(m_tokens.toString());
		for (int i=0; i<m_tokens.size();i++){
			if (p.isDefinedElement(m_tokens.get(i))){ // Token refers to a formula or variable defined by this process or ancestor
				String solIdString = Objects.equals(solId, "")? "" : solId+"_"; 
			    pl +=  solIdString+prefix+"_"+m_tokens.get(i);
			} else {
				if (isRelationReferenceToken(m_tokens.get(i)) && !isSelfAttributeToken(m_tokens.get(i))){ // Token refers to a variable from a related process
					pl+= solId+sep_char+relation_instance_prefix+"_"+m_tokens.get(i).split("\\.")[1].trim();
				} else if (Objects.equals(m_tokens.get(i),"this.º")){ // Deprecated?
					pl+=getOrderFromPrefix(prefix);
				} else if (isRelationCollectionReference(m_tokens.get(i))) { // Token refers to a relation collection attribute
					pl += getLiteralRelationCollection(m_tokens.get(i), prefix, relation_instance_prefix, m);
					System.out.println("Token: "+ m_tokens.get(i) + " Relation instance prefix: "+relation_instance_prefix + "-- Prefix: " + prefix + "-- solId: " + solId);
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
