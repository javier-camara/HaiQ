package org.jcm.haiq.analyzerGUI;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.JFileChooser;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JButton;

import org.jcm.haiq.analyzerGUI.DesignSpaceRenderer;
import org.jcm.util.TextFileHandler;

public class HaiQGUI {

	private JTextPane m_log = null;
	
	private final String RESOURCE_DIR="resources";
	private final String ALLOY_TMP="tmpstyle.als";
	
	
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HaiQGUI window = new HaiQGUI();
					window.frame.setTitle("HaiQ Analyzer");
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HaiQGUI() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel StylePanel = new JPanel();
		tabbedPane.addTab("Style Specification", null, StylePanel, null);
		StylePanel.setLayout(new BorderLayout(0, 5));
		
		JScrollBar scrollBar = new JScrollBar();
		StylePanel.add(scrollBar, BorderLayout.EAST);
		
		
		JTextPane textStyle = new JTextPane();
		textStyle.setEditable(false);
		StylePanel.add(textStyle, BorderLayout.CENTER);

		JPanel LogPanel = new JPanel();
	    tabbedPane.addTab("Log", null, LogPanel, null);
	    LogPanel.setLayout(null);
	        
	    JTextPane textLog = new JTextPane();
	    textLog.setEditable(false);
	    textLog.setBounds(0, 0, 779, 510);
	    LogPanel.add(textLog);
	    m_log = textLog;
		
		JButton btnGenerateConfigurations = new JButton("Generate Configurations");
		StylePanel.add(btnGenerateConfigurations, BorderLayout.SOUTH);
		btnGenerateConfigurations.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileName=getRunPath().substring(0,getRunPath().length()-1)+RESOURCE_DIR+"/"+ALLOY_TMP;
				TextFileHandler fh = new TextFileHandler(fileName);
				fh.exportFile(textStyle.getText());
//				m_project.generateAlloySolutions(fileName);
//				log("Alloy generated "+String.valueOf(m_project.getAlloySolutionCount())+" solutions.");
//				log(m_project.getAlloySolution("sol_0"));
			}
		});

		
		
		JPanel DesignSpacePanel = new JPanel();
		tabbedPane.addTab("Design Space", null, DesignSpacePanel, null);
		
		JInternalFrame internalFrame = new JInternalFrame("Tradeoff Space");
		DesignSpacePanel.add(internalFrame);
		internalFrame.setVisible(true);
		
		 // setup OpenGL Version 2
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        
        DesignSpaceRenderer r=new DesignSpaceRenderer();
		
		GLCanvas glcanvas = new GLCanvas(capabilities);
        glcanvas.addGLEventListener(r);

        glcanvas.setSize(400, 300 );
        
        final FPSAnimator animator = new FPSAnimator(glcanvas, 60);		
        animator.start();
        
        internalFrame.getContentPane().add(glcanvas);        
        
       
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu mnStyle = new JMenu("Style");
        menuBar.add(mnStyle);
        
        JMenuItem mntmOpenStyle = new JMenuItem("Open");
        mntmOpenStyle.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		final JFileChooser fc = new JFileChooser();
        		int returnVal = fc.showOpenDialog(frame);
        		
        		if (returnVal== JFileChooser.APPROVE_OPTION){
        			try{
        				FileReader fileReader = new FileReader(fc.getSelectedFile());
        				textStyle.read(fileReader,"textStyle");
        				fileReader.close();
        			} catch (IOException ex) {
        				System.out.println("Error reading Style file "+fc.getSelectedFile());
        				log("Error reading Style file "+fc.getSelectedFile());
        			}
        			
        		}
        		
        	}
        });
        mnStyle.add(mntmOpenStyle);
        
        JMenu mnNewMenu = new JMenu("Behavior");
        menuBar.add(mnNewMenu);
        
        JMenu mnQuantifiers = new JMenu("Properties");
        menuBar.add(mnQuantifiers);
        
        JMenu mnViews = new JMenu("Views");
        menuBar.add(mnViews);
	
        log("Rover Running from "+getRunPath());
	}
	
	public void log(String s){
		m_log.setText(m_log.getText()+s+"\n");
	}
	
	public String getRunPath(){
        File currentDirFile = new File(".");
        return (currentDirFile.getAbsolutePath());
	}
	
}
