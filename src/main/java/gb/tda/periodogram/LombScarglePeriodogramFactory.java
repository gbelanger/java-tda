package gb.tda.periodogram;

import org.apache.log4j.Logger;
import gb.tda.eventlist.IEventList;
import gb.tda.timeseries.IBinnedTimeSeries;
import gb.tda.utils.BasicStats;
import gb.tda.utils.MinMax;

public class LombScarglePeriodogramFactory {

    private static Logger logger  = Logger.getLogger(LombScarglePeriodogramFactory.class);

    /**
     * <code>makeLombScarglePeriodogram</code>
     *
     * @param lc a <code>TimeSeries</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>LombScarglePeriodogram</code> value
     */
    public static LombScarglePeriodogram create(IBinnedTimeSeries lc, double nuMin, double nuMax, int samplingFactor) {
        logger.info("Making LombScarglePeriodogram with sampling factor "+samplingFactor);
        //  Check min and max frequencies
        double duration = lc.duration();
        double dtMin = lc.minBinWidth();
        double[] nuMinAndNuMax = PeriodogramUtils.checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
        int sampling = PeriodogramUtils.checkSamplingFactor(samplingFactor);
        samplingFactor = sampling;
        //  Define test frequencies
        double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
        int nTrials = testFreqs.length;
        //  Get rates info
        double[] binCentres = lc.getBinCentres();
        double[] rates = lc.getIntensities();
        double meanIntensity = lc.meanIntensity();
        double varianceInMeanSubtractedRates = BasicStats.getVariance(lc.getMeanSubtractedIntensities());
        //  Calculate powers
        logger.info("Calculating Lomb powers");
        double[] powers = new double[nTrials];
        for (int i=0; i < nTrials; i++) {
            double period = 1.0/testFreqs[i];
            powers[i] = PowerCalculator.getLombPower(binCentres, rates, period, meanIntensity, varianceInMeanSubtractedRates);
        }
        return new LombScarglePeriodogram(testFreqs, powers, samplingFactor);
    }

    public static LombScarglePeriodogram create(IBinnedTimeSeries ts) {
        double nuMin = 1/ts.duration();
        double dt = MinMax.getMin(ts.getBinWidths());
        //System.out.println(dt+" s");
        double nyquistFrequency = 1/(2*dt);
        //System.out.println(nyquistFrequency+" Hz");
        nyquistFrequency -= Math.ulp(nyquistFrequency);
        int samplingFactor = 1;
        return create(ts, nuMin, nyquistFrequency, samplingFactor);
    }

}