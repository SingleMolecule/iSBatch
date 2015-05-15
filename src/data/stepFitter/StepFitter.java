/*
 * 
 */
package data.stepFitter;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

// TODO: Auto-generated Javadoc
/**
 * The Class StepFitter.
 */
public class StepFitter {
	
	/** The data. */
	private double[] data;
	
	/** The sigma. */
	private double sigma;
	
	/** The steps. */
	private LinkedList<Step> steps = new LinkedList<Step>();
	
	/** The counter steps. */
	private LinkedList<Step> counterSteps = new LinkedList<Step>();
	
	/** The total chi squared. */
	private double totalChiSquared;
	
	/** The counter chi squared. */
	private double counterChiSquared;
	
	/**
	 * The Class Split.
	 */
	private class Split {
		
		/** The left. */
		public Step left;
		
		/** The right. */
		public Step right;
		
		/** The chi squared. */
		private double chiSquared;
		
		/**
		 * Instantiates a new split.
		 *
		 * @param left the left
		 * @param right the right
		 */
		public Split(Step left, Step right) {
			this.left = left;
			this.right = right;
			chiSquared = left.chiSquared + right.chiSquared;
		}
	}
	
	/**
	 * The Class Step.
	 */
	private class Step {
		
		/** The from. */
		public int from;
		
		/** The to. */
		public int to;
		
		/** The mean. */
		public double mean;
		
		/** The chi squared. */
		public double chiSquared;
		
		/**
		 * Instantiates a new step.
		 *
		 * @param from the from
		 * @param to the to
		 */
		public Step(int from, int to) {
			this.from = from;
			this.to = to;
			
			// calculate mean
			mean = 0.0;
			for (int i = from; i < to; i++)
				mean += data[i];
			mean /= (to - from);
			
			// calculate chi squared
			chiSquared = 0.0;
			for (int i = from; i < to; i++) {
				double residual = (data[i] - mean) / sigma;
				chiSquared += residual * residual;
			}
		}
		
		/**
		 * Gets the split point.
		 *
		 * @return the split point
		 */
		public Split getSplitPoint() {
			double minChiSquared = chiSquared;
			Split bestSplit = null;
			
			for (int i = from + 1; i < to; i++) {
				Step left = new Step(from, i);
				Step right = new Step(i, to);
				Split split = new Split(left, right);
				
				if (split.chiSquared < minChiSquared) {
					minChiSquared = split.chiSquared;
					bestSplit = split;					
				}
			}
			
			return bestSplit;
		}
	}
	
	/**
	 * Instantiates a new step fitter.
	 *
	 * @param data the data
	 * @param sigma the sigma
	 */
	public StepFitter(double[] data, double sigma) {
		this.data = data;
		this.sigma = sigma;
		
		clear();
	}
	
	/**
	 * Clear.
	 */
	public void clear() {
		steps.clear();
		
		Step step = new Step(0, data.length);
		totalChiSquared = step.chiSquared;
		
		steps.add(step);
		
		setCounterSteps();
	}
	
	/**
	 * Gets the steps x.
	 *
	 * @return the steps x
	 */
	public double[] getStepsX() {
		double[] x = new double[steps.size() * 2];
		
		int i = 0;
		for (Step step: steps) {
			x[i++] = step.from;
			x[i++] = step.to;
		}
		
		return x;
	}
	
	/**
	 * Gets the steps y.
	 *
	 * @return the steps y
	 */
	public double[] getStepsY() {
		double[] y = new double[steps.size() * 2];
		
		int i = 0;
		for (Step step: steps) {
			y[i++] = step.mean;
			y[i++] = step.mean;
		}
		
		return y;
	}
	
	/**
	 * Gets the counter steps x.
	 *
	 * @return the counter steps x
	 */
	public double[] getCounterStepsX() {
		double[] x = new double[counterSteps.size() * 2];
		
		int i = 0;
		for (Step step: counterSteps) {
			x[i++] = step.from;
			x[i++] = step.to;
		}
		
		return x;
	}
	
	/**
	 * Gets the counter steps y.
	 *
	 * @return the counter steps y
	 */
	public double[] getCounterStepsY() {
		double[] y = new double[counterSteps.size() * 2];
		
		int i = 0;
		for (Step step: counterSteps) {
			y[i++] = step.mean;
			y[i++] = step.mean;
		}
		
		return y;
	}
	
	/**
	 * Adds the step.
	 */
	public void addStep() {
		double minChiSquared = totalChiSquared;
		Split bestSplit = null;
		int index = -1;
		
		for (int i = 0; i < steps.size(); i++) {
			Step step = steps.get(i);
			
			if (totalChiSquared - step.chiSquared > minChiSquared || step.to - step.from < 2)
				continue;
			
			Split split = step.getSplitPoint();
			double chiSquared = (totalChiSquared - step.chiSquared) + split.chiSquared;
			
			if (chiSquared < minChiSquared) {
				minChiSquared = chiSquared;
				bestSplit = split;
				index = i;
			}
		}
		
		steps.remove(index);
		steps.add(index, bestSplit.right);
		steps.add(index, bestSplit.left);
		
		setCounterSteps();
		
		totalChiSquared = minChiSquared;
	}
	
	/**
	 * Sets the counter steps.
	 */
	private void setCounterSteps() {
		counterChiSquared = 0;
		counterSteps.clear();
		int last = 0;
		
		for (int i = 0; i < steps.size(); i++) {
			
			if (steps.get(i).to - steps.get(i).from == 1)
				continue;
			
			Split split = steps.get(i).getSplitPoint();
			Step step = new Step(last, split.left.to);
			counterChiSquared += step.chiSquared;
			counterSteps.add(step);
			last = split.right.from;
		}
		
		Step step = new Step(last, data.length);
		counterSteps.add(step);
		counterChiSquared += step.chiSquared;
	}
	
	/**
	 * Gets the chi squared.
	 *
	 * @return the chi squared
	 */
	public double getChiSquared() {
		return totalChiSquared;
	}
	
	/**
	 * Gets the counter chi squared.
	 *
	 * @return the counter chi squared
	 */
	public double getCounterChiSquared() {
		return counterChiSquared;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		String str = Integer.toString(steps.getFirst().from);
		
		for (Step step: steps)
			str += String.format(", %d", step.to);
		
		return str;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		
		
		
		
		
		
		
		
		try {
			Scanner scanner = new Scanner(new File("W:/Michiel/stepdata2.txt"));
			
			double[] data = new double[1024];
			int i = 0;
			
			while (scanner.hasNext() && i < data.length) {
				scanner.nextDouble();
				double y = scanner.nextDouble();
				data[i] = y;
				i++;
			}
			
			scanner.close();
			
			data = Arrays.copyOf(data, i);
			
			StepFitter fitter = new StepFitter(data, 1);
			
			int to = 10;//data.length;
			
			for (int n = 1; n < to; n++) {

				fitter.addStep();
				
				double chiSquared = fitter.getChiSquared();
				double counterChiSquared = fitter.getCounterChiSquared();
				System.out.printf("steps : %d, chiSquared : %f, chiSquared (counter fit) : %f, ratio : %f\n", n, chiSquared, counterChiSquared, chiSquared / counterChiSquared);
				System.out.printf("%s\n", fitter);
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
