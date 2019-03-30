package org.jcm.haiq.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jcm.haiq.solve.HQSolver;

import org.jcm.alloyutils.AlloyConnector;
import org.jcm.alloyutils.AlloySolution;
import org.jcm.alloyutils.AlloySolution.AlloySolutionNode;
import org.jcm.haiq.core.*;
import org.jcm.haiq.core.HQLabel.QuantifierType;
import org.jcm.haiq.core.HQPredicate.RelationType;
import org.jcm.haiq.translate.PrismTranslator;
import org.jcm.util.*;


/**
 * @author jcamara
 *
 */
public class ParserMark1 {

	private ArrayList<String> m_lines=null;
	private boolean m_debug = true;
	private boolean m_reading_process = false;
	private String m_current_sig="";
	private String m_current_sig_extends="";
	private boolean m_current_is_abstract = false;
	
	private HQModel m_bmodel = new HQModel();
	
	private HashMap<String, Boolean> m_signatures = new HashMap<String, Boolean>();
	private ArrayList<String> m_alloy_lines = new ArrayList<String>();
	private HashMap<String, ArrayList<String>> m_processes = new HashMap<String, ArrayList<String>>();
	private ArrayList<String> m_current_process = new ArrayList<String>();
	private ArrayList<PropertyEntry> m_properties = new ArrayList<PropertyEntry>();
	
	public class PropertyEntry {
	    private String m_id;
	    private String m_literal;
	    public PropertyEntry(String id, String literal){
	        this.m_id = id;
	        this.m_literal = literal;
	    }
	    public String getId(){ return m_id; }
	    public String getLiteral(){ return m_literal; }
	    public void setId(String id){ this.m_id = id; }
	    public void setLiteral(String literal){ this.m_literal = literal; }
	}
	
	
	
	/**
	 * @param m_debug the m_debug to set
	 */
	public void setDebugMode(boolean m_debug) {
		this.m_debug = m_debug;
	}

	/**
	 * Loads source from a text file
	 * @param filename
	 */
	public void loadSource (String filename){
		TextFileHandler th = new TextFileHandler(filename);
		m_lines = th.readFileLines();
	}
	
	/**
	 * Export alloy code to a file
	 * @param filename
	 */
	public void saveAlloyToFile (String filename){
		TextFileHandler th = new TextFileHandler(filename);
		th.exportFile(String.join("\n", m_alloy_lines));
	}
	
	public String getAlloySource(){
		return m_alloy_lines.toString();
	}
	
	
	/**
	 * Returns the source code in a string
	 * @return
	 */
	public String getSource(){
		return m_lines.toString();
	}
	
	/**
	 * Returns the behavioral model hierarchy constructed
	 * @return
	 */
	public HQModel getBehavioralModel(){
		return m_bmodel;
	}
	
	/**
	 * Returns the list of properties defined in the model
	 * @return
	 */
	public ArrayList<PropertyEntry> getProperties(){
		return m_properties;
	}
	
	/**
	 * Determines if a string contains a model type declaration
	 * @param s
	 * @return
	 */
	public boolean isModelTypeDeclaration(String s){
		return s.startsWith("ModelType:");
	}
	
	/**
	 * Determines if a string contains an abstract signature declaration
	 * @param s
	 * @return
	 */
	public boolean isAbstractSignatureDeclaration(String s){
		return Arrays.asList(s.split(" ")).contains("abstract") && (Arrays.asList(s.split(" ")).contains("sig"));
	}

	/**
	 * Determines if a string contains a signature declaration
	 * @param s
	 * @return
	 */
	public boolean isSignatureDeclaration(String s){
		return !Arrays.asList(s.split(" ")).contains("abstract") && (Arrays.asList(s.split(" ")).contains("sig"));
	}

	/**
	 * Determines if a signature declaration extends another signature
	 * @param s
	 * @return
	 */
	public boolean extendsSignature(String s){
		return Arrays.asList(s.split(" ")).contains("extends");		
	}
	
	/**
	 * Determines if a signature is already defined
	 * @param id
	 * @return
	 */
	public boolean isSignatureDefined(String id){
		return (m_signatures.containsKey(id));
	}
	
	
	/**
	 * Returns the index of string s in string array a
	 * @param s
	 * @param a
	 * @return int (-1) if not found
	 */
	public int stIndex (String s, String[] a){
		for (int i=0;i<a.length;i++) {
			if (a[i].equals(s))
			        return(i);
		}
		return (-1);
	}
	
	

	class SignatureAlreadyDefinedException extends Exception{
		public SignatureAlreadyDefinedException (String msg){
			super(msg);
		}
	}
	
	class SignatureNotDefinedException extends Exception{
		public SignatureNotDefinedException (String msg){
			super(msg);
		}
	}
	
	
	public ArrayList<String> getProcessSource(String id){
		if (m_processes.containsKey(id)){
			return (m_processes.get(id));
		} else {
			try{
				throw (new SignatureNotDefinedException("Cannot retrieve process \'"+id+"\': process not defined."));
			} catch (SignatureNotDefinedException e){
				System.out.println(e.getMessage());
				return null;
			}
		}
	}
	
	public HQLabel parseLabel(String s){
		String[] e = s.replaceAll("label ", "").trim().split("\\[");
		if (m_debug) System.out.println("\t >>> Label definition:" + s);
		String id = e[0].trim();
//		System.out.println(id);
		String[] be = e[1].trim().split("=");
		String qString = be[0].trim().split(" ")[0];
//		System.out.println(qString);
		QuantifierType q = QuantifierType.NONE;
		if (Objects.equals(qString,"all"))
			q = QuantifierType.ALL;
		if (Objects.equals(qString,"some"))
			q = QuantifierType.SOME;
		if (Objects.equals(qString,"one"))
			q = QuantifierType.ONE;
		String typeString=be[0].split(":")[0].split(" ")[1];
//		System.out.println(typeString);
		String predString=e[1].split(":")[1].replaceAll("\\]", "");
//		System.out.println(predString);
		HQPredicate p = parseBinaryPredicate(predString);
		HQLabel l = new HQLabel(id, q, typeString, p);
		if (m_debug) System.out.println("\t Label added: "+l.toString());				
		return l;
	}
	
	String getIdECParameterFromString(String s){
		String[] e = s.replaceAll("evolve ", "").trim().split(" ");
		String [] e1 = e[1].split("\\[");
		String id = e1[0].trim();
		return id;
	}

	HQECParameter parseECParameter(String s){
		String[] e = s.replaceAll("evolve ", "").trim().split(" ");
		String type = e[0].trim();
		String [] e1 = e[1].split("\\[");
		String id = e1[0].trim();
		String [] e2 = e1[1].replaceAll("\\.\\.", " ").split(" ");
		String min = e2[0].trim();
		String max = e2[1].replaceAll("\\];", "").trim();	
		HQECParameter p = new HQECParameter(id, type,min,max);
		return p;
	}
	
	String getIdECDistributionFromString(String s){
		String[] e = s.replaceAll("evolve ", "").replaceAll("distribution","").split("\\[");
		String id = e[0].trim();
		return id;
	}
	
	HQECDistribution parseECDistribution(String s){
		String[] e = s.replaceAll("evolve ", "").replaceAll("distribution","").trim().replaceAll("\\.\\."," ").split("\\[");
		String id = e[0].trim();
		HQECDistribution d = new HQECDistribution(id);
		for (int i=1; i<e.length;i++){
			String[] minmaxStr = e[i].replaceAll("\\]", "").replaceAll(";", "").split(" ");
			d.addMin(minmaxStr[0]);
			d.addMax(minmaxStr[1]);
		}
		return d;
	}
	
	/**
	 * Parses a .hq file into an HQModel object (m_bmodel)
	 */
	public void parse(){
		String line;
		String[] line_chunks;
		for (int i=0; i<m_lines.size();i++){
			line = m_lines.get(i);
			line_chunks = line.split(" ");
			if (line_chunks.length==0) continue; // Skip, blank line
			
			if (line.startsWith("//")){		// Comment, ignore
				if (m_debug) System.out.println("Comment:" + line);
			}
			
			if (line.contains("//")){ // If line contains comment, remove it
				if (m_debug) System.out.println("Removing comment from line "+String.valueOf(i)+":\n\t"+line);
				line = line.split("//")[0].trim();
			}
			
			
			if (isSignatureDeclaration(line) || isAbstractSignatureDeclaration(line)){  // New signature
				String sig_id = line_chunks[stIndex("sig",line_chunks)+1];  // Determine signature identifier
				if (isSignatureDefined(sig_id)){  // If signature already defined, Exception, abort.
					try{
						throw (new SignatureAlreadyDefinedException("Signature \'"+sig_id+"\' is already defined. Aborting."));
					} catch (SignatureAlreadyDefinedException e){
						System.out.println(e.getMessage());
						System.exit(-1);
					}
				}
				
			
				if (extendsSignature(line)){ // If current signature is extending an abstract signature
					String extends_id = line_chunks[stIndex("extends",line_chunks)+1].trim();
					if (extends_id.indexOf("{")!=-1)
						extends_id = extends_id.substring(0, extends_id.indexOf("{")).trim(); // Determine parent signature identifier
					m_current_sig_extends = extends_id;  // Used later for process expansion (parseProcess invocation)
					if (!isSignatureDefined(extends_id)){ // If parent signature not defined, Exception, abort.
						try{
							throw (new SignatureNotDefinedException("Cannot extend signature \'"+sig_id+"\' from \'"+extends_id+"\': parent signature not defined. Aborting."));
						} catch (SignatureNotDefinedException e){
							System.out.println(e.getMessage());
							System.exit(-1);
						}
					}
					if (m_debug) System.out.println("Signature \'"+sig_id+"\' extends \'"+extends_id+"\'.");
				} else {
					m_current_sig_extends = "";  // The current signature does not extend another one
				}

				if (isAbstractSignatureDeclaration(line)) {  // Insert current signature
					m_signatures.put(sig_id, true);
					m_current_is_abstract = true;
				}
				else {
					m_signatures.put(sig_id, false);
					m_current_is_abstract = false;
				}
				m_current_sig = sig_id;                   // Set as current signature (for scope of process definitions)
				if (m_debug) System.out.println("* New signature \'"+m_current_sig+"\'.");	
				
			}
						

			if (isRewardDeclaration(line)) { // If we are declaring a reward (external to a process)
				System.out.println("PARSING REWARD:"+line);
				parseReward(line, "");
			}
			
			if (line.startsWith("</")){ // Starts behavior definition for signature
				m_current_process = new ArrayList<String>();
				m_reading_process = true;
				if (m_debug) System.out.println("Entering process definition mode for signature \'" + m_current_sig + "\'.");
			}
			
			if (m_reading_process){				// Incorporate line into current process
				if (!line.startsWith("</") && !line.startsWith("/>"))
					m_current_process.add(line);
			} else if (line.startsWith("label ")){ // Label definition
				m_bmodel.addLabel(parseLabel(line));
			} else if (isECEvolvableParameter(line)){
				if (m_debug) System.out.println("\t EvoChecker Evolvable parameter declaration >>> " + line );
				HQECParameter p = parseECParameter(line);
				m_bmodel.addECParameter(getIdECParameterFromString(line), p);
				if (m_debug) System.out.println("\t Added EvoChecker evolvable parameter " + p.toString() + " to model.");
			} else if (isECEvolvableDistribution(line)){
				if (m_debug) System.out.println("\t EvoChecker Evolvable distribution declaration >>> " + line);
				HQECDistribution d = parseECDistribution(line);
				m_bmodel.addECDistribution(getIdECDistributionFromString(line),d);
				if (m_debug) System.out.println("\t Added EvoChecker evolvable distribution " + d.toString() + " to model.");
			} else if (line.startsWith("property ")){ // Property definition
				String prop_literal ="";
				String declaration_body = String.join(" ",  Arrays.copyOfRange(line_chunks, 1, line_chunks.length));
				if (line.split(" as ").length>1){
					String alias = declaration_body.split(" as ")[1].replaceAll(";", "").trim();
					prop_literal = declaration_body.split(" as ")[0].trim();
					m_properties.add(new PropertyEntry(alias, prop_literal));
				} else {
					prop_literal = line.replaceAll("property", "").trim();
					m_properties.add(new PropertyEntry(prop_literal, prop_literal));	
				}
			} else if (isConstantDeclaration(line)){ // Constant declaration
				if (m_debug) System.out.println("\t Constant declaration >>> "+ line);
				String declaration_body="";
				boolean isDouble = false;
				if (line.contains("double")){
					isDouble = true;
					declaration_body = String.join(" ",  Arrays.copyOfRange(line_chunks, 2, line_chunks.length));
				}
				else
					declaration_body = String.join(" ",  Arrays.copyOfRange(line_chunks, 1, line_chunks.length));
				if (declaration_body.split("=").length<=1){ // If it is an undefined constant (value to be supplied in analyzer invocation)
					HQFormula f = new HQFormula(declaration_body.replaceAll(";", ""));
					f.setDefined(false);
					f.setDouble(isDouble);
					f.setConstant(true);
					m_bmodel.addFormula(f);
					if (m_debug) System.out.println("\t Added constant " + f.toString() + " to model.");
				} else {  // Constant defined with expression on the right-hand side of the equality
					String declaration_id = declaration_body.split("=")[0].trim();
					String declaration_expression = declaration_body.split("=")[1].replaceAll(";", "").trim();
					HQFormula f = new HQFormula(declaration_id, new HQExpression(declaration_expression));
					f.setConstant(true);
					f.setDouble(isDouble);
					f.setDefined(true);
					m_bmodel.addFormula(f);
					if (m_debug) System.out.println("\t Added constant " + f.toString() + " to model.");					
				}  // Finish processing constant declaration
				
			} else if (isModelTypeDeclaration(line)){ // Declaration of model type (DTMC by default, if not declared in haiq code)
				String modeltype = line.split(":")[1].trim();
				if (Objects.equals(modeltype, "dtmc"))
					m_bmodel.setType(HQModel.ModelType.DTMC);
				if (Objects.equals(modeltype, "mdp"))
					m_bmodel.setType(HQModel.ModelType.MDP);
				if (Objects.equals(modeltype, "smg"))
					m_bmodel.setType(HQModel.ModelType.SMG);
				if (Objects.equals(modeltype, "ctmc"))
					m_bmodel.setType(HQModel.ModelType.CTMC);				
			} else if (isRewardDeclaration(line)) { // If we are declaring a reward
				System.out.println("PARSING REWARD2:"+line);
				parseReward(line, "");
			} 
			else { // Incorporate into general Alloy specification
				m_alloy_lines.add(line);
			}
			
			if (line.startsWith("/>")){ // Finishes behavior definition for signature
				m_reading_process = false;
				m_processes.put(m_current_sig, m_current_process);   // Adds current process source to the list of existing processes
				
				HQProcess new_p = parseProcess(m_current_sig, m_current_sig_extends);  // Parse the process
				if (!Objects.equals(new_p.getExtends(), "")){  // If it extends any other, postprocess it to expand definitions
					new_p.expand(m_bmodel.getProcesses().get(new_p.getExtends()));
				}
				
				new_p.setAbstract(m_current_is_abstract);
				
				m_bmodel.addProcess(m_current_sig, new_p ); // Parses and adds generated current process object to behavior model
				
				if (m_debug) {
					System.out.println("Behavior specification added: \'" + m_current_process.toString() + "\'.");
					System.out.println("Process object added: \'" + m_bmodel.getProcesses().get(m_current_sig).toString() + "\'.");
					System.out.println("Exiting process definition mode for signature \'" + m_current_sig + "\'.\n\n");
				}

			}
			
			
			
		}
		
		if (m_debug) {
			System.out.println("\n\n * PROCESSES INGESTED:\n"+m_processes.toString());
			System.out.println("\n\n * RELATIONAL MODEL INGESTED:\n"+m_alloy_lines.toString());
			}
	}
	
	
	/**
	 * Determines if a String contains an evochecker evolvable parameter declaration
	 * @param s
	 * @return
	 */
	public boolean isECEvolvableParameter(String s){
		if (Objects.equals(s.split(" ")[0],"evolve")){
			return (Objects.equals(s.split(" ")[1],"double") 
					|| Objects.equals(s.split(" ")[1],"int"));
		}
		return false;
	}

	/**
	 * Determines if a String contains an evochecker evolvable probability distribution declaration
	 * @param s
	 * @return
	 */
	public boolean isECEvolvableDistribution(String s){
		if (Objects.equals(s.split(" ")[0],"evolve")){
			return (Objects.equals(s.split(" ")[1],"distribution"));
		}
		return false;
	}
	
	/**
	 * Determines if a String contains a formula declaration
	 * @param s
	 * @return
	 */
	public boolean isFormulaDeclaration(String s){
		return Objects.equals(s.split(" ")[0],"formula");		
	}
	
	/**
	 * Determines if a String contains a constant declaration
	 * @param s
	 * @return
	 */
	public boolean isConstantDeclaration(String s){
		return Objects.equals(s.split(" ")[0],"const");		
	}
	
	/**
	 * Determines if a String contains a reward declaration
	 * @param s
	 * @return
	 */
	public boolean isRewardDeclaration(String s){
		if ((s.split(" ").length)==0) return false;
		return Objects.equals(s.split(" ")[0],"reward");		
	}
	
	/**
	 * Determines if a String contains a variable declaration
	 * @param s
	 * @return
	 */
	public boolean isVariableDeclaration(String s){
		if ((s.split(" ").length)==0) return false;
		return Objects.equals(s.split(" ")[0], "var");
	}
	
	/**
	 * Determines if a String contains an enum declaration
	 * @param s
	 * @return
	 */
	public boolean isEnumerationDeclaration(String s){
		if ((s.split(" ").length)==0) return false;
		return Objects.equals(s.split(" ")[0], "enum");
	}
	
	/**
	 * Determines if a String contains a command declaration
	 * @param s
	 * @return
	 */
	public boolean isCommandDeclaration(String s){
		if ((s.split(" ").length)==0) return false;
		return s.split(" ")[0].startsWith("[");
	}
	
	public String trimParentheses(String s){
		String res = s;
		if (res.startsWith("("))
			res = res.substring(1, res.length());
		if (res.endsWith(")"))
			res = res.substring(0,res.length()-1);
		return res;
	}
	
	/**
	 * Determines if a string encodes a unary predicate
	 * @param s
	 * @return
	 */
	public boolean isUnaryPredicate(String s){
		return ((s.split("<=").length==1)
				&&(s.split(">=").length==1)
				&&(s.split("<").length==1)
				&&(s.split(">").length==1)
				&&(s.split("=").length==1));
	}
	
	
	class UnknownRelationTypeException extends Exception{
		public UnknownRelationTypeException (String msg){
			super(msg);
		}
	}
	
	/**
	 * Determines the type of binary relation present in a predicate
	 * @param s
	 * @return
	 */
	public HQPredicate.RelationType getBinaryRelationType(String s){	
		if (s.split("<=").length>1) return HQPredicate.RelationType.LEQ;
		if (s.split(">=").length>1) return HQPredicate.RelationType.GEQ;
		if (s.split("!=").length>1) return HQPredicate.RelationType.NOT_EQUALS;
		if (s.split(">").length>1) return HQPredicate.RelationType.GREATER_THAN;
		if (s.split("<").length>1) return HQPredicate.RelationType.LESS_THAN;
		if (s.split("=").length>1) return HQPredicate.RelationType.EQUALS;
		
		
		try{
			throw (new UnknownRelationTypeException("Unsupported binary relation type in predicate \'"+s+"\'. Must be: <=, >=, =, <, >."));
		} catch (UnknownRelationTypeException e){
			System.out.println(e.getMessage());
		}
		return RelationType.UNARY;		
	}
		
	
	
	/**
	 * Generates a HQPredicate object (for a binary predicate), parsing from a string
	 * @param s
	 * @return
	 */
	public HQPredicate parseBinaryPredicate(String s){
		HQPredicate.RelationType t = getBinaryRelationType(s);
		String left="";
		String right="";
		switch (t){
		case LEQ:
			left = trimParentheses(s.split("<=")[0].trim());
			right = trimParentheses(s.split("<=")[1].trim());			
			break;

		case GEQ:
			left = trimParentheses(s.split(">=")[0].trim());
			right = trimParentheses(s.split(">=")[1].trim());			
			break;
			
		case NOT_EQUALS:
			left = trimParentheses(s.split("!=")[0].trim());
			right = trimParentheses(s.split("!=")[1].trim());			
			break;

		case GREATER_THAN:
			left = trimParentheses(s.split(">")[0].trim());
			right = trimParentheses(s.split(">")[1].trim());			
			break;

		case LESS_THAN:
			left = trimParentheses(s.split("<")[0].trim());
			right = trimParentheses(s.split("<")[1].trim());			
			break;

		case EQUALS:
			left = trimParentheses(s.split("=")[0].trim());
			right = trimParentheses(s.split("=")[1].trim());			
			break;

		}
		
		return (new HQPredicate(new HQExpression(left), t, new HQExpression(right)));		
	}
	
	
	
	/**
	 * Generates an HQReward object from a string encoding a reward
	 * @param s
	 * @return
	 */
			
	public void parseReward(String line, String id){
		String[] line_chunks = line.split(" ");
		if (m_debug) System.out.println("\t Reward declaration >>> "+ line);
		String declaration_body = String.join(" ",  Arrays.copyOfRange(line_chunks, 2, line_chunks.length)).trim();
		String reward_id = line_chunks[1];
		if (Objects.equals(m_bmodel.getRewardStructures().get(reward_id),null)){
			m_bmodel.addRewardStructure(reward_id, new HQRewardStructure(reward_id));
		}
		String action_body = "";
		String action ="";
		String relation ="";
		String guard = "";
		String expression = "";
		if (declaration_body.contains("[")){ // action reward

			action_body = declaration_body.substring(1+declaration_body.indexOf("["), declaration_body.indexOf("]")).trim();
			if (action_body.split(":").length<=1){ // Action is simple or no action
				action = action_body;
			} else{  // Action is scoped to a relation
				relation = action_body.split(":")[0];
				action = action_body.split(":")[1];
			}				
			declaration_body = declaration_body.split("]")[1].trim();
			guard = declaration_body.substring(0, declaration_body.indexOf(":")).trim();
			expression = declaration_body.substring(declaration_body.indexOf(":")+1, declaration_body.length()-1).trim();
		} else { // state reward
			guard = declaration_body.substring(0, declaration_body.indexOf(":")).trim();
			expression = declaration_body.substring(declaration_body.indexOf(":")+1, declaration_body.length()-1).trim();
		}

		HQAction raction = new HQAction(action, relation);
		HQGuard rguard = parseGuard(guard);
		HQExpression rexpression = new HQExpression(expression);
		HQReward r = new HQReward(raction, rguard, rexpression, id);
		m_bmodel.getRewardStructures().get(reward_id).addReward(r);
	}
	
	
	/**
	 * Generates an HQGuard object from a string encoding a command guard
	 * @param s
	 * @return
	 */
			
	public HQGuard parseGuard(String s){
		HQGuard g = new HQGuard();
				
		String[] preds = s.split("\\&");
		for (int i=0; i<preds.length;i++){
			preds[i] = trimParentheses(preds[i].trim());
			//System.out.println(preds[i]);		
			
			if (isUnaryPredicate(preds[i])){ // If we have a unary predicate, add guard
				g.addPredicate(new HQPredicate (new HQExpression(preds[i]), RelationType.UNARY, new HQExpression("")));
			} else { // Binary predicate
				g.addPredicate(parseBinaryPredicate(preds[i]));
			}						
		}		
		//System.out.println(g.toString());
		return g;
	}
	
	/**
	 * Generates an HQUpdate object from a string encoding an update
	 * @param s
	 * @return
	 */
	public HQUpdate parseUpdate (String s){
		String varUpdatesString ="";
		String update_probability = "";
		if (s.split(":").length>1) { // Update includes a probability
			update_probability = trimParentheses(s.split(":")[0].trim());
			varUpdatesString = s.split(":")[1].trim();
		} else {
			varUpdatesString = s;
		}

		HQUpdate u = new HQUpdate(new HQExpression(update_probability));

		String[] var_updates = varUpdatesString.split("\\&");
		for (int i=0; i<var_updates.length;i++){
			var_updates[i] = trimParentheses(var_updates[i].trim());
			String update_variable = trimParentheses(var_updates[i].split("=")[0].trim().replace("'", ""));
			if (var_updates[i].split("=").length>1){
				String update_expression = trimParentheses(var_updates[i].split("=")[1].trim());
				u.addVariableUpdate(update_variable, new HQExpression(update_expression));
			} else {
				System.out.println("Error parsing update in line:\n\t "+s+"\n(update index:"+String.valueOf(i)+")");
			}
		}
		return u;
	}
	
	/**
	 * Parses process source definition, inserting it into an HQProcess object
	 * @param id
	 * @return
	 */
	public HQProcess parseProcess(String id, String doesExtend){
		HQProcess p = new HQProcess(id, doesExtend);
		String[] s = getProcessSource(id).toArray(new String[0]);		
		
		String line;
		String[] line_chunks;
		for (int i=0; i<s.length;i++){  // Start ingesting source text lines
			line = s[i].trim();
			line_chunks = line.split(" ");
			if (line_chunks.length==0) continue; // Skip if blank line
			
			if (isCommandDeclaration(line)){ // If we are introducing a new command
				if (m_debug) System.out.println("\t >>> Command declaration: "+ line);				
				String action_body = line_chunks[0].split("]")[0].replace("[", "").trim();
				String action = "";
				String relation = "";
				if (action_body.split(":").length<=1){ // Action is simple or no action
					action = action_body;
				} else{  // Action is scoped to a relation
					relation = action_body.split(":")[0];
					action = action_body.split(":")[1];
				}

				String guardString = trimParentheses(line.substring(line.indexOf("]")+1, line.indexOf("->")).trim());
				HQGuard guard = parseGuard(guardString);
				HQCommand c = new HQCommand(new HQAction(action, relation), guard); // We create the command object
				
				// Add the different updates to the command object
				String[] update_literals = line.substring(line.indexOf("->")+2, line.length()).replaceAll(";", "").split("\\+");
				for (int j=0; j<update_literals.length; j++){
					String u = update_literals[j].trim();	
					c.addUpdate(parseUpdate(u));
				}
				p.addCommand(c); // Add command to process
				if (m_debug) System.out.println("\t Added command " + c.toString() + " to process \'"+ id +"'.");	
			} // Finish processing command
			
			if (isEnumerationDeclaration(line)){ // If we are declaring an enumeration
				if (m_debug) System.out.println("\t Enum declaration >>> "+ line);
				String declaration_body = String.join(" ",  Arrays.copyOfRange(line_chunks, 1, line_chunks.length));
				String declaration_id = declaration_body.split(":")[0].trim();
				String declaration_tail = declaration_body.split(":")[1].replaceAll(";|\\{|\\}", "").trim();
				String[] enum_items = declaration_tail.split(",");
				HQEnum enum_object = new HQEnum(declaration_id);
				int count=0;
				for (int j=0; j <enum_items.length;j++){
					enum_object.addItem(enum_items[j].trim());
					HQFormula f = new HQFormula(enum_items[j].trim(), new HQExpression(String.valueOf(count)));
					m_bmodel.addFormula(f);
					if (m_debug) System.out.println("\t Added enum item " + f.toString() + " to process \'"+ id +"'.");	
					count++;
				}
				m_bmodel.addEnum(enum_object);
				if (m_debug) System.out.println("\t Added enum " + enum_object.toString() + " to process \'"+ id +"'.");	
				
			}
			
			if (isECEvolvableParameter(line)){
				if (m_debug) System.out.println("\t EvoChecker Evolvable parameter declaration >>> " + line );
				HQECParameter mp = parseECParameter(line);
				p.addECParameter(mp);
				if (m_debug) System.out.println("\t Added EvoChecker evolvable parameter " + mp.toString() + " to process \'"+ id +"'.");

			}
			
			if (isECEvolvableDistribution(line)){
				if (m_debug) System.out.println("\t EvoChecker Evolvable distribution declaration >>> " + line);
				HQECDistribution d = parseECDistribution(line);
				p.addECDistribution(d);
				if (m_debug) System.out.println("\t Added EvoChecker evolvable distribution " + d.toString() + " to process \'"+ id +"'.");
			}
			
			if (isVariableDeclaration(line)){ // If we are declaring a variable
				if (m_debug) System.out.println("\t Variable declaration >>> "+ line);
				String declaration_body = String.join(" ",  Arrays.copyOfRange(line_chunks, 1, line_chunks.length));
				String declaration_id = declaration_body.split(":")[0].trim();
				String declaration_tail = declaration_body.split(":")[1].trim();
				if (declaration_tail.startsWith("bool")){ // Declaring a boolean variable
					String declaration_init = line_chunks[line_chunks.length-1].replaceAll(";", "").trim();
					boolean declaration_init_b = false;
					if (Objects.equals(declaration_init, "true")){
						declaration_init_b = true;
					}
					HQVariable v = new HQVariable (declaration_id, declaration_init_b);
					p.addVariable(v);
					if (m_debug) System.out.println("\t Added boolean variable " + v.toString() + " to process \'"+ id +"'.");
				} else { // Numeric variable
					String declaration_init = line_chunks[line_chunks.length-1].replaceAll(";", "");
					if (m_bmodel.containsEnumItem(declaration_init)){ // If initialization is with symbolic enum item, translate to index
						declaration_init = String.valueOf(m_bmodel.getEnumItemIndex(declaration_init));
					}
					String dec_min="";
					String dec_max="";
					if (declaration_tail.split("\\.\\.").length<2){  // Using enum to specify variable range
						String enumKey = declaration_tail.split("]")[0].replace("[","").trim();
						if (m_bmodel.getEnums().containsKey(enumKey)){
							dec_min = "0";
							dec_max = String.valueOf(m_bmodel.getEnums().get(enumKey).getItems().size()-1);
						}
					} else {  // Absolute values in variable range
						dec_min = declaration_tail.split("\\.\\.")[0].replace("[","").trim();
						dec_max = declaration_tail.split("\\.\\.")[1].split("]")[0].trim();		
					}
					HQVariable v = new HQVariable (declaration_id, dec_min,dec_max, declaration_init);
//					HQVariable v = new HQVariable (declaration_id, Integer.parseInt(dec_min), Integer.parseInt(dec_max), Integer.parseInt(declaration_init));
					p.addVariable(v);
					if (m_debug) System.out.println("\t Added Integer variable " + v.toString() + " to process \'"+ id +"'.");
				}
			} // Finish processing variable declarations
			
			
			if (isRewardDeclaration(line)) { // If we are declaring a reward
				parseReward(line, id);	
			} // Finish processing reward declaration
			
			if (isFormulaDeclaration(line)){  // If we are declaring a formula
				if (m_debug) System.out.println("\t Formula declaration >>> "+ line);
				String declaration_body = String.join("",  Arrays.copyOfRange(line_chunks, 1, line_chunks.length));
				if (declaration_body.split("=").length<=1){ // If it is an undefined formula (to be implemented by inheriting processes)
					HQFormula f = new HQFormula(declaration_body.replaceAll(";", ""));
					p.addFormula(f);
					if (m_debug) System.out.println("\t Added formula " + f.toString() + " to process \'"+ id +"'.");
				} else {  // Formula defined with expression on the right-hand side of the equality
					String declaration_id = declaration_body.split("=")[0].trim();
					String declaration_expression = declaration_body.split("=")[1].replaceAll(";", "").trim();
					HQFormula f = new HQFormula(declaration_id, new HQExpression(declaration_expression));
					p.addFormula(f);
					if (m_debug) System.out.println("\t Added formula " + f.toString() + " to process \'"+ id +"'.");					
				}
			} // Finish processing formula declaration
			
		} // Finish processing HQProcess
		
		return p;
	}  // Finish parsing of process

}
