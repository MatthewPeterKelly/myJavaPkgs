package mpk_dsc;

import mpk_gui.DrawPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class is used to store a surface function. Bilinear interpolation is used to sample an arbitrary location within the grid
 * @author matt
 *
 */
public class Surface {

	private Double xLow; // The minimum value along the x axis
	private Double xUpp; // The maximum value along the x axis
	private Double yLow; // The minimum value along the y axis
	private Double yUpp; // The maximum value along the y axis

	private Integer nx; // Number of grid points along the x axis
	private Integer ny; // Number of grid points along the y axis
	private Double mx; // Number of grid cells along x axis
	private Double my; // Number of grid cells along y axis

	private Double[][] z; // z[i][j] = z(x[i],y[j])
	private Double[] x, y;
	private Double zLow;
	private Double zUpp;

	public SurfacePlotter plot;

	private final double EPSILON = 1e-10; // Numbers closer than this are considered identical

	/** Constructs a Surface object from a data file. 
	 * File parsing tutorial:  
	 * http://pages.cs.wisc.edu/~hasti/cs368/JavaTutorial/NOTES/JavaIO_Scanner.html#fileIN
	 * 
	 * I've written a matlab function to produce this file:
	 * createSurfaceFile.m
	 * 
	 * An Example file: (No leading spaces)
	 * 
	 * xLow 0.000
	 * xUpp 1.000
	 * yLow 0.000
	 * yUpp 6.2831853071795862
	 * nx 5
	 * ny 7
	 * 3.000 3.250 3.500 3.750 4.000 
	 * 2.000 2.250 2.500 2.750 3.000 
	 * 0.004 0.254 0.504 0.754 1.004 
	 * -1.000 -0.750 -0.500 -0.250 0.000 
	 * -0.009 0.250 0.500 0.750 1.00 
	 * 2.000 2.250 2.500 2.750 3.000 
	 * 3.000 3.250 3.500 3.750 4.000 
	 * 
	 * @throws FileNotFoundException 
	 */
	public Surface(File file) throws FileNotFoundException {

		if ( file.exists() )                          // check that the file exists
		{                                          
			Scanner inFile = new Scanner( file );
			String line;
			String delims = "[ ]+"; 
			String[] tokens;

			int lineNum = 0;
			while ( inFile.hasNext() )
			{
				lineNum++;
				line = inFile.nextLine();   // read the next line

				tokens = line.split(delims);

				/// This is a very simple algorithm - no flexibility.
				switch (lineNum) {
				case 1:	if ("xLow".equals(tokens[0])){
					xLow = Double.parseDouble(tokens[1]); } break;
				case 2:	if ("xUpp".equals(tokens[0])){
					xUpp = Double.parseDouble(tokens[1]); } break;
				case 3:	if ("yLow".equals(tokens[0])){
					yLow = Double.parseDouble(tokens[1]); } break;
				case 4:	if ("yUpp".equals(tokens[0])){
					yUpp = Double.parseDouble(tokens[1]); } break;
				case 5:	if ("nx".equals(tokens[0])){
					nx = Integer.parseInt(tokens[1]); } break;
				case 6:	if ("ny".equals(tokens[0])){
					ny = Integer.parseInt(tokens[1]); } 

				/// Assume that the first part was read correctly. Now allocate memory.
				mx = nx-1.0; my = ny-1.0;   // number grid spaces
				x = new Double[nx];  /// Allocate memory
				y = new Double[ny];
				z = new Double[nx][ny];
				for (int i=0; i<nx; i++){  /// Compute the x and y values
					for (int j=0; j<ny; j++){
						x[i] = xLow + (xUpp-xLow)*(i/mx);
						y[j] = yLow + (yUpp-yLow)*(j/my);
					}
				}				
				break;
				} 

				if (lineNum > 6 && lineNum <= (6+ny)){
					/// Start parsing the data:
					for (int i=0; i<nx; i++){ 
						z[i][lineNum-7] = Double.parseDouble(tokens[i]); }
				}
			}

			// close the Scanner object attached to the file
			inFile.close();

			/// Compute the bounds on z:
			double val;
			zLow = Double.MAX_VALUE;
			zUpp = Double.MIN_VALUE;
			for (int i=0; i<nx; i++){  /// Compute the x and y values
				for (int j=0; j<ny; j++){
					val = z[i][j];
					if (val < zLow) zLow = val;
					else if (val > zUpp) zUpp = val;
				}
			}

			/// Create a new surface plotter
			plot = new SurfacePlotter(xLow,xUpp,yLow,yUpp);

		} else {
			throw new FileNotFoundException();
		}

	}

	/**
	 * Interpolate from 2D data. Any inputs that are out of the valid 
	 * domain will be clamped to the valid domain before proceeding.
	 * 
	 * Algorithm taken from wikipedia:
	 * http://en.wikipedia.org/wiki/Bilinear_interpolation
	 * 
	 * @param u - The desired x-coordinate
	 * @param v - The desired y-coordinate
	 * @return q - The value obtained via interpolation
	 */
	public double interp(double u, double v){

		/// Clamp values where necessary:
		if (u<xLow) u = xLow;
		else if (u>xUpp) u = xUpp;
		if (v<yLow) v = yLow;
		else if (v>yUpp) v = yUpp;

		/// Compute the effective index along each coordinate
		double uIdx = mx*((u-xLow)/(xUpp-xLow));
		double vIdx = my*((v-yLow)/(yUpp-yLow));

		/// Determine the correct indices to use
		int iLow = (int) Math.floor(uIdx);
		int iUpp = (int) Math.ceil(uIdx);
		int jLow = (int) Math.floor(vIdx);
		int jUpp = (int) Math.ceil(vIdx);

		/// Rename the values of the function at these points:
		double q11 = z[iLow][jLow];
		double q12 = z[iLow][jUpp];
		double q21 = z[iUpp][jLow];
		double q22 = z[iUpp][jUpp];

		/// Rename the values of the grid at these points:
		double u1 = x[iLow];
		double u2 = x[iUpp];
		double v1 = y[jLow];
		double v2 = y[jUpp];

		/// Interpolate along the x direction:
		double du = u2-u1; double r1, r2;
		if (Math.abs(du) < EPSILON){  // Prevent divide by zero
			r1 = 0.5*q11 + 0.5*q21;
			r2 = 0.5*q12 + 0.5*q22;
		} else {
			r1 = (q11*(u2-u) + q21*(u-u1))/du;
			r2 = (q12*(u2-u) + q22*(u-u1))/du;
		}

		/// Interpolate along the y direction:
		double dv = v2-v1; double q;
		if (Math.abs(dv) < EPSILON) { // Prevent divide by zero
			q = 0.5*r1 + 0.5*r2;
		} else {
			q = (r1*(v2-v) + r2*(v-v1))/dv;
		}
		return q;

	}

	/** Print the data */
	public void print() {

		/// Various things for formatting text and table
		int cellWidth = 8;
		int precision = 4;
		char[] underscore = new char[cellWidth];
		underscore[0] = '|'; 
		for (int idx=1; idx < cellWidth; idx++) underscore[idx] = '_'; 
		char[] xLabel = new char[cellWidth+2];
		xLabel[0] = 'x'; xLabel[1] = '-'; xLabel[2] = '-'; xLabel[3] = '>';
		for (int idx=4; idx < cellWidth+2; idx++) xLabel[idx] = ' '; 
		char[] yLabel = new char[cellWidth+2];
		yLabel[0] = 'y'; yLabel[1] = ':';
		for (int idx=2; idx < cellWidth+2; idx++) yLabel[idx] = ' '; 
		String formatString = "%" + cellWidth + "." + precision +"f";

		/// Print out header line with x values:
		System.out.print(xLabel);
		for (int i=0; i<nx; i++){
			System.out.printf(formatString,x[i]);
		} System.out.print("\n");
		System.out.print(yLabel);
		for (int i=0; i<nx; i++){
			System.out.print(underscore);
		} System.out.print("|\n");

		/// Print out the data lines, starting with 
		for (int j=0; j<ny; j++){
			System.out.printf(formatString,y[j]);
			System.out.print(" |");
			for (int i=0; i<nx; i++){
				System.out.printf(formatString,z[i][j]);
			} System.out.print("\n");
		} System.out.print("\n");

	}

	@SuppressWarnings("serial")
	public class SurfacePlotter extends DrawPanel {

		/// Everything is stored here
		public BufferedImage image;

		/// The hue for the color map
		public float hue = (float) (2.0/3.0);  // Blue

		public float contourHue = (float) (1.0/3.0); // Green
		public double contourWidth = 0.015;  // Range: (0,1)
		public int nContours = 10; // Number of contour lines to display
		public boolean drawContourLines = true;

		public SurfacePlotter(double xLow, double xUpp, double yLow, double yUpp){
			super();
			this.xLow = xLow;
			this.xUpp = xUpp;
			this.yLow = yLow;
			this.yUpp = yUpp;
		}

		@Override
		public void paint() {

			int w = getWidth();
			int h = getHeight();

			/// Create a new BufferedImage
			image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
			float value;
			double xTmp, yTmp, zTmp;

			for (int i=0; i<w; i++){  /// Compute the x and y values
				for (int j=0; j<h; j++){
					xTmp = xLow + (xUpp-xLow)*(i/(w-1.0));
					yTmp = yLow + (yUpp-yLow)*(1.0 - j/(h-1.0)); // Flip Image
					zTmp = interp(xTmp,yTmp);
					value = (float) ((zTmp - zLow)/(zUpp - zLow));	
					image.setRGB(i, j, colorMap(value));		
				}
			}

			g2.drawImage(image, 0,0, null);

		}

		/** Computes the color map to be used for plotting 
		 * @param x = value between 0.0 and 1.0
		 * @return rgb color for image.setRGB */
		private int colorMap(double x){

			int rgb;

			boolean drawContourColorMap = false;
			if (drawContourLines) {
				double h = x*(nContours);
				double h1 = h - Math.floor(h); 
				double h2 = Math.ceil(h) - h;
				if (h1 < contourWidth || h2 < contourWidth){
					drawContourColorMap = true;
				}
			}

			double val, sat;

			if (drawContourColorMap){

				x = 2.0*x;
				if (x<0.0) x = 0.0;
				if (x>2.0) x = 2.0;

				val = 2.0f-x; if (val>1.0f) val = 1.0f;
				sat = x; if (sat>1.0f) sat = 1.0f;
				rgb = Color.HSBtoRGB(contourHue,(float) sat, (float) val); // Black -- Blue -- White

			} else {

				x = 2.0*x;
				if (x<0.0) x = 0.0;
				if (x>2.0) x = 2.0;

				sat = 2.0f-x; if (sat>1.0f) sat = 1.0f;
				val = x; if (val>1.0f) val = 1.0f;
				rgb = Color.HSBtoRGB(hue,(float) sat, (float) val); // Black -- Blue -- White

			}
			return rgb;
		}

	}

}
