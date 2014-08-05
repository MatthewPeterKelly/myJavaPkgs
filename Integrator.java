package mpk_dsc;

/** This class contains several different integration methods */
public class Integrator {

	private DynamicalSystem dynamicalSystem;

	/** How many sub steps should the integration method take? Must be >= 1 */
	public int number_of_substeps = 1;

	/** Determine which method to use */
	public Method method = Method.EULER;

	/// Temp variables
	private double[] z, dz;
	private double[] z1, z2, z3, z4;
	private double[] dz1, dz2, dz3, dz4; // For Runge-Kutta 4th order method
	private int n;  // Dimension of the state space
	
	/** Create a new integrator */
	public Integrator(DynamicalSystem dynamicalSystem){
		this.dynamicalSystem = dynamicalSystem;
		n = dynamicalSystem.getState().length;
		z = new double[n];
		z1 = new double[n];
		z2 = new double[n];
		z3 = new double[n];
		z4 = new double[n];
		dz = new double[n];
		dz1 = new double[n];
		dz2 = new double[n];
		dz3 = new double[n];
		dz4 = new double[n];
	}

	/** Select which algorithm to use */
	public enum Method {
		EULER,
		RK4
	}

	/** Integrate the system */
	public void timeStep(double DT){

		z = dynamicalSystem.getState();
		int n = z.length;

		double dt = DT/number_of_substeps;

		for (int i=0; i<number_of_substeps; i++){
			
			dz = dynamicalSystem.dynamics(z);

			switch (method) {
			case EULER:
				for (int j=0; j<n; j++){
					z[j] = z[j] + dt*dz[j];
				}
				break;

			case RK4:   // 4th-order Runge-Kutta

				z1 = z;
				dz1 = dz;

				for (int j=0; j<n; j++)   z2[j] = z[j] + 0.5*dt*dz1[j];
				dz2 = dynamicalSystem.dynamics(z2);

				for (int j=0; j<n; j++)   z3[j] = z[j] + 0.5*dt*dz2[j];
				dz3 = dynamicalSystem.dynamics(z3);

				for (int j=0; j<n; j++)   z4[j] = z[j] + dt*dz3[j];
				dz4 = dynamicalSystem.dynamics(z4);

				for (int j=0; j<n; j++) {
					z[j] = z[j] + (dt/6.0)*(dz1[j] + 2.0*dz2[j] + 2.0*dz3[j] + dz4[j]);
				}
				
				break;
				
			}

		}

		dynamicalSystem.setState(z);

	}

}