package org.jcm.haiq.solve;

import java.util.ArrayList;

import org.jcm.haiq.core.HQModel;
import org.jcm.haiq.parse.ParserMark1.PropertyEntry;
import org.jcm.haiq.solve.HQSolverIterative.PolicyExport;
import org.jcm.haiq.solve.HQSolverIterative.StructureExport;
import java.util.HashMap;

public interface HQSolver {

	void setMaxConfigs(int m_max_configs);

	void setExportModels(boolean b);
	
	public void setExportPolicies(boolean b);

	int generateSolutions(String string, HQModel behavioralModel);

	void setConstantDefinitions(String m_consts_string);

	String runExperiment(PropertyEntry propertyEntry, HQModel behavioralModel);

	Object getScoreBoard();

	void exportConfigurations(String m_configurationsJSON_output, StructureExport json);	

	void exportPolicies(String m_policies_output, PolicyExport plaintext, ArrayList<String> m_policies_export_params);

	HashMap<String, String> getConfigurations();
	
	public int getTotalCommandsGenerated();
	
	public int getTotalLinesGenerated();
	
	public int getTotalFormulasGenerated();
	
	public int getTotalModulesGenerated();
	
	public int getTotalSolutions();

	
}
