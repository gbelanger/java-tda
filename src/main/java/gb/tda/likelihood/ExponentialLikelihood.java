package gb.tda.likelihood;

import java.util.Date;
import cern.jet.random.Exponential;
import cern.jet.random.engine.MersenneTwister64;
import gb.tda.utils.BasicStats;

public class ExponentialLikelihood extends OneParameterLikelihood {

    static MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());    

    public double pdfValue(double tau, double x) {
		double lambda = 1d/tau;
		Exponential exp = new Exponential(lambda, engine);
		return exp.pdf(x);
    }

    public double getMLE(double[] data) {
		return BasicStats.getMean(data);
    }

    public double getLogLikelihood(double tau, double data) {
		return -Math.log(tau) - data/tau;
    }

    public double getLogLikelihoodOfModel(double[] model, double[] data) throws IllegalArgumentException {
		Utils.checkArrayLengthsAreEqual(model, data);
		double logLikelihood = 0;
		for (int i=0; i < data.length; i++) {
			logLikelihood += getLogLikelihood(model[i], data[i]);
		}
		return logLikelihood;
    }

    public double getLikelihoodAtMode(double tau) {
		double mode = 0;
		return pdfValue(tau, mode);
    }

    public double getBStatistic(double[] model, double[] data) throws IllegalArgumentException {
		Utils.checkArrayLengthsAreEqual(model, data);
		double bStat = -0.5*getLogLikelihoodOfModel(model, data);
		return bStat;
    }

}
