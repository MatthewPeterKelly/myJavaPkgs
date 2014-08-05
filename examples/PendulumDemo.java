package mpk_DEMO;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import mpk_dsc.DynamicalSystem;
import mpk_dsc.Integrator;
import mpk_dsc.Pendulum;
import mpk_gui.IO_Boolean;
import mpk_gui.IO_Double;
import mpk_gui.RingBuffer;
import mpk_gui.ScopePanel;
import mpk_gui.Timer;


/** A demonstration for a single damped pendulum with control */
public class PendulumDemo {

	public static double frames_per_second = 45;

	public static void main (String args[]){

		/// Create a new JFrame and add the two scopes to it
		JFrame application = new JFrame();
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/// Create the pendulum
		Pendulum pendulum = new Pendulum();

		/// Data Logging
		double buffer_duration = 7.5;  // Seconds
		int nBuffer = (int) (buffer_duration*frames_per_second);   /// Keep 10 seconds worth of data
		RingBuffer th = new RingBuffer(nBuffer);
		RingBuffer w = new RingBuffer(nBuffer);
		RingBuffer t = new RingBuffer(nBuffer);
		RingBuffer tau = new RingBuffer(nBuffer);

		ScopePanel scopeAngle = new ScopePanel(t,th);
		scopeAngle.xDataIsMonotonic = true;
		scopeAngle.setAxisExtentsY(-7,7);
		scopeAngle.xLabel = "Time (s)";
		scopeAngle.yLabel = "Angle (rad)";
		scopeAngle.title = "";
		scopeAngle.lineWidth = 2;
		scopeAngle.setSize(300, 300);

		ScopePanel scopeRate = new ScopePanel(t,w);
		scopeRate.xDataIsMonotonic = true;
		scopeRate.setAxisExtentsY(-7,7);
		scopeRate.xLabel = "Time (s)";
		scopeRate.yLabel = "Rate (rad)";
		scopeRate.title = "";
		scopeRate.lineWidth = 2;
		scopeRate.setSize(300, 300);

		ScopePanel scopeTorque = new ScopePanel(t,tau);
		scopeTorque.xDataIsMonotonic = true;
		scopeTorque.setAxisExtentsY(-15,15);
		scopeTorque.xLabel = "Time (s)";
		scopeTorque.yLabel = "Torque (Nm)";
		scopeTorque.title = "";
		scopeTorque.lineWidth = 2;
		scopeTorque.setSize(300, 300);

		/// Assemble JFrame
		JPanel scopePanel = new JPanel();
		scopePanel.setLayout(new GridLayout(3,1));
		scopePanel.add(scopeAngle);
		scopePanel.add(scopeRate);
		scopePanel.add(scopeTorque);

		application.setLayout(new GridLayout(1,2));
		application.add(scopePanel);
		application.add(pendulum.plot);
		application.setSize (1200, 1000);  //  (width, height)
		application.setVisible(true);

		/// Animation time!
		double time = 0; double dt = 1.0/frames_per_second;
		th.put(pendulum.z[0]);
		w.put(pendulum.z[1]);
		tau.put(pendulum.tau);
		t.put(time);
		Timer timer = new Timer(0.1,time);
		while (true){
			pendulum.timeStep(dt);
			time += dt;

			th.put(pendulum.z[0]);
			w.put(pendulum.z[1]);
			tau.put(pendulum.tau);
			t.put(time);

			pendulum.plot.repaint();
			scopeAngle.update();
			scopeRate.update();
			scopeTorque.update();

			timer.smartPause(time);		
		}

	}

}