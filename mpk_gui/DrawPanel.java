package mpk_gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/** This abstract class makes it easier to draw in a JFrame by adding some nice
 * methods that are in 'real' coordinates (rather than pixels) */
@SuppressWarnings("serial")
public abstract class DrawPanel extends JPanel {
	
	/** The lower bound in world coordinates of the x-axis */
	public double xLow;
	/** The upper bound in world coordinates of the x-axis */
	public double xUpp;
	/** The lower bound in world coordinates of the y-axis */
	public double yLow;
	/** The upper bound in world coordinates of the y-axis */
	public double yUpp;
	
	/** The graphics object for this panel */
	protected Graphics2D g2;
	
	/** Set to "true" to force the axis to have equal units */
	public boolean axisEqual = true;
	
	/// Private / local variables for transforming from pixels to world
	private int width, height;
	private int bottom, top, left, right;
	private double Mx, Bx, My, By;  // Mapping between real world and plot axis
	private double xLower, xUpper, yLower, yUpper;   // For axisEquals
	
	/** Construct a DrawPanel with unit dimensions */
	public DrawPanel(){
		this(0.0, 1.0, 0.0, 1.0); // Start drawing on a unit square
	}
	
	/** Construct a Draw panel with custom dimensions */
	public DrawPanel(double xLow, double xUpp, double yLow, double yUpp){
		super();
		this.xLow = xLow;
		this.xUpp = xUpp;
		this.yLow = yLow;
		this.yUpp = yUpp;
	}	
	
	/** Move the center of the plot to a new location */
	public void panTo(double x, double y){
		double dx = 0.5*(xUpp-xLow);
		double dy = 0.5*(yUpp-yLow);
		xLow = x - dx;
		xUpp = x + dx;
		yLow = y + dy;
		yUpp = y - dy;
	}
	
	/** This must be implemented by the user! Call drawing functions in this 
	 * class, which wraps the functions found in a Graphics object	 */
	public abstract void paint();
	
	/** Drawing starts here */
	public void paintComponent(Graphics g){
		
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		
		setCoordinateSystem();
		
		paint();
		
	}

	/** Sets up the internal coordinates system for the axis and such */
	private void setCoordinateSystem(){
		width = getWidth();
		height = getHeight();

		if (axisEqual){  // Stretch bounds to make everything work out
			double aspectRatioPlot = ((double) height)/((double) width);
			double aspectRatioAxis = (yUpp-yLow)/(xUpp-xLow);
			if (aspectRatioPlot > aspectRatioAxis){ // increase yBounds
				double yMid = 0.5*(yUpp+yLow);
				double yDel = 0.5*(yUpp-yLow);
				yDel = yDel*(aspectRatioPlot/aspectRatioAxis);
				yUpper = yMid + yDel;
				yLower = yMid - yDel;
				xLower = xLow;
				xUpper = xUpp;
			} else {  // increase xBounds
				double xMid = 0.5*(xUpp+xLow);
				double xDel = 0.5*(xUpp-xLow);
				xDel = xDel*aspectRatioAxis/(aspectRatioPlot);
				xUpper = xMid + xDel;
				xLower = xMid - xDel;
				yLower = yLow;
				yUpper = yUpp;
			}
		} else {
			xLower = xLow;
			xUpper = xUpp;
			yLower = yLow;
			yUpper = yUpp;
		}
		
		top = 0;
		bottom = height;
		left = 0;
		right = width;

		/// Figure out transform to get into plot coordinates:
		/// [DATA] = M*[PIXELS] + B
		Mx = (right - left)/(xUpper-xLower);
		Bx = left - xLower*Mx;
		My = (top - bottom)/(yUpper-yLower);
		By = bottom - yLower*My;

	}
	
	///////////////////////////////////////////////////////////////////////
	////                     Utility functions!                        ////
	///////////////////////////////////////////////////////////////////////

	/** Set the line width */
	public void setLineWidth(int w){
		g2.setStroke(new BasicStroke(w));
	}
	
	/** Set the color using a Color object*/
	public void setColor(Color color){
		g2.setColor(color);
	}
	
	/** Sets the color fusing RGB */
	public void setColor(int r, int g, int b){
		g2.setColor(new Color(r,g,b));
	}	
	
	/** Wrapper for drawString */
	public void drawString(String string, double x, double y){
		g2.drawString(string,((int) (Mx*x + Bx)),((int) (My*y + By)));		
	}
	
	/** Wrapper for drawLine */
	public void drawLine(double x1, double y1, double x2, double y2){
		g2.drawLine(((int) (Mx*x1 + Bx)), ((int) (My*y1 + By)),
				((int) (Mx*x2 + Bx)), ((int) (My*y2 + By)));
	}
		 
	/** Wrapper for drawOval - draws a circle of radius @param r centered at 
	 * the position @param x and @param y */
	public void drawCircle(double x, double y, double r){
		int xLow = (int) (Mx*(x-r)+Bx);
		int yLow = (int) (My*(y+r)+By);
		int xWid = (int) (Math.abs(2*Mx*r));
		int yWid = (int) (Math.abs(2*My*r));
		
		g2.drawOval(xLow,yLow,xWid,yWid);
	}
	
	/** Wrapper for drawOval - draws a circle of radius @param r centered at 
	 * the position @param x and @param y */
	public void fillCircle(double x, double y, double r){
		int xLow = (int) (Mx*(x-r)+Bx);
		int yLow = (int) (My*(y+r)+By);
		int xWid = (int) (Math.abs(2*Mx*r));
		int yWid = (int) (Math.abs(2*My*r));
		
		g2.fillOval(xLow,yLow,xWid,yWid);
	}
	
	/** Wrapper for drawOval - draws a circle of radius @param r centered at 
	 * the position @param x and @param y */
	public void fillRect(double xLow, double yLow, double xWidth, double yWidth){
		int xL = (int) (Mx*xLow+Bx);
		int yL = (int) (My*yLow+By);
		int xW = (int) (Math.abs(Mx*xWidth));
		int yW = (int) (Math.abs(My*yWidth));
		
		g2.fillRect(xL,yL,xW,yW);
	}
		
}
