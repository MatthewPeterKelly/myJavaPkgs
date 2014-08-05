package mpk_dsc;

/** This interface is used to make a system compatible with general 
 * integration methods */
public interface DynamicalSystem {

	/** @return the state vector */
	public double[] getState();
	
	/** @param the state vector */
	public void setState(double[] z);
	
	/** @return the time derivative of the state vector */
	public double[] dynamics(double[] z);
	
	/** @param the time step for the integration method */
	public void timeStep(double dt);
	
}
