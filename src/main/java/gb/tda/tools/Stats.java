package gb.tda.tools;

import java.text.DecimalFormat;
import java.util.Date;

import cern.jet.math.Arithmetic;
import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.engine.MersenneTwister64;
//import gb.codetda.binner.Binner;
//import gb.codetda.periodogram.PeriodogramUtils;
import org.apache.log4j.Logger;


/**
 *
 * @date June 2015
 * - Replaced TimingUtils.getPhases by PeriodogramUtils.getPhases
 * - Tightened the code by removing blanck lines within methods
 * - Corrected the variance calculation in getLombPower to use the mean-subtracted rates
 * - Moverd all periodogram related methods to gb.codetda.periodogram.PowerCalculator
 * @date May 2014  
 * - Modified the calculation of the LombPower using mean and variance as arguments
 * @date April 2011
 *
 * @author  Guillaume Belanger (ESA/ESAC, Spain)
 *
 **/

public final class Stats {

    static Logger logger  = Logger.getLogger(Stats.class);
    public static DecimalFormat number = new DecimalFormat("0.0000");

	
    public static double getChi2_value(double[] data) {
		double mean = BasicStats.getMean(data);
		double chi2 = 0;
		for (int i=0; i < data.length; i++) {
		    chi2 += Math.pow((data[i] - mean), 2)/mean;
		}
		return chi2;
    }    
	
    public static double getChi2_value(double[] data, double[] errors) {
		double wmean = BasicStats.getWMean(data, errors);
		double chi2 = 0;
		for (int i=0; i < data.length; i++)
		    chi2 += Math.pow((data[i] - wmean)/errors[i], 2);
		return chi2;
    }
	
    public static double computeChi2Test(double[] data, double[] expected) {
		int bins = Math.min(data.length, expected.length);
		double pearsonChi2 = 0;
		for (int i=0; i < bins; i++) {
		    pearsonChi2 += Math.pow((data[i] - expected[i]), 2)/expected[i];
		}
		int dof = bins - 1;
		pearsonChi2 /= dof;	
		// 	logger.info("Log  : Pearson's Reduced Chi Squared ("+dof+" DOF) = "
		// 			   +number.format(pearsonChi2));
		return pearsonChi2;
    }
	
    public static double computeChi2Test(double[] data, double[] expected, int nevents) {
		int bins = data.length;
		double pearsonChi2 = 0;
		for (int i=0; i < bins; i++) {
		    pearsonChi2 += Math.pow(nevents*(data[i] - expected[i]), 2)/(nevents*expected[i]);
		}
		int dof = bins - 1;
		pearsonChi2 /= dof;	
		// 	logger.info("Log  : Pearson's Reduced Chi Squared ("+dof+" DOF) = "
		// 			   +number.format(pearsonChi2));
		return pearsonChi2;
    }
	
    public static double getGregoryLoredoOdds(double[] times, double period, int nPhaseBins) {
		int n = times.length;
		int m = nPhaseBins;
		double[] phases = PeriodogramUtils.getPhases(times, period);
		double[] foldedLC = Binner.binPhases(phases, nPhaseBins)[0];
		//  Apply formulae from Gregory and Loredo 1978  
		double logOfNFac = 0;
		for (int i=1; i <= n; i++)
		    logOfNFac += Math.log(i);
		double logOfMminusOneFac = 0;
		for (int i=1; i < m; i++)
		    logOfMminusOneFac += Math.log(i);
		double logOfNplusMminusOneFac  = 0;
		for (int i=1; i < (n+m); i++) 
		    logOfNplusMminusOneFac += Math.log(i);
		double logOfProd = 0;
		for (int i=0; i < m; i++)
		    logOfProd += Math.log(Arithmetic.factorial((int) foldedLC[i]));
		double logOfW = logOfNFac - logOfProd;
		double logOfMtotheNoverW = n*Math.log(m) - logOfW;
		double mtotheNoverW = Math.exp(logOfMtotheNoverW);
		double logOfRatio = logOfNFac + logOfMminusOneFac - logOfNplusMminusOneFac;
		double ratio = Math.exp(logOfRatio);
		double odds = period * ratio * mtotheNoverW;
		//logger.info(logOfNFac+"	"+logOfProd+"	"+odds);
		return odds;
    }


    /** IMPORTANT

    	Everything from here on has been commented out because the functionality
		has been incorporated in a better way in the likelihood package.

		It will need to be cleaned out at one point.
	
	**/


//     /**  This computes the log likelihood function for a poisson distribution
//     *     for the given value of the parameter theta, and the vector of
//     *     measurements. (For a single measurement, x[] is just a number.)
//     *
//     *     This is the Bayseian form of the function that takes into account 
//     *     a prior theta.
//     *
//     *     Note that the values of the individual members of x are expected to
//     *     be integers, i.e., the number of events measured. I keep x[] as double[]
//     *     for simplicity.
//     **/
//     public static double poissonLogLikelihood(double thetaPrior, double theta, double[] x) {
// 		double n = (double) x.length; // sample size = the number of measurements
// 		double sumOfX = 0;
// 		double productOfXFactorial = 1;
// 		for (int i=0; i < x.length; i++) {
// 		    sumOfX += x[i];
// 		    productOfXFactorial *= Arithmetic.factorial((int) x[i]);
// 		}
// 		double logLikelihood = -n*(theta+thetaPrior) + Math.log(theta*thetaPrior)*sumOfX - 2*Math.log(productOfXFactorial);
// 		return logLikelihood;
//     }
    
//     /**  This computes the log likelihood function for a poisson distribution
//     *     for the given value of the parameter theta, and the vector of
//     *     measurements. (For a single measurement, x[] is a 1-element array.)
//     *
//     *     Note that the values of the individual members of x are expected to
//     *     be integers, i.e., the number of events measured. I keep x[] as double[]
//     *     for simplicity.
//     **/
//     public static double poissonLogLikelihood(double theta, double[] x) {
// 		double n = (double) x.length; // sample size = the number of measurements
// 		double sumOfX = 0;
// 		double productOfXFactorial = 1;
// 		for (int i=0; i < x.length; i++) {
// 		    sumOfX += x[i];
// 		    productOfXFactorial *= Arithmetic.factorial((int) x[i]);
// 		}
// 		double logLikelihood = -n*theta + Math.log(theta)*sumOfX - Math.log(productOfXFactorial);
// 		return logLikelihood;
//     }
    
//     public static double firstDerivativeOfPoissonLogLikelihood(double theta, double[] x) {
// 		double n = (double) x.length;
// 		double sumOfX = BasicStats.getSum(x);
// 		double firstDerivative = -n + sumOfX/theta;
// 		return firstDerivative;
//     }

//     public static double[] getLogLikelihoodPoissonOneSigmaBounds(double x) {
// 		if (x==0) x=1e-6;
// 		double[] xAsArray = new double[] {x};
// 		return getLogLikelihoodPoissonOneSigmaBounds(xAsArray);
//     }

//     public static double[] getLogLikelihoodPoissonOneSigmaBounds(double[] x) {
// 		double thetaHat = BasicStats.getMean(x); // this is the ML estimate; it's just the arithmetic mean of the measurements
// 		double thetaPrior = 1;
// 		double lMax = poissonLogLikelihood(thetaHat, x);  // this is the logLikelihood function for the MLE (thetaHat)
// 		double lMaxMinusHalf = lMax - 0.5;
// 		double theta = thetaHat;
// 		double l = lMax;
// 		double diff = 0.5;
// 		double deltaTheta = 1e-3;

// 		//  Determine lower bound
// 		while (diff > 1e-4) {
// 		    theta -= deltaTheta;
// 		    l = poissonLogLikelihood(theta, x);
// 		    diff = l - lMaxMinusHalf;
// 		}
// 		double thetaMinus = theta;
// 		if (thetaMinus < 0) thetaMinus = 0;

// 		//  Determine upper bound
// 		theta = thetaHat;
// 		l = lMax;
// 		diff = 0.5;
// 		while (diff > 1e-4) {
// 		    theta += deltaTheta;
// 		    l = poissonLogLikelihood(theta, x);
// 		    diff = l - lMaxMinusHalf;
// 		}
// 		double thetaPlus = theta;

// 		return new double[] {thetaMinus, thetaPlus};
//     }

//     public static double[] getGehrelsPoissonBounds(int nObs) {
// 		if (nObs < 0) {
// 		    throw new ArithmeticException("nObs must be a positive integer");
// 		}

// 		MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
// 		double oneSigmaTail = (1 - 0.6826)/2; // = 0.1587;
// 		double oneSigmaArea = 0.6826 + oneSigmaTail;
// 		double step = 1e-2;
// 		double precision = 1e-4;

// 		//  Calculate Upper Bound
// 		int upperBound = (int) Math.round(nObs+step);
// 		Poisson upperDist = new Poisson(upperBound, engine);
// 		double area = upperDist.cdf(nObs);
// 		double diff = area - oneSigmaTail;
// 		while (diff > precision) {
// 		    upperBound += step;
// 		    upperDist.setMean(upperBound);
// 		    area = upperDist.cdf(nObs);
// 		    diff = area - oneSigmaTail;
// 		}

// 		//  Calculate Lower Bound
// 		int lowerBound = (int) Math.round(nObs-step);
// 		if (nObs > 0) {
// 		    Poisson lowerDist = new Poisson(lowerBound, engine);
// 		    area = lowerDist.cdf(nObs-1);
// 		    diff = oneSigmaArea - area;
// 		    while (diff > precision && lowerBound > 0) {
// 			lowerBound -= step;
// 			lowerDist.setMean(lowerBound);
// 			area = lowerDist.cdf(nObs-1);
// 			diff = oneSigmaArea - area;
// 		    }
// 		}
// 		else lowerBound = 0;

// 		return new double[] {lowerBound, upperBound};
//     }

//     public static double[] getPoissonOneSigmaBounds(int nObs) {

// 		if (nObs < 0) {
// 		    throw new ArithmeticException("nObs must be a positive integer");
// 		}

// 		MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
// 		double oneSigmaArea = 0.3413;
// 		double step = 1e-2;
// 		double precision = 1e-4;

// 		int upperBound = (int) nObs+step;
// 		Poisson upperDist = new Poisson(upperBound, engine);
// 		double area = upperDist.cdf(upperBound) - upperDist.cdf(nObs);
// 		double diff = oneSigmaArea - area;
// 		while (diff > precision) {
// 		    upperDist.setMean(upperBound);
// 		    area = upperDist.cdf(upperBound) - upperDist.cdf(n);
// 		    diff = oneSigmaArea - area;
// 		    upperBound += step;
// 		}

// 	 	double lowerBound = nObs-step;
// 	 	Poisson lowerDist = new Poisson(lowerBound, engine);
// 		if (nObs == 0) lowerBound = 0;
// 	 	else {
// 	 	    area = lowerDist.cdf((new Double(nObs)).doubleValue()) - lowerDist.cdf(lowerBound);
// 	 	    diff = oneSigmaArea - area;
// 	 	    while (  diff > precision && lowerBound > 0) {
// 	 		lowerDist.setMean(lowerBound);
// 			double cdfAtNobs = lowerDist.cdf((new Double(nObs)).doubleValue());
// 			double cdfAtLowerBound = lowerDist.cdf(lowerBound);
// 	 		area =  cdfAtNobs - cdfAtLowerBound;
// 	 		diff = oneSigmaArea - area;
// 	 		lowerBound -= step;
// 	 	    }
// 		    lowerBound = Math.max(lowerBound, 0);
// 	 	}
// 		return new double[] {lowerBound, upperBound};
//     }

//     public static double[] getGaussianOneSigmaBounds(double nObs) {

// 		if (nObs < 0) {
// 		    throw new ArithmeticException("nObs must be a positive integer");
// 		}

// 		MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
// 		double oneSigmaArea = 0.3413;
// 		double step = 1e-3;

// 		Normal upperDist = new Normal(nObs, Math.sqrt(nObs), engine);
// 		double upperBound = nObs+step;
// 		upperDist.setState(upperBound, Math.sqrt(upperBound));
// 		double area = upperDist.cdf(upperBound) - upperDist.cdf((new Double(nObs)).doubleValue());
// 		double diff = oneSigmaArea - area;

// 		while (diff > 1e-3) {
// 		    upperBound += step;
// 		    upperDist.setState(upperBound, Math.sqrt(upperBound));
// 		    area = upperDist.cdf(upperBound) - upperDist.cdf((new Double(nObs)).doubleValue());
// 		    diff = oneSigmaArea - area;
// 		}

// 	 	double lowerBound = nObs-step;
// 	 	Normal lowerDist = new Normal(lowerBound, Math.sqrt(lowerBound), engine);
// 	 	if (nObs == 0) lowerBound = 0;
// 	  	else {
// 	 	    area = lowerDist.cdf((new Double(nObs)).doubleValue()) - lowerDist.cdf(lowerBound);
// 	 	    diff = oneSigmaArea - area;
// 		    //System.out.println(lowerBound+"	"+area+"	"+diff);
// 	 	    while (diff > 1e-3 && lowerBound > 0) {
// 	 		lowerBound -= step;
// 	 		lowerDist.setState(lowerBound, Math.sqrt(lowerBound));
// 	 		area = lowerDist.cdf((new Double(nObs)).doubleValue()) - lowerDist.cdf(lowerBound);
// 	 		diff = oneSigmaArea - area;
// 			//System.out.println(lowerBound+"	"+area+"	"+diff);
// 	  	    }
// 	 	}

// 		return new double[] {lowerBound, upperBound};


// 	// 	MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
// 	// 	Normal aDist = new Normal(nObs, Math.sqrt(nObs), engine);
// 	// 	Normal bDist = new Normal(nObs, Math.sqrt(nObs), engine);
// 	// 	double prob = 0.1587;

// 	// 	double alpha = 1 - aDist.cdf(nObs);
// 	// 	double a = nObs;
// 	// 	double diff = alpha - prob;
// 	// 	while (diff > 1e-4) {
// 	// 	    a -= 1e-2;
// 	// 	    aDist.setState(a, Math.sqrt(a));
// 	// 	    alpha = 1 - aDist.cdf(nObs);
// 	// 	    //System.out.println("alpha = "+alpha+"	 a = "+a);
// 	// 	    diff = alpha - prob;
// 	// 	}

// 	// 	double beta = bDist.cdf(nObs);	
// 	// 	double b = nObs;
// 	// 	diff = beta - prob;
// 	// 	while (diff > 1e-4) {
// 	// 	    b += 1e-2;
// 	// 	    bDist.setState(b, Math.sqrt(b));
// 	// 	    beta = bDist.cdf(nObs);
// 	// 	    //System.out.println("beta = "+beta+"	 b = "+b);
// 	// 	    diff = beta - prob;
// 	// 	}

// 	// 	return new double[] {a, b};
//     }
		
}
