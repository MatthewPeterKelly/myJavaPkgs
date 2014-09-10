package mpk_fractal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Mandelbrot {

	public GUI gui;

	/** Conversion between pixels and real numbers */
	private double scale = 200.0; // scale = pixels / unit 

	/** Maximum number of iterations */
	private int maxIter = 50;

	/** The center point of the image */
	private double[] center = {0.0,0.0};

	/** Creates a new Mandelbrot Set object. */
	public Mandelbrot(){

		gui = new GUI();

	}

	/** A class for handling the graphics and user interaction */
	private class GUI extends JPanel {

		private BufferedImage image; 

		/** Create a new GUI object */
		private GUI() {

		}

		/** Draws a new fractal every time that the JFrame paints */
		public void paintComponent(Graphics g){
			super.paintComponent(g);

			int w = getWidth();
			int h = getHeight();
			image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

			double cr,ci; // test point
			double zr,zi; // current iteration
			double zR,zI; // temp variables
			int i,j,iter; // iterators
			double value; // normalized value for plotting
			for (i=0; i<w; i++){  /// Compute the x and y values
				for (j=0; j<h; j++){
					cr = (i-0.5*w)/scale + center[0];
					ci = -(j-0.5*h)/scale + center[1];
					zr = 0.0; zi = 0.0;
					for (iter=0; iter<maxIter; iter++){
						zR = zr*zr - zi*zi + cr;
						zI = 2*zr*zi + ci;
						zi = zI; zr = zR;
						if (zi*zi + zr*zr > 4.0) break;
					}
					if (iter==maxIter){
						value = 0.0;
					} else {
						value = ((double)(iter))/((double)(maxIter));
					}
					image.setRGB(i, j, colorMap(value));		
				}
			}

			g.drawImage(image, 0,0, null);
		}

		/** Computes the color map to be used for plotting 
		 * @param x = value between 0.0 and 1.0
		 * @return rgb color for image.setRGB */
		private int colorMap(double x){

			int rgb;
			double val, sat;
			float hue = 1.0f/3.0f;

			x = 2.0*x;
			if (x<0.0) x = 0.0;
			if (x>2.0) x = 2.0;

			sat = 2.0f-x; if (sat>1.0f) sat = 1.0f;
			val = x; if (val>1.0f) val = 1.0f;
			rgb = Color.HSBtoRGB(hue,(float) sat, (float) val); // Black -- Blue -- White

			return rgb;
		}

	}

	/** Entry point method. Creates a JFrame GUI */
	public static void main(String[] args) {
		Mandelbrot mandelbrot = new Mandelbrot();
		JFrame frame = new JFrame("Mandelbrot Set");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(mandelbrot.gui);
		frame.setSize((int)(4*mandelbrot.scale),(int)(4*mandelbrot.scale));
		frame.setVisible(true);        
	}

}
