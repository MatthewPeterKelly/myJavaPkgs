package mpk_dsc;

import mpk_gui.DrawPanel;

/** This is where the drawing takes place */
public class Pendulum implements DynamicalSystem{

	public double[] z = new double[] {2.4,0.0};   // The state of the pendulum
	public double[] dz = new double[2];  // The derivative of the state
	public double tau = 0;

	/* m*l*l*ddTh + c*dTh + m*g*l*Math.sin(th) = tau
	 * ddTh = tau/(m*l*l) - (c*dTh)/(m*l*l) - (g/l)*Math.sin(th) */
	private double m = 1.0;  // (kg) mass
	private double g = 9.81; // (m/s^2) gravity
	private double l = 1.0;  // (m) length
	private double c = 0.15;  // (N*m*s) damping

	private Integrator integrator;

	public PendulumPlotter plot;
	
	/** Constructor! */
	public Pendulum(){
		super(); // Create a draw panel 

		integrator = new Integrator(this);
		integrator.method = Integrator.Method.RK4;
		integrator.number_of_substeps = 25;
		
		plot = new PendulumPlotter();
	}

	/** Returns the current state vector */
	@Override
	public double[] getState(){
		return z;
	}

	/** Sets the current state */
	@Override
	public void setState(double[] z){
		this.z = z;
	}

	/** Computes the dynamics for a single pendulum with damping and control 
	 * m*l*l*ddTh + c*dTh + m*g*l*Math.sin(th) = tau
	 * ddTh = tau/(m*l*l) - (c*dTh)/(m*l*l) - (g/l)*Math.sin(th) */
	@Override
	public double[] dynamics(double[] z){

		double th = z[0];
		double w = z[1];

		dz[0] = w;
		dz[1] = tau/(m*l*l) - (c*w)/(m*l*l) - (g/l)*Math.sin(th);

		return dz;

	}

	@Override
	public void timeStep(double dt) {
		integrator.timeStep(dt);		
	}
	
	/** ********************************************************************
	 * A class for plotting the pendulum
	 */
	@SuppressWarnings("serial")
	public class PendulumPlotter extends DrawPanel{
	
		/** Create a new pendulum plotter */
		public PendulumPlotter(){
			super();
			xLow = -1.25*l;
			xUpp = 1.25*l;
			yLow = -1.25*l;
			yUpp = 1.25*l;
			setSize (600,600);
		}

		@Override
		public void paint() {
			double x = l*Math.sin(z[0]);
			double y = -l*Math.cos(z[0]);
			setLineWidth(6);
			drawLine(0,0,x,y);
			fillCircle(0,0,0.1*l);
			setColor(0,0,255);			
			fillCircle(x,y,0.2*l);
			setColor(0,0,0);			
			drawCircle(x,y,0.2*l);
			setLineWidth(3);
		}

		
	}
	
}