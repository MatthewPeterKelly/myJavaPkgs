package mpk_dsc;

import mpk_gui.DrawPanel;

/** This is where the drawing takes place */
public class DoublePendulum implements DynamicalSystem{

	// The state of the pendulum {th,phi,dth,dphi}
	public double[] z = new double[] {0.2, 0.42, 0.0, 0.0};   
	public double[] dz = new double[4];  // The derivative of the state

	/* parameters for the double pendulum */
	private double m1 = 1.0;  // (kg) mass of bob 1 (end of first massless link)
	private double m2 = 1.0;  // (kg) mass of bob 2 (end of second massless link)
	private double g = 9.81; // (m/s^2) gravity
	private double l = 1.0;  // (m) length of one link (both are the same)

	private Integrator integrator;

	public PendulumPlotter plot;

	/** Constructor! */
	public DoublePendulum(){
		super(); // Create a draw panel 

		integrator = new Integrator(this);
		integrator.method = Integrator.Method.RK4;
		integrator.number_of_substeps = 50;  // Between animation frames

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

	/** Computes the dynamics for a double pendulum. These equations were
	 * derived automatically using Matlab */
	@Override
	public double[] dynamics(double[] z){

		// Declare variables:                                     
		double th, phi, Dth, Dphi;    //State variables          
		double M1, M2, F1_x, F1_y, F2_x, F2_y;   //Input variables       

		// Matlab generated local variables                     
		double  tmp2, tmp3, tmp4, tmp5, tmp6, tmp7, tmp8, tmp9;  
		double  DDth;  
		double  tmp10, tmp11, tmp12, tmp13, tmp14, tmp15, tmp16, tmp17, tmp18, tmp19, tmp20, tmp21, tmp22, tmp23;  
		double  DDphi;                 
		
		// Store the state variables                         
		th     = z[0];                                     
		phi    = z[1];                                     
		Dth    = z[2];                                     
		Dphi   = z[3];                                     

		// Store the input variables (none for now)                    
		M1   = 0; // U[0];                                       
		M2   = 0; // U[1];                                       
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

		// Pack up the state derivative                    
		dz[0] = Dth;                                     
		dz[1] = Dphi;                                     
		dz[2] = DDth;                                     
		dz[3] = DDphi;                                     

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
			xLow = -2.1*l;
			xUpp = 2.1*l;
			yLow = -2.1*l;
			yUpp = 2.1*l;
			setSize (600,600);
		}

		@Override
		public void paint() {
			double x1 = l*Math.cos(z[0]);
			double y1 = l*Math.sin(z[0]);
			double x2 = x1 + l*Math.cos(z[1]);
			double y2 = y1 + l*Math.sin(z[1]);
			setLineWidth(6);
			fillCircle(0,0,0.05*l);	
			drawLine(0,0,x1,y1);
			fillCircle(x1,y1,0.15*l);
			drawLine(x1,y1,x2,y2);
			fillCircle(x2,y2,0.15*l);	
			setLineWidth(3);
		}


	}

}
