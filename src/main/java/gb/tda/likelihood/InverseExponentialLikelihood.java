package gb.tda.likelihood;

import cern.jet.random.engine.MersenneTwister64;
import gb.tda.binner.BinningException;
import gb.tda.binner.BinningUtils;
import java.util.Date;

public class InverseExponentialLikelihood extends OneParameterLikelihood {

    static MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());  

    public double pdfValue(double tau, double x) {
	return tau/(x*x) * Math.exp(-tau/x);
    }

    public double getMLE(double[] data) {
	double sum = 0;
	int n = data.length;
	for (int i=0; i < n; i++) sum += 1/data[i];
	return n/sum;
    }

    public double getLogLikelihood(double tau, double data) {
	return Math.log(tau) - 2*Math.log(data) - tau/data;
    }

    public double getLogLikelihoodOfModel(double[] model, double[] data) throws BinningException {

	BinningUtils.checkArrayLengthsAreEqual(model, data);
	double logLikelihood = 0;
	for (int i=0; i < data.length; i++) {
	    logLikelihood += getLogLikelihood(model[i], data[i]);
	}
	return logLikelihood;
    }


    public double getLikelihoodAtMode(double tau) {
	double mode = tau/2;
	return pdfValue(tau, mode);
    }

}
