package examples;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.Timer;

import mpk_dsc.DoublePendulum;
import mpk_gui.IO_Double;
import mpk_gui.RingBuffer;
import mpk_gui.ScopePanel;

@SuppressWarnings("serial")
public class DoublePendulumGui extends JPanel implements KeyListener{

	public double frames_per_second = 30.0;
	private final double buffer_duration = 7.5;  // Seconds

	private DoublePendulum doublependulum;
	private double time;
	private IO_Double rate;
	private int nBuffer = (int) (buffer_duration*frames_per_second);   /// Keep 10 seconds worth of data
	private RingBuffer th;
	private RingBuffer phi;
	private RingBuffer t;
	private ScopePanel scopeAngle;
	private ScopePanel scopeRate;
	public Timer timer;
	private double dt;

	private boolean pause = false;

	public DoublePendulumGui() {
		setFocusable(true);
		addKeyListener(this);

		DoublePendulumListener listener = new DoublePendulumListener(this);

		dt = 1.0/frames_per_second;
		timer = new Timer((int)(1000*dt), listener);

		// Create the pendulum
		doublependulum = new DoublePendulum();

		th = new RingBuffer(nBuffer);
		phi = new RingBuffer(nBuffer);
		t = new RingBuffer(nBuffer);

		th.put(doublependulum.z[0]);
		phi.put(doublependulum.z[1]);
		t.put(time);

		rate = new IO_Double(0.1,1,3,"timeRate");

		scopeAngle = new ScopePanel(t,th);
		scopeAngle.xDataIsMonotonic = true;
		scopeAngle.setAxisExtentsY(-15,15);
		scopeAngle.xLabel = "Time (s)";
		scopeAngle.yLabel = "Angle 1 (rad)";
		scopeAngle.title = "";
		scopeAngle.lineWidth = 2;
		scopeAngle.setSize(300, 450);

		scopeRate = new ScopePanel(t,phi);
		scopeRate.xDataIsMonotonic = true;
		scopeRate.setAxisExtentsY(-15,15);
		scopeRate.xLabel = "Time (s)";
		scopeRate.yLabel = "Angle 2 (rad)";
		scopeRate.title = "";
		scopeRate.lineWidth = 2;
		scopeRate.setSize(300, 450);

		/// Assemble sub JPanel on left
		JPanel scopePanel = new JPanel();
		scopePanel.setLayout(new GridLayout(2,1));
		scopePanel.add(scopeAngle);
		scopePanel.add(scopeRate);

		/// Assemble sub-sub panel:
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(3,1));
		infoPanel.add(rate.slider);
		infoPanel.add(new JLabel("R = reset playback speed"));
		infoPanel.add(new JLabel("Space = toggle simulation"));
		
		/// Assemble sub JPanel right
		JPanel graphicsPanel = new JPanel();
		graphicsPanel.setLayout(new GridLayout(2,1));
		graphicsPanel.add(doublependulum.plot);
		graphicsPanel.add(infoPanel);

		/// Assemble main JPanel
		setLayout(new GridLayout(1,2));
		add(scopePanel);
		add(graphicsPanel);
		setVisible(true);
	}

	// Main simulation stuff goes here
	public void step(){

		if (!pause){

			double h = dt*rate.get();

			doublependulum.timeStep(h);
			time += h;

			th.put(doublependulum.z[0]);
			phi.put(doublependulum.z[1]);
			t.put(time);
			scopeAngle.update();
			scopeRate.update();

			doublependulum.plot.repaint();
		}

	};

	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(KeyEvent.getKeyText(e.getKeyCode()));

		switch(e.getKeyCode()) {
		case KeyEvent.VK_SPACE:  // Toggle simulation pause
			pause = !pause;
			break;
		case KeyEvent.VK_R:  // Reset the timer rate
			rate.set(1.0);
			break;
		default:
		}

	}

	public class DoublePendulumListener implements ActionListener {

		private DoublePendulumGui gui;

		public DoublePendulumListener(DoublePendulumGui gui) {
			this.gui = gui;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			gui.step();
		}

	}


}
