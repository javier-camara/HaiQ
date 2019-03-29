package org.jcm.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class ProgressBar {
	
	private boolean m_console_mode = false;
	private int m_current;
	private int m_top;
	private double m_scale;
	
	public ProgressBar ( int top, double scale){
		m_current = 0;
		m_top = top;
		m_scale = scale;
	}
	
	public void setCurrent(int c){
		m_current = c;
	}
	
	public int getCurrent () {
		return m_current;
	}
	
	public void setTop (int t){
		m_top = t;
	}
	
	public int getTop() {
		return m_top;
	}
	
	public double getProgress(){
		return ((double) m_current/ (double) m_top)*100;
	}
	
	public double getProgressScaled(){
		return getProgress() * m_scale;
	}
	
	public int getIntProgress(){
		return (int)Math.floor(getProgress());
	}

	public int getIntProgressScaled(){
		return (int)Math.floor(getProgressScaled());
	}
	
	public int scaledTotal(){
		return (int) Math.floor((double) m_scale * 100.0);
	}
	
	public void tick(){
		if (m_current<m_top)
			m_current = m_current +1;
	}
	
	public void print(){
		NumberFormat f = new DecimalFormat ("#0.00");
		String barString="\r[";
		for (int i=0; i < scaledTotal(); i++ )
			if (i<= getIntProgressScaled() ) 
				barString += "=";
			else 
				barString +=" ";
		barString+="] " + f.format(getProgress())+" %";
		try{
			System.out.write(barString.getBytes());
		} catch (Exception e){}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ProgressBar b = new ProgressBar(140, 0.5);
		for (int i=0; i < 140; i++){
			b.tick();
			b.print();
			try{
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (Exception e){
				System.out.println("X");
			}
		}
		
	}

}
