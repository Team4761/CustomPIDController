import edu.wpi.first.wpilibj.PIDSource;

import java.util.ArrayList;

public class PIDController implements Runnable{
	private double p, i, d;
	private PIDSource source;

	private double error = 0;
	private double setpoint = 0;

	private boolean autoTune = false;
	private double tI = 0, tD = 0;

	private Thread thread = new Thread(this);

	private ArrayList<Double> accumulator = new ArrayList<>();

	public PIDController(double p, double i, double d, PIDSource source) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.source = source;
	}

	public PIDController(double p, double i, PIDSource source) {
		this(p, i, 0, source);
	}

	public PIDController(double p, PIDSource source) {
		this(p, 0, source);
	}

	public PIDController(PIDSource source) {
		this(0, source);
	}

	public PIDController(boolean autoTune, PIDSource source) {
		this(source);
		this.autoTune = autoTune;
	}

	private void updateError() {
		error = setpoint - source.pidGet();
	}

	public double get() {
		updateError();
		if (autoTune) {

		}
	}

	public void enable() {
		thread.start();
	}

	public void disable() {
		thread.interrupt(); // This may or may not work
	}


	@Override
	/**
	 * DO NOT RUN THIS MANUALLY
	 */
	public void run() {
		while(!Thread.interrupted()) {
			// Code here
			double currentVal = source.pidGet();

			accumulator.add(currentVal);

			// If it is the last element
			if (accumulator.lastIndexOf(currentVal) == accumulator.size() - 1) {
				double totalVal = Utility.addArrayElements(accumulator);
				accumulator.clear();
				accumulator.add(totalVal);
			}

			
		}
	}
}

class Utility {
	public static double addArrayElements(ArrayList<Double> arr) {
		double val = 0;
		for (double v : arr) {
			val += v;
		}

		return val;
	}
}
