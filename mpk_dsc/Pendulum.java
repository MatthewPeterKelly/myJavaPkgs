package mpk_dsc;

import mpk_gui.DrawPanel;

/** This is where the drawing takes place */
public class Pendulum implements DynamicalSystem{

	private double[] z0 = new double[] {1.8,0.0};  // Initial state
	
	private double[] z = new double[2];   // The state of the pendulum
	private double[] dz = new double[2];  // The derivative of the state
	public double tau = 0;

	private double time = 0.0;
	public boolean displayTime = true; //Prints the simulation time on plot
	
	/* m*l*l*ddTh + c*dTh + m*g*l*Math.sin(th) = tau
	 * ddTh = tau/(m*l*l) - (c*dTh)/(m*l*l) - (g/l)*Math.sin(th) */
	private double m = 1.0;  // (kg) mass
	private double g = 10.0; // (m/s^2) gravity
	private double l = 0.3;  // (m) length
	private double c = 0.0;  // (N*m*s) damping

	private double maxTimeStep = 0.01;
	
	private Integrator integrator;

	public PendulumPlotter plot;
	
	/** Constructor! */
	public Pendulum(){
		super(); // Create a draw panel 

		integrator = new Integrator(this);
		integrator.method = Integrator.Method.RK4;
		
		reset();
		
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
	
	public void reset(){
		z[0] = z0[0]; 
		z[1] = z0[1];
		time = 0.0;
	}
	
	/** Sets the current value of the damping constant */
	public void setDamping(double c){
		this.c = c;
	}
	
	/** Set the maximum time step for the integrator */
	public void setMaxTimeStep(double dt){
		maxTimeStep = dt;
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
	
	@Override
	public void simulate(double duration){
		int nSteps = (int)(Math.ceil(duration/maxTimeStep));
		integrator.timeStep(duration/nSteps, nSteps);
	}
	
	/** Return the system's mechanical energy
	 * @return energy[] = {total, kinetic, potential}*/
	public double[] getEnergy(){
//		double x = l*Math.sin(z[0]);
		double y = -l*Math.cos(z[0]);
		double dx = l*Math.cos(z[0])*z[1];
		double dy = l*Math.sin(z[0])*z[1];
		double kinetic = 0.5*m*(dx*dx+dy*dy);
		double potential = m*g*(y+l); 
		double total = kinetic + potential;
		return new double[] {total, kinetic, potential};
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
			if (displayTime){
				drawString(String.format("Time: %6.4f (s)",time),xLow+0.1*l,yUpp-0.1*l);
			}
		}

		
	}

	@Override
	public double getTime() {
		return time;
	}

	@Override
	public void setTime(double t) {
		time = t;		
	}
	
}