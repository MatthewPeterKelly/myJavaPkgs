package examples;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import mpk_dsc.AnimatedSystem;
import mpk_dsc.Animator;
import mpk_dsc.Pendulum;
import mpk_gui.IO_Double;
import mpk_gui.RingBuffer;
import mpk_gui.ScopePanel;


/** A demonstration for a single damped pendulum with control */
public class PendulumDemo implements KeyListener, AnimatedSystem{

	private IO_Double timeRate;
	private IO_Double damping;
	private IO_Double timeStep;
	private boolean isPaused = false;
	
	ScopePanel scopeAngle;
	ScopePanel scopeRate;
	ScopePanel scopeEnergy;
	
	RingBuffer th;
	RingBuffer w;
	RingBuffer t;
	RingBuffer energy;
	
	JPanel scopePanel;
	JPanel graphicsPanel;

	Pendulum pendulum;

	public PendulumDemo(){
		
		/// Create the pendulum
		pendulum = new Pendulum();
		
		/// Sliders for user interaction
		timeRate = new IO_Double(0.1,1.0,5.0,"Time Rate");
		timeRate.set(1.0);
		damping = new IO_Double(-0.25,0.0,0.25,"Damping");
		timeStep = new IO_Double(0.005,0.02,0.2,"Time Step");
		
		/// Data Logging
		int nBuffer = 500;  
		th = new RingBuffer(nBuffer);
		w = new RingBuffer(nBuffer);
		t = new RingBuffer(nBuffer);
		energy = new RingBuffer(nBuffer);

		scopeAngle = new ScopePanel(t,th);
		scopeAngle.xDataIsMonotonic = true;
		scopeAngle.setAxisExtentsY(-7,7);
		scopeAngle.xLabel = "Time (s)";
		scopeAngle.yLabel = "Angle (rad)";
		scopeAngle.title = "";
		scopeAngle.lineWidth = 2;
		scopeAngle.setSize(300, 300);

		scopeRate = new ScopePanel(t,w);
		scopeRate.xDataIsMonotonic = true;
		scopeRate.setAxisExtentsY(-7,7);
		scopeRate.xLabel = "Time (s)";
		scopeRate.yLabel = "Rate (rad)";
		scopeRate.title = "";
		scopeRate.lineWidth = 2;
		scopeRate.setSize(300, 300);

		scopeEnergy = new ScopePanel(t,energy);
		scopeEnergy.xDataIsMonotonic = true;
		scopeEnergy.setAxisExtentsY(0,2.0*pendulum.getEnergy()[0]);
		scopeEnergy.autoPanY = false;
		scopeEnergy.xLabel = "Time (s)";
		scopeEnergy.yLabel = "Energy (J)";
		scopeEnergy.title = "";
		scopeEnergy.lineWidth = 2;
		scopeEnergy.setSize(300, 300);

		/// Assemble the scope panel
		scopePanel = new JPanel();
		scopePanel.setLayout(new GridLayout(3,1));
		scopePanel.add(scopeAngle);
		scopePanel.add(scopeRate);
		scopePanel.add(scopeEnergy);
		
		/// Assemble the graphics panel
		graphicsPanel = new JPanel(new GridLayout(2,1));
		graphicsPanel.add(pendulum.plot);
		JPanel sliderPanel = new JPanel(new GridLayout(3,1));
		sliderPanel.add(timeRate.slider);
		sliderPanel.add(damping.slider);
		sliderPanel.add(timeStep.slider);
		graphicsPanel.add(sliderPanel);
		
		graphicsPanel.setFocusable(true);
		graphicsPanel.addKeyListener(this);
		
		
	}

	public static void main (String args[]){

		/// Create a new JFrame and add the two scopes to it
		JFrame application = new JFrame();
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		PendulumDemo gui = new PendulumDemo();
		
		application.setLayout(new GridLayout(1,2));
		application.add(gui.scopePanel);
		application.add(gui.graphicsPanel);
		application.setSize (1200, 900);  //  (width, height)
		application.setVisible(true);

		Animator animator = new Animator(gui);
		animator.run();

	}

	@Override
	public void simulate(double duration) {
		pendulum.setDamping(damping.get());
		pendulum.setMaxTimeStep(timeStep.get());
		pendulum.simulate(duration);
		}

	@Override
	public void updateGraphics() {
		th.put(pendulum.getPos()[0]);
		w.put(pendulum.getVel()[0]);
		energy.put(pendulum.getEnergy()[0]);
		t.put(pendulum.getTime());

		pendulum.plot.repaint();
		scopeAngle.update();
		scopeRate.update();
		scopeEnergy.update();
	}

	@Override
	public double getTimeRate() {
		return timeRate.get();
	}

	@Override
	public boolean isPaused() {
		return isPaused;
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
		case KeyEvent.VK_ESCAPE:  // Restart the simulation
			pendulum.reset();
			th.reset();
			w.reset();
			t.reset();
			energy.reset();
			break;
		default:
		} 

	}

}