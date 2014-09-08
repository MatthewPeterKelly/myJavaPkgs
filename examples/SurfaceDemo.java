package examples;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFrame;

import mpk_dsc.Surface;

/**
 * This class demonstrates how to use the Surface object to store a surface 
 * function, interpolate from it, and plot it as an image
 * @author matt
 *
 */
public class SurfaceDemo {

	public static void main(String[] args) throws FileNotFoundException {
			
			String fullPath = "/home/matt/GitHub/myJavaPkgs/myJavaPkgs/examples/surface.txt";
			File file = new File(fullPath);             // create a File object
			Surface  surface = new Surface(file);

			surface.print();  // Prints the data from the file to the console

			/// Example of use for interpolation
			double x = 0.4;
			double y = 3.8;
			double z = surface.interp(x,y);
			System.out.printf("x: %8.6f \n",x);
			System.out.printf("y: %8.6f \n",y);
			System.out.printf("z(x,y): %8.6f \n",z);

			/// Example of use for plotting as an image
			JFrame frame = new JFrame("Surface Plot");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(surface.plot);
			frame.setSize(600,600);
			frame.setVisible(true);        

	}

}
