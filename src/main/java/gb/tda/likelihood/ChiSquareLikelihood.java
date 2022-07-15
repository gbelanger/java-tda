package gb.tda.likelihood;

import cern.jet.random.ChiSquare;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.stat.Gamma;
import gb.tda.binner.BinningException;
import gb.tda.binner.BinningUtils;
import gb.tda.tools.BasicStats;
import java.util.Date;
import org.apache.log4j.Logger;

public class ChiSquareLikelihood extends OneParameterLikelihood {

    static Logger logger  = Logger.getLogger(ChiSquareLikelihood.class);
    static MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());

    public double pdfValue(double k, double x) {

	double kOverTwo = k/2;
	double norm = 1/(Math.pow(2, kOverTwo) * Gamma.gamma(kOverTwo));
	double pdf = norm * Math.pow(x, kOverTwo-1) * Math.exp(-x/2);
	return pdf;

// 	ChiSquare chi2 = new ChiSquare(k, engine);
// 	return chi2.pdf(x);

    }

    public double getMLE(double[] data) {
	return BasicStats.getMean(data);
    }

    public double getLogLikelihood(double k, double data) {

	double kOverTwo = k/2d;
	return (kOverTwo - 1)*Math.log(data) - data/2d - kOverTwo*Math.log(2) - Gamma.logGamma(kOverTwo);
    }

    public double getLogLikelihoodOfModel(double[] model, double[] data) throws BinningException {

	BinningUtils.checkArrayLengthsAreEqual(model, data);
	double logLikelihood = 0;
	for (int i=0; i < data.length; i++) {
	    logLikelihood += getLogLikelihood(model[i], data[i]);
	}
	return logLikelihood;
    }

    public double getDStatistic(double[] model, double[] data) throws BinningException {

	BinningUtils.checkArrayLengthsAreEqual(model, data);
	double bStat = 0;
	for (int i=0; i < data.length; i++) {
	    double kOverTwo = model[i]/2d;
	    bStat += (kOverTwo - 1)*Math.log(data[i]) - kOverTwo*Math.log(2) - Gamma.logGamma(kOverTwo);
	}
	return -2*bStat;
    }


}
