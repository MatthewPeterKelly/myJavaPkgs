package examples;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mpk_dsc.AnimatedSystem;
import mpk_dsc.Animator;
import mpk_dsc.DoublePendulum;
import mpk_gui.IO_Double;
import mpk_gui.RingBuffer;
import mpk_gui.ScopePanel;

/** A demonstration for a double pendulum*/
public class DoublePendulumDemo extends JPanel implements KeyListener, AnimatedSystem {

	/** Eclipse auto-generated magic */
	private static final long serialVersionUID = 426564941272914836L;

	public static void main(String[] args) {

		JFrame frame = new JFrame("Double Pendulum Animation");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		DoublePendulumDemo gui = new DoublePendulumDemo();
		frame.add(gui,BorderLayout.CENTER);

		frame.setSize (1200, 800);  //  (width, height)
		frame.setVisible(true);     

		Animator animator = new Animator(gui);
		animator.run();

	}

	private boolean isPaused = false;

	private DoublePendulum doublependulum;

	private double time;
	private IO_Double timeRate;
	private IO_Double damping;
	private IO_Double timeStep;

	private int nBuffer = 200; 
	private RingBuffer th;
	private RingBuffer phi;
	private RingBuffer energy;
	private RingBuffer t;
	private ScopePanel scopeAngle;
	private ScopePanel scopeRate;
	private ScopePanel scopeEnergy;

	public DoublePendulumDemo() {
		setFocusable(true);
		addKeyListener(this);

		// Create the pendulum
		doublependulum = new DoublePendulum();

		t = new RingBuffer(nBuffer);
		th = new RingBuffer(nBuffer);
		phi = new RingBuffer(nBuffer);
		energy = new RingBuffer(nBuffer);

		th.put(doublependulum.getPos()[0]);
		phi.put(doublependulum.getPos()[1]);
		t.put(time);

		/// Sliders for user interaction
		timeRate = new IO_Double(0.05,1.0,5.0,"Time Rate");
		damping = new IO_Double(-0.1,0.0,0.4,"Damping");
		timeStep = new IO_Double(0.001,0.005,1/30.0,"Time Step");
		timeRate.set(1.0); damping.set(0.0);

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

		scopeEnergy = new ScopePanel(t,energy);
		scopeEnergy.xDataIsMonotonic = true;
		scopeEnergy.autoPanY = false;
		scopeEnergy.setAxisExtentsY(0,2.5*doublependulum.getEnergy()[0]);
		scopeEnergy.xLabel = "Time (s)";
		scopeEnergy.yLabel = "Energy (J)";
		scopeEnergy.title = "";
		scopeEnergy.lineWidth = 2;
		scopeEnergy.setSize(300, 450);

		/// Assemble sub JPanel on left
		JPanel scopePanel = new JPanel();
		scopePanel.setLayout(new GridLayout(3,1));
		scopePanel.add(scopeAngle);
		scopePanel.add(scopeRate);
		scopePanel.add(scopeEnergy);

		/// Assemble sub-sub panel:
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(2,1));
		infoPanel.add(timeRate.slider);
		infoPanel.add(new JLabel("Space = toggle simulation"));

		/// Assemble sub JPanel right
		JPanel graphicsPanel = new JPanel(new GridLayout(2,1));
		graphicsPanel.add(doublependulum.plot);
		JPanel sliderPanel = new JPanel(new GridLayout(3,1));
		sliderPanel.add(timeRate.slider);
		sliderPanel.add(damping.slider);
		sliderPanel.add(timeStep.slider);
		graphicsPanel.add(sliderPanel);

		/// Assemble main JPanel
		setLayout(new GridLayout(1,2));
		add(scopePanel);
		add(graphicsPanel);
		setVisible(true);
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		//			System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
		switch(e.getKeyCode()) {
		case KeyEvent.VK_SPACE:  // Toggle simulation pause
			isPaused = !isPaused;
			break;
		case KeyEvent.VK_ESCAPE:  // Restart the simulation
			isPaused = true;
			doublependulum.reset();
			th.reset();
			phi.reset();
			t.reset();
			energy.reset();
			break;
		default:
		} 

	}

	@Override
	public void simulate(double dt) {
		doublependulum.setDamping(damping.get());
		doublependulum.setMaxTimeStep(timeStep.get());
		doublependulum.simulate(dt);		
	}

	@Override
	public void updateGraphics() {
		if (!isPaused){
			th.put(doublependulum.getPos()[0]);
			phi.put(doublependulum.getPos()[1]);
			energy.put(doublependulum.getEnergy()[0]);
			t.put(doublependulum.getTime());
		}
		scopeAngle.update();
		scopeRate.update();
		scopeEnergy.update();
		doublependulum.plot.repaint();		
	}

	@Override
	public double getTimeRate() {
		return timeRate.get();
	}

	@Override
	public boolean isPaused() {
		return isPaused;
	}


}