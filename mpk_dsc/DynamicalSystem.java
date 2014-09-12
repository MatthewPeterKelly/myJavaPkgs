package mpk_dsc;

/** This interface is used to make a system compatible with general 
 * integration methods */
public interface DynamicalSystem {

	/** @return the position */
	public double[] getPos();
	
	/** @return the velocity */
	public double[] getVel();
	
	/** @return the system time */
	public double getTime();
	
	/** @param p the position vector */
	public void setPos(double[] p);
	
	/** @param v the velocity vector */
	public void setVel(double[] v);
	
	/** @param t the system time */
	public void setTime(double t);
	
	/** Reset the system to initial state */
	public void reset();
	
	/** Computes the dynamics of a second order system
	 * @param p = position vector
	 * @param v = velocity vector
	 * @return dv = derivative of the velocity vector*/
	public double[] dynamics(double[] p, double[]v);
	
	/** Take a single time step using the default integration method
	 * @param dt the time step for the integration method */
	public void timeStep(double dt);
	
	/** Run a simulation with many time steps to get the state after 
	 * a more lengthy period of time.
	 *  @param duration of the simulation */
	public void simulate(double duration);
	
}
