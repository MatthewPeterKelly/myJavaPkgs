package mpk_dsc;

/** This class contains several different integration methods. Note 
 * that the symplectic methods assume that the state is of the form:
 * [x0,x1,...,xN,v0,v1,...,vN] where xi is position, and vi is velocity */
public class Integrator {

	private DynamicalSystem sys;

	/** Determine which method to use */
	public Method method = Method.EULER;

	/// Temp variables
	private double[] p1, p2, p3, p4; // Positions throughout the interval
	private double[] v1, v2, v3, v4; // Velocity throughout the interval
	private double[] a1, a2, a3, a4; // Acceleration throughout the interval
	private int n;  // Dimension of the state space
	
	/** Create a new integrator */
	public Integrator(DynamicalSystem dynamicalSystem){
		this.sys = dynamicalSystem;
		n = dynamicalSystem.getPos().length;
		p1 = new double[n];  // Position at start of interval
		p2 = new double[n];
		p3 = new double[n];
		p4 = new double[n];
		v1 = new double[n]; // Velocity at start of interval
		v2 = new double[n];
		v3 = new double[n];
		v4 = new double[n];
		a1 = new double[n]; // Acceleration at start of interval
		a2 = new double[n];
		a3 = new double[n];
		a4 = new double[n];
		
	}

	/** Select which algorithm to use */
	public enum Method {
		EULER,   // Euler's method 
		SYM1,  // Symplectic Euler
		RK4,   // 4th-order Runge-Kutta
		SYM2   // Symplectic 2nd order (Verlet)
	}

	/** Take a single time step of the system */
	public void timeStep(double dt){
		timeStep(dt,1);
	}

	/** Integrate the system. 
	 * @param DT = the total time of integration
	 * @param nSubSteps = number of steps to take over interval*/
	public void timeStep(double DT, int nSubSteps){

		p1 = sys.getPos();
		v1 = sys.getVel();

		double dt = DT/nSubSteps;

		for (int i=0; i<nSubSteps; i++){

			a1 = sys.dynamics(p1,v1);

			switch (method) {
			case EULER:
				for (int j=0; j<n; j++){
					p1[j] = p1[j] + dt*v1[j];
					v1[j] = v1[j] + dt*a1[j];
				}
				break;
			case RK4:   // 4th-order Runge-Kutta
				
				/// First estimate of the midpoint:
				for (int j=0; j<n; j++) {
					p2[j] = p1[j] + 0.5*dt*v1[j];
					v2[j] = v1[j] + 0.5*dt*a1[j];
				} a2 = sys.dynamics(p2, v2);
				
				/// Second estimate of the midpoint:
				for (int j=0; j<n; j++) {
					p3[j] = p1[j] + 0.5*dt*v2[j];
					v3[j] = v1[j] + 0.5*dt*a2[j];
				} a3 = sys.dynamics(p3, v3);
				
				/// First estimate of the endpoint:
				for (int j=0; j<n; j++) {
					p4[j] = p1[j] + dt*v3[j];
					v4[j] = v1[j] + dt*a3[j];
				} a4 = sys.dynamics(p4, v4);

				/// Final estimate of the endpoint:
				for (int j=0; j<n; j++) {
					p1[j] = p1[j] + (dt/6.0)*(v1[j] + 2.0*v2[j] + 2.0*v3[j] + v4[j]);
					v1[j] = v1[j] + (dt/6.0)*(a1[j] + 2.0*a2[j] + 2.0*a3[j] + a4[j]);
				}
				break;
				
			case SYM1:
				for (int j=0; j<n; j++){
					v1[j] = v1[j] + dt*a1[j];
					p1[j] = p1[j] + dt*v1[j]; // Uses updated velocity!
				}
				break;
				
			case SYM2:   // Second Order Symplectic Integrator (Verlet)
				// http://en.wikipedia.org/wiki/Verlet_integration
				for (int j=0; j<n; j++){
					p2[j] = p1[j] + dt*v1[j] + 0.5*dt*dt*a1[j]; 
				}
				a2 = sys.dynamics(p2,v1);
				for (int j=0; j<n; j++){
					p1[j] = p2[j];
					v1[j] = v1[j] + 0.5*dt*(a1[j]+a2[j]);
				}
				break;
				
				
			}

		}

		sys.setPos(p1);
		sys.setVel(v1);
		sys.setTime(sys.getTime() + DT);
	}

}
