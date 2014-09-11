package mpk_gui;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.prism.paint.Color;

/** This class wraps a boolean value for interfacing with the GUI */
@SuppressWarnings("serial")
public class IO_Boolean {

	private boolean val;  // current value
	private boolean init;  // the initial value

	private String name;

	/** A toggle switch to be used in a GUI that links to this parameter */
	public MyCheckBox checkBox;

	/** Creates a new boolean parameter for interfacing with the gui */
	public IO_Boolean(String name, boolean init){
		this.name = name;
		this.init = init;
		this.val = init;
		checkBox = new MyCheckBox(name,init);
	}

	/** @return the value of the parameter */
	public boolean get() { return val; }

	/** Sets the value of the parameter, and updates any toggle switches */
	public void set(boolean val) {
		this.val = val;
		checkBox.button.setSelected(val);
	}

	/** Reset this parameter to it's original state */
	public void reset() {
		set(init);
	}

	/** Get the name of this parameter */
	public String name() { return name; }
	
	
	/** ********************************************************************************* 
	 * A class that wraps a JCheckBox by pairing it with a ParamBool. This allows
	 * the state of the button to easily be used throughout the code */
	private class MyCheckBox extends JPanel implements ChangeListener{

		/** The checkbox that this toggle switch is wrapping */
		public JCheckBox button;

		/** Create a new toggle switch, tied to a parameter */
		public MyCheckBox(String name, boolean init){
			button = new JCheckBox(name);
			button.addChangeListener(this);
			button.setFocusable(false);
			button.setSelected(init);
			add(button);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			JCheckBox b = (JCheckBox)e.getSource();
			val = b.isSelected();		
		}
		
	}

}
