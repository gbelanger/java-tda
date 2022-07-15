package gb.tda.likelihood;

import cern.jet.random.Poisson;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.stat.Gamma;
//import org.apache.commons.math3.special.Gamma;  // no difference: both stop at 170 (gamma of 8.58e307)
import gb.tda.binner.BinningException;
import gb.tda.binner.BinningUtils;
import gb.tda.tools.BasicStats;

public class PoissonLikelihood extends OneParameterLikelihood {

    //  Continuous analogue of Poisson p.d.f. where the factorial is replaced by the Gamma function
    double pdfValue(double parameter, double x) {
		return Math.exp(-parameter) * Math.pow(parameter, x) / Gamma.gamma(x+1);
    }
    
    //  Discrete (true) Poisson distribution function
//     double pdfValue(double parameter, int x) {
// 	Poisson poisson = new Poisson(parameter, new MersenneTwister64(new java.util.Date()));
// 	return poisson.pdf(x);
//     }

    public double getMLE(double[] data) {
		return BasicStats.getMean(data);
    }

    public double getLogLikelihood(double parameterValue, double data) {
		return data*Math.log(parameterValue) - parameterValue - Math.log(Gamma.gamma(data+1));
    }
    
    public double getLogLikelihoodOfModel(double[] model, double[] data) throws BinningException {
		BinningUtils.checkArrayLengthsAreEqual(model, data);
		double logLikelihood = 0;
		for (int i=0; i <  data.length; i++) {
		    logLikelihood += getLogLikelihood(model[i], data[i]);
		}
		return logLikelihood;
    }

    public double getCStatistic(double[] model, double[] data) throws BinningException {
		BinningUtils.checkArrayLengthsAreEqual(model, data);
		double cStat = 0;
		for (int i=0; i <  data.length; i++) {
		    cStat += data[i]*Math.log(model[i]) - model[i];
		}
		return -2*cStat;
    }

}
