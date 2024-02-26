package org.jcm.prismutils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class newPolicyReader {

    private static String m_policyFile;
    public LinkedList<LinkedList<String>> m_tupleMap = new LinkedList<LinkedList<String>>();
    public ArrayList<String> m_plan = new ArrayList<String>();

    
    public newPolicyReader(String policyFile) {
        m_policyFile = policyFile;
    }

    
    public boolean readPolicy() {
    	  
        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(m_policyFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int line_no = 0;
            String currentSource="";
            String currentTarget="";
            String currentProb="";
            String currentAction="";

            while((line = bufferedReader.readLine()) != null) {                
                ++line_no;
                if (line_no == 1)
                {
                    continue;                // Skip the first line
                }

                String[] elements = line.split(" ");
                
                currentSource = elements[0];
                currentTarget = elements[1];
                currentProb   = elements[2];
                currentAction = "";
                
                if (elements.length == 4) {
                    currentAction = elements[3];
                }
             
                m_tupleMap.add(new LinkedList<String>(Arrays.asList(currentSource, 
                											 currentTarget,
                											 currentProb,
                											 currentAction)));
            }

            bufferedReader.close(); 
            this.extractPolicy ();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + m_policyFile + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '"  + m_policyFile + "'");                  
        }
        return true;
    	
    }
	

    private void extractPolicy() {
    	m_plan.clear();
    	String current="";
    	for (int i=0; i<this.m_tupleMap.size();i++) {    		
    		current  = "[src: " +m_tupleMap.get(i).get(0) +", ";
    		current += "tgt: " +m_tupleMap.get(i).get(1) +", ";
    		current += "probability: " +m_tupleMap.get(i).get(2) +", ";
    		current += "action: " +m_tupleMap.get(i).get(3) +"]";    		
    		m_plan.add(current);
    	}
    }

    
    public ArrayList<String> getPlan() {
        return m_plan;
    }
      
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
