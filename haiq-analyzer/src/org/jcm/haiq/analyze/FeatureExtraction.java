package org.jcm.haiq.analyze;

import java.util.HashMap;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import org.jcm.alloyutils.*;
import org.jcm.alloyutils.AlloySolution.AlloySolutionNode;
import org.jcm.alloyutils.AlloySolution.AlloySolutionArc;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.KStepMarkov;

import edu.uci.ics.jung.algorithms.shortestpath.*;

/**
 * @author javiercamaramoreno
 * Class that extract features from a configuration/solution structural graph
 */
public class FeatureExtraction {

	protected static HashMap<String, String> m_structures;
	protected static HashMap<String, HashMap<String, Double>> m_features;
	protected static LinkedList<String> m_feature_names;
	
	protected static final String cardinalityIdString = "[C]";
	protected static final String betweennessCentralityIdString = "[B]";

	/**
	 * @param sols Set of Alloy solutions
	 */
	public FeatureExtraction(HashMap<String,String> sols){
		// TODO Auto-generated constructor stub
		m_features = new HashMap<String,HashMap<String,Double>>();
		m_feature_names = new LinkedList<String>();
		m_structures = sols;
	
	}

	
	public DirectedSparseGraph<String, String> graphFromAlloySolution(String solStr) {
		DirectedSparseGraph<String, String> g = new DirectedSparseGraph<String, String>();
		AlloySolution sol = new AlloySolution();
		sol.loadFromString(solStr);
		String[] linkparts = {""};
		for (AlloySolutionNode node : sol.getNodes().values()) {
		    	g.addVertex(node.getId());
		}
		for (AlloySolutionArc arc: sol.getArcs().values()) {
			linkparts= arc.getId().split("-");
	    	g.addEdge(arc.getId(), linkparts[1], linkparts[2]);
		}
		return g;
	}
	
	
	public HashMap<String, Double> extractNodeDistanceFeatureFromSolution (String solStr){
		HashMap<String, Double> res = new HashMap<String, Double>();
		DirectedSparseGraph<String, String> g = graphFromAlloySolution (solStr);
//		Double diameter = DistanceStatistics.diameter(g);
//		System.out.println ("Diameter:" + diameter);
//		DistanceStatistics.averageDistances(g);
		BetweennessCentrality<String, String> ranker = new BetweennessCentrality<String, String>(g);
		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.evaluate();		
//		System.out.println(ranker.getRankings().toString());	
		AlloySolution sol = new AlloySolution();
		sol.loadFromString(solStr);
		for (AlloySolutionNode node : sol.getNodes().values()) {
//	    	System.out.println("Nodeid: " + node.getId() + " -  Rank: " + ranker.getVertexRankScore(node.getId()));
	    	res.put(betweennessCentralityIdString+node.getId(), ranker.getVertexRankScore(node.getId()));
		}
		return res;
	}
	
	public void extractDistanceFeatures() {
		for (Map.Entry<String, String> e: m_structures.entrySet()){
			HashMap<String, Double> tmp = extractNodeDistanceFeatureFromSolution(e.getValue());
			for (Map.Entry<String, Double> e2: tmp.entrySet()) {
				m_features.get(e2.getKey()).put(e.getKey(), e2.getValue());
			}
		}
	}
	
	
	/**
	 * @return the set of Alloy solutions (local copy)
	 */
	public HashMap<String,String> getSolutions(){
		return m_structures;
	}
	
	/**
	 * Populates m_feature_names and m_features with feature names and values
	 */
	public void extractFeatures() {
		m_feature_names = extractNodeFeatureNames();
		m_feature_names.addAll(extractArcFeatureNames());
		m_feature_names.addAll(extractNodeandArcCardinalityFeatureNames());	
		m_feature_names.addAll(extractNodeFeatureNames(betweennessCentralityIdString));
		m_features = new HashMap<String, HashMap<String,Double>>();
		for (int i=0;i<m_feature_names.size();i++) {
			m_features.put(m_feature_names.get(i), new HashMap<String, Double>());
		}
		
		extractNodeAndArcPresenceFeatures();
		extractNodeCardinalityFeatures();
		extractArcCardinalityFeatures();
		extractDistanceFeatures();
	}
	

	
	/**
	 * @return List with the feature names for cardinality of nodes and arcs
	 */
	public LinkedList<String> extractNodeandArcCardinalityFeatureNames() {
		LinkedList<String> res = new LinkedList<String>();

		String ntype="";
		String[] linkparts = {""};
		for (Map.Entry<String, String> e: m_structures.entrySet()){
			AlloySolution sol = new AlloySolution();
			sol.loadFromString(e.getValue());
		    for (AlloySolutionNode node : sol.getNodes().values()) {
		    	ntype = cardinalityIdString + node.getId().split("\\$")[0];
//		    	System.out.println("Nid "+node.getId());
//		    	System.out.println(ntype);
		    	if (!res.contains(ntype)){
		    		res.add(ntype);
		    	}		
		    }
		}

		for (Map.Entry<String, String> e: m_structures.entrySet()){
			AlloySolution sol = new AlloySolution();
			sol.loadFromString(e.getValue());
		    for (AlloySolutionArc arc : sol.getArcs().values()) {
		    	linkparts= arc.getId().split("-");
		    	ntype = cardinalityIdString + linkparts[0]+"-"+linkparts[1].split("\\$")[0]+"-"+linkparts[2].split("\\$")[0];
		    	if (!res.contains(ntype)){
		    		res.add(ntype);
		    	}		
		    }
		}

		
		return res;
	}
	
	
	/**
	 *  Extract the cardinality of different nodes into m_features
	 */
	public void extractNodeCardinalityFeatures() {
		for (Map.Entry<String, String> e: m_structures.entrySet()){
			HashMap<String, Double> tmp = extractNodeCardinalityFeatureFromSolution(e.getValue());
			for (Map.Entry<String, Double> e2: tmp.entrySet()) {
				m_features.get(e2.getKey()).put(e.getKey(), e2.getValue());
			}
		}
	}
	
	
	/**
	 * @param solStr Encoding of the alloy solution 
	 * @return A map of cardinalities indexed by node type name (form [C]x, where x is node type)
	 */
	public HashMap<String,Double> extractNodeCardinalityFeatureFromSolution(String solStr) {
		HashMap<String,Double> res = new HashMap<String,Double>();
		LinkedList<String> node_names = extractNodeFeatureNames();
		for (int i=0;i<node_names.size();i++) {
			res.put(cardinalityIdString+node_names.get(i).split("\\$")[0], 0.0);
		}
		String ntype = "";
		AlloySolution sol = new AlloySolution();
		sol.loadFromString(solStr);
		for (AlloySolutionNode node : sol.getNodes().values()) {
			ntype = cardinalityIdString + node.getId().split("\\$")[0];
//			System.out.println (ntype);
			res.put(ntype, res.get(ntype)+1.0);
		}
		return res;		
	}
	
	
	public void extractArcCardinalityFeatures() {
		for (Map.Entry<String,String> e: m_structures.entrySet()) {
			HashMap<String, Double> tmp = extractArcCardinalityFeatureFromSolution(e.getValue());
			for (Map.Entry<String, Double> e2: tmp.entrySet()) {
				m_features.get(e2.getKey()).put(e.getKey(), e2.getValue());
			}
		}
	}
	
	public HashMap<String,Double> extractArcCardinalityFeatureFromSolution(String solStr){
		HashMap<String,Double> res = new HashMap<String,Double>();
		LinkedList<String> arc_names = extractArcFeatureNames();
		String[] linkparts = {""};
		String ntype = "";
		for (int i=0;i<arc_names.size();i++) {
			linkparts= arc_names.get(i).split("-");
	    	ntype = cardinalityIdString + linkparts[0]+"-"+linkparts[1].split("\\$")[0]+"-"+linkparts[2].split("\\$")[0];
	    	res.put(ntype, 0.0);
		}
		AlloySolution sol = new AlloySolution();
		sol.loadFromString(solStr);
		for (AlloySolutionArc arc: sol.getArcs().values()) {
			linkparts= arc.getId().split("-");
	    	ntype = cardinalityIdString + linkparts[0]+"-"+linkparts[1].split("\\$")[0]+"-"+linkparts[2].split("\\$")[0];
//			System.out.println (ntype);
	    	res.put(ntype, res.get(ntype)+1.0);
		}		
		return res;
	}
	
	
	/**
	 * Populates part of m_features and m_feature_names with information about
	 * the presence of arcs and nodes in solutions
	 */
	public void extractNodeAndArcPresenceFeatures() {
		for (Map.Entry<String, String> e: m_structures.entrySet()){
		    int nnodes = extractNodeFeatureNames().size();
			AlloySolution sol = new AlloySolution();
			sol.loadFromString(e.getValue());
			for (int i=0; i<m_feature_names.size();i++) {
				if (i<nnodes) {
			    	if (sol.getNodes().containsKey(m_feature_names.get(i))) {
			    		m_features.get(m_feature_names.get(i)).put(e.getKey(),1.0d);
			    	}
			    	else { 
			    		m_features.get(m_feature_names.get(i)).put(e.getKey(),0.0d);
			    		}
				} else {
			       	if (sol.getArcs().containsKey(m_feature_names.get(i))) {
			    		m_features.get(m_feature_names.get(i)).put(e.getKey(),1.0d);
			    	} else { 
			    		m_features.get(m_feature_names.get(i)).put(e.getKey(),0.0d);
			    		} 	
					}
		       	}
		}
	}
		
	/**
	 * @return A list of feature names corresponding to the presence of nodes in a solution
	 */
	public LinkedList<String> extractNodeFeatureNames() {
		return extractNodeFeatureNames("");
	}

	public LinkedList<String> extractNodeFeatureNames(String prefix) {
		LinkedList<String> res = new LinkedList<String>();
		for (Map.Entry<String, String> e: m_structures.entrySet()){
			AlloySolution sol = new AlloySolution();
			sol.loadFromString(e.getValue());
		    for (AlloySolutionNode node : sol.getNodes().values())
		    	if (!res.contains(prefix+node.getId())){
		    		res.add(prefix+node.getId());
		    	}		
		
		    
		}
		return res;
	}
	
	/**
	 * @return A list of feature names corresponding to the presence of arc in a solution
	 */
	public LinkedList<String> extractArcFeatureNames() {
		LinkedList<String> res = new LinkedList<String>();
		for (Map.Entry<String, String> e: m_structures.entrySet()){
			AlloySolution sol = new AlloySolution();
			sol.loadFromString(e.getValue());
		    for (AlloySolutionArc arc : sol.getArcs().values())
		    	if (!res.contains(arc.getId())){
		    		res.add(arc.getId());
		    	}				
		}
		return res;
	}
	
	
	/**
	 * @return The list of feature names
	 */
	public LinkedList<String> getFeatureNames(){
		return m_feature_names;
	}
	
	/**
	 * @return A tab-separated string with all the feature names
	 */
	public String getFeatureNamesString() {
		String res ="";
		for (int i=0;i<getFeatureNames().size();i++) {
			res += "\t"+getFeatureNames().get(i);
		}
		return res;
	}
	
	/**
	 * @param solId Id of the Alloy solution 
	 * @param fId Name of the feature
	 * @return The value of a feature for a given alloy solution
	 */
	public String getFeatureValueString (String solId, String fId) {
		return m_features.get(fId).get(solId).toString();
	}
	
	
	/**
	 * @param solId Id of the Alloy solution
	 * @return A tab-separated string with all feature values for a given alloy solution
	 */
	public String getFeatureValuesString(String solId) {
		String res="";
		for (int i=0;i<m_feature_names.size();i++) {
			res +="\t"+getFeatureValueString(solId,m_feature_names.get(i));
		}
		return res;
	}
	
	public static void main(String[] args) {
		
	}
	
}
