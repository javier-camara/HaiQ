package org.jcm.haiq.solve;


import java.util.HashMap;
import java.util.Objects;
import java.io.File;

import org.jcm.util.*;

import java.util.LinkedList;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.jcm.haiq.analyze.*;



public class ScoreBoard {
	private HashMap<String, HashMap<String, String>> m_results = new HashMap<String, HashMap<String, String>>();
	private LinkedList<String> m_propids = new LinkedList<String>();
	
	/**
	 * @return the m_results
	 */
	public HashMap<String, HashMap<String, String>> getResults() {
		return m_results;
	}


	
	public void addSolutionforProperty(String config, String prop, String res){
		addSolutionforProperty (config, prop, res, "");	
	}
		
	public void addSolutionforProperty(String configId, String property, String result, String constants){
		String config = configId+"-"+constants;
		if (!m_results.keySet().contains(config)){
			m_results.put(config, new HashMap<String, String>());
		}	
		if (!m_propids.contains(property))
			m_propids.add(property);
		m_results.get(config).put(property, result);
	}
	

	public String generateScoreboardText(){
		return generateScoreboardText(true, true, "\t", "\n");
	}
	
	public String generateScoreboardText(boolean generateIds, boolean generateHeader, String colSep, String rowSep){
		String res = "";
		boolean generate_heading = generateHeader;	
		for (Map.Entry<String, HashMap<String, String>> e: m_results.entrySet()){
			if (generate_heading) {
				for (Map.Entry<String, String> eh: m_results.get(e.getKey()).entrySet())
					res +=colSep+eh.getKey();
				res += rowSep;
				generate_heading = false;
			}
			if (generateIds) 
				res += e.getKey();
			for (Map.Entry<String, String> e2: m_results.get(e.getKey()).entrySet()){	
				res +=colSep+e2.getValue();
			}
			res += rowSep;
		}
		return res;
	}


	public String generateScoreboardPCAText(HashMap<String, String> sols){
		return generateScoreboardPCAText(true, true, "\t", "\n", sols);
	}

	
	
	
	public String generateScoreboardPCAText(boolean generateIds, boolean generateHeader, String colSep, String rowSep, HashMap<String, String> sols){
		String res = "";
		boolean generate_heading = generateHeader;
		FeatureExtraction fx = new FeatureExtraction(sols);
		fx.extractFeatures();
		for (Map.Entry<String, HashMap<String, String>> e: m_results.entrySet()){
			String solId = e.getKey().split("-")[0];
			String constStr = e.getKey().split("-")[1];
			String[] consts = constStr.split(",");
			if (generate_heading) {
				for (Map.Entry<String, String> eh: m_results.get(e.getKey()).entrySet())
					res +=colSep+eh.getKey();
				res += fx.getFeatureNamesString(); // Add heading for topological feature names
				
				// Generate headings for parameters (constants)
				for (int i=0;i<consts.length;i++) {
					res += colSep+consts[i].split("=")[0];
				}
				
				res += rowSep;
				generate_heading = false;
			}
			if (generateIds) 
				res += e.getKey();
			for (Map.Entry<String, String> e2: m_results.get(e.getKey()).entrySet()){	
				res +=colSep+e2.getValue();
			}
			
			res += fx.getFeatureValuesString(solId); // Values for topological feature names
			
			// Generate values for parameters (constants)
			for (int i=0;i<consts.length;i++) {
				res += colSep+consts[i].split("=")[1];
			}
					
			res += rowSep;
		}
		return res;
	}
	
	public void generateScoreboardPCA(String filename, HashMap<String, String> sols){
		TextFileHandler fhdata = new TextFileHandler(filename+".csv");
		fhdata.exportFile(generateScoreboardPCAText(sols));
	}

	
	public void generateScoreboardJSON(String filename){
		TextFileHandler fhdata = new TextFileHandler(filename+".json");
		fhdata.exportFile(getScoreboardJSONString());
	}

	public String getScoreboardJSONString(){
		JSONArray sbentries = new JSONArray();
		for (Map.Entry<String, HashMap<String, String>> e: m_results.entrySet()){
			
			JSONObject entry = new JSONObject();
			JSONArray prop_results = new JSONArray();
			
			for (Map.Entry<String, String> e2: m_results.get(e.getKey()).entrySet()){	
				JSONObject auxobj = new JSONObject();
				auxobj.put(e2.getKey(), e2.getValue());
				prop_results.add(auxobj);
			}
			entry.put(e.getKey(), prop_results);
			sbentries.add(entry);
		}
		return sbentries.toJSONString();
	}
	
	public double getMin(String k){
		double minD = Double.MAX_VALUE;
		double aux;
		for (Map.Entry<String, HashMap<String, String>> e: m_results.entrySet()){
			aux = Double.parseDouble(m_results.get(e.getKey()).get(k));
			if (aux< minD)
				minD = aux;
		}
		return minD;
	}
	
	public double getMax(String k){
		double maxD = Double.MIN_VALUE;
		double aux;
		for (Map.Entry<String, HashMap<String, String>> e: m_results.entrySet()){
			aux = Double.parseDouble(m_results.get(e.getKey()).get(k));
			if (aux > maxD)
				maxD = aux;
		}
		return maxD;
	}
	

	public String generateConfsMapData(String k1, String k2, String k3){
		String res = "";
		String maintxt = "polygons={";
		boolean first = true;
		for (Map.Entry<String, HashMap<String, String>> e: m_results.entrySet()){
			res += generateConfMapData(e.getKey(), k1, k2, k3);
			if (res.contains("\n"+e.getKey()+"polygons = [")){
				if (!first)
					maintxt +=", ";
				maintxt += "\""+e.getKey()+"\""+":"+e.getKey()+"polygons";
				first = false;
			}
		}
		maintxt += "}\n";
		res =  res + "\n" + maintxt;
//		System.out.println(res);
		return res;
	}
	
	public String generateConfMapData(String confid, String k1, String k2, String k3){
		String res = "";
	
			double minX = getMin(k1);
			double maxX = getMax(k1);
			double stepX = (maxX-minX)/20;

			double minY = getMin(k2);
			double maxY = getMax(k2);
			double stepY = (maxY-minY)/20;

			double i, j;
			int count = 0;
			for (i=minX; i<maxX; i+=stepX){				
				for (j=minY; j<maxY; j+=stepY){
					if ( Objects.equals(getConfigKeyZConstrained(i,j, k1, k2, k3),confid)){
						res = res + confid+"polygon"+ String.valueOf(count)+"= Polygon(["+
								"("+ String.valueOf(i-stepX)+"," + String.valueOf(j-stepY) +"), " + 
								"("+ String.valueOf(i-stepX)+"," + String.valueOf(j+stepY) +"), " +
								"("+ String.valueOf(i+stepX)+"," + String.valueOf(j+stepY) +"), " + 
								"("+ String.valueOf(i+stepX)+"," + String.valueOf(j-stepY) +"), " + 
								"("+ String.valueOf(i-stepX)+"," + String.valueOf(j-stepY) +")" 
								+"])\n";
						count++;
					}
				}
			}
			
			if (count==0) 
				return res;
			
			res += res + "\n"+confid+"polygons = [";
			for (int c=0; c<count; c++){
				if (c>0) 
					res +=",";
				res+= confid+"polygon"+String.valueOf(c);
			}
			res +="]\n";
	
		return res;
	}
	

	public String generateScoreboardData(String k1, String k2, String k3, String colSep, String rowSep){
		String res = "";
	
			double minX = getMin(k1);
			double maxX = getMax(k1);
			double stepX = (maxX-minX)/20;
			System.out.println("Xmin: "+String.valueOf(minX)+" Xmax: "+ String.valueOf(maxX));
			
			double minY = getMin(k2);
			double maxY = getMax(k2);
			double stepY = (maxY-minY)/20;
			System.out.println("Ymin: "+String.valueOf(minY)+" Ymax: "+ String.valueOf(maxY));

			double i, j;
			for (i=minX; i<maxX; i+=stepX){				
				for (j=minY; j<maxY; j+=stepY){
					double zconst = getMinZConstrained(i,j, k1, k2, k3);
					if (zconst < Double.MAX_VALUE)
						res += String.valueOf(i) + colSep + String.valueOf(j) + colSep + String.valueOf(zconst) + rowSep;
				}
			}
		return res;
	}


	
	
	
	public double getMinZConstrained(double bx, double by, String k1, String k2, String k3){
		double minD = Double.MAX_VALUE;
		String po="";
		String kz="";
		for (Map.Entry<String, HashMap<String, String>> e: m_results.entrySet()){
			double x = Double.parseDouble(e.getValue().get(k1));
			double y = Double.parseDouble(e.getValue().get(k2));
			double z = Double.parseDouble(e.getValue().get(k3));
			if ( (x > bx) && (y < by) && (z < minD) ){
				minD = z;
				kz = e.getKey();
			}
		}
		po = kz + " ";
		//if (m_results.containsKey(kz))
		//	for (Map.Entry<String, String> e2: m_results.get(kz).entrySet()){	
		//			po += e2.getKey()+":"+e2.getValue()+" ";
		//		}
		//System.out.println(po);
		System.out.println("for : [x: "+String.valueOf(bx)+", y: "+String.valueOf(by)+"] minZ:" + String.valueOf(minD));
		return minD;	
	}
	
	
	public String getConfigKeyZConstrained(double bx, double by, String k1, String k2, String k3){
		double minD = Double.MAX_VALUE;
		String kz="";
		for (Map.Entry<String, HashMap<String, String>> e: m_results.entrySet()){
			double x = Double.parseDouble(e.getValue().get(k1));
			double y = Double.parseDouble(e.getValue().get(k2));
			double z = Double.parseDouble(e.getValue().get(k3));
			if ( (x > bx) && (y < by) && (z < minD) ){
				minD = z;
				kz = e.getKey();
			}
		}		
		return kz;	
	}
	
	
	public void generateScatterPGFPlot3D (String filename){
		String res = "\\documentclass{article}\n\\usepackage{tikz}\n\\usepackage{pgfplots}\n\\usetikzlibrary{shadows}\n\\begin{document}\n\n";
		res+="\\begin{tikzpicture}\n\\begin{axis}[";
		String[] plotlabels = {"x", "y", "z"}; 
		String coordAllocStr = "";
		for (int i=0; i < Integer.max(plotlabels.length,m_propids.size());i++){
			res+= plotlabels[i]+"label="+m_propids.get(i)+",";
			coordAllocStr += plotlabels[i]+"="+m_propids.get(i)+",";
		}
		res+=  "]\n";
		String filenameStr = filename.substring(filename.lastIndexOf(File.separator)+1);
		res+="\\addplot3[only marks,mark=cube*,mark size=3, color=green, draw=black, opacity=0.6] table["+coordAllocStr+"] {"+filenameStr+".dat"+"};\n";
		res+="\\end{axis}\n\\end{tikzpicture}\n\\end{document}\n";
		
		TextFileHandler fhplot = new TextFileHandler(filename+".tex");
		fhplot.exportFile(res);
		TextFileHandler fhdata = new TextFileHandler(filename+".dat");
		fhdata.exportFile(generateScoreboardText());
	}

	public void generatePGFPlotSurfaceData (String filename){
		TextFileHandler fhdata = new TextFileHandler(filename);
//		fhdata.exportFile(generateScoreboardData("safety", "time", "energy", "\t", "\n"));
		fhdata.exportFile(generateScoreboardData("reliability", "responseTime", "cost", "\t", "\n"));
	}

	public void generatePGFMapData (String filename){
		TextFileHandler fhdata = new TextFileHandler(filename);
		fhdata.exportFile(generateConfsMapData("reliability", "responseTime", "cost"));
	}
	
}
