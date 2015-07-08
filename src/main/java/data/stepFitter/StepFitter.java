/************************************************************************
 * 				iSBatch  Copyright (C) 2015  							*
 *		Victor E. A. Caldas -  v.e.a.caldas at rug.nl					*
 *		C. Michiel Punter - c.m.punter at rug.nl						*
 *																		*
 *	This program is distributed in the hope that it will be useful,		*
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of		*
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the		*
 *	GNU General Public License for more details.						*
 *	You should have received a copy of the GNU General Public License	*
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************/
package data.stepFitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class StepFitter {
	private double[] data;
	private double sigma;
	private LinkedList<Step> steps = new LinkedList<Step>();
	private LinkedList<Step> counterSteps = new LinkedList<Step>();
	private double totalChiSquared;
	private double counterChiSquared;

	private class Split {
		public Step left;
		public Step right;
		private double chiSquared;

		public Split(Step left, Step right) {
			this.left = left;
			this.right = right;
			chiSquared = left.chiSquared + right.chiSquared;
		}
	}

	private class Step {
		public int from;
		public int to;
		public double mean;
		public double chiSquared;

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

	public StepFitter(double[] data, double sigma) {
		this.data = data;
		this.sigma = sigma;

		clear();
	}

	public void clear() {
		steps.clear();

		Step step = new Step(0, data.length);
		totalChiSquared = step.chiSquared;

		steps.add(step);

		setCounterSteps();
	}

	public double[] getStepsX() {
		double[] x = new double[steps.size() * 2];

		int i = 0;
		for (Step step : steps) {
			x[i++] = step.from;
			x[i++] = step.to;
		}

		return x;
	}

	public double[] getStepsY() {
		double[] y = new double[steps.size() * 2];

		int i = 0;
		for (Step step : steps) {
			y[i++] = step.mean;
			y[i++] = step.mean;
		}

		return y;
	}

	public double[] getCounterStepsX() {
		double[] x = new double[counterSteps.size() * 2];

		int i = 0;
		for (Step step : counterSteps) {
			x[i++] = step.from;
			x[i++] = step.to;
		}

		return x;
	}

	public double[] getCounterStepsY() {
		double[] y = new double[counterSteps.size() * 2];

		int i = 0;
		for (Step step : counterSteps) {
			y[i++] = step.mean;
			y[i++] = step.mean;
		}

		return y;
	}

	public void addStep() {
		double minChiSquared = totalChiSquared;
		Split bestSplit = null;
		int index = -1;

		for (int i = 0; i < steps.size(); i++) {
			Step step = steps.get(i);

			if (totalChiSquared - step.chiSquared > minChiSquared
					|| step.to - step.from < 2)
				continue;

			Split split = step.getSplitPoint();
			double chiSquared = (totalChiSquared - step.chiSquared)
					+ split.chiSquared;

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

	public double getChiSquared() {
		return totalChiSquared;
	}

	public double getCounterChiSquared() {
		return counterChiSquared;
	}

	@Override
	public String toString() {
		String str = Integer.toString(steps.getFirst().from);

		for (Step step : steps)
			str += String.format(", %d", step.to);

		return str;
	}

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

			int to = 10;// data.length;

			for (int n = 1; n < to; n++) {

				fitter.addStep();

				double chiSquared = fitter.getChiSquared();
				double counterChiSquared = fitter.getCounterChiSquared();
				System.out
						.printf("steps : %d, chiSquared : %f, chiSquared (counter fit) : %f, ratio : %f\n",
								n, chiSquared, counterChiSquared, chiSquared
										/ counterChiSquared);
				System.out.printf("%s\n", fitter);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
