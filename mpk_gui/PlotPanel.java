package mpk_gui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

/** A class for making creating simple line plots. Most parameters that are
 * used in the generation of the plot are available to the user as fields. There
 * is minimal input validation, so if you enter bad data into fields, expect to
 * get unpredictable results. 
 * 
 * LEFT-CLICK: Add a data-label to the nearest data-point
 * RIGHT-CLICK: Remove the nearest data-label
 * 
 * */
public class PlotPanel extends JPanel implements MouseInputListener{

	/** The label for the horizontal (x) axis of the plot */
	public String xLabel = "xLabel";
	private int xLabel_x, xLabel_y;  // anchor point

	/** The label for the vertical (y) axis of the plot */
	public String yLabel = "yLabel";
	private int yLabel_x, yLabel_y;  // anchor point

	/** The title of the plot */
	public String title = "title";
	private int title_x, title_y;  // anchor point

	/** The height of x-axis and y-axis font, in pixels */
	public int labelFontSize = 16; 
	/** The height of the title font, in pixels */
	public int titleFontSize = 18;
	/** The height of the axis fonts, in pixels */
	public int axisFontSize = 12;
	/** The estimated amount of horizontal space used by a number string */
	private int axisFontWidthX,axisFontWidthY;
	/** The amount of free space (in pixels) between plot items */
	public int freeSpace = 5;  // Amount of free space around regions of the plot
	/** The thickness of the line displaying the data, in pixels */
	public int lineWidth = 1;
	/** The thickness of the lines used for the axis and tick marks */
	public int axisLineWidth = 1;
	/** The color of the line */
	public Color lineColor = Color.black;
	/** The color of the axis and text */
	public Color axisColor = Color.black;
	
	private String fontName = "Courier";
	private double fontAspectRatio = 0.6;  // For "Courier"

	/// Extents of the plot windows
	/** The lowest value on the x-axis */
	public double xLow = 0.0;
	/** The upper value of the x-axis */
	public double xUpp = 1.0;
	/** The lowest value on the y-axis */
	public double yLow = 0.0;
	/** The upper value on the y-axis */
	public double yUpp = 1.0;
	
	/** true => The computer will fix the x-axis scale to the data */
	public boolean autoScaleX = true;
	/** true => auto-scaling algorithm assumes {xLow=x[0], xUpp=x[n-1]} */
	public boolean xDataIsMonotonic = false; //Makes autoscaling run faster
	/** true => The computer will fix the y-axis scale to the data */
	public boolean autoScaleY = true;
	/** true => auto-scaling algorithm assumes {yLow=y[0], yUpp=y[n-1]} */
	public boolean yDataIsMonotonic = false; //Makes autoscaling run faster

	/// Tick marks along the axis
	/** Automatically pick the number of tick marks on the each axis */
	public boolean axisTickCountAuto = true;
	private int axisTickCountX = 5; 
	private int axisTickCountY = 5;
	/** {fewest allowable tick marks, most allowable tick marks} */
	public int[] axisTickCountBound = new int[] {3,8};  // Never use fewer than this when autoscaling
	/** Length of the axis tick marks, in pixels */
	public int axisTickMarkLength = 10;

	/** Precision for the string formatting on the x-axis data values */
	public int axisPrecisionX = 4;
	/** Precision for the string formatting on the y-axis data values */
	public int axisPrecisionY = 4;
	private int axisFormatLengthX = axisPrecisionX;
	private int axisFormatLengthY = axisPrecisionY;
	private String axisStringFormatX = "%" + axisFormatLengthX +"." + axisPrecisionX + "g";
	private String axisStringFormatY = "%" + axisFormatLengthY +"." + axisPrecisionY + "g";
	
	/// Data
	/** Data values along the horizontal axis */
	private double[] x = null;
	/** Data values along the vertical axis */
	private double[] y = null;
	/** The number of data values */
	private int n;

	/// Private / local variables
	private int width, height;
	private int bottom, top, left, right;

	private int plot_x_low, plot_x_upp, plot_y_low, plot_y_upp;
	private double Mx, Bx, My, By;  // Mapping between real world and plot axis

	private double tickSpaceX, tickSpaceY;  // Spacing between tick marks (pixels)

	/** Construct a PlotPanel with no data in it. Use setData() to add data */
	public PlotPanel(){
		this(null,null); // pass empty data sets
	}
	
	/** Construct a PlotPanel from a data set
	 * @param x a vector of x-data values
	 * @param y a vector of y-data values*/
	public PlotPanel(double[] x, double[]y){
		super();
		addMouseListener(this);
		setData(x,y);
	}

	/** Everything happens here */
	public void paintComponent (Graphics g){

		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		setCoordinateSystem(); // Sets local variables as a function of panel dimensions

		drawLabels(g2);

		dataAnalysis();  // Any automatic features are set here

		drawAxis(g2);

		plotData(g2);

		for(DataTip d : dataTipSet){
			d.paint(g2);
		} removeDataTips();

	}


	///////////////////////////////////////////////////////////////////////////
	////              Public getter and setter methods                     ////
	///////////////////////////////////////////////////////////////////////////


	/** Sets the data vectors*/
	public void setData(double[] x, double[] y){
		this.x = x;
		this.y = y;
		repaint();
	}

	/** Set the extents of the plot window, and disable autoscaling */
	public void setAxisExtents(double xLow, double xUpp, double yLow, double yUpp){
		setAxisExtentsX(xLow,xUpp);
		setAxisExtentsY(yLow,yUpp);
	}

	/** Set the axis extents and disable autoScaling on the x axis*/
	public void setAxisExtentsX(double xLow, double xUpp){
		this.xLow = xLow;
		this.xUpp = xUpp;
		autoScaleX = false;
	}

	/** Set the axis extents and disable autoScaling on the y axis */
	public void setAxisExtentsY(double yLow, double yUpp){
		this.yLow = yLow;
		this.yUpp = yUpp;
		autoScaleY = false;
	}

	/** Set the number of tick marks along each axis*/
	public void setAxisTickCount(int xCount, int yCount){
		axisTickCountAuto = false;
		axisTickCountX = xCount;
		axisTickCountY = yCount;
	}
	
	/** @return the x-data in the plot */
	public double[] getDataX(){
		return x;
	}
	
	/** @return the y-data in the plot */
	public double[] getDataY(){
		return y;
	}


	///////////////////////////////////////////////////////////////////////////
	////                   Private helper methods                          ////
	///////////////////////////////////////////////////////////////////////////


	

	/** Plot the data */
	private void plotData(Graphics2D g2){
		g2.setStroke(new BasicStroke(lineWidth));
		g2.setColor(lineColor);
		if (x != null) { if (x.length >1) {
			n = Math.min(x.length,y.length);
			int x1 = (int) (x[0]*Mx + Bx);
			int y1 = (int) (y[0]*My + By);
			int x2,y2;
			for (int i=1; i<n; i++){
				x2 = (int) (x[i]*Mx + Bx);
				y2 = (int) (y[i]*My + By);
				g2.drawLine(x1,y1,x2,y2);
				x1 = x2; y1 = y2;
			}
		}}
	}

	/** Draw the plot axis */
	private void drawAxis(Graphics2D g2) {

		g2.setFont(new Font(fontName, Font.PLAIN, axisFontSize));
		g2.setStroke(new BasicStroke(axisLineWidth));
		g2.setColor(axisColor);
		
		/// X axis
		g2.drawLine(plot_x_low, plot_y_low,  plot_x_upp, plot_y_low);
		tickSpaceX = (plot_x_upp-plot_x_low)/(axisTickCountX-1);
		int yText = plot_y_low + axisFontSize + freeSpace;
		int xPos; double xVal;
		for (int i=0; i<axisTickCountX; i++){
			xPos = (int) (tickSpaceX*i) + plot_x_low;
			xVal = xLow + (((double)i)/(axisTickCountX-1))*(xUpp-xLow);
			g2.drawString(String.format(axisStringFormatX, xVal), xPos,yText);
			g2.drawLine(xPos,plot_y_low,xPos,plot_y_low-axisTickMarkLength);
		}

		/// Y axis
		g2.drawLine(plot_x_low, plot_y_low,  plot_x_low, plot_y_upp);
		tickSpaceY = (plot_y_upp-plot_y_low)/(axisTickCountY-1);
		int xText = plot_x_low - axisFontWidthY - freeSpace;
		int yPos; double yVal;
		String strVal;
		for (int i=0; i<axisTickCountY; i++){
			yPos = (int) (tickSpaceY*i) + plot_y_low;
			yVal = yLow + (((double)i)/(axisTickCountY-1))*(yUpp-yLow);
			strVal = String.format(axisStringFormatY, yVal); 
			if (strVal.length() > axisFormatLengthY+2) strVal = strVal.substring(0, axisFormatLengthY+2);
			g2.drawString(strVal,xText,yPos);
			g2.drawLine(plot_x_low, yPos, plot_x_low+axisTickMarkLength, yPos);
		}
	}

	/** Draws the axis labels and title for the plot */
	private void drawLabels(Graphics2D g2) {
		g2.setFont(new Font(fontName, Font.PLAIN, labelFontSize));
		g2.setColor(axisColor);

		g2.drawString(xLabel, xLabel_x, xLabel_y);

		g2.rotate(-0.5*Math.PI, yLabel_x, yLabel_y);
		g2.drawString(yLabel, yLabel_x, yLabel_y);
		g2.rotate(0.5*Math.PI, yLabel_x, yLabel_y);

		g2.setFont(new Font(fontName, Font.PLAIN, titleFontSize));
		g2.drawString(title, title_x, title_y);
	}

	/** Sets up the internal coordinates system for the axis and such */
	private void setCoordinateSystem(){
		width = getWidth();
		height = getHeight();

		top = 0;
		bottom = height;
		left = 0;
		right = width;

		axisFontWidthX = (int) (fontAspectRatio*(axisFormatLengthX+2)*axisFontSize);
		axisFontWidthY = (int) (fontAspectRatio*(axisFormatLengthY+2)*axisFontSize);

		plot_x_low = left + 4*freeSpace + labelFontSize + axisFontWidthY;
		plot_x_upp = right - freeSpace - axisFontWidthX;
		plot_y_low = bottom - 4*freeSpace - 2*labelFontSize;
		plot_y_upp = top + 2*freeSpace + titleFontSize;

		xLabel_x = (int) (0.5*(plot_x_low + plot_x_upp));
		xLabel_x -= (int) (0.5*xLabel.length()*labelFontSize*fontAspectRatio);
		xLabel_y = bottom - freeSpace;

		yLabel_x = left + freeSpace + labelFontSize;
		yLabel_y = (int) (0.5*(plot_y_low + plot_y_upp));
		yLabel_y += (int) (0.5*yLabel.length()*labelFontSize*fontAspectRatio);

		title_x = (int) (0.5*(plot_x_low + plot_x_upp));
		title_x -= (int) (0.5*title.length()*labelFontSize*fontAspectRatio);
		title_y = plot_y_upp - freeSpace;

		/// Figure out transform to get into plot coordinates:
		/// [DATA] = M*[PIXELS] + B
		Mx = (plot_x_upp - plot_x_low)/(xUpp-xLow);
		Bx = plot_x_low - xLow*Mx;
		My = (plot_y_upp - plot_y_low)/(yUpp-yLow);
		By = plot_y_low - yLow*My;

	}

	/** Look at the data to do things like automatically scaling the axis */
	private void dataAnalysis(){

		if (autoScaleX && x!=null){
			if (xDataIsMonotonic){
				xLow = x[0];
				xUpp = x[x.length-1];
			} else {
				double minVal=x[0], maxVal=x[0];
				for (double val : x){
					if (val<minVal) minVal=val;
					if (val>maxVal) maxVal=val;
				}
				xLow = minVal; xUpp = maxVal;
			}
		}

		if (autoScaleY && y!=null){
			if (yDataIsMonotonic){
				yLow = y[0];
				yUpp = y[y.length-1];
			} else {
				double minVal=y[0], maxVal=y[0];
				for (double val : y){
					if (val<minVal) minVal=val;
					if (val>maxVal) maxVal=val;
				}
				yLow = minVal; yUpp = maxVal;
			}
		}

		if (axisTickCountAuto){
			int xCount = (int) ((plot_x_upp-plot_x_low)/(axisFontWidthX*2.0));
			int yCount = (int) ((plot_y_low-plot_y_upp)/(axisFontSize*4.0));

			if (xCount < axisTickCountBound[0]) xCount = axisTickCountBound[0];
			if (xCount > axisTickCountBound[1]) xCount = axisTickCountBound[1];
			if (yCount < axisTickCountBound[0]) yCount = axisTickCountBound[0];
			if (yCount > axisTickCountBound[1]) yCount = axisTickCountBound[1];

			axisTickCountX = xCount;
			axisTickCountY = yCount;

		}
	}

	///////////////////////////////////////////////////////////////////////////
	////                  Mouse interaction stuff                          ////
	///////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == 1){  // LEFT CLICK  --  Add data tip
			new DataTip(e.getX(),e.getY());
		} else { // RIGHT CLICK -- Remove closest data tip
			double xTmp = (((double) e.getX()) - Bx)/Mx;
			double yTmp = (((double) e.getY()) - By)/My;
			double dx,dy, val;
			Double minVal=null; DataTip minObj=null;
			for (DataTip d : dataTipSet){
				dx = d.xDataVal-xTmp;
				dy = d.yDataVal-yTmp;
				val = dx*dx+dy*dy;
				if (minVal==null){
					minVal = val;
					minObj = d;
				} else if (val < minVal){
					minVal = val;
					minObj = d;
				}
			}
			if (minObj!=null){
				minObj.remove();
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseDragged(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}

	///////////////////////////////////////////////////////////////////////////
	////                         Data Tip Stuff                            ////
	///////////////////////////////////////////////////////////////////////////
	private HashSet<DataTip> dataTipSet = new HashSet<DataTip>();
	private HashSet<DataTip> deleteDataTip = new HashSet<DataTip>();
	
	/** Deletes all data tips that are schedule for deletion */
	private void removeDataTips(){
		/// Remove data tips from the list of things to be plotted
		for (DataTip d : deleteDataTip){
			dataTipSet.remove(d);
		}
		/// Empty the list of objects to clear
		deleteDataTip.clear();
		repaint();
	}

	/** A class for displaying data at a specific point in the plot window */
	private class DataTip {
		private double xDataVal; 
		private double yDataVal; 
		private int xDataPixel;
		private int yDataPixel;		
		
		/** Create a new data tip at (x,y) coordinate in pixels */
		private DataTip(int xDataPixel, int yDataPixel){

			/// Find the data point closest to point clicked:
			double dx, dy;
			double val=0.0, minVal=0.0; // Should never stay at these values
			int minIdx=0; // dummy initializer
			for (int i=0; i<n; i++){
				dx = xDataPixel - (Mx*x[i] + Bx);  // distance in pixel space
				dy = yDataPixel - (My*y[i] + By);
				val = dx*dx + dy*dy; 
				if (i==0 || val<minVal) {
					minVal = val; minIdx = i;
				}
			}
			
			xDataVal = x[minIdx];
			yDataVal = y[minIdx];			
			dataTipSet.add(this);
		}

		/** Schedules a data tip to be deleted */
		private void remove(){
			/*
			 * This will add the DataTip to a list of objects to be deleted. 
			 * By doing this, it is possible to schedule deletion while 
			 * iterating over a list of DataTips without causing a concurrent
			 * modification exception.
			 */
			deleteDataTip.add(this);
		}

		private void paint(Graphics2D g2){

			g2.setColor(axisColor);
			
			xDataPixel = (int) (Mx*xDataVal + Bx);
			yDataPixel = (int) (My*yDataVal + By);

			/// Check if the data tip left the screen -> remove || plot
			if (xDataPixel < plot_x_low || xDataPixel > plot_x_upp) {
				this.remove();
			} else if (yDataPixel > plot_y_low || yDataPixel < plot_y_upp) {
				this.remove();
			} else {
				
				/// Plot a little "+" sign at location of data tip:
				g2.setStroke(new BasicStroke(axisLineWidth));
				g2.drawLine(xDataPixel, yDataPixel - axisTickMarkLength/2,
							xDataPixel, yDataPixel + axisTickMarkLength/2);
				g2.drawLine(xDataPixel - axisTickMarkLength/2, yDataPixel,
						xDataPixel + axisTickMarkLength/2, yDataPixel);
				
				
				/// Figure out where to put the label
				int xAnchor, yAnchor;
				int xShift = axisFontSize/2;
				int yShift = (plot_y_low - plot_y_upp)/3;
				if (xDataPixel<(plot_x_low + plot_x_upp)/2){
					xAnchor = xDataPixel + xShift;
				} else {  // Shift label to prevent running off window
					xAnchor = (int) (xDataPixel - xShift -
							(Math.max(axisFontWidthX,axisFontWidthY) +
									3*fontAspectRatio*axisFontSize));
				}
				if(yDataPixel>(plot_y_low + plot_y_upp)/2){
					yAnchor = yDataPixel - yShift ;
				} else { 
					yAnchor = yDataPixel + yShift ;
				}
	
				/// Draw the label
				g2.setFont(new Font(fontName, Font.PLAIN, axisFontSize));
				g2.drawString(String.format("x: "+axisStringFormatX, xDataVal),
						xAnchor, yAnchor - axisFontSize/2);
				g2.drawString(String.format("y:"+ axisStringFormatY, yDataVal),
						xAnchor, yAnchor + axisFontSize/2);
				
				/// Draw a connecting line
				g2.drawLine(xDataPixel, yDataPixel,  xDataPixel, yAnchor);
				
			}


		}
	}


}
