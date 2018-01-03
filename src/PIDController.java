import edu.wpi.first.wpilibj.PIDSource;

import java.util.LinkedList;
import java.util.Queue;

public class PIDController implements Runnable{
	private final double UPDATE_RATE = 0.02; // In seconds

	private double p, i, d;
	private PIDSource source;

	private double error = 0;
	private double setpoint = 0;

	private boolean autoTune = false;
	private double tI = 0, tD = 0;

	private Thread thread = new Thread(this);

	private LinkedList<Double> accumulator = new LinkedList<>();

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

	private void updateError(double currentVal) {
		error = setpoint - currentVal;
	}

	private double getIntegral() {
		return Utility.addArrayElements(accumulator);
	}

	private double getDerivative() {
		return (accumulator.peekLast() - accumulator.peekFirst()) / accumulator.size()*UPDATE_RATE; // This may not work
	}

	public double get() {
		double output = 0;
		if (autoTune) {
			double pTerm = error;
			double iTerm = (1/tI)*getIntegral();
			double dTerm = tD * getDerivative();

			output = p * (pTerm + iTerm + dTerm);
		} else {
			double pTerm = error * p;
			double iTerm = getIntegral() * i;
			double dTerm = getDerivative() * d;

			output = pTerm + iTerm + dTerm;
		}

		return output;
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

			if (accumulator.size() == 10) {
				accumulator.remove();
			}
			accumulator.add(currentVal);

			updateError(currentVal);
		}
	}
}

class Utility {
	public static double addArrayElements(Queue<Double> arr) {
		double val = 0;
		for (double v : arr) {
			val += v;
		}

		return val;
	}
}