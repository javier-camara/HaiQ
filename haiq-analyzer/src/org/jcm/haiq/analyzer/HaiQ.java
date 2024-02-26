package org.jcm.haiq.analyzer;

import org.jcm.haiq.parse.ParserMark1;
import org.jcm.haiq.solve.HQSolverIterative;
import org.jcm.haiq.solve.HQSolverCombined;
import org.jcm.haiq.solve.HQSolverEvoChecker;
import org.jcm.haiq.solve.HQSolver;
import org.jcm.haiq.solve.ScoreBoard;
import org.jcm.voyagerserver.VoyagerServer;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;

/**
 * @author jcamara
 * HaiQ Analyzer
 */

public class HaiQ {

	public static enum modes {MODE_ITERATIVE, MODE_COMBINED, MODE_EVOCHECKER};
	
	private static String VERSION_STR = "HaiQ Relational Probabilistic Model Analyzer v0.2a";
	private static LinkedList<String> m_properties = new LinkedList<String>();
	private static String m_model;
	private static String m_engine="hybrid";
	private static modes  m_mode=modes.MODE_ITERATIVE;
	private static boolean m_showscoreboard;
	private static boolean m_verbose;
	private static boolean m_exportscoreboardJSON;
	private static boolean m_exportfeatures;
	private static boolean m_exportconfigurationsJSON;
	private static boolean m_exportconfigurationsTIKZ;
	private static boolean m_exportpolicies;
	private static boolean m_exportpoliciesBRASS;
	private static boolean m_exportscatter;
	private static boolean m_exportsurfacedata;
	private static boolean m_exportmapdata;
	private static boolean m_set_max_configs;
	private static boolean m_skip_model_checking;
	private static boolean m_voyager_server;
	private static boolean m_export_models;
	private static boolean m_show_stats;
	private static int m_max_configs;
	private static String m_scatter_output;
	private static String m_scoreboardJSON_output;
	private static String m_features_output;
	private static String m_configurationsJSON_output;
	private static String m_configurationsTIKZ_output;
	private static String m_policies_output;
	private static ArrayList<String> m_policies_export_params = new ArrayList<String>();
	private static String m_surfacedata_output;
	private static String m_mapdata_output;
	private static String m_consts_string;
	private static String m_tmp_path;
	
	
	
	
	/**
	 * Unpacks the list of properties to check into m_properties
	 * @param s
	 */
	public static void setPropertyList(String s){
		String[] propIndexes = s.trim().split(",");
		for (int i=0; i<propIndexes.length;i++)
			m_properties.add(propIndexes[i]);
	}
	
	
	/**
	 * Processes parameters for run-time configuration
	 * @param args
	 */
	public static void processParameters(String[] args){
		boolean propertiesSet=false;
		boolean modelSet=false;
		boolean showHelp=false;
		boolean showVersion=false;
		String paramStr="";
		for (int i=0; i<args.length; i++){
			paramStr=args[i];
			if (paramStr.startsWith("-properties[")){
				setPropertyList(paramStr.split("\\[")[1].replace("]", ""));
				propertiesSet=true;
			}
			if (paramStr.startsWith("-model[")){
				m_model=paramStr.split("\\[")[1].replace("]", "");
				modelSet=true;
			}
			if (Objects.equals(paramStr,"-showScoreboard")){
				m_showscoreboard=true;
			}
			if (paramStr.startsWith("-setMaxConfigs[")){
				m_max_configs=Integer.parseInt(paramStr.split("\\[")[1].replace("]", ""));
				m_set_max_configs=true;
			}
			if (paramStr.startsWith("-exportScoreboardJSON[")){
				m_exportscoreboardJSON=true;
				m_scoreboardJSON_output=paramStr.split("\\[")[1].replace("]", "");
			}
			if (paramStr.startsWith("-exportFeatures[")){
				m_exportfeatures=true;
				m_features_output=paramStr.split("\\[")[1].replace("]", "");
			}
			if (paramStr.startsWith("-exportConfigurationsJSON[")){
				m_exportconfigurationsJSON=true;
				m_configurationsJSON_output=paramStr.split("\\[")[1].replace("]", "");
			}
			if (paramStr.startsWith("-exportConfigurationsTIKZ[")){
				m_exportconfigurationsTIKZ=true;
				m_configurationsTIKZ_output=paramStr.split("\\[")[1].replace("]", "");
			}
			if (paramStr.startsWith("-exportPolicies[")){
				m_exportpolicies=true;
				m_policies_output=paramStr.split("\\[")[1].replace("]", "");
			}
			if (paramStr.startsWith("-exportPoliciesBRASS[")){
				m_exportpoliciesBRASS=true;
				String paramAuxStr=paramStr.split("\\[")[1].replace("]", "");
				m_policies_output = paramAuxStr.split(",")[0];
				m_policies_export_params.add(paramAuxStr.split(",")[1]); 
			}
			if (paramStr.startsWith("-exportScatterPlot[")){
				m_exportscatter=true;
				m_scatter_output=paramStr.split("\\[")[1].replace("]", "");
			}
			if (paramStr.startsWith("-exportSurfacePlotData[")){
				m_exportsurfacedata=true;
				m_surfacedata_output=paramStr.split("\\[")[1].replace("]", "");
			}
			if (paramStr.startsWith("-mode[")){
				String modeStr=paramStr.split("\\[")[1].replace("]", "");
				if (Objects.equals(modeStr,"combined"))
					m_mode = modes.MODE_COMBINED;
				if (Objects.equals(modeStr,"evochecker"))
					m_mode = modes.MODE_EVOCHECKER;
			}
			if (paramStr.startsWith("-engine[")){
				m_engine=paramStr.split("\\[")[1].replace("]", "");
			}
			if (paramStr.startsWith("-exportMapData[")){
				m_exportmapdata=true;
				m_mapdata_output=paramStr.split("\\[")[1].replace("]", "");
			}
			if (paramStr.startsWith("-consts[")){
				m_consts_string=paramStr.split("\\[")[1].replace("]", "");
			}
			if (Objects.equals(paramStr,"-verbose")){
				m_verbose=true;
			}
			if (Objects.equals(paramStr,"-h") || Objects.equals(paramStr,"-help")){
				showHelp=true;
			}
			if (Objects.equals(paramStr,"-version")){
				showVersion=true;
			}
			if (Objects.equals(paramStr,"-skipModelChecking")){
				m_skip_model_checking=true;
			}
			if (Objects.equals(paramStr,"-exportModels")){
				m_export_models=true;
			}
			if (Objects.equals(paramStr,"-showStats")){
				m_show_stats=true;
			}
			if (Objects.equals(paramStr,"-runVoyagerServer")){
				m_voyager_server=true;
			}

			
		}
		
		if (!modelSet && !showHelp && !showVersion){
			System.out.println("Model file not specified.");
			System.out.println("Use switch -h for help.");
			System.exit(-1);
		}			
		if (!propertiesSet  && !showHelp && !showVersion){
			System.out.println("No properties to check specified.");
			System.out.println("Use switch -h for help.");
			System.exit(-1);
		}
		if(showVersion){
			System.out.println(VERSION_STR);
			System.exit(0);
		}
		if(showHelp){
			System.out.println("Usage: haiq -model[<model-file>] -properties[<property-list>] [options]\n"
				    +          "Example:haiq -model[foo.haiq] -properties[0,1] -verbose \n\n"
				    +          "Options:\n\n"
					+          "-help ...................................... Display this help message\n"
					+          "-version ................................... Display tool version\n\n"
					+          "-verbose ................................... Display debug information\n"
					+          "-consts[<const-vals>] ...................... Defines constant values for experiments\n"
					+          "-setMaxConfigs[<val>] ...................... Constrains the generation of configurations to a maximum of <val>\n"
					+          "-showScoreboard ............................ Displays property values for all configurations\n"
					+          "-exportScoreboardJSON[<file>] .............. Exports property values for all configurations to a JSON file.\n"
					+          "-exportFeatures[<file>] .................... Exports property values and attributes for all configurations to a CSV file.\n"
					+          "-exportConfigurationsJSON[<path>] .......... Exports all configurations to JSON files in <path>.\n"
					+          "-exportConfigurationsTIKZ[<path>] .......... Exports all configurations to LaTeX tikz/pgfplots files in <path>.\n"
					+          "-exportModels .............................. Exports all models to files in default temp folder.\n"
					+          "-showStats ................................. Shows number of lines, commands, and preds generated.\n"
					+          "-exportPolicies[<path>] .................... Exports all policies to files in <path>.\n"
					+          "-exportPoliciesBRASS[<path>,<start>]........ Exports all policies to files in <path> (BRASS format).\n"
					+          "-engine[explicit | hybrid] ................. Sets the engine used for probabilistic model checking.\n"
					+          "-mode[iterative | combined | evochecker ] .. Sets the analysis mode for probabilistic model checking.\n"
					+          "-skipModelChecking .................... Does not carry out model checking (e.g., only used for generating structures).\n"
					+          "-runVoyagerServer ..................... Runs a Voyager compatible HTTP server for trade-off analysis\n"
					
					//					+          "-exportScatterPlot[<file>] ..... Exports a PGFplots scatter plot of configurations to <file>\n"
//					+          "-exportSurfacePlotData[<file>] . Exports a PGFplots surface plot data to <file>\n"
				    +"\n");
			System.exit(0);
		}
	}

	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
	       
		// Read environment variables
		Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            //System.out.format("%s=%s%n", envName, env.get(envName));
            if (Objects.equals(envName, "HAIQ_TMP_PATH")){
            	m_tmp_path = env.get(envName);
            	//System.out.println(m_tmp_path+"/tmp.als");
            }
        }
		
		
		// Hello
		System.out.println("\n"+VERSION_STR+"\n============* * * * ==============================");

		// Process params
		processParameters(args);
		String modelFile = m_model;
		System.out.println("Model input: "+ modelFile);
		// Create parser and load model file
		ParserMark1 p = new ParserMark1();
		if (m_verbose)
			p.setDebugMode(true);
		p.loadSource(modelFile);		
		// Parse file and generate alloy output to temp file
		p.parse();
		p.saveAlloyToFile(m_tmp_path+"/temp.als");		
		
		// Generate behavioral models for S-Set 
		System.out.print("Generating S-Set... ");
		
		HQSolver hqs = null;
		
		if (Objects.equals(m_mode, modes.MODE_ITERATIVE)) {
			hqs = (HQSolverIterative) new HQSolverIterative(m_tmp_path, m_engine);
		} 
		if (Objects.equals(m_mode, modes.MODE_COMBINED)) {
			hqs = (HQSolverCombined) new HQSolverCombined(m_tmp_path, m_engine);
		} 
		if (Objects.equals(m_mode, modes.MODE_EVOCHECKER)) {
			hqs = (HQSolverEvoChecker) new HQSolverEvoChecker(m_tmp_path);
		} 
		
		if (m_set_max_configs)
			hqs.setMaxConfigs(m_max_configs);
		if (m_export_models)
			hqs.setExportModels(true);
		if (m_exportpolicies)
			hqs.setExportPolicies(true);
		Long st = System.nanoTime();
		int sol_count = hqs.generateSolutions(m_tmp_path+"/temp.als", p.getBehavioralModel());
		Long et = System.nanoTime();
		System.out.println(String.valueOf(sol_count)+" solutions generated in "+String.valueOf((double)(et-st)/ 1000000000.0)+" seconds.");
		
		// Set constant definitions for the solver
		if (!Objects.equals(m_consts_string, null)){
				//System.out.println(m_consts_string);
				hqs.setConstantDefinitions(m_consts_string);
		}
		// Check solution to property
		if (!m_skip_model_checking)
			for (String propIndex: m_properties) {
				int propertyIndex = Integer.parseInt(propIndex);
				if (p.getProperties().size()>propertyIndex){
					System.out.print("\nModel checking "+ p.getProperties().get(propertyIndex).getId() +" on the S-Set: ");
					st = System.nanoTime();
					String res = hqs.runExperiment(p.getProperties().get(propertyIndex), p.getBehavioralModel());
					et = System.nanoTime();
					System.out.println("\nTime for model checking: "+String.valueOf((double)(et-st)/ 1000000000.0)+" seconds.");
					System.out.println("Result for M-PCTL property "+ p.getProperties().get(propertyIndex).getId()  + " is: \n"+res);
				} else {
					System.out.println("No properties to analyze.");
				}
			}
		
		if (m_showscoreboard)
			System.out.println("\nSCOREBOARD\n=========\n" + ((ScoreBoard) hqs.getScoreBoard()).generateScoreboardText());
		
		if (m_exportscatter && p.getProperties().size()>2)
			((ScoreBoard) hqs.getScoreBoard()).generateScatterPGFPlot3D(m_scatter_output);

		if (m_exportsurfacedata && p.getProperties().size()>2)
			((ScoreBoard) hqs.getScoreBoard()).generatePGFPlotSurfaceData(m_surfacedata_output);

		if (m_exportmapdata && p.getProperties().size()>2)
			((ScoreBoard) hqs.getScoreBoard()).generatePGFMapData(m_mapdata_output);

		if (m_exportscoreboardJSON)
			((ScoreBoard) hqs.getScoreBoard()).generateScoreboardJSON(m_scoreboardJSON_output);

		if (m_exportfeatures)
			((ScoreBoard) hqs.getScoreBoard()).generateScoreboardPCA(m_features_output, hqs.getConfigurations());

		if (m_exportconfigurationsJSON){
			hqs.exportConfigurations(m_configurationsJSON_output, HQSolverIterative.StructureExport.JSON);
		}

		if (m_exportconfigurationsTIKZ){
			hqs.exportConfigurations(m_configurationsTIKZ_output, HQSolverIterative.StructureExport.TIKZ);
		}

		if (m_exportpolicies){
			hqs.exportPolicies(m_policies_output, HQSolverIterative.PolicyExport.PLAINTEXT, m_policies_export_params);
		}

		if (m_show_stats){
			System.out.println("Input code\n----------------\n");
			System.out.println("Number of lines: " + p.getM_n_total_lines());
			System.out.println("Number of Alloy lines: " + p.getM_n_alloy_lines()+"("+Double.valueOf(p.getM_n_alloy_lines())/Double.valueOf(p.getM_n_total_lines())+")");
			System.out.println("Number of Prism lines: " + p.getM_n_prism_lines()+"("+Double.valueOf(p.getM_n_prism_lines())/Double.valueOf(p.getM_n_total_lines())+")");
			System.out.println ("Number of signatures: " + p.getM_n_signatures());
			System.out.println ("Number of commands: " + p.getM_n_commands());
			System.out.println ("Number of formulas: " + p.getM_n_formulas());
			System.out.println("\nGenerated code\n----------------\n");
			int n_commands = hqs.getTotalCommandsGenerated();
			int n_lines = hqs.getTotalLinesGenerated();
			int n_formulas = hqs.getTotalFormulasGenerated();
			int n_solutions = hqs.getTotalSolutions();
			int n_modules = hqs.getTotalModulesGenerated();
			Double n_commands_s =  Double.valueOf(n_commands) / Double.valueOf(n_solutions);
			Double n_formulas_s =  Double.valueOf(n_formulas) / Double.valueOf(n_solutions);
			Double n_lines_s =  Double.valueOf(n_lines) / Double.valueOf(n_solutions);
			Double n_modules_s =  Double.valueOf(n_modules) / Double.valueOf(n_solutions);
			System.out.println ("Number of solutions: " + n_solutions);
			System.out.println ("Number of lines: " + n_lines);
			System.out.println ("Number of modules: " + n_modules);
			System.out.println ("Number of commands: " + n_commands);
			System.out.println ("Number of formulas: " + n_formulas);
			System.out.println ("Number of lines/solution: "+n_lines_s);
			System.out.println ("Number of modules/solution: "+n_modules_s);
			System.out.println ("Number of commands/solution: "+n_commands_s);
			System.out.println ("Number of formulas/solution: "+n_formulas_s);

			System.out.println("\nRatios \n----------------\n");
			System.out.println("Signature/module ratio: "+Double.valueOf(p.getM_n_signatures())/Double.valueOf(n_modules));
			System.out.println("Line reduction: "+Double.valueOf(1-(p.getM_n_total_lines())/Double.valueOf(n_lines)));
			System.out.println("Command reduction: "+Double.valueOf(1-(p.getM_n_commands())/Double.valueOf(n_commands)));
			System.out.println("Formula reduction: "+Double.valueOf(1-(p.getM_n_formulas())/Double.valueOf(n_formulas)));
			
			System.out.println("Line reduction (per sol): "+Double.valueOf(1-(p.getM_n_total_lines())/Double.valueOf(n_lines_s)));
			System.out.println("Command reduction (per sol): "+Double.valueOf(1-(p.getM_n_commands())/Double.valueOf(n_commands_s)));					
			System.out.println("Formula reduction (per sol): "+Double.valueOf(1-(p.getM_n_formulas())/Double.valueOf(n_formulas_s)));
			System.out.println("Signature/module ratio (per sol): "+Double.valueOf(p.getM_n_signatures())/Double.valueOf(n_modules_s));
			System.out.println("\n\n"+tikzStatsGraph(p,hqs));
			System.out.println("\n---------------");
			System.out.println("\n\n"+statsLine(p,hqs));
		}

		if (m_exportpoliciesBRASS){
			hqs.exportPolicies(m_policies_output, HQSolverIterative.PolicyExport.BRASS_PLAINTEXT, m_policies_export_params );
		}
		
		if (m_voyager_server) {
			VoyagerServer server = new VoyagerServer(); 
			String json = ((ScoreBoard) hqs.getScoreBoard()).getScoreboardJSONString();
			server.Start(json);
		} else {
			System.out.flush(); // bye
		}
	}

	public static String statsLine(ParserMark1 p, HQSolver hqs) {
		DecimalFormat df = new DecimalFormat("#.##");
		String res = "";
		res += p.getM_n_total_lines()+ " & ";
		res += p.getM_n_alloy_lines()+" ("+df.format( Double.valueOf(p.getM_n_alloy_lines())/Double.valueOf(p.getM_n_total_lines()))+") &";
		res += p.getM_n_prism_lines()+" ("+df.format( Double.valueOf(p.getM_n_prism_lines())/Double.valueOf(p.getM_n_total_lines()))+") &";
		res += p.getM_n_signatures()+ " & ";
		res += p.getM_n_commands() + " & ";
		res += p.getM_n_formulas() + " & ";
		int n_commands = hqs.getTotalCommandsGenerated();
		int n_lines = hqs.getTotalLinesGenerated();
		int n_formulas = hqs.getTotalFormulasGenerated();
		int n_solutions = hqs.getTotalSolutions();
		int n_modules = hqs.getTotalModulesGenerated();
		Double n_commands_s =  Double.valueOf(n_commands) / Double.valueOf(n_solutions);
		Double n_formulas_s =  Double.valueOf(n_formulas) / Double.valueOf(n_solutions);
		Double n_lines_s =  Double.valueOf(n_lines) / Double.valueOf(n_solutions);
		Double n_modules_s =  Double.valueOf(n_modules) / Double.valueOf(n_solutions);
		res += n_solutions + " & ";
		res += n_lines + " & ";
		res += n_modules + " & ";
		res += n_commands + " & ";
		res += n_formulas + " & ";
		res += n_lines_s + " & ";
		res += n_commands_s + " & ";
		res += df.format(Double.valueOf(p.getM_n_signatures())/Double.valueOf(n_modules)) + " & ";
		res += df.format(Double.valueOf(p.getM_n_total_lines())/Double.valueOf(n_lines)) + " & ";
		res += df.format(Double.valueOf(p.getM_n_commands())/Double.valueOf(n_commands)) + " & ";
		res += df.format(Double.valueOf(p.getM_n_signatures())/Double.valueOf(n_modules)) + " & ";
		res += df.format(Double.valueOf(p.getM_n_formulas())/Double.valueOf(n_formulas)) + " & ";
		res += df.format(Double.valueOf(p.getM_n_total_lines())/Double.valueOf(n_lines_s)) + " & ";
		res += df.format(Double.valueOf(p.getM_n_commands())/Double.valueOf(n_commands_s)) + " & ";					
		res += df.format(Double.valueOf(p.getM_n_formulas())/Double.valueOf(n_formulas_s)) + " & ";
		res += df.format(Double.valueOf(p.getM_n_signatures())/Double.valueOf(n_modules_s)) + " & ";
		res += "\\\\\n";
		return res;
	}
	
	public static String tikzStatsGraph(ParserMark1 p, HQSolver hqs) {
		
		int n_commands = hqs.getTotalCommandsGenerated();
		int n_lines = hqs.getTotalLinesGenerated();
		int n_formulas = hqs.getTotalFormulasGenerated();
		int n_solutions = hqs.getTotalSolutions();
		Double n_commands_s =  Double.valueOf(n_commands) / Double.valueOf(n_solutions);
		Double n_formulas_s =  Double.valueOf(n_formulas) / Double.valueOf(n_solutions);
		Double n_lines_s =  Double.valueOf(n_lines) / Double.valueOf(n_solutions);
		String res = "\\begin{tikzpicture}\n";
		res+="\\begin{axis}[width=7.5cm, height=3.5cm,style={font=\\scriptsize},\n";
				res+="\t    ybar,\n";
//				res+="\t enlargelimits=0.15,\n";
				res+="\t legend style={at={(0.5,1.2)},\n";
				res+="\t anchor=north,legend columns=3},\n";
//				res+="\t ylabel={Count},\n";
				res+="\t ymin=0,\n";
				res+="\t ymode=log,\n";
				res+="\t ylabel style={at={(0.2,0.5)}},\n";
				res+="\t symbolic x coords={ln,cmd,form},\n";
				res+="\t ymajorgrids,\n";
				res+="\t enlarge x limits={0.2},\n";
//				res+="\t enlarge y limits={0.1},\n";
//				res+="\t title={Stats},\n";
				res+="\t ]\n";
				res+="\t \\addplot[color=black,fill=gray] coordinates {(ln,"+p.getM_n_total_lines()+") (cmd,"+p.getM_n_commands()+") (form,"+p.getM_n_formulas()+")};\n";
				res+="\t \\addplot[color=black,fill=white] coordinates {(ln,"+n_lines_s+") (cmd,"+n_commands_s+") (form,"+n_formulas_s+")};\n";
				res+="\t \\addplot[color=black,fill=black] coordinates {(ln,"+n_lines+") (cmd,"+n_commands+") (form,"+n_formulas+")};\n";
				res+="\t \\legend{HaiQ, Prism (avg/sol), Prism (total)}\n";
				res+="\\end{axis}\n";
				res+="\\end{tikzpicture}\n";
		return res;
	}
	
	
}
