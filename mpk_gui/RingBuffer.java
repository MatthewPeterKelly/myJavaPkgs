package mpk_gui;

/** A method that is used for continuously logging data.  */
public class RingBuffer {

	private double[] data;    
	private double[] output;	

	private int n;  // length of the buffer
	private boolean init = false;

	private int pointer;

	/**
	 * Creates a special array that is designed for data-logging and plotting.
	 * FIFO
	 * @param n - The number of elements to retain.
	 */
	public RingBuffer(int n){
		data = new double[n];
		output = new double[n];
		pointer = 0;
		this.n = n;
	}

	/** 
	 * Add a single element to the ring buffer
	 * @param x - a data value
	 */
	public void put(double x){
		if (!init) {  // Copy the first value
			for (int i=0; i<n; i++){
				data[pointer] = x;
				pointer++;
				if (pointer>=n) pointer = 0;
			} init = true;
		} else {
			data[pointer] = x;
			pointer++;
			if (pointer>=n) pointer = 0;
		}
	}

	/**
	 * Read the entire buffer, in FIFO order. Note that if the buffer is 
	 * partially filled this will return a full array, padded with the 
	 * first value added to the RingBuffer.
	 */
	public double[] read(){
		int j = pointer;
		for (int i=0; i<n; i++){
			if (j>=n) j = 0;
			output[i] = data[j];
			j++;
		}
		return output;
	}
	
	/**
	 * Reset the buffer
	 */
	public void reset(){
		init = false;  // Flag to overwrite all data on next run
	}
	
	/** 
	 * Compute the mean value of the buffer 
	 */
	public double mean(){
		double sum = 0;
		for (int i=0; i<n; i++){
			sum += data[i];
		}
		return sum/n;
	}
}
