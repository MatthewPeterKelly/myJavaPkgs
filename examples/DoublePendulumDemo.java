package examples;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import mpk_dsc.DoublePendulum;
import mpk_gui.RingBuffer;
import mpk_gui.ScopePanel;
import mpk_gui.Timer;


/** A demonstration for a single damped pendulum with control */
public class DoublePendulumDemo {

	public static double frames_per_second = 30;

	public static void main (String args[]){

		/// Create a new JFrame and add the two scopes to it
		JFrame application = new JFrame();
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/// Create the pendulum
		DoublePendulum doublependulum = new DoublePendulum();

		/// Data Logging
		double buffer_duration = 7.5;  // Seconds
		int nBuffer = (int) (buffer_duration*frames_per_second);   /// Keep 10 seconds worth of data
		RingBuffer th = new RingBuffer(nBuffer);
		RingBuffer phi = new RingBuffer(nBuffer);
		RingBuffer t = new RingBuffer(nBuffer);

		ScopePanel scopeAngle = new ScopePanel(t,th);
		scopeAngle.xDataIsMonotonic = true;
		scopeAngle.setAxisExtentsY(-15,15);
		scopeAngle.xLabel = "Time (s)";
		scopeAngle.yLabel = "Angle 1 (rad)";
		scopeAngle.title = "";
		scopeAngle.lineWidth = 2;
		scopeAngle.setSize(300, 450);

		ScopePanel scopeRate = new ScopePanel(t,phi);
		scopeRate.xDataIsMonotonic = true;
		scopeRate.setAxisExtentsY(-15,15);
		scopeRate.xLabel = "Time (s)";
		scopeRate.yLabel = "Angle 2 (rad)";
		scopeRate.title = "";
		scopeRate.lineWidth = 2;
		scopeRate.setSize(300, 450);

		/// Assemble JFrame
		JPanel scopePanel = new JPanel();
		scopePanel.setLayout(new GridLayout(2,1));
		scopePanel.add(scopeAngle);
		scopePanel.add(scopeRate);

		application.setLayout(new GridLayout(1,2));
		application.add(scopePanel);
		application.add(doublependulum.plot);
		application.setSize (1200, 1000);  //  (width, height)
		application.setVisible(true);

		/// Animation time!
		double time = 0; double dt = 1.0/frames_per_second;
		th.put(doublependulum.z[0]);
		phi.put(doublependulum.z[1]);
		t.put(time);
		Timer timer = new Timer(0.1,time);
		while (true){
			doublependulum.timeStep(dt);
			time += dt;

			th.put(doublependulum.z[0]);
			phi.put(doublependulum.z[1]);
			t.put(time);

			doublependulum.plot.repaint();
			scopeAngle.update();
			scopeRate.update();

			timer.smartPause(time);		
		}

	}

}