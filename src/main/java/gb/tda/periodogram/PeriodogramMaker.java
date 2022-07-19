package gb.tda.periodogram;

import cern.colt.list.DoubleArrayList;
import org.apache.log4j.Logger;
import gb.tda.binner.BinningException;
import gb.tda.eventlist.AstroEventList;
import gb.tda.likelihood.ExponentialLikelihood;
import gb.tda.timeseries.TimeSeries;
import gb.tda.timeseries.TimeSeriesException;
import gb.tda.timeseries.TimeSeriesMaker;
import gb.tda.timeseries.TimeSeriesResampler;
import gb.tda.timeseries.TimeSeriesUtils;
import gb.tda.utils.BasicStats;
import gb.tda.utils.Complex;
import gb.tda.utils.ComplexNumbers;
import gb.tda.utils.DataUtils;
import gb.tda.utils.MinMax;
import gb.tda.tools.FFT;
//import gb.tda.tools.MyFFT;



    //  Factory methods for ModifiedRayleighPeriodogram (including the General Modified Rayleigh Periodogram)

    private static double[] checkNuMinAndNuMax(double duration, double dtMin, double nuMin, double nuMax) {
		//  Check nuMin
		double min = 1/duration;
		if (nuMin < min) {
			logger.warn("  Specified nuMin < 1/duration: "+nuMin+" < "+min+". Resetting to 1/duration.");
			nuMin = min;
		}
		//  Check nuMax
		double nyquistFrequency = 1/(2*dtMin);
		//nyquistFrequency -= Math.ulp(nyquistFrequency);
		double max = nyquistFrequency;
		if (nuMax > max) {
			logger.warn("  Specified nuMax > Nyquist frequency = 1/(2*dtMin): "+nuMax+" > "+max+". Resetting to Nyquist frequency.");
			nuMax = nyquistFrequency;
		}
		return new double[] {nuMin, nuMax};
    }

    private static int checkSamplingFactor(int samplingFactor) {
		int sampling = samplingFactor;
		if (samplingFactor < 1) {
			logger.warn("  Specified sampling factor < 1. Resetting to 1.");
			sampling = 1;
		}
		return sampling;
    }

    public static LikelihoodPeriodogram makeLikelihoodPeriodogram(Periodogram dataPeriodogram, double[] modelPowers) throws PeriodogramException {
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

    public static LikelihoodPeriodogram makeLikelihoodPeriodogram(Periodogram dataPeriodogram, Periodogram modelPeriodogram) throws PeriodogramException {
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

    /**
     * <code>makeModifiedRayleighPeriodogram</code>
     *
     * @param evlist an <code>AstroEventList</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static ModifiedRayleighPeriodogram makeModifiedRayleighPeriodogram(AstroEventList evlist) throws PeriodogramException {
		int harmonic = 1;
		return makeModifiedRayleighPeriodogram(evlist, harmonic);
    }

    /**
     * <code>makeModifiedRayleighPeriodogram</code>
     *
     * @param evlist an <code>AstroEventList</code> value
     * @param harmonic an <code>int</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static ModifiedRayleighPeriodogram makeModifiedRayleighPeriodogram(AstroEventList evlist, int harmonic) throws PeriodogramException {
		double duration = evlist.duration();
		double nuMin = 1/duration;
		double nyquistFrequency = 2*evlist.minEventSpacing();
		double effectiveNyquistFrequency = 2*evlist.meanRate();
		effectiveNyquistFrequency += Math.ulp(effectiveNyquistFrequency);
		double nuMax = effectiveNyquistFrequency;
		int samplingFactor = 1;
		return makeModifiedRayleighPeriodogram(evlist, nuMin, nuMax, samplingFactor, harmonic);
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
    public static  ModifiedRayleighPeriodogram makeModifiedRayleighPeriodogram(AstroEventList evlist, double nuMin, double nuMax, int samplingFactor) throws PeriodogramException {
		int harmonic = 1;
		return makeModifiedRayleighPeriodogram(evlist, nuMin, nuMax, samplingFactor, harmonic);
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
    public static  ModifiedRayleighPeriodogram makeModifiedRayleighPeriodogram(AstroEventList evlist, double nuMin, double nuMax, int samplingFactor, int harmonic) throws PeriodogramException {
		logger.info("Making ModifiedRayleighPeriodogram from AstroEventList");
		logger.info("  harmonic = "+harmonic);
		logger.info("  sampling = "+samplingFactor);
		int sampling = checkSamplingFactor(samplingFactor);
		samplingFactor = sampling;
		logger.info("  nuMin = "+nuMin);
		logger.info("  nuMax = "+nuMax);
		double duration = evlist.duration();
		double dtMin = evlist.minEventSpacing();
		double[] nuMinAndNuMax = checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
		//  Define the test frequencies
		double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
		int nTrials = testFreqs.length;
		return makeModifiedRayleighPeriodogram(evlist, testFreqs, samplingFactor, harmonic);
    }

    public static ModifiedRayleighPeriodogram makeModifiedRayleighPeriodogram(AstroEventList evlist, double[] testFreqs, int samplingFactor) throws PeriodogramException {
		int harmonic = 1;
		return makeModifiedRayleighPeriodogram(evlist, testFreqs, samplingFactor, harmonic);
    }

    public static ModifiedRayleighPeriodogram makeModifiedRayleighPeriodogram(AstroEventList evlist, double[] testFreqs, int samplingFactor, int harmonic) throws PeriodogramException {
	double[] powers = calculateModifiedRayleighPowers(evlist, testFreqs, harmonic);
	return new ModifiedRayleighPeriodogram(testFreqs, powers, samplingFactor, harmonic);
    }

    public static double[] calculateModifiedRayleighPowers(AstroEventList evlist, double[] testFreqs) {
	int harmonic = 1;
	return calculateModifiedRayleighPowers(evlist, testFreqs, harmonic);
    }

    public static double[] calculateModifiedRayleighPowers(AstroEventList evlist, double[] testFreqs, int harmonic) {
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

    public static double[][] calculateModifiedRayleighPowerComponents(AstroEventList evlist, double[] testFreqs, int harmonic) {
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
     * @param lc a <code>TimeSeries</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     */
    public static  ModifiedRayleighPeriodogram makeModifiedRayleighPeriodogram(TimeSeries lc, int samplingFactor) {
	double nuMin = 1/lc.duration();
	double dt = lc.minBinWidth();
	dt = lc.maxBinWidth();
	double nyquistFrequency = 1/(2*dt);
	return makeModifiedRayleighPeriodogram(lc, nuMin, nyquistFrequency, samplingFactor);
    }

    public static  ModifiedRayleighPeriodogram makeModifiedRayleighPeriodogram(TimeSeries lc) {
	return makeModifiedRayleighPeriodogram(lc, 1);
    }


    /**
     * <code>makeModifiedRayleighPeriodogram</code>
     *
     * @param lc a <code>TimeSeries</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     */
    public static  ModifiedRayleighPeriodogram makeModifiedRayleighPeriodogram(TimeSeries lc, double nuMin, double nuMax, int samplingFactor) {
	int harmonic = 1;
	return makeModifiedRayleighPeriodogram(lc, nuMin, nuMax, samplingFactor, harmonic);
    }

    /**
     * <code>makeModifiedRayleighPeriodogram</code>
     *
     * @param lc a <code>TimeSeries</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @param harmonic an <code>int</code> value
     * @return a <code>ModifiedRayleighPeriodogram</code> value
     */
    public static  ModifiedRayleighPeriodogram makeModifiedRayleighPeriodogram(TimeSeries lc, double nuMin, double nuMax, int samplingFactor, int harmonic) {
	logger.info("Making ModifiedRayleighPeriodogram from TimeSeries");
	logger.info("  harmonic (k) = "+harmonic);
	logger.info("  sampling = "+samplingFactor);
	int sampling = checkSamplingFactor(samplingFactor);
	samplingFactor = sampling;
	logger.info("  nuMin = "+nuMin);
	logger.info("  nuMax = "+nuMax);
	double duration = lc.duration();
	double dtMin = lc.minBinWidth();
	double[] nuMinAndNuMax = checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
	//  Define test frequencies
	double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
	int nTrials = testFreqs.length;
	//  Get rates
	double[] binCentres = lc.getBinCentres();
	double[] rates = lc.getRates();
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
	double meanRate = lc.meanRate();
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

    /**
     * <code>makeLombScarglePeriodogram</code>
     *
     * @param lc a <code>TimeSeries</code> value
     * @param nuMin a <code>double</code> value
     * @param nuMax a <code>double</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>LombScarglePeriodogram</code> value
     */
    public static LombScarglePeriodogram makeLombScarglePeriodogram(TimeSeries lc, double nuMin, double nuMax, int samplingFactor) {
	logger.info("Making LombScarglePeriodogram with sampling factor "+samplingFactor);
	//  Check min and max frequencies
	double duration = lc.duration();
	double dtMin = lc.minBinWidth();
	double[] nuMinAndNuMax = checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
	int sampling = checkSamplingFactor(samplingFactor);
	samplingFactor = sampling;
	//  Define test frequencies
	double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
	int nTrials = testFreqs.length;
	//  Get rates info
	double[] binCentres = lc.getBinCentres();
	double[] rates = lc.getRates();
	double meanRate = lc.meanRate();
	double varianceInMeanSubtractedRates = BasicStats.getVariance(lc.getMeanSubtractedRates());
	//  Calculate powers
	logger.info("Calculating Lomb powers");
	double[] powers = new double[nTrials];
	for (int i=0; i < nTrials; i++) {
	    double period = 1.0/testFreqs[i];
	    powers[i] = PowerCalculator.getLombPower(binCentres, rates, period, meanRate, varianceInMeanSubtractedRates);
	}
	return new LombScarglePeriodogram(testFreqs, powers, samplingFactor);
    }

    public static LombScarglePeriodogram makeLombScarglePeriodogram(TimeSeries ts) {
	double nuMin = 1/ts.duration();
	double dt = MinMax.getMin(ts.getBinWidths());
	//System.out.println(dt+" s");
	double nyquistFrequency = 1/(2*dt);
	//System.out.println(nyquistFrequency+" Hz");
	nyquistFrequency -= Math.ulp(nyquistFrequency);
	int samplingFactor = 1;
	return makeLombScarglePeriodogram(ts, nuMin, nyquistFrequency, samplingFactor);
    }

    /**
     * <code>makeRayleighPeriodogram</code>
     *
     * @param evlist an <code>AstroEventList</code> value
     * @return a <code>RayleighPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static RayleighPeriodogram makeRayleighPeriodogram(AstroEventList evlist) throws PeriodogramException {
	double duration = evlist.duration();
	double nuMin = 1/duration;
	double effectiveNyquistFrequency = 2*evlist.meanRate();
	effectiveNyquistFrequency -= Math.ulp(effectiveNyquistFrequency);
	int samplingFactor = 1;
	return makeRayleighPeriodogram(evlist, nuMin, effectiveNyquistFrequency, samplingFactor);
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
    public static  RayleighPeriodogram makeRayleighPeriodogram(AstroEventList evlist, double nuMin, double nuMax, int samplingFactor) throws PeriodogramException {
	logger.info("Making RayleighPeriodogram from AstroEventList with sampling factor of "+samplingFactor);
	double duration = evlist.duration();
	double dtMin = evlist.minEventSpacing();
	double[] nuMinAndNuMax = checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
	int sampling = checkSamplingFactor(samplingFactor);
	samplingFactor = sampling;
	//  Define the test frequencies
	double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
	int nTrials = testFreqs.length;
	//  Calculate powers
	double[] arrivalTimes = evlist.getArrivalTimes();
	double[] powers = new double[nTrials];
	int nHarmonics = 1;
	for (int i=0; i < nTrials; i++) {		
	    double period = 1.0/testFreqs[i];
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
    public static  RayleighPeriodogram makeRayleighPeriodogram(TimeSeries lc) throws PeriodogramException {
	double nuMin = 1/lc.duration();
	double dt = MinMax.getMin(lc.getBinWidths());
	double nyquistFrequency = 1/(2*dt);
	nyquistFrequency -= Math.ulp(nyquistFrequency);
	int samplingFactor = 1;
	return makeRayleighPeriodogram(lc, nuMin, nyquistFrequency, samplingFactor);
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
    public static  RayleighPeriodogram makeRayleighPeriodogram(TimeSeries lc, double nuMin, double nuMax, int samplingFactor) throws PeriodogramException {
	logger.info("Making RayleighPeriodogram from TimeSeries with sampling factor of "+samplingFactor);
	double duration = lc.duration();
	double dtMin = lc.minBinWidth();
	double[] nuMinAndNuMax = checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
	int sampling = checkSamplingFactor(samplingFactor);
	samplingFactor = sampling;
	//  Define test frequencies
	double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
	int nTrials = testFreqs.length;
	//  Calculate powers
	double[] binCentres = lc.getBinCentres();
	double[] rates = lc.getRates();
	double[] errors = lc.getUncertainties();
	double[] powers = new double[nTrials];
	int nHarmonics = 1;
	for (int i=0; i < nTrials; i++) {
	    double period = 1.0/testFreqs[i];
	    powers[i] = PowerCalculator.getZ2stats(binCentres, rates, errors, period, nHarmonics)[2];
	}
	return new RayleighPeriodogram(testFreqs, powers, samplingFactor);
    }

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
    public static  ModifiedZPeriodogram makeModifiedZPeriodogram(AstroEventList evlist, double nuMin, double nuMax, int samplingFactor, int nHarmonics) throws PeriodogramException {
	double duration = evlist.duration();
	double dtMin = evlist.minEventSpacing();
	double[] nuMinAndNuMax = checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
	int sampling = checkSamplingFactor(samplingFactor);
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
    public static  ZPeriodogram makeZPeriodogram(AstroEventList evlist, double nuMin, double nuMax, int samplingFactor, int nHarmonics) throws PeriodogramException {
	double duration = evlist.duration();
	double dtMin = evlist.minEventSpacing();
	double[] nuMinAndNuMax = checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
	int sampling = checkSamplingFactor(samplingFactor);
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
    public static  ZPeriodogram makeZPeriodogram(TimeSeries lc, double nuMin, double nuMax, int samplingFactor, int nHarmonics) throws PeriodogramException {
	double duration = lc.duration();
	double dtMin = lc.minBinWidth();
	double[] nuMinAndNuMax = checkNuMinAndNuMax(duration, dtMin, nuMin, nuMax);
	int sampling = checkSamplingFactor(samplingFactor);
	samplingFactor = sampling;
	//  Define test frequencies
	double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMinAndNuMax[0], nuMinAndNuMax[1], duration, samplingFactor);
	int nTrials = testFreqs.length;
	//  Calculate powers
	double[] binCentres = lc.getBinCentres();
	double[] rates = lc.getRates();
	double[] errors = lc.getUncertainties();
	double[] powers = new double[nTrials];
	for (int i=0; i < nTrials; i++) {
	    double period = 1.0/testFreqs[i];
	    powers[i] = PowerCalculator.getZ2stats(binCentres, rates, errors, period, nHarmonics)[2];
	}
	return new ZPeriodogram(testFreqs, powers, samplingFactor, nHarmonics);
    }


}
