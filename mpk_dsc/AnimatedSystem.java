package mpk_dsc;

public interface AnimatedSystem {

	/** Runs the physics for the system. 
	 * This funtion can be called several times between each 
	 * call to the graphics. 
	 * @param duration of the simulation to do
	 */
	public void simulate(double duration);
	
	/** This function should update any graphics. It should not
	 * modify the state in any way	 */
	public void updateGraphics();
	
	/** How fast should time run in the simulation? Return 1.0 for
	 * normal speed simulation. Fast-forward > 1.0, Slow-Motion < 1.0.	 */
	public double getTimeRate();
	
	/** Should the simulation be paused? */
	public boolean isPaused();
	
}
