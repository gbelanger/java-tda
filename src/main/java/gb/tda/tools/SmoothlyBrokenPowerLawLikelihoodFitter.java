package gb.tda.tools;

import java.text.DecimalFormat;
import edu.stanford.rsl.jpop.FunctionOptimizer;
import edu.stanford.rsl.jpop.FunctionOptimizer.OptimizationMode;
import edu.stanford.rsl.jpop.OptimizableFunction;
import cern.colt.list.DoubleArrayList;
import gb.tda.io.AsciiDataFileReader;


public class SmoothlyBrokenPowerLawLikelihoodFitter implements OptimizableFunction {

    private static DecimalFormat exp = new DecimalFormat("0.000000E0");
    private static DecimalFormat num = new DecimalFormat("0.000000");
    
    double [] data;
    double [] samplingValues;
    int dimension;

    public SmoothlyBrokenPowerLawLikelihoodFitter(double[] data, double[] samplingValues) {
	dimension = 6;
	this.data=data;
	this.samplingValues = samplingValues;
    }

    // The broken power law function
    // param[0] is norm
    // param[1] is alpha1
    // param[2] is nuBreak
    // param[3] is alpha2
    // param[4] is poissonFloor (constant)
    // param[5] is delta (transition region)
    public static double smoothlyBrokenPowerLawFunction(double value, double[] param) {
	double power = param[0] * Math.pow(value/param[2], -param[1]) *
	    Math.pow(0.5*(1 + Math.pow(value/param[2], 1/param[5])), (param[3]-param[1])*param[5]) + param[4];
	return power;
    }
    // public static double smoothlyBrokenPowerLawFunction(double value, double[] param) {
    // 	double omega = 2*Math.PI*value;
    // 	double omegaBreak = 2*Math.PI*param[2];
    // 	double power = param[0] * Math.pow(omega/omegaBreak, param[1]) *
    // 	    Math.pow(0.5*(1 + Math.pow(omega/omegaBreak, 1/param[5])), (param[3]-param[1])*param[5]);
    // 	return power;
    // }

    
    // Similarity Metric
    public double getLogLikelihoodOfModel(double[] param) {
	double logLikelihood = 0;
	for (int i=0; i < data.length; i++) {
	    logLikelihood += getLogLikelihood(smoothlyBrokenPowerLawFunction(samplingValues[i], param), data[i]);
	}
	return logLikelihood;
    }

    public double getLogLikelihood(double tau, double data) {
	return -Math.log(tau) - data/tau;
    }

    @Override
    public void setNumberOfProcessingBlocks(int number) {
	// single threaded implementation does not need this.
    }

    @Override
    public int getNumberOfProcessingBlocks() {
	return 1;
    }

    @Override
    public double evaluate(double[] x, int block) {
	double val = (-0.5 * getLogLikelihoodOfModel(x));
	if (Double.isNaN(val)) return Double.MAX_VALUE;
	return val;
    }

    public static double[] fit(double[] f, double[] p) throws Exception {

	// Use the whole data set to get norm
	double[] indexAndNorm = LeastSquaresFitter.fitPowerLaw(f, p);
	double alpha = -indexAndNorm[0];
	double norm = indexAndNorm[1];
	System.out.println("Least Squares (full dataset):  alpha="+num.format(indexAndNorm[0])+"  norm="+exp.format(indexAndNorm[1]));
	
	// Take initial guess of nuBreak as mid-range frequency in log-space
	double nuBreak = Math.pow(10, -0.5*(Math.log10(f[f.length-1]) - Math.log10(f[0])));
	
	//  Get initial guess from analytical least squares for each half
	DoubleArrayList f_low = new DoubleArrayList();
	DoubleArrayList p_low = new DoubleArrayList();
	DoubleArrayList f_high = new DoubleArrayList();
	DoubleArrayList p_high = new DoubleArrayList();
	int k = 0;
	while (f[k] < nuBreak) {
	    f_low.add(f[k]);
	    p_low.add(p[k]);
	    k++;
	}
	while (k < f.length) {
	    f_high.add(f[k]);
	    p_high.add(p[k]);
	    k++;
	}
	f_low.trimToSize();
	p_low.trimToSize();
	f_high.trimToSize();
	p_high.trimToSize();

	// Get alpha for low frequency half
	indexAndNorm = LeastSquaresFitter.fitPowerLaw(f_low.elements(), p_low.elements());
	System.out.println("Least Squares (low frequencies):  alpha="+num.format(indexAndNorm[0])+"  norm="+exp.format(indexAndNorm[1]));
	double alpha1 = -indexAndNorm[0];
	double norm1 = indexAndNorm[1];	
	
	// Get alpha for high frequency half
	indexAndNorm = LeastSquaresFitter.fitPowerLaw(f_high.elements(), p_high.elements());
	System.out.println("Least Squares (high frequencies):  alpha="+num.format(indexAndNorm[0])+"  norm="+exp.format(indexAndNorm[1]));	
	double alpha2 = -indexAndNorm[0];
	double norm2 = indexAndNorm[1];	

	// Define names and initial values of parameters
	double poissonFloor = 2;
	double delta = 0.01;
	double[] param = new double[] {norm, alpha1, nuBreak, alpha2, poissonFloor, delta};
	String[] paramNames = new String[] {"Normalisation", "Alpha1", "NuBreak", "Alpha2", "Poisson Floor", "Delta"};
	double[] paramLowerBounds = new double[] {Math.min(norm1,norm2), 0, 0.02, 0, 2, 1e-3};
	double[] paramHigherBounds = new double[] {Math.max(norm1,norm2), 3, 0.03, 3, 2, 1e-2};
	int n = f.length;
	double[] samplingValues = new double[n];
	double[] data = new double[n];
	for (int i=0; i < n; i++) {
	    samplingValues[i] = f[i];
	    data[i] = p[i];
	}

	// Set up of function optimizer
	boolean print = true;
	FunctionOptimizer functionOptimizer = new FunctionOptimizer();
	functionOptimizer.setDimension(6);
	functionOptimizer.setConsoleOutput(true);
	functionOptimizer.setOptimizationMode(OptimizationMode.Function);
	double[] init = param.clone();
	functionOptimizer.setInitialX(init);
	functionOptimizer.setMinima(paramLowerBounds);
	functionOptimizer.setMaxima(paramHigherBounds);

	
	// Optimization
	OptimizableFunction function = new SmoothlyBrokenPowerLawLikelihoodFitter(data, samplingValues);
	double[] bestModel = functionOptimizer.optimizeFunction(function);

	// Best Model
	System.out.println("Best Fit Model");
	System.out.println("Initial B-stat: " +  function.evaluate(init, 0));
	System.out.println("Best B-stat: " + function.evaluate(bestModel, 0));
	System.out.println("  "+paramNames[0]+" = "+exp.format(bestModel[0]));
	System.out.println("  "+paramNames[1]+" = "+num.format(bestModel[1]));
	System.out.println("  "+paramNames[2]+" = "+num.format(bestModel[2]));	
	System.out.println("  "+paramNames[3]+" = "+num.format(bestModel[3]));
	System.out.println("  "+paramNames[4]+" (fixed) = "+num.format(bestModel[4]));
	System.out.println("  "+paramNames[5]+" = "+num.format(bestModel[5]));	

	return bestModel;
    }

}
