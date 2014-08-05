package mpk_DEMO;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import mpk_gui.IO_Boolean;
import mpk_gui.IO_Double;
import mpk_gui.RingBuffer;
import mpk_gui.ScopePanel;
import mpk_gui.Timer;

/** This class is used to demonstrate how the scope panel works */
public class ScopePanelTester {

	public static IO_Double forcingAmplitude, forcingFreq;
	public static IO_Boolean showColor;
	
	public static void main (String args[]){

		/// Set up the timing variables
		double[] tSpan = new double[] {0,200};
		double dt = 0.01;
		int n = (int) ((tSpan[1]-tSpan[0])/dt);
		
		/// Create the buffers for plotting
		int nBuffer = (int) (10/dt);   /// Keep 10 seconds worth of data
		RingBuffer x = new RingBuffer(nBuffer);
		RingBuffer v = new RingBuffer(nBuffer);
		RingBuffer t = new RingBuffer(nBuffer);

		/// Create an IO_Double for interfacing with the dynamics
		forcingFreq = new IO_Double(0.2,1.0,5.0,"forcing frequencey");
		forcingAmplitude = new IO_Double(0.1,1.0,4.0,"forcing amplitude");
		showColor = new IO_Boolean("showColor", false);
		
		/// Create a scope panel to show the position 
		ScopePanel plotPosition = new ScopePanel(t,x);
		plotPosition.xDataIsMonotonic = true;
		plotPosition.setAxisExtentsY(-15,15);
		plotPosition.xLabel = "Time (s)";
		plotPosition.yLabel = "Angle (rad)";
		plotPosition.title = "Spring-Mass-Damper";
		plotPosition.lineWidth = 2;
		
		/// Create a scope panel to show velocity
		ScopePanel plotVelocity = new ScopePanel(t,v);
		plotVelocity.xDataIsMonotonic = true;
		plotVelocity.setAxisExtentsY(-3,3);
		plotVelocity.xLabel = "Time (s)";
		plotVelocity.yLabel = "Angular Rate (rad/s)";
		plotVelocity.title = "Driven-Damped-Pendulum";
		plotVelocity.lineWidth = 2;
		
		/// Create a new JFrame and add the two scopes to it
		JFrame application = new JFrame();
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		application.setLayout(new GridLayout(3,1));
		application.add(plotPosition);
		application.add(plotVelocity);
		
		/// Add a few IO things for fun
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new GridLayout(3,1));		
		ioPanel.add(forcingFreq.slider);
		ioPanel.add(forcingAmplitude.slider);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,2));	
		buttonPanel.add(Timer.slowMotion.checkBox);
		buttonPanel.add(showColor.checkBox);
		ioPanel.add(buttonPanel);
		application.add(ioPanel);
		
		application.setSize (600, 800);
		application.setVisible(true);
		
		/// Initialize the dynamical system (Driven-Damped-Pendulum)
		double tOld = tSpan[0]; t.put(tOld);
		double xOld = 06; x.put(xOld);
		double vOld = 0.2; v.put(vOld);
		double tNew, xNew, vNew;
		
		/// Create a timer to get real-time plotting speed
		Timer timer = new Timer(0.1,tOld);
		
		/// Run a simple simulation, adding each new point to the plots
		double[] z = new double[2]; double dz[] = new double[2];
		for (int i=1; i<n; i++){
			tNew = tOld + dt;
			z[0] = xOld; z[1] = vOld;
			dz = springMassDamper(tOld, z);  // dynamics
			xNew = xOld + dt*dz[0];  // euler integration
			vNew = vOld + dt*dz[1];
			
			/// Add the new points to the plots
			t.put(tNew); tOld = tNew;
			x.put(xNew); xOld = xNew;
			v.put(vNew); vOld = vNew;
			
			timer.smartPause(tNew); // Attempt to synchronize the loop with tNew
			
			if (showColor.get()){
				plotPosition.lineColor = Color.red;
				plotVelocity.lineColor = Color.blue;
			} else {
				plotPosition.lineColor = Color.black;
				plotVelocity.lineColor = Color.black;
			}
			
			/// Tell the plots to update
			plotPosition.update();
			plotVelocity.update();
			
		}
	}
	
	
	/** Computes the dynamics for a driven-damped-pendulum */
	private static double[] springMassDamper(double t, double[] z){
		
		double x = z[0];
		double v = z[1];
		
		double a = forcingAmplitude.get();
		double w = forcingFreq.get();
		double tau = a*Math.cos(w*t);
		
		double dx = v;
		double dv = tau - 0.1*v-Math.sin(x);
		
		return new double[] {dx, dv};
	}

}