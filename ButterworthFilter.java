package mpk_dsc;

/** A simple implementation of a second-order butterworth filter */
public class ButterworthFilter {
	
	private double a1,a2;
	private double b0,b1,b2;
	
	private Double z0;  // z(k)
	private Double z1;  // z(k-1)
	private Double z2;  // z(k-2)
	private Double y0;  // y(k);
	private Double y1;  // y(k-1);
	private Double y2;  // y(k-2);
	
	/** Create a new butterworth filter */
	public ButterworthFilter(double dt, double T, Double initVal){
		double r = dt/T;  // fCutoff / fSample
		if (r >= 0.5) {
			System.out.println("WARNING - Butterworth filter cannot have a cutoff frequency below the Nyquist frequency!");
			r = 0.45;  // Force a viable filter for now
		}
		double c = 1/Math.tan(Math.PI*r); 
		double q = Math.sqrt(2);
		b0 = 1/(1.0 + q*c + c*c);
		b1 = 2*b0;
		b2 = b0;
		a1 = 2.0*(c*c-1)*b0;
		a2 = -(1.0 - q*c + c*c)*b0;
		reset(initVal);
	}
	
	/** Create a new butterworth filter, with no initial guess */
	public ButterworthFilter(double dt, double T){
		this(dt,T,null);
	}
	
	/** Reset the filter to the initial value
	 * @param initVal = the initial filter value. If null, then use data */
	public void reset(Double initVal){
		z0 = initVal;
		z1 = initVal;
		z2 = initVal;
		y0 = initVal;
		y1 = initVal;
		y2 = initVal;
	}
	
	/** Runs the filter
	 * @param - the current measurement 
	 * @return - the current estimate */
	public double run(double z){
		
		/// Update
		z2 = z1;
		z1 = z0;
		z0 = z;
		y2 = y1;
		y1 = y0;
		
		if (z2 == null){ // Then still working on initialization
			y0 = z;
		} else {  // second order butterworth filter
			y0 = b0*z0 + b1*z1 + b2*z2  +  a1*y1 + a2*y2;
		}
		return y0;
	}
}
