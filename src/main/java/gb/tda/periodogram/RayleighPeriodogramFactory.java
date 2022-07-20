package gb.tda.periodogram;

import org.apache.log4j.Logger;
import gb.tda.eventlist.IEventList;
import gb.tda.timeseries.IBinnedTimeSeries;
import gb.tda.utils.BasicStats;
import gb.tda.utils.MinMax;

public class RayleighPeriodogramFactory {

    private static Logger logger  = Logger.getLogger(RayleighPeriodogramFactory.class);

    /**
     * <code>makeRayleighPeriodogram</code>
     *
     * @param evlist an <code>AstroEventList</code> value
     * @return a <code>RayleighPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static RayleighPeriodogram create(IEventList evlist) throws PeriodogramException {
        double duration = evlist.duration();
        double nuMin = 1/duration;
        double effectiveNyquistFrequency = 2 * evlist.meanRate();
        effectiveNyquistFrequency -= Math.ulp(effectiveNyquistFrequency);
        int samplingFactor = 1;
        return create(evlist, nuMin, effectiveNyquistFrequency, samplingFactor);
    }

    /**
     * <code>makeRayleighPeriodogram</code>
     *
     * @param evlist an <code>AstroEventList</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>RayleighPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  RayleighPeriodogram create(IEventList evlist, double nuMin, double nuMax, int samplingFactor) throws PeriodogramException {
        logger.info("Making RayleighPeriodogram from AstroEventList with sampling factor of "+samplingFactor);
        double duration = evlist.duration();
        double dtMin = evlist.minEventSpacing();
        double[] nuMinAndNuMax = PeriodogramUtils.checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
        int sampling = PeriodogramUtils.checkSamplingFactor(samplingFactor);
        samplingFactor = sampling;
        //  Define the test frequencies
        double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
        int nTrials = testFreqs.length;
        //  Calculate powers
        double[] arrivalTimes = evlist.getArrivalTimes();
        double[] powers = new double[nTrials];
        int nHarmonics = 1;
        for (int i=0; i < nTrials; i++) {
            double period = 1.0 / testFreqs[i];
            powers[i] = PowerCalculator.getZ2stats(arrivalTimes, period, nHarmonics)[2];
        }
        return new RayleighPeriodogram(testFreqs, powers, samplingFactor);
    }

    /**
     * <code>makeRayleighPeriodogram</code>
     *
     * @param lc a <code>TimeSeries</code> value
     * @return a <code>RayleighPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  RayleighPeriodogram create(IBinnedTimeSeries lc) throws PeriodogramException {
        double nuMin = 1/lc.duration();
        double dt = MinMax.getMin(lc.getBinWidths());
        double nyquistFrequency = 1/(2*dt);
        nyquistFrequency -= Math.ulp(nyquistFrequency);
        int samplingFactor = 1;
        return create(lc, nuMin, nyquistFrequency, samplingFactor);
    }

    /**
     * <code>makeRayleighPeriodogram</code>
     *
     * @param lc a <code>TimeSeries</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>RayleighPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  RayleighPeriodogram create(IBinnedTimeSeries lc, double nuMin, double nuMax, int samplingFactor) throws PeriodogramException {
        logger.info("Making RayleighPeriodogram from TimeSeries with sampling factor of "+samplingFactor);
        double duration = lc.duration();
        double dtMin = lc.minBinWidth();
        double[] nuMinAndNuMax = PeriodogramUtils.checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
        int sampling = PeriodogramUtils.checkSamplingFactor(samplingFactor);
        samplingFactor = sampling;
        //  Define test frequencies
        double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
        int nTrials = testFreqs.length;
        //  Calculate powers
        double[] binCentres = lc.getBinCentres();
        double[] rates = lc.getIntensities();
        double[] errors = lc.getUncertainties();
        double[] powers = new double[nTrials];
        int nHarmonics = 1;
        for (int i=0; i < nTrials; i++) {
            double period = 1.0/testFreqs[i];
            powers[i] = PowerCalculator.getZ2stats(binCentres, rates, errors, period, nHarmonics)[2];
        }
        return new RayleighPeriodogram(testFreqs, powers, samplingFactor);
    }

}