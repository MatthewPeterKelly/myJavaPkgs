package mpk_DEMO;
import java.awt.GridLayout;

import javax.swing.JFrame;

import mpk_gui.PlotPanel;


public class PlotPanelTester {

	public static void main (String args[]){

		double[] tSpan = new double[] {0,3};
		double dt = 0.01;
		int n = (int) ((tSpan[1]-tSpan[0])/dt);
		double[] x = new double[n];
		double[] v = new double[n];
		double[] t = new double[n];
		
		t[0] = tSpan[0];
		x[0] = 0.5;
		v[0] = 0;
		
		double[] z = new double[2]; double dz[] = new double[2];
		for (int i=1; i<n; i++){
			t[i] = t[i-1] + dt;
			z[0] = x[i-1]; z[1] = v[i-1];
			dz = springMassDamper(z);  // dynamics
			x[i] = x[i-1] + dt*dz[0];  // Euler integration
			v[i] = v[i-1] + dt*dz[1];
		}

		PlotPanel plotPosition = new PlotPanel();
		plotPosition.xDataIsMonotonic = true;
		plotPosition.xLabel = "Time (s)";
		plotPosition.yLabel = "Position (m)";
		plotPosition.title = "Spring-Mass-Damper";
		plotPosition.setData(t,x);
		
		PlotPanel plotVelocity = new PlotPanel();
		plotVelocity.xDataIsMonotonic = true;
		plotVelocity.xLabel = "Time (s)";
		plotVelocity.yLabel = "Velocity (m/s)";
		plotVelocity.title = "Spring-Mass-Damper";
		plotVelocity.setData(t,v);

		JFrame application = new JFrame();

		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		application.setLayout(new GridLayout(2,1));
	
		application.add(plotPosition);
		application.add(plotVelocity);
		
		application.setSize (600, 800);
		application.setVisible(true);

	}
	
	/** Computes the dynamics for a spring-mass-damper */
	private static double[] springMassDamper(double[] z){
			
		double m = 1;
		double c = 3;
		double k = 25;
		
		double x = z[0];
		double v = z[1];
		
		double dx = v;
		double dv = (-c*v-k*x)/m;
		
		return new double[] {dx, dv};
	}

}