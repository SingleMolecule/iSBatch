/*
 * 
 */
package analysis;


/**
 * The Class LevenbergMarquardt.
 */
public abstract class LevenbergMarquardt {
	public double precision = 1e-6;
	public int iterations = 0;
	public int maxIterations = 100;
	public double chiSq = 0;
	public double factor = 10;
	
	/**
	 * Solve.
	 *
	 * @param x the x
	 * @param y the y
	 * @param s the s
	 * @param n the n
	 * @param p the p
	 * @param vary the vary
	 * @param e the e
	 * @param lambda the lambda
	 * @return the double
	 */
	public double solve(double[][] x, double[] y, double[] s, int n, double[] p, boolean[] vary, double[] e, double lambda) {
		
		// determine number of parameters that can vary
		int np = 0;
		
		for (int i = 0; i < p.length; i++) {
			if (vary == null || vary[i])
				np++;
		}
		
		double[][] alpha = new double[np][np];
		double[][] beta = new double[np][1];
		double[] dyda = new double[p.length];
		
		double error = Double.POSITIVE_INFINITY;
		int iteration = 0;
		
		while (true) {
			
			// initialize matrices
			for (int i = 0; i < np; i++) {
				alpha[i][i] = 0;
				beta[i][0] = 0;
			}
			
			// determine alpha, beta and the sum of squares
			double before = error;
			error = 0;
			
			for (int i = 0; i < n; i++) {
				double ssq = (s == null) ? 1 : s[i] * s[i];
				double residual = y[i] - getValue(x[i], p, dyda);
				
				if (ssq == 0)
					ssq = 1;
				
				error += (residual * residual) / ssq;
				
				for (int j = 0, k = 0; j < p.length; j++) {
					if (vary == null || vary[j])
						dyda[k++] = dyda[j];
				}
				
				for (int j = 0; j < np; j++) {
					for (int k = 0; k <= j; k++)
						alpha[j][k] += (dyda[j] * dyda[k]) / ssq;
					
					beta[j][0] += (dyda[j] * residual) / ssq;
				}
			}
			
			// fill in symmetric side
			for (int i = 0; i < np; i++) {
				for (int j = i + 1; j < np; j++)
					alpha[i][j] = alpha[j][i];
			}

			// include damping factor
			for (int i = 0; i < np; i++)
				alpha[i][i] *= 1 + lambda;
				//alpha[i][i] += lambda * alpha[i][i];	// prevents singularities but takes more iterations to converge
			
			// stop condition
			iteration++;
			
			if (Math.abs(before - error) < precision || iteration >= maxIterations)
				break;
			
			gaussJordan(alpha, beta);
			
			// adjust damping factor
			if (error < before) {
				
				// new estimate
				for (int i = 0, j = 0; i < p.length; i++) {
					if (vary == null || vary[i])
						p[i] += beta[j++][0];
				}
				
				lambda /= factor;
			}
			else
				lambda *= factor;
		}
		
		double[][] covar = new double[np][np];
		
		// identity matrix
		for (int i = 0; i < covar.length; i++)
			covar[i][i] = 1;
		
		// invert alpha
		gaussJordan(alpha, covar);

		for (int i = 0, j = 0; i < p.length; i++) {
			if (vary == null || vary[i]) {
				e[i] = Math.sqrt(covar[j][j] * error / (n - np));
				j++;
			}
		}
		
		chiSq = error;
		iterations = iteration;
		
		return lambda;
	}
	
	/**
	 * Gauss jordan.
	 *
	 * @param left the left
	 * @param right the right
	 */
	public void gaussJordan(double[][] left, double[][] right) {
		int n = left.length;
		int rCols = right[0].length;
		
		for (int i = 0; i < n; i++) {
			
			// find pivot
			int max = i;
			
			for (int j = i + 1; j < n; j++) {
				if (Math.abs(left[j][i]) > Math.abs(left[max][i]))
					max = j;
			}
			
			// swap rows
			double[] t = left[i];
			left[i] = left[max];
			left[max] = t;
			
			t = right[i];
			right[i] = right[max];
			right[max] = t;
			
			// reduce
			for (int j = 0; j < n; j++) {
				
				if (j != i) {
					double d = left[j][i] / left[i][i];
					
					left[j][i] = 0;
					
					for (int k = i + 1; k < n; k++)
						left[j][k] -= d * left[i][k];
					
					for (int k = 0; k < rCols; k++)
						right[j][k] -= d * right[i][k];
				}
			}
		}
		
		for (int i = 0; i < n; i++) {
			double d = left[i][i];
			
			for (int k = 0; k < rCols; k++)
				right[i][k] /= d;
			
			left[i][i] = 1;
		}
	}
	
	/**
	 * Gets the value.
	 *
	 * @param x the x
	 * @param p the p
	 * @param dyda the dyda
	 * @return the value
	 */
	public abstract double getValue(double[] x, double[] p, double[] dyda);
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		double[][] x = {{0}, {1}, {2}, {3}, {4}, {5}, {6},
				{7}, {8}, {9}, {10}, {11}, {12}, {13}};
		
		double y[] = {3365.333251953, 3206.923095703, 3215.769287109,
				3474.846191406, 4320.333496094, 5953.307617188,
				7291.846191406, 7010.307617188, 5404.307617188,
				4016.153808594, 3668.281982422, 3543.769287109,
				3320.820556641, 3248.000000000};
		
		double[] s = {100, 200, 150, 300, 100, 100, 50, 105, 102, 1012, 12, 123, 122, 100};
		
		LevenbergMarquardt lm = new LevenbergMarquardt() {
			@Override
			public double getValue(double[] x, double[] p, double[] dyda) {
				double d = -p[2] + x[0];
				
				dyda[0] = 1.0;
				dyda[1] = Math.exp(-((d * d) / (2 * p[3] * p[3])));
				dyda[2] = (p[1] * dyda[1] * d) / (p[3] * p[3]);
				dyda[3] = (p[1] * dyda[1] * d * d) / (p[3] * p[3] * p[3]);
				
				
				dyda[1] = Math.exp(-(0.5 * (-p[2] + x[0]) * (-p[2] + x[0])) / (p[3] * p[3]));
				dyda[2] = (p[1] * dyda[1] * (-p[2] + x[0])) / (p[3] * p[3]);
				dyda[3] = (p[1] * dyda[1] * (-p[2] + x[0]) * (-p[2] + x[0])) / (p[3] * p[3] * p[3]);
				
				return p[0] + p[1] * Math.exp(-0.5 * ((x[0] - p[2]) / p[3]) * ((x[0] - p[2]) / p[3]));
			}
		};
		
		double[] p = {1000, 1000, 6, 1.44359};
		double[] e = new double[p.length];
		boolean[] vary = {true, true, true, true};
		
		lm.solve(x, y, s, x.length, p, vary, e, 0.001);
		
		for (int i = 0; i < p.length; i++)
			System.out.printf("%f, %f\n", p[i],  e[i]);
		
		System.out.printf("chi^2 %f\n", lm.chiSq);
		System.out.printf("iterations %d\n", lm.iterations);
		
		// calculating R^2
		double mean = 0;
		double w = 0;
		for (int i = 0; i < y.length; i++) {
			mean += y[i] / (s[i] * s[i]);
			w += 1 / (s[i] * s[i]);
		}
		
		mean /= w;
		
		double sst = 0;
		for (int i = 0; i < y.length; i++) {
			double deviation = y[i] - mean;
			sst += (deviation * deviation) / (s[i] * s[i]);
		}
		
		System.out.printf("R^2 %f\n",  1 - (lm.chiSq / (x.length - p.length)) / (sst / (x.length - 1)));
		
		
		
		// test 2
		double[][] x2 = {{0.1}, {0.2}, {0.3}, {0.4}, {0.5}, {0.6}, {0.7}, {0.8}, {0.9}, {1}, {1.1}, {1.2}, {1.3}, {1.4}, {1.5}, {1.6}, {1.7}, {1.8}, {1.9}, {2}, {2.1}, {2.2}, {2.3}, {2.4}, {2.5}};
		double[] y2 = {0.114, 0.264, 0.417, 0.571, 0.724, 0.878, 1.027, 1.173, 1.317, 1.455, 1.59, 1.718, 1.84, 1.96, 2.078, 2.185, 2.287, 2.381, 2.475, 2.568, 2.656, 2.741, 2.822, 2.899, 2.965};
		double[] s2 = {0.128, 0.285, 0.453, 0.626, 0.796, 0.961, 1.125, 1.289, 1.448, 1.601, 1.743, 1.872, 1.988, 2.102, 2.222, 2.343, 2.47, 2.598, 2.726, 2.868, 3.004, 3.135, 3.254, 3.358, 3.447};
		
		double msd = y2[y2.length - 1];
		double t = x2[x2.length - 1][0];
		double[] p2 = new double[]{msd / (4 * t)};
		double[] e2 = new double[1];
		
		LevenbergMarquardt lm2 = new LevenbergMarquardt() {
			
			@Override
			public double getValue(double[] x, double[] p, double[] dyda) {
				dyda[0] = 4 * x[0];
				return 4 * p[0] * x[0];
			}
		};
		
		lm2.solve(x2, y2, s2, x2.length, p2, vary, e2, 0.001);
		
		System.out.println("test 2");
		System.out.println("p[0]=" + (msd/ (4*t)) );
		for (int i = 0; i < p2.length; i++)
			System.out.printf("%f, %f\n", p2[i], e2[i]);
		
		
	}
}
