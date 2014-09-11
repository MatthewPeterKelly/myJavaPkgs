package mpk_dsc;

/** This interface is used to make a system compatible with general 
 * integration methods */
public interface DynamicalSystem {

	/** @return the state vector */
	public double[] getState();
	
	/** @param z the state vector */
	public void setState(double[] z);
	
	/** @return z the time derivative of the state vector */
	public double[] dynamics(double[] z);
	
	/** Take a single time step using the default integration method
	 * @param dt the time step for the integration method */
	public void timeStep(double dt);
	
	/** Run a simulation with many time steps to get the state after 
	 * a more lengthy period of time.
	 *  @param duration of the simulation */
	public void simulate(double duration);
	
}
