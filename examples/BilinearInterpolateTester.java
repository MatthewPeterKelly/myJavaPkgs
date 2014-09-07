package examples;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import mpk_dsc.BilinearInterpolate;

public class BilinearInterpolateTester {

	public static void main(String[] args) {
		
		BilinearInterpolate surface = null;
		
		String fullPath = "/home/matt/GitHub/myJavaPkgs/myJavaPkgs/examples/surface.txt";
		
		File file = new File(fullPath);             // create a File object
		
		try {
			surface = new BilinearInterpolate(file);
			
			surface.print();
			
			double x = 0.4;
			double y = 3.8;
			double z = surface.interp(x,y);
			System.out.printf("x: %8.6f \n",x);
			System.out.printf("y: %8.6f \n",y);
			System.out.printf("z(x,y): %8.6f \n",z);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JFrame frame = new JFrame("Surface Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(surface.plot);
       
        frame.pack();
        frame.setVisible(true);        
	}

}
