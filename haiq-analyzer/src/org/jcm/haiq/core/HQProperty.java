package org.jcm.haiq.core;

//maxP | minP | maxR | minR  -> Return the max/min probability/reward for the entire S-Set
//rangeP | rangeR -> Return the couple (minP, maxP)/(minR, maxR) for the entire S-Set -- these can be easily derived from ops above

//allP~x | someP~x | allR~x | someR~x -> Returns a boolean stating whether Operator~x, where ~ \in {>=, <=, >, <}
//noP~x | noR~x -> These are easily derived ( e.g., noP~x == !someP~x)

//SmaxP | SminP | SmaxR | SminR -> Return the structures associated with the max/min Reward or Probability
//SallP~x | SallR~x -> Returns all the structures that satisfy Operator~x
//SsomeP~x | SsomeR~x -> Returns some structure that satisfies Operator~x 

//e.g., 
//maxP [ F allsnode ] // Returns the maximum probability that eventually all node.s become true (across all possible structures) 
//someR{t}>=10 [ F allsnode ] // Returns a boolean, states if there exists a structure in which elapsed time is at least 10 seconds before all s.node become true
//SallP>=1 [ G allsnode ] // Returns all structures in which always al s.node is true with probability one


import java.util.HashMap;

public class HQProperty {

	public enum PropertyType { PROPERTY_BOOLEAN, PROPERTY_QUANTITATIVE, PROPERTY_QUANTITATIVE_RANGE, PROPERTY_STRUCTSET }
		
	private PropertyType m_type;
	private String m_literal;
	private String m_pathFormula;
	
	public enum QuantifierType { MAX_P, MIN_P, MAX_R, MIN_R, RANGE_P, RANGE_R, ALL_P, SOME_P, NO_P, ALL_R, SOME_R, NO_R, 
								S_ALL_P, S_SOME_P, S_MAX_P, S_MIN_P, S_ALL_R, S_SOME_R, S_MAX_R, S_MIN_R,
								 MAX_P_MIN, MAX_P_MAX, MIN_P_MAX, MIN_P_MIN, MAX_R_MIN, MAX_R_MAX, MIN_R_MIN, MIN_R_MAX, 
								 RANGE_P_MAX, RANGE_P_MIN, RANGE_R_MAX, RANGE_R_MIN, ALL_P_MAX, ALL_P_MIN, SOME_P_MAX, SOME_P_MIN, 
								 NO_P_MAX, NO_P_MIN, ALL_R_MAX, ALL_R_MIN, SOME_R_MAX, SOME_R_MIN, NO_R_MAX, NO_R_MIN,
								 S_MAX_P_MIN, S_MAX_P_MAX, S_MIN_P_MAX, S_MIN_P_MIN, S_MAX_R_MIN, S_MAX_R_MAX, S_MIN_R_MIN, S_MIN_R_MAX, 
								 S_ALL_P_MAX, S_ALL_P_MIN, S_SOME_P_MAX, S_SOME_P_MIN, S_ALL_R_MAX, S_ALL_R_MIN, S_SOME_R_MAX, S_SOME_R_MIN 
								}
	private QuantifierType m_qtype;
	public HashMap<String, QuantifierType> m_quantifiers = new HashMap<String, QuantifierType>();
	
	public enum QuantifierRelationType { LESS, GEQ, GREATER, LEQ }
	public QuantifierRelationType m_relType;
	public HashMap<QuantifierRelationType, String> m_relTypes = new HashMap<QuantifierRelationType, String>();
	
	public String m_bound ="";	
	private String m_reward;
	
	
	public HQProperty(String propertyString){
		// For DTMC / avg case
		m_quantifiers.put("maxP", QuantifierType.MAX_P);
		m_quantifiers.put("minP", QuantifierType.MIN_P);
		m_quantifiers.put("maxR", QuantifierType.MAX_R);
		m_quantifiers.put("minR", QuantifierType.MIN_R);	
		
		m_quantifiers.put("rangeP", QuantifierType.RANGE_P);	
		m_quantifiers.put("rangeR", QuantifierType.RANGE_R);	
		
		m_quantifiers.put("allP", QuantifierType.ALL_P);	
		m_quantifiers.put("someP", QuantifierType.SOME_P);	
		m_quantifiers.put("noP", QuantifierType.NO_P);		
		
		m_quantifiers.put("allR", QuantifierType.ALL_R);	
		m_quantifiers.put("someR", QuantifierType.SOME_R);	
		m_quantifiers.put("noR", QuantifierType.NO_R);		
		
		m_quantifiers.put("SallP", QuantifierType.S_ALL_P);					
		m_quantifiers.put("SsomeP", QuantifierType.S_SOME_P);					
		m_quantifiers.put("SmaxP", QuantifierType.S_MAX_P);					
		m_quantifiers.put("SminP", QuantifierType.S_MIN_P);					
		
		m_quantifiers.put("SallR", QuantifierType.S_ALL_R);					
		m_quantifiers.put("SsomeR", QuantifierType.S_SOME_R);					
		m_quantifiers.put("SmaxR", QuantifierType.S_MAX_R);					
		m_quantifiers.put("SminR", QuantifierType.S_MIN_R);					

		m_quantifiers.put("allP", QuantifierType.ALL_P);	
		m_quantifiers.put("someP", QuantifierType.SOME_P);	
		m_quantifiers.put("noP", QuantifierType.NO_P);		
		
		m_quantifiers.put("allR", QuantifierType.ALL_R);	
		m_quantifiers.put("someR", QuantifierType.SOME_R);	
		m_quantifiers.put("noR", QuantifierType.NO_R);		
		
		m_quantifiers.put("SallP", QuantifierType.S_ALL_P);					
		m_quantifiers.put("SsomeP", QuantifierType.S_SOME_P);					
		m_quantifiers.put("SmaxP", QuantifierType.S_MAX_P);					
		m_quantifiers.put("SminP", QuantifierType.S_MIN_P);					
		
		m_quantifiers.put("SallR", QuantifierType.S_ALL_R);					
		m_quantifiers.put("SsomeR", QuantifierType.S_SOME_R);					
		m_quantifiers.put("SmaxR", QuantifierType.S_MAX_R);					
		m_quantifiers.put("SminR", QuantifierType.S_MIN_R);
		
		
		// For MDP, PTA, etc. max/min case
		m_quantifiers.put("maxPmin", QuantifierType.MAX_P_MIN);
		m_quantifiers.put("maxPmax", QuantifierType.MAX_P_MAX);
		m_quantifiers.put("minPmin", QuantifierType.MIN_P_MIN);
		m_quantifiers.put("minPmax", QuantifierType.MIN_P_MAX);
		m_quantifiers.put("maxRmin", QuantifierType.MAX_R_MIN);
		m_quantifiers.put("maxRmax", QuantifierType.MAX_R_MAX);
		m_quantifiers.put("minRmin", QuantifierType.MIN_R_MIN);	
		m_quantifiers.put("minRmax", QuantifierType.MIN_R_MAX);	
		
		m_quantifiers.put("rangePmin", QuantifierType.RANGE_P_MIN);	
		m_quantifiers.put("rangeRmin", QuantifierType.RANGE_R_MIN);	
		m_quantifiers.put("rangePmax", QuantifierType.RANGE_P_MAX);	
		m_quantifiers.put("rangeRmax", QuantifierType.RANGE_R_MAX);	
		
		m_quantifiers.put("allPmin", QuantifierType.ALL_P_MIN);	
		m_quantifiers.put("somePmin", QuantifierType.SOME_P_MIN);	
		m_quantifiers.put("noPmin", QuantifierType.NO_P_MIN);		
		m_quantifiers.put("allPmax", QuantifierType.ALL_P_MAX);	
		m_quantifiers.put("somePmax", QuantifierType.SOME_P_MAX);	
		m_quantifiers.put("noPmax", QuantifierType.NO_P_MAX);		
		
		m_quantifiers.put("allRmin", QuantifierType.ALL_R_MIN);	
		m_quantifiers.put("someRmin", QuantifierType.SOME_R_MIN);	
		m_quantifiers.put("noRmin", QuantifierType.NO_R_MIN);		
		m_quantifiers.put("allRmax", QuantifierType.ALL_R_MAX);	
		m_quantifiers.put("someRmax", QuantifierType.SOME_R_MAX);	
		m_quantifiers.put("noRmax", QuantifierType.NO_R_MAX);		
		
		m_quantifiers.put("SallPmin", QuantifierType.S_ALL_P_MIN);					
		m_quantifiers.put("SsomePmin", QuantifierType.S_SOME_P_MIN);					
		m_quantifiers.put("SmaxPmin", QuantifierType.S_MAX_P_MIN);					
		m_quantifiers.put("SminPmin", QuantifierType.S_MIN_P_MIN);					
		m_quantifiers.put("SallPmax", QuantifierType.S_ALL_P_MAX);					
		m_quantifiers.put("SsomePmax", QuantifierType.S_SOME_P_MAX);					
		m_quantifiers.put("SmaxPmax", QuantifierType.S_MAX_P_MAX);					
		m_quantifiers.put("SminPmax", QuantifierType.S_MIN_P_MAX);					
		
		m_quantifiers.put("SallRmin", QuantifierType.S_ALL_R_MIN);					
		m_quantifiers.put("SsomeRmin", QuantifierType.S_SOME_R_MIN);					
		m_quantifiers.put("SmaxRmin", QuantifierType.S_MAX_R_MIN);					
		m_quantifiers.put("SminRmin", QuantifierType.S_MIN_R_MIN);		
		m_quantifiers.put("SallRmax", QuantifierType.S_ALL_R_MAX);					
		m_quantifiers.put("SsomeRmax", QuantifierType.S_SOME_R_MAX);					
		m_quantifiers.put("SmaxRmax", QuantifierType.S_MAX_R_MAX);					
		m_quantifiers.put("SminRmax", QuantifierType.S_MIN_R_MAX);		

		
		m_relTypes.put(QuantifierRelationType.GREATER,">");
		m_relTypes.put(QuantifierRelationType.LESS, "<");
		m_relTypes.put(QuantifierRelationType.LEQ, "<=");
		m_relTypes.put(QuantifierRelationType.GEQ, ">=");
		
		m_literal = propertyString;
		
		if (isRewardQuantifierString(m_literal))
			m_reward = getQuantifierRewardString (m_literal);
		
		String quantifierFullStr = m_literal.trim().split("\\[")[0].trim();
		
		m_relType = getRelationQuantifierType(quantifierFullStr);
		
		String quantifierStr = getQuantifierString(quantifierFullStr);
		m_bound = getQuantifierBoundString(quantifierFullStr);
		m_pathFormula = "["+m_literal.trim().split("\\[")[1].replaceAll("\\]", "")+"]";
		
		if (!m_quantifiers.containsKey(quantifierStr)){
			System.out.println("M-PCTL: Quantifier type not supported.");
			System.exit(-1);
		}
		
		m_qtype = m_quantifiers.get(quantifierStr);
		
		switch(m_qtype){
		case MAX_P:	case MIN_P:	case MAX_R:	case MIN_R:
		case MAX_P_MAX:	case MIN_P_MAX:	case MAX_R_MAX:	case MIN_R_MAX:			
		case MAX_P_MIN:	case MIN_P_MIN:	case MAX_R_MIN:	case MIN_R_MIN:			
			m_type = PropertyType.PROPERTY_QUANTITATIVE;
			break;
		case RANGE_P: case RANGE_R:
		case RANGE_P_MAX: case RANGE_R_MAX:
		case RANGE_P_MIN: case RANGE_R_MIN:
			m_type = PropertyType.PROPERTY_QUANTITATIVE_RANGE;
			break;
		case ALL_P: case SOME_P: case NO_P: case ALL_R: case SOME_R: case NO_R:
		case ALL_P_MAX: case SOME_P_MAX: case NO_P_MAX: case ALL_R_MAX: case SOME_R_MAX: case NO_R_MAX:
		case ALL_P_MIN: case SOME_P_MIN: case NO_P_MIN: case ALL_R_MIN: case SOME_R_MIN: case NO_R_MIN:
			m_type = PropertyType.PROPERTY_BOOLEAN;
			break;		
		case S_ALL_P: case S_SOME_P: case S_MAX_P: case S_MIN_P: case S_ALL_R: case S_SOME_R: case S_MAX_R: case S_MIN_R:
		case S_ALL_P_MAX: case S_SOME_P_MAX: case S_MAX_P_MAX: case S_MIN_P_MAX: case S_ALL_R_MAX: case S_SOME_R_MAX: case S_MAX_R_MAX: case S_MIN_R_MAX:
		case S_ALL_P_MIN: case S_SOME_P_MIN: case S_MAX_P_MIN: case S_MIN_P_MIN: case S_ALL_R_MIN: case S_SOME_R_MIN: case S_MAX_R_MIN: case S_MIN_R_MIN:
			m_type = PropertyType.PROPERTY_STRUCTSET;
			break;
		}
		
	}
	
	public boolean isMaximizationProperty(QuantifierType q){
		switch (q){
			case MAX_P: case MAX_R: 
			case MAX_P_MAX: case MAX_R_MAX:
			case MAX_P_MIN: case MAX_R_MIN:
			case S_MAX_P_MAX: case S_MAX_R_MAX:
			case S_MAX_P_MIN: case S_MAX_R_MIN:
				return true;				
			default:
				return false;
		}
	}
	
	public boolean isMinimizationProperty(QuantifierType q){
		switch (q){
			case MIN_P: case MIN_R:	
			case MIN_P_MIN:	case MIN_R_MIN:
			case MIN_P_MAX: case MIN_R_MAX:
			case S_MIN_P_MIN: case S_MIN_R_MIN:
			case S_MIN_P_MAX: case S_MIN_R_MAX:
				return true;	
			default:
				return false;
		}
	}
	
	
	public boolean isMaximizationQuantifier(QuantifierType q){
		switch (q){
			case MAX_P_MAX:	case MIN_P_MAX:	case MAX_R_MAX:	case MIN_R_MAX:
			case RANGE_P_MAX: case RANGE_R_MAX:
			case ALL_P_MAX: case SOME_P_MAX: case NO_P_MAX: case ALL_R_MAX: case SOME_R_MAX: case NO_R_MAX:
			case S_ALL_P_MAX: case S_SOME_P_MAX: case S_MAX_P_MAX: case S_MIN_P_MAX: case S_ALL_R_MAX: case S_SOME_R_MAX: case S_MAX_R_MAX: case S_MIN_R_MAX:
				return true;				
			default:
				return false;
		}
	}
	
	public boolean isMinimizationQuantifier(QuantifierType q){
		switch (q){
			case MAX_P_MIN:	case MIN_P_MIN:	case MAX_R_MIN:	case MIN_R_MIN:
			case RANGE_P_MIN: case RANGE_R_MIN:
			case ALL_P_MIN: case SOME_P_MIN: case NO_P_MIN: case ALL_R_MIN: case SOME_R_MIN: case NO_R_MIN:
			case S_ALL_P_MIN: case S_SOME_P_MIN: case S_MAX_P_MIN: case S_MIN_P_MIN: case S_ALL_R_MIN: case S_SOME_R_MIN: case S_MAX_R_MIN: case S_MIN_R_MIN:
				return true;	
			default:
				return false;
		}
	}
	
	public boolean isProbabilisticQuantifier(QuantifierType q){
		switch(q){
		case MAX_P: case MIN_P: case RANGE_P: case ALL_P: case SOME_P: case NO_P: 
		case S_ALL_P: case S_SOME_P: case S_MAX_P: case S_MIN_P:
		case MAX_P_MIN:	case MIN_P_MIN: case MAX_P_MAX:	case MIN_P_MAX:
		case ALL_P_MIN: case SOME_P_MIN: case NO_P_MIN: case ALL_P_MAX: case SOME_P_MAX: case NO_P_MAX:
		case RANGE_P_MAX: case RANGE_P_MIN:
		case S_ALL_P_MIN: case S_SOME_P_MIN: case S_MAX_P_MIN: case S_MIN_P_MIN:
		case S_ALL_P_MAX: case S_SOME_P_MAX: case S_MAX_P_MAX: case S_MIN_P_MAX:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isRewardQuantifier(QuantifierType q){
		switch(q){
		case MAX_R: case MIN_R: case RANGE_R: case ALL_R: case SOME_R: case NO_R: 
		case S_ALL_R: case S_SOME_R: case S_MAX_R: case S_MIN_R:
		case MAX_R_MIN:	case MIN_R_MIN: case MAX_R_MAX:	case MIN_R_MAX:
		case ALL_R_MIN: case SOME_R_MIN: case NO_R_MIN: case ALL_R_MAX: case SOME_R_MAX: case NO_R_MAX:
		case RANGE_R_MAX: case RANGE_R_MIN:
		case S_ALL_R_MIN: case S_SOME_R_MIN: case S_MAX_R_MIN: case S_MIN_R_MIN:
		case S_ALL_R_MAX: case S_SOME_R_MAX: case S_MAX_R_MAX: case S_MIN_R_MAX:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isRewardQuantifierString(String s){
		return s.contains("{");			
	}
	
	public String getQuantifierRewardString(String s){
		return s.substring(1+s.indexOf("{"), s.indexOf("}")).trim();
	}
	
	public String getQuantifierString(String s){
		String qstr =  s.trim().split(m_relTypes.get(getRelationQuantifierType(s)))[0];
		if (isRewardQuantifierString(s)){
			qstr = s.split("\\{")[0];
		}
		return qstr;
	}
	
	public String getQuantifierBoundString(String s){
		String[] chunks = s.trim().split(m_relTypes.get(getRelationQuantifierType(s)));
		if (chunks.length>1)
			return chunks[1];
		return "";
	}

	public QuantifierRelationType getRelationQuantifierType(String s){
		QuantifierRelationType res = QuantifierRelationType.GEQ;
		if (s.contains(">="))
			return QuantifierRelationType.GEQ;
		if (s.contains("<="))
			return QuantifierRelationType.LEQ;
		if (s.contains("<"))
			return QuantifierRelationType.LESS;		
		if (s.contains(">"))
			return QuantifierRelationType.GREATER;				
		return res;
	}
	
	public boolean evalAgainstBound(String n){
		Double dn = Double.parseDouble(n);
		Double db = Double.parseDouble(m_bound);
		switch (m_relType){
		case GEQ:
			return dn >= db;
		case LEQ:
			return dn <= db;
		case LESS:
			return dn < db;
		case GREATER:
			return dn > db;			
		}
		return false;
	}
	
	public String getPCTLTranslation(){
		String result = "";
		String qStr = "";
		String relStr="";
		String boundStr="";
		String maxminQStr="";
		
		if (isMaximizationQuantifier(m_qtype))
			maxminQStr="max";
		if (isMinimizationQuantifier(m_qtype))
			maxminQStr="min";
		
		if(isProbabilisticQuantifier(m_qtype)){
			qStr = "P"+maxminQStr+"=?";
		}
		
		if(isRewardQuantifier(m_qtype)){
			qStr = "R{\""+m_reward+"\"}"+maxminQStr+"=?";
		}
		
		
		result = qStr + relStr + boundStr + " " + m_pathFormula;
		
		return result;
	}
	
	public String getECAnnotation(){
		String result="//";
		String maxminQStr="";
		
		if (m_type==PropertyType.PROPERTY_BOOLEAN){
			result+="constraint, ";
			if (m_relType==QuantifierRelationType.LEQ||m_relType==QuantifierRelationType.LESS){
				maxminQStr="max";
			} else {
				maxminQStr="min";
			}
			result+= maxminQStr + ", " + m_bound;
		}		
		if (m_type==PropertyType.PROPERTY_QUANTITATIVE){
			result+="objective, ";
			if (isMaximizationProperty(m_qtype))
				maxminQStr="max";
			if (isMinimizationProperty(m_qtype))
				maxminQStr="min";
			result+= maxminQStr;
		}
		return result;
	}
	
	
	/**
	 * @return the m_qtype
	 */
	public QuantifierType getQtype() {
		return m_qtype;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HQProperty [m_type=" + m_type + ", m_literal=" + m_literal + ", m_pathFormula=" + m_pathFormula
				+ ", m_qtype=" + m_qtype + ", m_quantifiers=" + m_quantifiers + ", m_relType=" + m_relType
				+ ", m_relTypes=" + m_relTypes + ", m_bound=" + m_bound + ", m_reward=" + m_reward + "]";
	}


	public static void main (String[] argv){
		HQProperty p = new HQProperty("maxP [F allsfoo]");
		System.out.println(String.valueOf(p));		
		System.out.println(p.getPCTLTranslation());
		
		HQProperty p2 = new HQProperty("somePmax<=1.0[F allsfoo]");
		System.out.println(String.valueOf(p2));		
		System.out.println(p2.getPCTLTranslation());
		
				
	}
	
	
}
