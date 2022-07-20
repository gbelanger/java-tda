package gb.tda.periodogram;

import org.apache.log4j.Logger;
import gb.tda.eventlist.IEventList;
import gb.tda.timeseries.IBinnedTimeSeries;

public class ZPeriodogramFactory {

    private static Logger logger  = Logger.getLogger(ZPeriodogramFactory.class);

    /**
     * <code>makeModifiedZPeriodogram</code>
     *
     * @param evlist an <code>AstroEventList</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @param nHarmonics an <code>int</code> value
     * @return a <code>ModifiedZPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  ModifiedZPeriodogram makeModifiedZPeriodogram(IEventList evlist, double nuMin, double nuMax, int samplingFactor, int nHarmonics) throws PeriodogramException {
        double duration = evlist.duration();
        double dtMin = evlist.minEventSpacing();
        double[] nuMinAndNuMax = PeriodogramUtils.checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
        int sampling = PeriodogramUtils.checkSamplingFactor(samplingFactor);
        samplingFactor = sampling;
        //  Define test frequencies
        double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
        int nTrials = testFreqs.length;
        //  Calculate powers
        double[] arrivalTimes = evlist.getArrivalTimes();
        double[] powers = new double[nTrials];
        for (int i=0; i < nTrials; i++) {
            double period = 1.0/testFreqs[i];
            powers[i] = PowerCalculator.getModifiedZ2Power(arrivalTimes, period, nHarmonics);
        }
        return new ModifiedZPeriodogram(testFreqs, powers, samplingFactor, nHarmonics);
    }

    /**
     * <code>makeZPeriodogram</code>
     *
     * @param evlist an <code>AstroEventList</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @param nHarmonics an <code>int</code> value
     * @return a <code>ZPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  ZPeriodogram makeZPeriodogram(IEventList evlist, double nuMin, double nuMax, int samplingFactor, int nHarmonics) throws PeriodogramException {
        double duration = evlist.duration();
        double dtMin = evlist.minEventSpacing();
        double[] nuMinAndNuMax = PeriodogramUtils.checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
        int sampling = PeriodogramUtils.checkSamplingFactor(samplingFactor);
        samplingFactor = sampling;
        //  Define test frequencies
        double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
        int nTrials = testFreqs.length;
        //  Calculate powers
        double[] arrivalTimes = evlist.getArrivalTimes();
        double[] powers = new double[nTrials];
        for (int i=0; i < nTrials; i++) {
            double period = 1.0/testFreqs[i];
            powers[i] = PowerCalculator.getZ2stats(arrivalTimes, period, nHarmonics)[2];
        }
        return new ZPeriodogram(testFreqs, powers, samplingFactor, nHarmonics);
    }

    /**
     * <code>makeZPeriodogram</code>
     *
     * @param lc a <code>TimeSeries</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @param nHarmonics an <code>int</code> value
     * @return a <code>ZPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  ZPeriodogram makeZPeriodogram(IBinnedTimeSeries ts, double nuMin, double nuMax, int samplingFactor, int nHarmonics) throws PeriodogramException {
        double duration = ts.duration();
        double dtMin = ts.minBinWidth();
        double[] nuMinAndNuMax = PeriodogramUtils.checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
        int sampling = PeriodogramUtils.checkSamplingFactor(samplingFactor);
        samplingFactor = sampling;
        //  Define test frequencies
        double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
        int nTrials = testFreqs.length;
        //  Calculate powers
        double[] binCentres = ts.getBinCentres();
        double[] rates = ts.getIntensities();
        double[] errors = ts.getUncertainties();
        double[] powers = new double[nTrials];
        for (int i=0; i < nTrials; i++) {
            double period = 1.0/testFreqs[i];
            powers[i] = PowerCalculator.getZ2stats(binCentres, rates, errors, period, nHarmonics)[2];
        }
        return new ZPeriodogram(testFreqs, powers, samplingFactor, nHarmonics);
    }

}