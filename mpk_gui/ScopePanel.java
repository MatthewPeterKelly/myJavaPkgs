package mpk_gui;

/** This is a class for plotting data as it is received */
@SuppressWarnings("serial")
public class ScopePanel extends PlotPanel {

	private RingBuffer xBuffer, yBuffer;
	public boolean autoPanY = true;

	/** Create a new ScopePanel that reads off of two RingBuffers:
	 * @param xBuffer - a buffer for the x-data
	 * @param yBuffer - a buffer for the y-data
	 */
	public ScopePanel(RingBuffer xBuffer, RingBuffer yBuffer){
		super();  // Create an empty plot panel
		this.xBuffer = xBuffer;
		this.yBuffer = yBuffer;
	}


	/** Pushes any changes in the RingBuffers to the plot */
	public void update(){
		setData(xBuffer.read(),yBuffer.read());
		if (autoPanY){
			autoPanY(yBuffer.mean());
		};
	}

	/** Add a new value to each ring buffer and update the plot */
	public void update(double x, double y){
		xBuffer.put(x);
		yBuffer.put(y);
		update();
	}

	/** Move the center of the plot to a new location */
	private void autoPanY(double y){
		double dy = 0.5*(yUpp-yLow);
		yLow = y - dy;
		yUpp = y + dy;
	}

}
