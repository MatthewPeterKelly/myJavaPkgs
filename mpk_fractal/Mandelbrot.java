package mpk_fractal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class Mandelbrot extends JPanel implements MouseInputListener, KeyListener{

	/** Auto-generated voo-doo magic by Eclipse */
	private static final long serialVersionUID = -7536655286231130283L;

	/** Conversion between pixels and real numbers */
	private double scale = 200.0; // scale = pixels / unit 

	/** How much to change scale on each click */
	private double scaleRate = 0.4; // 0 = no change, 1 = double or halve

	/** How many pixels to move with each key press */
	private int panRate = 25;

	/** Maximum number of iterations */
	private int maxIter = 250;

	/** The center point of the image */
	private double[] center = {0.0,0.0};

	private BufferedImage image; 

	/** Construct a new Mandelbrot set object */
	public Mandelbrot() {
		super();
		addMouseListener(this);
		addKeyListener(this);
	};

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
					// Magic from:  http://linas.org/art-gallery/escape/escape.html
					iter++; zR = zr*zr - zi*zi + cr; zI = 2*zr*zi + ci;	zi = zI; zr = zR;
					iter++; zR = zr*zr - zi*zi + cr; zI = 2*zr*zi + ci;	zi = zI; zr = zR;
					value = Math.sqrt(zi*zi + zr*zr);
					value = iter - (Math.log(Math.log(value))/Math.log(2.0));
					value = (value)/((double)(maxIter));
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

	/** Entry point method. Creates a JFrame GUI */
	public static void main(String[] args) {
		Mandelbrot mandelbrot = new Mandelbrot();
		JFrame frame = new JFrame("Mandelbrot Set");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(mandelbrot);
		frame.setSize((int)(4.5*mandelbrot.scale),(int)(4.5*mandelbrot.scale));
		frame.setVisible(true);        
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
		int w = getWidth();
		int h = getHeight();
		double scaleNew;
		if (e.getButton() == 1){  // LEFT CLICK  --  zoom in
			scaleNew = scale*(1.0+scaleRate);
		} else { // RIGHT CLICK -- zoom out
			scaleNew = scale*(1.0-scaleRate);
		}
		center[0] = (e.getX()-0.5*w)*(1/scale - 1/scaleNew) + center[0];
		center[1] = -(e.getY()-0.5*h)*(1/scale - 1/scaleNew) + center[1];
		scale = scaleNew;
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseDragged(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}

	///////////////////////////////////////////////////////////////////////////
	////                  Keyboard Interaction stuff                       ////
	///////////////////////////////////////////////////////////////////////////

	@Override
	public void keyPressed(KeyEvent e) {		
		switch(e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			center[0] -= ((double)(panRate))/scale;
			break;
		case KeyEvent.VK_RIGHT:
			center[0] += ((double)(panRate))/scale;
			break;	
		case KeyEvent.VK_UP:
			center[1] -= ((double)(panRate))/scale;
			break;
		case KeyEvent.VK_DOWN:
			center[1] += ((double)(panRate))/scale;
			break;
		default:
		}}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}


}
