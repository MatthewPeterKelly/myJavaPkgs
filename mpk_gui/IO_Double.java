package mpk_gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class allows parameters to be get and set by various other classes
 * @author Matt
 *
 */
@SuppressWarnings("serial")
public class IO_Double {

	private double val;
	private double nom;  //  the value of this parameter upon initialization
	public double low;  // lower bound on the parameter
	public double upp;  // upper bound on the parameter
	private int nBins = 100;  // Default number of bins
	private String name;
	private String shortName;

	/** A slider that is connected to this double */
	public MySliderDouble slider;
	
	/** A button to reset the slider */
	private MyButton button;

	/**
	 * 
	 * @param low - the lower bound on this parameter
	 * @param val - the initial value of this parameter
	 * @param upp - the upper bound on this parameter
	 * @param name
	 * @param shortName
	 */
	public IO_Double(double low, double val, double upp, String name, String shortName){
		this.low = low;
		this.val = val;
		this.nom = val;
		this.upp = upp;
		this.name = name;
		this.shortName = shortName;
		slider = new MySliderDouble();
		slider.setValue(val);
	}

	/**
	 * An easier version that only uses one name
	 */
	public IO_Double(double low, double val, double upp, String name){
		this(low,val,upp,name,name);
	}


	/** Returns the value of the parameter */
	public double get() {
		return val;
	}

	/** Sets the value of the parameter */
	public void set(double val) {
		this.val = val;
		slider.setValue(val);
	}

	/** Returns the name of the name of the parameter */
	public String name() {
		return name;
	}

	/** Returns the short version of the name */
	public String shortName() {
		return shortName;
	}

	/** Resets the parameter to its nominal value */
	public void reset(){
		set(nom);
	}



	/** *****************************************************************************
	 * This class is a wrapper for a JSlider that allows it to directly change
	 * parameters in other classes, automatically pull default values, and have
	 * bounds that are non-integer. Slider is Horizontal.
	 * @author Matt
	 */
	public class MySliderDouble extends JPanel implements ChangeListener{

		/** The slider that MySliderDouble is wrapping around */
		public JSlider jSlider;

		private Hashtable <Integer, JLabel> table;  // label table for slider

		private JLabel label;
		
		public String format = "%4.4f";

		/** 
		 * A slider that automatically updates parameters across classes, and wraps
		 * the standard JSlider with a function to convert to doubles
		 * @param param - a Parameter to be tied to this slider
		 * @param nBins - the number of bins to use on the slider
		 */
		public MySliderDouble(){

			/// Makes everything in the panel fit nicely
			setLayout(new GridLayout(2,1));

			jSlider = new JSlider(JSlider.HORIZONTAL,0,nBins,0);  // Create the JSlider
			jSlider.addChangeListener(this);
			jSlider.setFocusable(false);
			
			/// Add labels to the slider
			int center = nBins/2;
			table = new Hashtable<Integer, JLabel>();
			table.put( 0, new JLabel(String.format(format,low)));
			label =  new JLabel(String.format(format,val));
			table.put( center, label);
			table.put( nBins, new JLabel(String.format(format,upp)));
			jSlider.setLabelTable(table);
			jSlider.setPaintLabels(true);
			
			/// Create the title and reset button
			button = new MyButton();
			JPanel titleBar = new JPanel();
			titleBar.setLayout(new GridLayout(1,2));
			titleBar.add(new JLabel(name,JLabel.CENTER));
			titleBar.add(button);
			
			/// Add the slider to the JPanel
			add(titleBar);
			add(jSlider);
			
			/// Create a border
			Border outline = BorderFactory.createLineBorder(Color.BLACK);
			Border margins = BorderFactory.createEmptyBorder(10,10,10,10);
			setBorder(BorderFactory.createCompoundBorder(outline,margins));

		}

		/** Set the current value of the slider to this value */
		public void setValue(double val){
			double m = nBins/(upp-low);
			double b = -m*low;
			jSlider.setValue(((int) (m*val+b)));
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			int raw = (int)source.getValue();
			int min = source.getMinimum();
			int max = source.getMaximum();
			double alpha = ((double)(raw - min))/((double)(max-min));
			label.setText(String.format(format,val));
			val = (1-alpha)*low + alpha*upp;
		}
	}
	
	/** ********************************************************************************* 
	 * A class that wraps a JButton that is used to reset the slider */
	private class MyButton extends JPanel implements ActionListener{

		/** The check box that this toggle switch is wrapping */
		public JButton button;

		/** Create a new toggle switch, tied to a parameter */
		public MyButton(){
			button = new JButton("reset");
			button.addActionListener(this);
			button.setFocusable(false);
			add(button);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			reset(); // Reset the slider
		}
		
	}

}