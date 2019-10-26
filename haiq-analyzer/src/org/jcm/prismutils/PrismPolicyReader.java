package org.jcm.prismutils;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class PrismPolicyReader {
    private static String m_policyFile;
    public ArrayList<String> m_plan = new ArrayList<String>();
    private String m_start_state;


    public PrismPolicyReader(String policyFile) {
        m_policyFile = policyFile;
    }


    /**
     * Generates a linear plan from a policy
     * @param initial_state
     * @param stateActionMap
     * @param startEndStateMap
     */
    private void extractPolicy(Map<String, LinkedList<String>> stateActionMap, Map<String, LinkedList<String>> startEndStateMap) {

        String state = m_start_state;
        String action = "";
        while (startEndStateMap.containsKey(state)) { // While current state is mapped to something
        	boolean foundState = false;
        	String previousState = state;
    		for (String e: startEndStateMap.get(state)){ // For each of the alternative states to which a source state can be mapped (probabilistic branches)
    			if (startEndStateMap.containsKey(e)){  // Lookahead
    				action = stateActionMap.get(state).get(0); 
    				state = e;
    				foundState=true;
    			}
        	}
    		if (!foundState) {
	        	if ((startEndStateMap.get(state).size()==1) && (!startEndStateMap.containsKey(startEndStateMap.get(state).get(0)))){ // Special case for final state
	        		action = stateActionMap.get(state).get(0);
	        		state = startEndStateMap.get(state).get(0);
	        	}
	        	else if ((startEndStateMap.get(state).size() > 1 && !startEndStateMap.containsKey(startEndStateMap.get(state).get(0)) && !startEndStateMap.containsKey(startEndStateMap.get(state).get(1)) )) {
	        		action = stateActionMap.get(state).get(0);
	        		state = startEndStateMap.get(state).get(0);
	        	}
    		}
        	
            if (action != "") {
                m_plan.add(action);
            }
    		if (previousState.equals(state)) break;

        }
    }


    // Checks if a state id is mapped from something in the data structure
    public static boolean isHit( Map<String, LinkedList<String>> l, String s){
    	for (Map.Entry<String, LinkedList<String>> e : l.entrySet()){
    		if (e.getValue().contains(s))
    			return true;
    	}
    	return false;
    }
    
    /**
     * Reads an adversary/strategy file into a policy
     * @return
     */
    public boolean readPolicy() {
        Map<String, LinkedList<String>> stateActionMap = new HashMap<String, LinkedList<String>>();
        Map<String, LinkedList<String>> startEndStateMap = new HashMap<String, LinkedList<String>>();

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                    new FileReader(m_policyFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                    new BufferedReader(fileReader);

            int line_no = 0;
            boolean firstActionFound = false;
            String startState="";
            String endState="";

            while((line = bufferedReader.readLine()) != null) {                
                ++line_no;
                if (line_no == 1)
                {
                    continue;                // Skip the first line
                }
                
                String[] elements = line.split(" ");
                startState=elements[0];
                endState = elements[1];
                String action = "";

                if (elements.length == 4) {
                    action = elements[3];
                }
                
                if (elements.length == 5) {
                	action = elements[4];
                }

                if (!stateActionMap.containsKey(startState)){
                	stateActionMap.put(startState, new LinkedList<String>());
                }
                stateActionMap.get(startState).add(action);
                
                if (!startEndStateMap.containsKey(startState)){
                	startEndStateMap.put(startState, new LinkedList<String>());
                }
                startEndStateMap.get(startState).add(endState);

                if (!firstActionFound && elements.length==4) {
                    firstActionFound = true;
                }
                m_start_state = startState;
            }

            bufferedReader.close(); 
            this.extractPolicy (stateActionMap, startEndStateMap);
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + m_policyFile + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '"  + m_policyFile + "'");                  
        }
        return true;
    }

    /**
     * Returns the linear plan associated with the policy
     * @return List of strings encoding sequence of action labels, e.g., [action1, ..., actionN]
     */
    public ArrayList<String> getPlan() {
        return m_plan;
    }
    
}