package gb.tda.montecarlo;

import cern.jet.random.Poisson;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;
import gb.tda.binner.BinningException;
import gb.tda.binner.BinningUtils;
import gb.tda.binner.Resampler;
import gb.tda.io.AsciiDataFileWriter;
import gb.tda.periodogram.PeriodogramUtils;
import gb.tda.timeseries.TimeSeriesException;
import gb.tda.tools.Complex;
import gb.tda.tools.Converter;
import gb.tda.tools.DistributionFunc;
import hep.aida.ref.histogram.Histogram1D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import org.apache.log4j.Logger;
import gb.tda.timeseries.TimeSeriesMaker;
import gb.tda.timeseries.TimeSeries;
import gb.tda.eventlist.AstroEventList;
import gb.tda.binner.Binner;


/**
 * The class <code>RedNoiseGenerator</code> is used to simulate red noise. 
 * The term "red noise" refers to a signal that has the most power at the 
 * lowest frequencies, i.e., the "red" part of the spectrum. More precisely, 
 * the power spectrum of red noise follows a power-law with a negative index. 
 * The index determines the "redness" of the noise: the higher the value the 
 * redder the noise. At the lower limit, red noise with a spectral index of 0
 * is just white noise.
 *
 * @author <a href="mailto: guilaume.belanger@esa.int">Guillaume Belanger</a>
 * @version January 2020 (last modified)
 *
 */

public final class RedNoiseGenerator {

    static Logger logger = Logger.getLogger(RedNoiseGenerator.class);
    static DecimalFormat sci = new DecimalFormat("0.0##E00");
    static DecimalFormat number = new DecimalFormat("0.0##");
    static DecimalFormat freq = new DecimalFormat("0.0##E00");

    public static double[] generateArrivalTimes(final double meanRate, final double duration, final double alpha) throws BinningException, TimeSeriesException {
		MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
		int nFreqsPerIFS = 1;
		return generateArrivalTimes(meanRate, duration, alpha, nFreqsPerIFS, engine);
    }

    public static double[] generateArrivalTimes(final double meanRate, final double duration, final double alpha, final int nFreqsPerIFS) throws BinningException, TimeSeriesException {
		MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
		return generateArrivalTimes(meanRate, duration, alpha, nFreqsPerIFS, engine);
    }
    
    public static double[] generateArrivalTimes(final double meanRate, final double duration, final double alpha, final RandomEngine engine) throws BinningException, TimeSeriesException {
		int nFreqsPerIFS = 1;
		return generateArrivalTimes(meanRate, duration, alpha, nFreqsPerIFS, engine);
    }

    public static double[] generateArrivalTimes(final double meanRate, final double duration, final double alpha, final int nFreqsPerIFS, final RandomEngine engine) throws BinningException, TimeSeriesException {
		if ( alpha == 0 ) {
		    return WhiteNoiseGenerator.generateArrivalTimes(meanRate, duration, engine);
		}
		logger.info("Generating red noise arrival times:");
		logger.info("  Mean rate (specified) = "+number.format(meanRate));
		logger.info("  Duration (specified) = "+number.format(duration));
		logger.info("  Spectral index (specified) = "+alpha);
		logger.info("  Effective Nyquist frequency (2*meanRate) = "+number.format(2*meanRate)+" Hz");
		logger.info("  Effective Nyquist bintime = "+number.format(1/(2*meanRate))+" s");	
		
		// Define basic variables
		double nuMin = 1d/duration;
		double nuMax = 2d*meanRate;
		//nuMax = meanRate;
		double dtMin = 1d/nuMax;
		double df = nuMin/nFreqsPerIFS;
		double nFreqs = (nuMax-nuMin)/df;

		//  Adjust (down) nFreqs to be a power of 2
	 	double exponent = Math.floor(Math.log10(nFreqs)/Math.log10(2));
		int nNewBins = (int) Math.pow(2, exponent);
		nFreqs = nNewBins;
		
		nuMax = nuMin+df*nFreqs;
		double nTimeBins = 2*nFreqs;
		double dt = duration/nTimeBins;	
		int nIFS = (int) Math.floor((nuMax-nuMin)/nuMin);

		// Print out
		logger.info("  Minimum frequency (nuMin = 1/duration) = "+freq.format(nuMin)+" Hz");
		logger.info("  Maximum test frequency (adjusted to power of 2 frequencies) = "+number.format(nuMax)+" Hz");
		logger.info("  Number of IFS (nuMax-nuMin)/nuMin = "+nIFS);
		logger.info("  Test frequencies per IFS = "+nFreqsPerIFS);	
		logger.info("  Frequency step (df = nuMin/nFreqsPerIFS) = "+freq.format(df)+" Hz");
		logger.info("  Number of test frequencies (nFreqs = (nuMax - nuMin)/df) = "+((int)nFreqs));
		logger.info("  Red noise time-domain signal defined on "+((int)nTimeBins)+" bins"); 
		logger.info("  Inherent time resolution is "+sci.format(dt)+" s");

		// Get the TK rates
	 	double[] timmerRates = TimmerKonig.getRates(meanRate, duration, alpha, nFreqsPerIFS);

		//  Construct CDF from rates time series
		double tzero = 0;
		Histogram1D lcHisto = Converter.array2histo("light curve", tzero, dt, timmerRates);
		Histogram1D cdfHisto = DistributionFunc.getCDFHisto(lcHisto);
		
		//  Define the pseudo-random number of events to be drawn
		Poisson poisson = new Poisson(0, engine);
		int nevents = (int) Math.round(meanRate*duration);
		nevents = poisson.nextInt(nevents);

		//  Draw arrival times
		boolean drawFromCDF = true;
		double[] times = new double[nevents];
		if (drawFromCDF) {
		    //  From cdf
		    times = DistributionFunc.getRandom(cdfHisto, nevents);
		}
		else {
		    //  From timmerRates
		    times = DistributionFunc.getRandomFromRates(cdfHisto, nevents, timmerRates);
		}
		Arrays.sort(times);
		
		//  Adjust duration
	 	times[times.length-1] = times[0] + duration;
		double actualMean = times.length/duration;
		
		logger.info("Arrival times generated");
		logger.info("  nEvents = "+times.length);
		logger.info("  Mean rate (actual) = "+actualMean);

		//  OPTIONAL:
		//  Make plots and write event lists
		boolean compare = false;
		if (compare) {
		    try {
			print_tkRates(timmerRates, duration, dt);
			print_tkHistos(lcHisto, cdfHisto);
			AstroEventList evlist = new AstroEventList(times);
			evlist.writeTimesAsQDP("tk_times_cdf.qdp");
			// Draw from TK rates
			double[] times2 = DistributionFunc.getRandomFromRates(cdfHisto, nevents, timmerRates);
			AstroEventList evlist2 = new AstroEventList(times2);
			evlist2.writeTimesAsQDP("tk_times_rates.qdp");
		    }
		    catch (Exception e) {new Exception(e);}
		}
		return times;
    }
	    

    public static double[] generateTwoComponentArrivalTimes(final double meanRate, final double duration, final double alpha1, final double alpha2, final double nuBreak) throws BinningException, TimeSeriesException {
		int nFreqsPerIFS = 1;
		return generateTwoComponentArrivalTimes(meanRate, duration, alpha1, alpha2, nuBreak, nFreqsPerIFS);
    }

    public static double[] generateTwoComponentArrivalTimes(final double meanRate, final double duration, final double alpha1, final double alpha2, final double nuBreak, final int nFreqsPerIFS) throws BinningException, TimeSeriesException {
		logger.info("Generating two-component red noise arrival times");
		logger.info("  Mean rate (specified) = "+meanRate+" cps");
		logger.info("  Duration = "+duration+" s");
		logger.info("  Spectral index1 = "+alpha1);
		logger.info("  Spectral index2 = "+alpha2);
		logger.info("  Break frequency = "+nuBreak+" Hz");

		double nuMin = 1d/duration;
		double nuMax = 2d*meanRate;
		double df = nuMin/nFreqsPerIFS;
		double nFreqs = (nuMax - nuMin)/df;

		//  Adjust nuMax to have 2^x frequencies
	 	double exponent = Math.floor(Math.log10(nFreqs)/Math.log10(2));
		int nNewBins = (int) Math.pow(2, exponent);
		if ( nFreqs != nNewBins ) {
		    nNewBins = (int) Math.pow(2, exponent+1);
		    logger.warn("nFreqs ("+nFreqs+") was not a power of 2. Using "+nNewBins+" instead");
		    nFreqs = nNewBins;
		}
		nuMax = nuMin + df*(nFreqs);
		double[] frequencies = PeriodogramUtils.getFourierFrequencies(nuMin, nuMax, df);
		double powAtNuMin = meanRate*duration;
		Complex[] fourierComponents = TimmerKonig.getFourierComponentsForFrequencies(frequencies, alpha1, alpha2, nuBreak);
		double[] timmerRates = TimmerKonig.getRatesFromFourierComponents(fourierComponents);

	// 	// TEMP
	// 	int[] bins = new int[timmerRates.length];
	// 	for ( int i=0; i < timmerRates.length; i++ ) bins[i] = i;
	// 	String[] h = new String[] {"DEV /XS"};
	// 	AsciiDataFileWriter out = null;
	// 	try {
	// 	    out = new AsciiDataFileWriter("timmerRates.qdp");
	// 	    out.writeData(h, bins, timmerRates);
	// 	}
	// 	catch ( Exception e ) {};
	// 	// 

		//  Define the number of events
		MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
		Poisson poisson = new Poisson(0, engine);
		int nevents = (int) Math.round(meanRate*duration);
		logger.info("Nominal number of events = "+nevents+" (duration*meanRate)");
		nevents = poisson.nextInt(nevents);
		logger.info("Random Poisson number of events = "+nevents);

		//  Draw arrival times from the light curve's CDF
		double tzero = 0;
		double nTimeBins = 2*nFreqs;
		double tkBinTime = duration/nTimeBins;
		Histogram1D lcHisto = Converter.array2histo("light curve", tzero, tkBinTime, timmerRates);
		Histogram1D cdfHisto = DistributionFunc.getCDFHisto(lcHisto);
		double[] times = DistributionFunc.getRandom(cdfHisto, nevents);
		Arrays.sort(times);

	// 	// TEMP
	// 	try {
	// 	    AsciiDataFileWriter lc = new AsciiDataFileWriter("lcHisto.qdp");
	// 	    lc.writeHisto(lcHisto, "LC");
	// 	}
	// 	catch ( Exception e ) {}
	// 	//

		//  Adjust actual duration to specified duration
		times[times.length-1] = times[0] + duration;
		double actualMean = times.length/duration;
		
		logger.info("Arrival times in list = "+times.length);
		logger.info("Mean rate (actual) = "+actualMean);

		return times;
    }



    public static double[] generateThreeComponentArrivalTimes(final double meanRate, final double duration, final double alpha1, final double alpha2, final double alpha3, final double nuBreak1, final double nuBreak2) throws BinningException, TimeSeriesException {
		int nFreqsPerIFS = 1;
		logger.info("Generating three-component red noise arrival times");
		logger.info("  Mean rate (specified) = "+meanRate);
		logger.info("  Duration = "+sci.format(duration));
		logger.info("  Spectral index1 = "+alpha1);
		logger.info("  Spectral index2 = "+alpha2);
		logger.info("  Spectral index3 = "+alpha3);
		logger.info("  Break frequency1 = "+nuBreak1);
		logger.info("  Break frequency2 = "+nuBreak2);

		double nuMin = 1d/duration;
		double nuMax = 2*meanRate;
		double df = nuMin/nFreqsPerIFS;
		double nFreqs = (nuMax - nuMin)/df;

		//  Adjust nuMax to have 2^x frequencies
	 	double exponent = Math.floor(Math.log10(nFreqs)/Math.log10(2));
		int nNewBins = (int) Math.pow(2, exponent);
		if ( nFreqs != nNewBins ) {
		    nNewBins = (int) Math.pow(2, exponent+1);
		    logger.warn("nFreqs ("+nFreqs+") was not a power of 2. Using "+nNewBins+" instead");
		    nFreqs = nNewBins;
		}
		nuMax = nuMin + df*nFreqs;
		double[] frequencies = PeriodogramUtils.getFourierFrequencies(nuMin, nuMax, df);
		double powAtNuMin = meanRate*duration;
		Complex[] fourierComponents = TimmerKonig.getFourierComponentsForFrequencies(frequencies, alpha1, alpha2, alpha3, nuBreak1, nuBreak2);
		double[] timmerRates = TimmerKonig.getRatesFromFourierComponents(fourierComponents);

		//  Define the number of events
		MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
		Poisson poisson = new Poisson(0, engine);
		int nevents = (int) Math.round(meanRate*duration);
		logger.info("Nominal number of events = "+nevents+" (duration*meanRate)");
		nevents = poisson.nextInt(nevents);
		logger.info("Random Poisson number of events = "+nevents);

		//  Draw arrival times from the light curve's CDF
		double tzero = 0;
		double nTimeBins =  2*nFreqs;
		double tkBinTime = duration/nTimeBins;
		Histogram1D lcHisto = Converter.array2histo("light curve", tzero, tkBinTime, timmerRates);
		Histogram1D cdfHisto = DistributionFunc.getCDFHisto(lcHisto);
		double[] times = DistributionFunc.getRandom(cdfHisto, nevents);
		Arrays.sort(times);

		//  Adjust actual duration to specified duration
		times[nevents-1] = times[0] + duration;
		double actualMean = times.length/duration;

		logger.info("Arrival times generated");	
		logger.info("  nEvents = "+times.length);
		logger.info("  Mean rate (actual) = "+actualMean);
		return times;
    }

    //// Single-component Red Noise Modulated Arrival Times

    public static double[] generateModulatedArrivalTimes(final double meanRate, final double duration, final double alpha, final double period, final double pulsedFrac) throws BinningException,  TimeSeriesException {
		int nFreqsPerIFS = 1;
		return generateModulatedArrivalTimes(meanRate, duration, alpha, nFreqsPerIFS, period, pulsedFrac);
    }

    public static double[] generateModulatedArrivalTimes(final double meanRate, final double duration, final double alpha, final int nFreqsPerIFS, final double period, final double pulsedFrac) throws BinningException, TimeSeriesException {
		MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
		return generateModulatedArrivalTimes(meanRate, duration, alpha, nFreqsPerIFS, period, pulsedFrac, engine);
    }
	
    public static double[] generateModulatedArrivalTimes(final double meanRate, final double duration, final double alpha, final double period, final double pulsedFrac, final RandomEngine engine) throws BinningException, TimeSeriesException {
		int nFreqsPerIFS = 1;
		return generateModulatedArrivalTimes(meanRate, duration, alpha, nFreqsPerIFS, period, pulsedFrac, engine);
    }
	
    public static double[] generateModulatedArrivalTimes(final double meanRate, final double duration, final double alpha, final int nFreqsPerIFS, final double period, final double pulsedFrac, final RandomEngine engine) throws BinningException, TimeSeriesException {
		if (alpha == 0.0) {
		    return WhiteNoiseGenerator.generateModulatedArrivalTimes(meanRate, duration, period, pulsedFrac, engine);
		}
		logger.info("Generating sinusoidally modulated red noise arrival times");
		logger.info("  Mean rate (specified) = "+meanRate);
		logger.info("  Duration = "+duration);
		logger.info("  Spectral index = "+alpha);
		logger.info("  Period = "+period);
		logger.info("  Pulsed Fraction = "+pulsedFrac);

		// Calculate the mean rates for red and pulsed events
		Poisson poisson = new Poisson(1, engine);
		int nevents = (int) Math.round(meanRate*duration);
		logger.info("Nominal number of events = "+nevents+" (duration*meanRate)");
		nevents = poisson.nextInt(nevents);
		logger.info("Random Poisson number of events = "+nevents);
		double nEvents = (double) nevents;
		double nPulsedEvents = pulsedFrac*nEvents;
		double pulsedMeanRate = nPulsedEvents/duration;
		logger.info("Pulsed events = "+nPulsedEvents);
		logger.info("Pulsed mean rate = "+pulsedMeanRate);
		double nRedNoiseEvents = nEvents - nPulsedEvents;
		double redNoiseMeanRate = nRedNoiseEvents/duration;
		logger.info("Red noise events = "+nRedNoiseEvents);
		logger.info("Red noise mean rate = "+redNoiseMeanRate);

		// Generate
		double[] redNoiseTimes = generateArrivalTimes(redNoiseMeanRate, duration, alpha, engine);
		double[] pulsedTimes = WhiteNoiseGenerator.generateModulatedArrivalTimes(pulsedMeanRate, duration, period, 0.999, engine);

		// Combine
		double[] times = combineArrivalTimes(pulsedTimes, redNoiseTimes, duration);
	    double actualMean = times.length/duration;		
	    logger.info("Arrival times generated");
	    logger.info("  nEvents = "+times.length);
	    logger.info("  Mean rate (actual) = "+actualMean);

	    return times;
    }

    //// Two-component Red Noise Modulated Arrival Times

    public static double[] generateTwoComponentModulatedArrivalTimes(final double meanRate, final double duration, final double alpha1, final double alpha2, final double nuBreak, final double period, final double pulsedFrac) throws BinningException, TimeSeriesException {
		int nFreqsPerIFS = 1;
		return generateTwoComponentModulatedArrivalTimes(meanRate, duration, alpha1, alpha2, nuBreak, nFreqsPerIFS, period, pulsedFrac);
    }

    public static double[] generateTwoComponentModulatedArrivalTimes(final double meanRate, final double duration, final double alpha1, final double alpha2, final double nuBreak, final int nFreqsPerIFS, final double period, final double pulsedFrac) throws BinningException, TimeSeriesException {
		MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
		return generateTwoComponentModulatedArrivalTimes(meanRate, duration, alpha1, alpha2, nuBreak, nFreqsPerIFS, period, pulsedFrac, engine);
    }

    public static double[] generateTwoComponentModulatedArrivalTimes(final double meanRate, final double duration, final double alpha1, final double alpha2, final double nuBreak, final int nFreqsPerIFS, final double period, final double pulsedFrac, final RandomEngine engine) throws BinningException, TimeSeriesException {
		if (alpha1 == 0.0 && alpha2 == 0.0) {
		    return WhiteNoiseGenerator.generateModulatedArrivalTimes(meanRate, duration, period, pulsedFrac, engine);
		}
		logger.info("Generating sinusoidally modulated red noise arrival times");
		logger.info("  Mean rate (specified) = "+meanRate);
		logger.info("  Duration = "+duration);
		logger.info("  Period = "+period);
		logger.info("  Pulsed Fraction = "+pulsedFrac);
		logger.info("  Spectral index1 = "+alpha1);
		logger.info("  Spectral index2 = "+alpha2);
		logger.info("  Break frequency = "+nuBreak+" Hz");


		// Calculate the mean rates for red and pulsed events
		Poisson poisson = new Poisson(1, engine);
		int nevents = (int) Math.round(meanRate*duration);
		logger.info("Nominal number of events = "+nevents+" (duration*meanRate)");
		nevents = poisson.nextInt(nevents);
		logger.info("Random Poisson number of events = "+nevents);
		double nEvents = (double) nevents;
		double nPulsedEvents = pulsedFrac*nEvents;
		double pulsedMeanRate = nPulsedEvents/duration;
		logger.info("Pulsed events = "+nPulsedEvents);
		logger.info("Pulsed mean rate = "+pulsedMeanRate);
		double nRedNoiseEvents = nEvents - nPulsedEvents;
		double redNoiseMeanRate = nRedNoiseEvents/duration;
		logger.info("Red noise events = "+nRedNoiseEvents);
		logger.info("Red noise mean rate = "+redNoiseMeanRate);

		// Generate
		double[] redNoiseTimes = generateTwoComponentArrivalTimes(redNoiseMeanRate, duration, alpha1, alpha2, nuBreak);
		double[] pulsedTimes = WhiteNoiseGenerator.generateModulatedArrivalTimes(pulsedMeanRate, duration, period, 0.999, engine);

		// Combine
		double[] times = combineArrivalTimes(pulsedTimes, redNoiseTimes, duration);
	    double actualMean = times.length/duration;		
	    logger.info("Arrival times generated");
	    logger.info("  nEvents = "+times.length);
	    logger.info("  Mean rate (actual) = "+actualMean);

	    return times;
    }


    public static double[] generateThreeComponentModulatedArrivalTimes(final double meanRate, final double duration, final double alpha1, final double alpha2, final double alpha3, final double nuBreak1, final double nuBreak2, final double period, final double pulsedFrac) throws BinningException, TimeSeriesException {
		int nFreqsPerIFS = 1;
		return generateThreeComponentModulatedArrivalTimes(meanRate, duration, alpha1, alpha2, alpha3, nuBreak1, nuBreak2, nFreqsPerIFS, period, pulsedFrac);
    }

    public static double[] generateThreeComponentModulatedArrivalTimes(final double meanRate, final double duration, final double alpha1, final double alpha2, final double alpha3, final double nuBreak1, final double nuBreak2, final int nFreqsPerIFS, final double period, final double pulsedFrac) throws BinningException, TimeSeriesException {
		MersenneTwister64 engine = new MersenneTwister64(new java.util.Date());
		return generateThreeComponentModulatedArrivalTimes(meanRate, duration, alpha1, alpha2, alpha3, nuBreak1, nuBreak2, nFreqsPerIFS, period, pulsedFrac, engine);
    }

    public static double[] generateThreeComponentModulatedArrivalTimes(final double meanRate, final double duration, final double alpha1, final double alpha2, final double alpha3, final double nuBreak1, final double nuBreak2, final int nFreqsPerIFS, final double period, final double pulsedFrac, final RandomEngine engine) throws BinningException, TimeSeriesException {
		if (alpha1 == 0.0 && alpha2 == 0.0 && alpha3 == 0.0) {
		    return WhiteNoiseGenerator.generateModulatedArrivalTimes(meanRate, duration, period, pulsedFrac, engine);
		}
		logger.info("Generating sinusoidally modulated red noise arrival times");
		logger.info("  Mean rate (specified) = "+meanRate);
		logger.info("  Duration = "+duration);
		logger.info("  Period = "+period);
		logger.info("  Pulsed Fraction = "+pulsedFrac);
		logger.info("  Spectral index1 = "+alpha1);
		logger.info("  Spectral index2 = "+alpha2);
		logger.info("  Spectral index3 = "+alpha3);
		logger.info("  Break frequency1 = "+nuBreak1);
		logger.info("  Break frequency2 = "+nuBreak2);


		// Calculate the mean rates for red and pulsed events
		Poisson poisson = new Poisson(1, engine);
		int n = (int) Math.round(meanRate*duration);
		logger.info("Nominal number of events = "+n+" (duration*meanRate)");
		n = poisson.nextInt(n);
		logger.info("Random Poisson number of events = "+n);
		double nEvents = (double) n;
		double nPulsedEvents = pulsedFrac*nEvents;
		double pulsedMeanRate = nPulsedEvents/duration;
		logger.info("Pulsed events = "+nPulsedEvents);
		logger.info("Pulsed mean rate = "+pulsedMeanRate);
		double nRedNoiseEvents = nEvents - nPulsedEvents;
		double redNoiseMeanRate = nRedNoiseEvents/duration;
		logger.info("Red noise events = "+nRedNoiseEvents);
		logger.info("Red noise mean rate = "+redNoiseMeanRate);

		// Generate
		double[] redNoiseTimes = generateThreeComponentArrivalTimes(redNoiseMeanRate, duration, alpha1, alpha2, alpha3, nuBreak1, nuBreak2);
		double[] pulsedTimes = WhiteNoiseGenerator.generateModulatedArrivalTimes(pulsedMeanRate, duration, period, 0.999, engine);

		// Combine
		double[] times = combineArrivalTimes(pulsedTimes, redNoiseTimes, duration);
	    double actualMean = times.length/duration;		
	    logger.info("Arrival times generated");
	    logger.info("  nEvents = "+times.length);
	    logger.info("  Mean rate (actual) = "+actualMean);

	    return times;
    }


    private static double[] combineArrivalTimes(final double[] pulsedTimes, final double[] redNoiseTimes, final double duration) {
		logger.info("Combining red noise with pulsed arrival times");	
		double[] times = new double[redNoiseTimes.length];
		if ( pulsedTimes.length <= 2 )
		    times = Arrays.copyOf(redNoiseTimes, redNoiseTimes.length);
		else {
		    int nTimes = pulsedTimes.length + redNoiseTimes.length - 1;
		    times = new double[nTimes];
		    int n = 0;
		    for ( int i=0; i < pulsedTimes.length - 1; i++ ) {
				times[i] = pulsedTimes[i];
				n++;
		    }
		    for ( int i=0; i < redNoiseTimes.length; i++ ) {
				times[i+n] = redNoiseTimes[i];
		    }
		    Arrays.sort(times);
		    //  Adjust duration
		    times[times.length-1] = times[0] + duration;
		}
	    return times;		
    }


    //  For testing purposes
    private static void print_tkRates(double[] timmerRates, double duration, double dt) throws BinningException {
		double[] binEdges = BinningUtils.getBinEdges(0, duration, timmerRates.length);
		double[] counts = new double[timmerRates.length];
		for (int i=0; i<counts.length; i++) {
		    counts[i] = timmerRates[i]*dt;
		}
		TimeSeries ts = TimeSeriesMaker.makeTimeSeries(binEdges, counts);
		try {
		    ts.writeCountsAsQDP("tk_rates.qdp");
		    AsciiDataFileWriter hist = new AsciiDataFileWriter("tk_rates_histo.qdp");
		    hist.writeHisto(Binner.makeHisto(timmerRates, timmerRates.length/50), "Rate");
		}
		catch (IOException e) {}
    }

    //  For testing purposes
    private static void print_tkHistos(Histogram1D lcHisto, Histogram1D cdfHisto) {
		try {
		    AsciiDataFileWriter histo = new AsciiDataFileWriter("tk_lcHisto.qdp");
		    histo.writeHisto(lcHisto, "Time (s)", "Rate");
		    histo = new AsciiDataFileWriter("tk_cdfHisto.qdp");
		    histo.writeHisto(cdfHisto, "Time (s)", "cdf");
		}
		catch (IOException e) {}
    }

	
}
