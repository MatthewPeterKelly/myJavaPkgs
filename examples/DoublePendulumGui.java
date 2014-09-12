package examples;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import mpk_gui.Timer;
import mpk_dsc.AnimatedSystem;
import mpk_dsc.DoublePendulum;
import mpk_gui.IO_Double;
import mpk_gui.RingBuffer;
import mpk_gui.ScopePanel;

@SuppressWarnings("serial")
public class DoublePendulumGui extends JPanel implements KeyListener, AnimatedSystem{


	private boolean isPaused = false;
	
	private DoublePendulum doublependulum;
	private double time;
	private IO_Double timeRate;
	private int nBuffer = 200; 
	private RingBuffer th;
	private RingBuffer phi;
	private RingBuffer energy;
	private RingBuffer t;
	private ScopePanel scopeAngle;
	private ScopePanel scopeRate;
	private ScopePanel scopeEnergy;
	
	public DoublePendulumGui() {
		setFocusable(true);
		addKeyListener(this);

		// Create the pendulum
		doublependulum = new DoublePendulum();

		t = new RingBuffer(nBuffer);
		th = new RingBuffer(nBuffer);
		phi = new RingBuffer(nBuffer);
		energy = new RingBuffer(nBuffer);

		th.put(doublependulum.getState()[0]);
		phi.put(doublependulum.getState()[1]);
		t.put(time);

		timeRate = new IO_Double(0.1,1.0,2.0,"Time Rate");
		timeRate.set(1.0);

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
		scopeEnergy.setAxisExtentsY(0,1.1*doublependulum.getEnergy()[0]);
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

	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
//		System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
		switch(e.getKeyCode()) {
		case KeyEvent.VK_SPACE:  // Toggle simulation pause
			isPaused = !isPaused;
			break;
		default:
		} 

	}

	@Override
	public void simulate(double dt) {
		doublependulum.simulate(dt);		
	}

	@Override
	public void updateGraphics() {
		th.put(doublependulum.getState()[0]);
		phi.put(doublependulum.getState()[1]);
		energy.put(doublependulum.getEnergy()[0]);
		t.put(doublependulum.getTime());
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
