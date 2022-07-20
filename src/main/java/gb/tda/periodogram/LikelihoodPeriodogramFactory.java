package gb.tda.periodogram;

import org.apache.log4j.Logger;
import gb.tda.likelihood.ExponentialLikelihood;

public class LikelihoodPeriodogramFactory {

    private static Logger logger  = Logger.getLogger(LikelihoodPeriodogramFactory.class);

    public static LikelihoodPeriodogram create(AbstractPeriodogram dataPeriodogram, double[] modelPowers) throws PeriodogramException {
        double[] freqs = dataPeriodogram.getFreqs();
        double[] dataPowers = dataPeriodogram.getPowers();
        if (dataPowers.length != modelPowers.length) {
            throw new PeriodogramException("Unequal number of data and model power values");
        }
        ExponentialLikelihood expL = new ExponentialLikelihood();
        double[] inverseLikelihoods = new double[dataPowers.length];
        for (int i=0; i < dataPowers.length; i++) {
            double likelihood = expL.getLogLikelihood(modelPowers[i], dataPowers[i]);
            inverseLikelihoods[i] = -likelihood;
            //double likelihood = expL.getLikelihood(modelPowers[i], dataPowers[i]);
            //inverseLikelihoods[i] = 1/likelihood;
        }
        return new LikelihoodPeriodogram(freqs, inverseLikelihoods, dataPeriodogram.samplingFactor());
    }

    public static LikelihoodPeriodogram create(AbstractPeriodogram dataPeriodogram, Periodogram modelPeriodogram) throws PeriodogramException {
        double[] dataFreqs = dataPeriodogram.getFreqs();
        double[] dataPowers = dataPeriodogram.getPowers();
        double[] modelFreqs = modelPeriodogram.getFreqs();
        double[] modelPowers = modelPeriodogram.getPowers();
        if (dataPowers.length != modelPowers.length) {
            throw new PeriodogramException("Unequal number of data and model power values");
        }
        ExponentialLikelihood expL = new ExponentialLikelihood();
        double[] inverseLikelihoods = new double[dataPowers.length];
        for (int i=0; i < dataPowers.length; i++) {
            double likelihood = expL.getLogLikelihood(modelPowers[i], dataPowers[i]);
            inverseLikelihoods[i] = -likelihood;
            //double likelihood = expL.getLikelihood(modelPowers[i], dataPowers[i]);
            //inverseLikelihoods[i] = 1/likelihood;
        }
        return new LikelihoodPeriodogram(dataFreqs, inverseLikelihoods, dataPeriodogram.samplingFactor());
    }

}