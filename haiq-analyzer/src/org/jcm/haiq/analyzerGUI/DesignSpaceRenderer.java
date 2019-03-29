package org.jcm.haiq.analyzerGUI;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public class DesignSpaceRenderer implements GLEventListener {
    private GLU glu = new GLU();
    private GL2 m_gl = null;
    
    private float m_scale=3.0f;
    private float m_xRotated=45.0f, m_yRotated=0.0f, m_zRotated=0.0f;
    private float m_xOffset=0.0f, m_yOffset=0.0f, m_zOffset=0.0f;
    
    private final float[] COLOR_AXES = {1.0f, 1.0f, 1.0f};
    private final float[] COLOR_SURFACE = {0.0f, 1.0f, 0.2f};

    
    public void setColor(float[] c){
        m_gl.glColor3f(c[0],c[1],c[2]);
    }

    
    public void addVertex ( float x, float y, float z){
    	GL2 gl = m_gl;
    	gl.glVertex3f(m_xOffset+x, m_yOffset+y, m_zOffset+z);
    }
    
    public void drawAxes(){
    	GL2 gl = m_gl;
    	setColor(COLOR_AXES);
        gl.glBegin(GL2.GL_LINES);
        addVertex( 0.0f, 0.0f, 0.0f);    // Top Right Of The Quad (Back)
        addVertex( 0.0f, m_scale, 0.0f);    // Top Left Of The Quad (Back)
        addVertex( 0.0f, 0.0f, 0.0f);    // Top Right Of The Quad (Back)
        addVertex( m_scale, 0.0f, 0.0f);    // Top Left Of The Quad (Back)
        addVertex( 0.0f, 0.0f, 0.0f);    // Top Right Of The Quad (Back)
        addVertex( 0.0f, 0.0f, m_scale);    // Top Left Of The Quad (Back)
        gl.glEnd();        
    }
    
    
    public void drawSurface(){
    	setColor(COLOR_SURFACE);
    	m_gl.glBegin(GL2.GL_LINES);
    	for (float x=0.0f; x<2.0f*(float)Math.PI; x+=0.1f){
    		addVertex(m_scale*x/(2.0f*(float)Math.PI), (float)Math.sin(x), 0.0f);
    	}
    	m_gl.glEnd();
    }
    
    public void display(GLAutoDrawable gLDrawable) 
    {    
    	GL2 gl = m_gl;
    	
    	gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	        
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f,0.0f,-10.5f);
        gl.glRotatef(m_xRotated,1.0f,0.0f,0.0f);
        // rotation about Y axis
        gl.glRotatef(m_yRotated,0.0f,1.0f,0.0f);
        // rotation about Z axis
        gl.glRotatef(m_zRotated,0.f,0.0f,1.0f);
        
        drawAxes();
        drawSurface();
        
//        gl.glBegin(GL2.GL_QUADS);
//        gl.glColor3f(0.0f,1.0f,0.0f);    // Color Blue
//        gl.glVertex3f( 1.0f, 1.0f,-1.0f);    // Top Right Of The Quad (Top)
//        gl.glVertex3f(-1.0f, 1.0f,-1.0f);    // Top Left Of The Quad (Top)
//        gl.glVertex3f(-1.0f, 1.0f, 1.0f);    // Bottom Left Of The Quad (Top)
//        gl.glVertex3f( 1.0f, 1.0f, 1.0f);    // Bottom Right Of The Quad (Top)
//        gl.glColor3f(1.0f,0.5f,0.0f);    // Color Orange
//        gl.glVertex3f( 1.0f,-1.0f, 1.0f);    // Top Right Of The Quad (Bottom)
//        gl.glVertex3f(-1.0f,-1.0f, 1.0f);    // Top Left Of The Quad (Bottom)
//        gl.glVertex3f(-1.0f,-1.0f,-1.0f);    // Bottom Left Of The Quad (Bottom)
//        gl.glVertex3f( 1.0f,-1.0f,-1.0f);    // Bottom Right Of The Quad (Bottom)
//        gl.glColor3f(1.0f,0.0f,0.0f);    // Color Red    
//        gl.glVertex3f( 1.0f, 1.0f, 1.0f);    // Top Right Of The Quad (Front)
//        gl.glVertex3f(-1.0f, 1.0f, 1.0f);    // Top Left Of The Quad (Front)
//        gl.glVertex3f(-1.0f,-1.0f, 1.0f);    // Bottom Left Of The Quad (Front)
//        gl.glVertex3f( 1.0f,-1.0f, 1.0f);    // Bottom Right Of The Quad (Front)
//        gl.glColor3f(1.0f,1.0f,0.0f);    // Color Yellow
//        gl.glVertex3f( 1.0f,-1.0f,-1.0f);    // Top Right Of The Quad (Back)
//        gl.glVertex3f(-1.0f,-1.0f,-1.0f);    // Top Left Of The Quad (Back)
//        gl.glVertex3f(-1.0f, 1.0f,-1.0f);    // Bottom Left Of The Quad (Back)
//        gl.glVertex3f( 1.0f, 1.0f,-1.0f);    // Bottom Right Of The Quad (Back)
//        gl.glColor3f(0.0f,0.0f,1.0f);    // Color Blue
//        gl.glVertex3f(-1.0f, 1.0f, 1.0f);    // Top Right Of The Quad (Left)
//        gl.glVertex3f(-1.0f, 1.0f,-1.0f);    // Top Left Of The Quad (Left)
//        gl.glVertex3f(-1.0f,-1.0f,-1.0f);    // Bottom Left Of The Quad (Left)
//        gl.glVertex3f(-1.0f,-1.0f, 1.0f);    // Bottom Right Of The Quad (Left)
//        gl.glColor3f(1.0f,0.0f,1.0f);    // Color Violet
//        gl.glVertex3f( 1.0f, 1.0f,-1.0f);    // Top Right Of The Quad (Right)
//        gl.glVertex3f( 1.0f, 1.0f, 1.0f);    // Top Left Of The Quad (Right)
//        gl.glVertex3f( 1.0f,-1.0f, 1.0f);    // Bottom Left Of The Quad (Right)
//        gl.glVertex3f( 1.0f,-1.0f,-1.0f);    // Bottom Right Of The Quad (Right) 
        
        gl.glEnd();				
        gl.glFlush();
        
        m_yRotated += 0.2f;
        m_xRotated += 0.0f;
    }
 
 
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) 
    {
    	System.out.println("displayChanged called");
    }
 
    public void init(GLAutoDrawable gLDrawable) 
    {
    	System.out.println("init() called");
        m_gl = gLDrawable.getGL().getGL2();
        m_gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        m_gl.glShadeModel(GL2.GL_FLAT);
    }
 
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) 
    {
    	System.out.println("reshape() called: x = "+x+", y = "+y+", width = "+width+", height = "+height);
        final GL2 gl = gLDrawable.getGL().getGL2();
 
        if (height <= 0) // avoid a divide by zero error!
        {
            height = 1;
        }
 
        final float h = (float) width / (float) height;
 
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(40.0f, h, 0.5, 20.0);
        //gluPerspective(40.0f,(float)width/(float)height,0.5,20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        

    }
 
 
	public void dispose(GLAutoDrawable arg0) 
	{
		System.out.println("dispose() called");
	}
}
