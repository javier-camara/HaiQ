package org.jcm.prismutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BRASSRobotPolicyFilter {

	ArrayList<String> m_plan;
	
	public BRASSRobotPolicyFilter (String filename) {
		PrismPolicyReader pr = new PrismPolicyReader (filename);
		pr.readPolicy();
		m_plan = pr.getPlan();
	}
	
	public BRASSRobotPolicyFilter (ArrayList<String> plan) {
		m_plan = plan;
	}
	
	public ArrayList<String> getPlan(String startLoc){
		ArrayList<String> reconf = new ArrayList<String>();
		ArrayList<String> task = new ArrayList<String>();
		ArrayList<String> taskn = new ArrayList<String>();
		String currentLoc = startLoc;
		
		for (int i=0;i<m_plan.size();i++) {
			if (m_plan.get(i).contains("_enable")||m_plan.get(i).contains("_disable"))
				reconf.add(m_plan.get(i));
			else
				task.add(m_plan.get(i));
		}
		
		while (task.size()>0) {
			String[] chunks = task.get(0).split("_");
			if (Objects.equals(chunks[2], currentLoc)||Objects.equals(chunks[4],currentLoc)) {
				taskn.add(reformatLabel(task.get(0), currentLoc));
				currentLoc = getNextLoc(chunks, currentLoc);
				task.remove(0);
			}
		}
		reconf.addAll(taskn);
		return reconf;
	}
	
	public String getNextLoc (String[] chunks, String currentLoc) {
		return Objects.equals(currentLoc, chunks[2]) ? chunks[4] : chunks[2];
	}
	
	public String reformatLabel(String label, String currentLoc) {
		String[] chunks = label.split("_");
		String nextLoc = getNextLoc (chunks, currentLoc);
		return currentLoc+"_to_"+nextLoc;
	}
	
	
	
	public static void main(String[] args) {
		ArrayList<String> p = new ArrayList<String>(Arrays.asList("_moveTo_l2_0_l1_0", "laserscanNodelet_0_enable", "_moveTo_l59_0_l2_0", "amcl_0_enable", "mapServerStd_0_enable", "kinect_0_enable"));
		BRASSRobotPolicyFilter f = new BRASSRobotPolicyFilter(p);
		System.out.println(f.getPlan("l1"));
	}
}
