package examples;

import java.awt.BorderLayout;

import javax.swing.JFrame;

/** A demonstration for a double pendulum*/
public class DoublePendulumDemo {

	public static void main(String[] args) {

		JFrame frame = new JFrame("Double Pendulum Animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        DoublePendulumGui gui = new DoublePendulumGui();
        frame.add(gui,BorderLayout.CENTER);
                
        gui.timer.start();
        
		frame.setSize (1200, 800);  //  (width, height)
        frame.setVisible(true);        
		
	}

}