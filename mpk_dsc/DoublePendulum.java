package mpk_dsc;

import mpk_gui.DrawPanel;

/** This is where the drawing takes place */
public class DoublePendulum implements DynamicalSystem{

	// The state of the pendulum {th,phi,dth,dphi}
	private double[] th0 = {1.8,0.5};
	private double[] w0 = {0.0, 0.0};
	private double[] th = new double[2];
	private double[] w = new double[2];  // The derivative of the state
	private double time = 0.0;
	private boolean displayTime = true; //Prints the simulation time on plot
	
	/* parameters for the double pendulum */
	private double m1 = 1.0;  // (kg) mass of bob 1 (end of first massless link)
	private double m2 = 1.0;  // (kg) mass of bob 2 (end of second massless link)
	private double g = 9.81; // (m/s^2) gravity
	private double l = 1.0;  // (m) length of one link (both are the same)
	private double damping = 0.0; // Viscous damping in both links
	
	private double maxTimeStep = 0.01;
	private double energyDatum = -m1*g*l + -m2*g*(2*l);
	
	private Integrator integrator;

	public PendulumPlotter plot;

	/** Constructor! */
	public DoublePendulum(){
		super(); // Create a draw panel 

		integrator = new Integrator(this);
		integrator.method = Integrator.Method.RK4;  // {EULER, RK4, SYM1, SYM2}
		
		plot = new PendulumPlotter();
	}
	
	/** Get the system energy
	 * @return {total, kinetic, potential} */
	public double[] getEnergy(){
//		double x1 = l*Math.cos(th[0]);
		double y1 = l*Math.sin(th[0]);
//		double x2 = x1 + l*Math.cos(th[1]);
		double y2 = y1 + l*Math.sin(th[1]);
		double dx1 = -l*Math.sin(th[0])*w[0];
		double dy1 = l*Math.cos(th[0])*w[0];
		double dx2 = dx1 - l*Math.sin(th[1])*w[1];
		double dy2 = dy1 + l*Math.cos(th[1])*w[1];
		
		double kinetic = 0.5*(m1*(dx1*dx1+dy1*dy1) + m2*(dx2*dx2+dy2*dy2));
		double potential = g*(m1*y1 + m2*y2) - energyDatum;
		double total = kinetic + potential;
		
		return new double[] {total, kinetic, potential};		
	}

	/** Computes the dynamics for a double pendulum. These equations were
	 * derived automatically using Matlab */
	@Override
	public double[] dynamics(double[] p, double[] v){

		// Declare variables:                                     
		double th, phi, Dth, Dphi;    //State variables          
		double M1, M2, F1_x, F1_y, F2_x, F2_y;   //Input variables       

		// Matlab generated local variables                     
		double  tmp2, tmp3, tmp4, tmp5, tmp6, tmp7, tmp8, tmp9;  
		double  DDth;  
		double  tmp10, tmp11, tmp12, tmp13, tmp14, tmp15, tmp16, tmp17, tmp18, tmp19, tmp20, tmp21, tmp22, tmp23;  
		double  DDphi;                 
		
		// Store the state variables                         
		th     = p[0];                                     
		phi    = p[1];                                     
		Dth    = v[0];                                     
		Dphi   = v[1];                                     

		// Model viscous damping as an external moment on each joint
		M1 = -Dth*damping;
		M2 = -(Dphi-Dth)*damping;
		
		// For now, no external force acts on the joints                                                     
		F1_x = 0; // U[2];                                       
		F1_y = 0; // U[3];                                       
		F2_x = 0; // U[4];                                       
		F2_y = 0; // U[5];                                       

		// Automatically generated equations:                     
		tmp2 = phi*2.0;  
		tmp3 = tmp2-th;  
		tmp4 = Math.cos(th);  
		tmp5 = Math.sin(th);  
		tmp6 = phi-th;  
		tmp7 = l*l;  
		tmp8 = tmp2-th*2.0;  
		tmp9 = Math.cos(tmp3);  
		DDth =  (1.0/(l*l)*(M1*2.0-M2*2.0-M2*Math.cos(tmp6)*2.0-F1_x*l*tmp5*2.0+F1_y*l*tmp4*2.0-F2_x*l*tmp5+F2_y*l*tmp4-F2_y*l*tmp9+F2_x*l*Math.sin(tmp3)+(Dphi*Dphi)*m2*tmp7*Math.sin(tmp6)*2.0+(Dth*Dth)*m2*tmp7*Math.sin(tmp8)-l*g*m1*tmp4*2.0-l*g*m2*tmp4+l*g*m2*tmp9))/(m1*2.0+m2-m2*Math.cos(tmp8));  
		tmp10 = phi-th;  
		tmp11 = Math.cos(tmp10);  
		tmp12 = Math.cos(phi);  
		tmp13 = Math.sin(phi);  
		tmp14 = th*2.0;  
		tmp15 = phi-tmp14;  
		tmp16 = Math.cos(tmp15);  
		tmp17 = Math.sin(tmp15);  
		tmp18 = m2*m2;  
		tmp19 = l*l;  
		tmp20 = phi*2.0;  
		tmp21 = Dth*Dth;  
		tmp22 = Math.sin(tmp10);  
		tmp23 = -tmp14+tmp20;  
		DDphi =  -(1.0/(l*l)*(M2*m1*-2.0-M2*m2*2.0+M1*m2*tmp11*2.0-M2*m2*tmp11*2.0+tmp18*tmp19*tmp21*tmp22*2.0-F1_x*l*m2*tmp13+F1_y*l*m2*tmp12+F2_x*l*m1*tmp13*2.0-F2_y*l*m1*tmp12*2.0+F2_x*l*m2*tmp13-F2_y*l*m2*tmp12+F1_y*l*m2*tmp16+F1_x*l*m2*tmp17+F2_y*l*m2*tmp16+F2_x*l*m2*tmp17+(Dphi*Dphi)*tmp18*tmp19*Math.sin(tmp23)+l*g*tmp12*tmp18-l*g*tmp16*tmp18+l*g*m1*m2*tmp12-l*g*m1*m2*tmp16+m1*m2*tmp19*tmp21*tmp22*2.0))/(m2*(m1*2.0+m2-m2*Math.cos(tmp23)));  
                 
		return new double[] {DDth, DDphi};

	}

	@Override
	public void timeStep(double dt) {
		integrator.timeStep(dt);
	}
	
	@Override
	public void simulate(double duration){
		int nSteps = (int)(Math.ceil(duration/maxTimeStep));
		integrator.timeStep(duration, nSteps);
	}
	
	/** Sets the current value of the damping constant */
	public void setDamping(double c){
		this.damping = c;
	}
	
	/** Set the maximum time step for the integrator */
	public void setMaxTimeStep(double dt){
		maxTimeStep = dt;
	}

	/** ********************************************************************
	 * A class for plotting the pendulum
	 */
	@SuppressWarnings("serial")
	public class PendulumPlotter extends DrawPanel{

		/** Create a new pendulum plotter */
		public PendulumPlotter(){
			super();
			xLow = -2.1*l;
			xUpp = 2.1*l;
			yLow = -2.1*l;
			yUpp = 2.1*l;
			setSize (600,600);
		}

		@Override
		public void paint() {
			double x1 = l*Math.cos(th[0]);
			double y1 = l*Math.sin(th[0]);
			double x2 = x1 + l*Math.cos(th[1]);
			double y2 = y1 + l*Math.sin(th[1]);
			setLineWidth(6);
			fillRect(-0.1*l, 0.1*l, 0.2*l, 0.2*l);	
			drawLine(0,0,x1,y1);
			fillCircle(x1,y1,0.15*l);
			drawLine(x1,y1,x2,y2);
			fillCircle(x2,y2,0.15*l);	
			setLineWidth(3);
			if (displayTime){
				drawString(String.format("Time: %6.4f (s)",time),xLow+0.1*l,yUpp-0.3*l);
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
		th = p.clone();
	}

	@Override
	public void setVel(double[] v) {
		w = v.clone();
	}

	@Override
	public void reset() {
		th = th0.clone();
		w = w0.clone();
		time = 0.0;		
	}	

}
