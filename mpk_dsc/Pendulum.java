package mpk_dsc;

import mpk_gui.DrawPanel;

/** This is where the drawing takes place */
public class Pendulum implements DynamicalSystem{

	private double th0 = 1.8;  // Initial angle
	private double w0 = 0.0;  // Initial rate
	private double[] th = new double[1];  // Angle
	private double[] w = new double[1];   // Rate
	
	public double tau = 0;

	private double time = 0.0;
	public boolean displayTime = true; //Prints the simulation time on plot
	
	/* m*l*l*ddTh + c*dTh + m*g*l*Math.sin(th) = tau
	 * ddTh = tau/(m*l*l) - (c*dTh)/(m*l*l) - (g/l)*Math.sin(th) */
	private double m = 1.0;  // (kg) mass
	private double g = 10.0; // (m/s^2) gravity
	private double l = 0.3;  // (m) length
	private double c = 0.0;  // (N*m*s) damping

	private double maxTimeStep = 0.02;
	
	private Integrator integrator;

	public PendulumPlotter plot;
	
	/** Constructor! */
	public Pendulum(){
		super(); // Create a draw panel 

		integrator = new Integrator(this);
		integrator.method = Integrator.Method.SYM2;  // {EULER, RK4, SYM1, SYM2}
		
		reset();
		
		plot = new PendulumPlotter();
	}
		
	/** Computes the dynamics for a single pendulum with damping and control 
	 * m*l*l*ddTh + c*dTh + m*g*l*Math.sin(th) = tau
	 * ddTh = tau/(m*l*l) - (c*dTh)/(m*l*l) - (g/l)*Math.sin(th) */
	@Override
	public double[] dynamics(double[] p, double[] v){

		double th = p[0];
		double w = v[0];
	
		return new double[] {tau/(m*l*l) - (c*w)/(m*l*l) - (g/l)*Math.sin(th)};

	}

	public void reset(){
		th[0] = th0;
		w[0] = w0;
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
//		double x = l*Math.sin(th[0]);
		double y = -l*Math.cos(th[0]);
		double dx = l*Math.cos(th[0])*w[0];
		double dy = l*Math.sin(th[0])*w[0];
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
			double x = l*Math.sin(th[0]);
			double y = -l*Math.cos(th[0]);
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

	@Override
	public double[] getPos() {
		return th;
	}

	@Override
	public double[] getVel() {
		return w;
	}

	@Override
	public void setPos(double[] p) {
		th[0] = p[0];		
	}

	@Override
	public void setVel(double[] v) {
		w[0] = v[0];		
	}

	
}