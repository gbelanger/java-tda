package gb.tda.periodogram;

import org.apache.log4j.Logger;
import gb.tda.eventlist.IEventList;
import gb.tda.timeseries.IBinnedTimeSeries;
//import gb.tda.utils.BasicStats;
import gb.tda.utils.DataUtils;

public class ModifiedRayleighPeriodogramFactory {

    private static Logger logger  = Logger.getLogger(ModifiedRayleighPeriodogramFactory.class);

    /**
     * <code>makeModifiedRayleighPeriodogram</code>
     *
     * @param evlist an <code>AstroEventList</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static ModifiedRayleighPeriodogram create(IEventList evlist) throws PeriodogramException {
        int harmonic = 1;
        return create(evlist, harmonic);
    }

    /**
     * <code>makeModifiedRayleighPeriodogram</code>
     *
     * @param evlist an <code>AstroEventList</code> value
     * @param harmonic an <code>int</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static ModifiedRayleighPeriodogram create(IEventList evlist, int harmonic) throws PeriodogramException {
        double duration = evlist.duration();
        double nuMin = 1/duration;
        double nyquistFrequency = 2*evlist.minEventSpacing();
        double effectiveNyquistFrequency = 2*evlist.meanRate();
        effectiveNyquistFrequency += Math.ulp(effectiveNyquistFrequency);
        double nuMax = effectiveNyquistFrequency;
        int samplingFactor = 1;
        return create(evlist, nuMin, nuMax, samplingFactor, harmonic);
    }

    /**
     * <code>makeModifiedRayleighPeriodogram</code>
     *
     * @param evlist an <code>AstroEventList</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  ModifiedRayleighPeriodogram create(IEventList evlist, double nuMin, double nuMax, int samplingFactor) throws PeriodogramException {
        int harmonic = 1;
        return create(evlist, nuMin, nuMax, samplingFactor, harmonic);
    }

    /**
     * <code>makeModifiedRayleighPeriodogram</code>
     *
     * @param evlist an <code>AstroEventList</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @param harmonic an <code>int</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  ModifiedRayleighPeriodogram create(IEventList evlist, double nuMin, double nuMax, int samplingFactor, int harmonic) throws PeriodogramException {
        logger.info("Making ModifiedRayleighPeriodogram from AstroEventList");
        logger.info("  harmonic = "+harmonic);
        logger.info("  sampling = "+samplingFactor);
        int sampling = PeriodogramUtils.checkSamplingFactor(samplingFactor);
        samplingFactor = sampling;
        logger.info("  nuMin = "+nuMin);
        logger.info("  nuMax = "+nuMax);
        double duration = evlist.duration();
        double dtMin = evlist.minEventSpacing();
        double[] nuMinAndNuMax = PeriodogramUtils.checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
        //  Define the test frequencies
        double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
        int nTrials = testFreqs.length;
        return create(evlist, testFreqs, samplingFactor, harmonic);
    }

    public static ModifiedRayleighPeriodogram create(IEventList evlist, double[] testFreqs, int samplingFactor) throws PeriodogramException {
        int harmonic = 1;
        return create(evlist, testFreqs, samplingFactor, harmonic);
    }

    public static ModifiedRayleighPeriodogram create(IEventList evlist, double[] testFreqs, int samplingFactor, int harmonic) throws PeriodogramException {
        double[] powers = calculateModifiedRayleighPowers(evlist, testFreqs, harmonic);
        return new ModifiedRayleighPeriodogram(testFreqs, powers, samplingFactor, harmonic);
    }

    public static double[] calculateModifiedRayleighPowers(IEventList evlist, double[] testFreqs) {
        int harmonic = 1;
        return calculateModifiedRayleighPowers(evlist, testFreqs, harmonic);
    }

    public static double[] calculateModifiedRayleighPowers(IEventList evlist, double[] testFreqs, int harmonic) {
        logger.info("Calculating modified Rayleigh powers");
        double[] arrivalTimes = evlist.getArrivalTimes();
        int nTrials = testFreqs.length;
        double[] powers = new double[nTrials];
        for (int i=0; i < nTrials; i++) {
            double period = 1.0/testFreqs[i];
            powers[i] = PowerCalculator.getCorrectedPowerForThisHarmonic(arrivalTimes, period, harmonic);
        }
        return powers;
    }

    public static double[][] calculateModifiedRayleighPowerComponents(IEventList evlist, double[] testFreqs, int harmonic) {
        double[] arrivalTimes = evlist.getArrivalTimes();
        int nTrials = testFreqs.length;
        double[] powers = new double[nTrials];
        double[] meansCos = new double[nTrials];
        double[] meansSin = new double[nTrials];
        double[] expectedMeansCos = new double[nTrials];
        double[] expectedMeansSin = new double[nTrials];
        double[] variancesOfCos = new double[nTrials];
        double[] variancesOfSin = new double[nTrials];
        double[] covariancesOfCosSin = new double[nTrials];
        for (int i=0; i < nTrials; i++) {
            double period = 1.0/testFreqs[i];
            double[] components = PowerCalculator.getCorrectedPowerComponentsForThisHarmonic(arrivalTimes, period, harmonic);
            powers[i] = components[0];
            meansCos[i] = components[1];
            meansSin[i] = components[2];
            expectedMeansCos[i] = components[3];
            expectedMeansSin[i] = components[4];
            variancesOfCos[i] = components[5];
            variancesOfSin[i] = components[6];
            covariancesOfCosSin[i] = components[7];
        }
        return new double[][] {powers, meansCos, meansSin, expectedMeansCos, expectedMeansSin, variancesOfCos, variancesOfSin, covariancesOfCosSin};
    }

    /**
     * <code>makeModifiedRayleighPeriodogram</code>
     *
     * @param lc a <code>IBinnedTimeSeries</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     */
    public static  ModifiedRayleighPeriodogram create(IBinnedTimeSeries lc, int samplingFactor) {
        double nuMin = 1/lc.duration();
        double dt = lc.minBinWidth();
        dt = lc.maxBinWidth();
        double nyquistFrequency = 1/(2*dt);
        return create(lc, nuMin, nyquistFrequency, samplingFactor);
    }

    public static  ModifiedRayleighPeriodogram create(IBinnedTimeSeries lc) {
        return create(lc, 1);
    }


    /**
     * <code>makeModifiedRayleighPeriodogram</code>
     *
     * @param lc a <code>IBinnedTimeSeries</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     */
    public static  ModifiedRayleighPeriodogram create(IBinnedTimeSeries lc, double nuMin, double nuMax, int samplingFactor) {
        int harmonic = 1;
        return create(lc, nuMin, nuMax, samplingFactor, harmonic);
    }

    /**
     * <code>makeModifiedRayleighPeriodogram</code>
     *
     * @param lc a <code>IBinnedTimeSeries</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @param harmonic an <code>int</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     */
    public static  ModifiedRayleighPeriodogram create(IBinnedTimeSeries lc, double nuMin, double nuMax, int samplingFactor, int harmonic) {
        logger.info("Making ModifiedRayleighPeriodogram from IBinnedTimeSeries");
        logger.info("  harmonic (k) = "+harmonic);
        logger.info("  sampling = "+samplingFactor);
        int sampling = PeriodogramUtils.checkSamplingFactor(samplingFactor);
        samplingFactor = sampling;
        logger.info("  nuMin = "+nuMin);
        logger.info("  nuMax = "+nuMax);
        double duration = lc.duration();
        double dtMin = lc.minBinWidth();
        double[] nuMinAndNuMax = PeriodogramUtils.checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
        //  Define test frequencies
        double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
        int nTrials = testFreqs.length;
        //  Get rates
        double[] binCentres = lc.getBinCentres();
        double[] rates = lc.getIntensities();
        double[] errors = new double[rates.length];
        if (lc.uncertaintiesAreSet()) {
            logger.info("Time series errors are set: using defined uncertainties on rates");
            errors = lc.getUncertainties();
        }
        else {
            logger.info("Time series errors are not set: using unweighted power calculation (errors[i]=1.0)");
            for (int i=0; i < errors.length; i++) {
                errors[i] = 1.0;
            }
        }
        double meanRate = lc.meanIntensity();
        //   Fill gaps
        // double[] filledRates = DataUtils.fillDataGaps(rates);
        // doublep[ filledErrors = DataUtils.fillDataGaps(errors);
        // meanRate = BasicStats.getMean(filledRates);
        //   Calculate power
        logger.info("Calculating modified Rayleigh powers");
        double[] powers = new double[nTrials];
        double[] times_reset = DataUtils.resetToZero(binCentres);
        for (int i=0; i < nTrials; i++) {
            double period = 1.0/testFreqs[i];
            powers[i] = PowerCalculator.getModRayleighPower(times_reset, rates, errors, period, meanRate, harmonic);
            //powers[i] = PowerCalculator.getModRayleighPower(times_reset, filledRates, filledErrors, period, meanRate, harmonic);
        }
        return new ModifiedRayleighPeriodogram(testFreqs, powers, samplingFactor, harmonic);
    }

}