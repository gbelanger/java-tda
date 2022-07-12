package gb.tda.tools;

import java.text.DecimalFormat;
import edu.stanford.rsl.jpop.FunctionOptimizer;
import edu.stanford.rsl.jpop.FunctionOptimizer.OptimizationMode;
import edu.stanford.rsl.jpop.OptimizableFunction;
import cern.colt.list.DoubleArrayList;
//import gb.codetda.io.AsciiDataFileReader;
//import gb.codetda.tools.LeastSquaresFitter;

// Class to fit a power law function plus constant floor
public class BrokenPowerLawLikelihoodFitter implements OptimizableFunction {

    private static DecimalFormat exp = new DecimalFormat("0.000000E0");
    private static DecimalFormat num = new DecimalFormat("0.000000");
    
    double [] data;
    double [] samplingValues;
    int dimension;

    public BrokenPowerLawLikelihoodFitter(double[] data, double[] samplingValues) {
	dimension = 5;
	this.data=data;
	this.samplingValues = samplingValues;
    }

    // The broken power law function
    //
    // param[0] is norm
    // param[1] is alpha1 > 0
    // param[2] is nuBreak
    // param[3] is alpha2 > 0
    // param[4] is poissonFloor (constant)
    //
    public static double brokenPowerLawFunction(double value, double[] param) {
	double power = 0;
	if (value <= param[2]) {
	    power = param[0] * Math.pow(value/param[2], -param[1]) + param[4];
	}
	else {
	    power = param[0] * Math.pow(value/param[2], -param[3]) + param[4];
	}
	return power;
    }

    //Normalisation = 1.795344E1
    //Alpha = 0.213200
    //Poisson floor (fixed) = 2.000000

    
    
    
    // Similarity Metric
    public double getLogLikelihoodOfModel(double[] param) {
	double logLikelihood = 0;
	for ( int i=0; i < data.length; i++ ) {
	    logLikelihood += getLogLikelihood(brokenPowerLawFunction(samplingValues[i], param), data[i]);
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

	// Take initial guess of nuBreak as mid-range frequency in log-space
	double nuBreak = Math.pow(10, -0.5*(Math.log10(f[f.length-1]) - Math.log10(f[0])));
	nuBreak = 0.026104;
	nuBreak = 0.028552;
	nuBreak = 0.022345;
	
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

	// Get alpha and norm for low frequency part
	double[] fit = PowerLawLikelihoodFitter.fit(f_low.elements(), p_low.elements());
	double alpha1 = fit[1];
	double norm = fit[0];
	// correct norm for the factor related to the break frequency	
	norm /= Math.pow(1/nuBreak, -alpha1);
	
	// Get alpha for high frequency part
	fit = PowerLawLikelihoodFitter.fit(f_high.elements(), p_high.elements());
	double alpha2 = fit[1];
	
	// Define names and initial values of parameters
	double poissonFloor = 2;
	double[] param = new double[] {norm, alpha1, nuBreak, alpha2, poissonFloor};
	String[] paramNames = new String[] {"Normalisation", "Alpha1", "NuBreak", "Alpha2", "Poisson floor"};
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
	functionOptimizer.setDimension(5);
	functionOptimizer.setConsoleOutput(true);
	functionOptimizer.setOptimizationMode(OptimizationMode.Function);
	double[] init = param.clone();
	functionOptimizer.setInitialX(init);

	// Allow a 20% margin above and below estimates of params
	double[] paramLowerBounds = new double[] {norm*0.8, alpha1*0.8, nuBreak*0.8, alpha2*0.8, poissonFloor};
	double[] paramHigherBounds = new double[] {norm*1.2, alpha1*1.2, nuBreak*1.2, alpha2*1.2, poissonFloor};
	functionOptimizer.setMinima(paramLowerBounds);
	functionOptimizer.setMaxima(paramHigherBounds);

	// 	Best Fit Model
	// Initial B-stat: 3306.798043323838
	// Best B-stat: 3275.205101618893
	//   Normalisation = 4.037399E1
	//   Alpha1 = 0.213295
	//   NuBreak = 0.017876
	//   Alpha2 = 1.383950
	//   Poisson floor (fixed) = 2.000000
	
	// Optimization
	OptimizableFunction function = new BrokenPowerLawLikelihoodFitter(data, samplingValues);
	double[] bestModel = functionOptimizer.optimizeFunction(function);

	// Best Model
	System.out.println("Best Fit Model");
	System.out.println("Initial B-stat: " +  function.evaluate(init, 0));
	System.out.println("Best B-stat: " + function.evaluate(bestModel, 0));
	System.out.println("  "+paramNames[0]+" = "+exp.format(bestModel[0]));
	System.out.println("  "+paramNames[1]+" = "+num.format(bestModel[1]));
	System.out.println("  "+paramNames[2]+" = "+num.format(bestModel[2]));	
	System.out.println("  "+paramNames[3]+" = "+num.format(bestModel[3]));
	System.out.println("  "+paramNames[4]+" = "+num.format(bestModel[4])+ " (fixed)");

	return bestModel;
    }

}
