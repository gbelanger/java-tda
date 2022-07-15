package gb.tda.montecarlo;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister64;
import gb.tda.binner.BinningException;
import gb.tda.binner.BinningUtils;
import gb.tda.io.AsciiDataFileWriter;
import gb.tda.periodogram.PeriodogramUtils;
import gb.tda.timeseries.TimeSeries;
import gb.tda.timeseries.TimeSeriesMaker;
import gb.tda.tools.BasicStats;
import gb.tda.tools.Complex;
import gb.tda.tools.FFT;
import java.text.DecimalFormat;
import java.util.Date;
import org.apache.log4j.Logger;


public final class TimmerKonig {

    private static Logger logger  = Logger.getLogger(TimmerKonig.class);

    
    /**
       Method <code>generateComponentsFromSpectrum</code> implements the Timmer-Konig algorithm.
       It generates the randomized Fourier components from the input spectrum.

       IMPORTANT: 
         nTimeBins = 2 nSpecBins
	 - A light curve defined on 64 bins allows for the testing of 32 independent frequencies.
	 Since the FFT yields results for the 32 IFS and their negative, 
	 we must suply 64 complex number to the FFT.
       
       @param spec a <code>double[]</code> array that defines spectrum
       @return a <code>Complex[]</code> array containing (un-scaled) Fourier components
     **/
    private static Complex[] generateComponentsFromSpectrum(final double[] spec) {
	MersenneTwister64 engine = new MersenneTwister64(new Date());
	Normal gaussian = new Normal(0, 1, engine);
	double gauss1 = 0;
	double gauss2 = 0;
	double re, im = 0;
	Complex[] complex = new Complex[2*spec.length];
	for (int i = 0; i < spec.length; i++) {
	    //  Positive frequency
	    gauss1 = gaussian.nextDouble();
	    gauss2 = gaussian.nextDouble();
	    re = gauss1 * Math.sqrt(0.5*spec[i]);
	    im = gauss2 * Math.sqrt(0.5*spec[i]);
	    complex[i] = new Complex(re, im);

	    // Timmer & Konig take the conjugate of the positive freq component
	    //complex[complex.length-1 -i] = complex[i].conjugate();

	    //  Negative frequency
	    gauss1 = gaussian.nextDouble();
	    gauss2 = gaussian.nextDouble();
	    re = gauss1*Math.sqrt(0.5*spec[i]);
	    im = gauss2*Math.sqrt(0.5*spec[i]);
	    complex[complex.length-1 - i] = new Complex(re, im);
	}
	return complex;
    }
    
    private static Complex[] generateComponents(final double[] frequencies, final double alpha) {
	double[] spec = new double[frequencies.length];
	for (int i = 0; i < frequencies.length; i++) {
	    double omega = 2*Math.PI*frequencies[i];
	    spec[i] = Math.pow(omega, -alpha);
	}
	return generateComponentsFromSpectrum(spec);
    }

    private static Complex[] generateComponents(final double[] frequencies, final double alpha1, final double alpha2, final double nuBreak) {
	//  Generate the two components of the spectrum
	double[] spec = new double[frequencies.length];
	int k = 0;
	int l = 0;
	for (int i = 0; i < frequencies.length; i++) {
	    double omega = 2*Math.PI*frequencies[i];
	    if (frequencies[i] < nuBreak) {
		spec[i] = Math.pow(omega, -alpha1);
		k++;
	    }
	    else {
		spec[i] = Math.pow(omega, -alpha2);
		l++;
	    }
	}
	//  Adjust second component to join with first
	double omegaAtBreak = 2*Math.PI*frequencies[k];
 	double normFactor = Math.pow(omegaAtBreak, -alpha1)/Math.pow(omegaAtBreak, -alpha2);
 	for (int i = 0; i < l; i++) {
 	    spec[k+i] *= normFactor;
 	}
	return generateComponentsFromSpectrum(spec);
    }

    
    private static Complex[] generateComponents(final double[] frequencies, final double alpha1, final double alpha2, final double alpha3, final double nuBreak1, final double nuBreak2) {
	//  Generate the three components of the spectrum
	double[] spec = new double[frequencies.length];
	int k = 0;
	int l = 0;
	int m = 0;
	double lastTerm = 0;
	for (int i = 0; i < frequencies.length; i++) {
	    double omega = 2*Math.PI*frequencies[i];
	    if (frequencies[i] < nuBreak1) {
		spec[i] = Math.pow(omega, -alpha1);
		k++;
	    }
	    else if (frequencies[i] < nuBreak2) {
		spec[i] = Math.pow(omega, -alpha2);
		l++;
	    }
	    else {
		spec[i] = Math.pow(omega, -alpha3);
		m++;
	    }
	}

	//  Adjust second to join with first
	double omegaAtBreak1 = 2*Math.PI*frequencies[k];
 	double normFactor1 = Math.pow(omegaAtBreak1, -alpha1) / Math.pow(omegaAtBreak1, -alpha2);
 	for (int i = 0; i < l; i++) {
 	    spec[k + i] *= normFactor1;
 	}

	//  Adjust third to join with second
	double omegaAtBreak2 = 2*Math.PI*frequencies[k+l];
 	double normFactor2 = normFactor1 * Math.pow(omegaAtBreak2, -alpha2) / Math.pow(omegaAtBreak2, -alpha3);
 	for (int i = 0; i < m; i++) {
 	    spec[k + l + i] *= normFactor2;
 	}
	return generateComponentsFromSpectrum(spec);
    }


    /**  getFourierComponents  **/
    public static Complex[] getFourierComponents(final double mean, final double duration, final double alpha) throws BinningException {
	int nFreqsPerIFS = 1;
	return getFourierComponents(mean, duration, alpha, nFreqsPerIFS);
    }

    public static Complex[] getFourierComponents(final double mean, final double duration, final double alpha, final int nFreqsPerIFS) throws BinningException {
	double nuMin = 1d/duration;
	double nuMax = 2d*mean;
	return getFourierComponentsForFrequencies(alpha, nuMin, nuMax, nFreqsPerIFS);
    }


    /**  getFourierComponentsForFrequencies  **/
    public static Complex[] getFourierComponentsForFrequencies(final double alpha, final double nuMin, final double nuMax) throws BinningException {
	int nFreqsPerIFS = 1;
	return getFourierComponentsForFrequencies(alpha, nuMin, nuMax, nFreqsPerIFS);
    }

    public static Complex[] getFourierComponentsForFrequencies(final double alpha, final double nuMin, final double nuMax, int nFreqsPerIFS) throws BinningException {
	double df = nuMin/nFreqsPerIFS;
	return getFourierComponentsForFrequencies(alpha, nuMin, nuMax, df);
    }

    public static Complex[] getFourierComponentsForFrequencies(final double alpha, final double nuMin, final double nuMax, final double df) throws BinningException {
	double[] frequencies = PeriodogramUtils.getFourierFrequencies(nuMin, nuMax, df);
	return generateComponents(frequencies, alpha);
    }

    public static Complex[] getFourierComponentsForFrequencies(final double[] frequencies, final double alpha) throws BinningException {
	return generateComponents(frequencies, alpha);
    }

    public static Complex[] getFourierComponentsForFrequencies(final double[] frequencies, final double alpha1, final double alpha2, final double nuBreak) {
	return generateComponents(frequencies, alpha1, alpha2, nuBreak);
    }

    public static Complex[] getFourierComponentsForFrequencies(final double[] frequencies, final double alpha1, final double alpha2, final double alpha3, final double nuBreak1, final double nuBreak2) {
	return generateComponents(frequencies, alpha1, alpha2, alpha3, nuBreak1, nuBreak2);
    }


    /**
     * The method <code>getRates</code> calls getFourierComponents and 
     * inverse Fourier transforms to get the associated rates that are then 
     * normalized to the value of meanRate. The normalization is done by first 
     * dividing the rates by their actual mean, and then multiplying by the 
     * specified mean. This ensures that the fractional RMS is preserved. 
     * This is important since the variance of the rates is a function of 
     * the spectral index of the red noise. Hence it must be preserved.
     *
     * @param mean a <code>double</code> value     
     * @param duration a <code>double</code> value
     * @param alpha a <code>double</code> value
     * @return a <code>double[]</code> value
     * @exception BinningException if an error occurs
     */
    public static double[] getRates(final double mean, final double duration, final double alpha) throws BinningException {
	int nFreqsPerIFS = 1;
	return getRates(mean, duration, alpha, nFreqsPerIFS);
    }

    public static double[] getRates(final double mean, final double duration, final double alpha, final int nFreqsPerIFS) throws BinningException {
	double nuMin = 1/duration;
	double nuMax = 2*mean;
	double df = nuMin/nFreqsPerIFS;
	double nFreqs = (nuMax - nuMin)/df;

	//  Adjust nuMax (down) to have power of 2 number of frequencies
 	double exponent = Math.floor(Math.log10(nFreqs)/Math.log10(2));
	int nNewBins = (int) Math.pow(2, exponent);
	if (nFreqs != nNewBins) {
	    nFreqs = nNewBins;
	}
	nuMax = nuMin + df*nFreqs;
	
	//  Generate Fourier components, get the rates, and scale them to the specified mean rate
	Complex[] fourierComp = getFourierComponentsForFrequencies(alpha, nuMin, nuMax, df);
	double[] rates = getRatesFromFourierComponents(fourierComp);
	double scalingFactor = mean/BasicStats.getMean(rates);
	for (int i = 0; i < rates.length; i++) {
	    rates[i] *= scalingFactor;
	}
	return rates;
    }

    public static double[] getRatesFromFourierComponents(final Complex[] fourierComp) throws BinningException {
	int n = fourierComp.length;
	if (! isPowerOfTwo(n)) {
	    throw new BinningException("Number of Fourier components ("+n+") is not a power of 2.");
	}

	//  Inverse Fourier transform the components to get the light curve
	Complex[] ifft = FFT.ifft(fourierComp);
	
	//  Taking the norm of each complex number gets 
	//  all the info contained in the result of the inverse FFT
	//  and ensures that there are no negative numbers.
	double[] rates = Complex.norm(ifft);
	return rates;
    }

    private static boolean isPowerOfTwo(final int n) {
	boolean isPowerOfTwo = true;
	double n1 = Math.floor(Math.log10(n) / Math.log10(2));
	double n2 = Math.log10(n) / Math.log10(2);
	if (n1 != n2) {
	    isPowerOfTwo = false;
	}
	return isPowerOfTwo;
	
    }


    public static TimeSeries getTimeSeries(final double mean, final double duration, final double alpha) throws BinningException {
	double[] rates = getRates(mean, duration, alpha, 1);
	return getTimeSeries(rates, duration);
    }

    public static TimeSeries getTimeSeries(final double mean, final double duration, final double alpha, final int nFreqsPerIFS) throws BinningException {
	double[] rates = getRates(mean, duration, alpha, nFreqsPerIFS);
	return getTimeSeries(rates, duration);
    }

    private static TimeSeries getTimeSeries(final double[] rates, final double duration) throws BinningException {
	int nTimeBins = rates.length;
	double[] binEdges  = BinningUtils.getBinEdges(0, duration, nTimeBins);
	double binWidth = duration/nTimeBins;
	double[] counts = new double[rates.length];
	for (int i = 0; i < rates.length; i++) {
	    counts[i] = rates[i]*binWidth;
	}
	return TimeSeriesMaker.makeTimeSeries(binEdges, counts);
    }


    /**
     * The method <code>getPowers</code> calls getFourierComponents and returns the corresponding powers.
     *
     * @param index a <code>double</code> value
     * @param duration a <code>double</code> value
     * @param nTimeBins an <code>int</code> value
     * @return a <code>double[]</code> value
     * @exception BinningException if an error occurs
     */
    public static double[] getPowers(final double mean, final double duration, final double alpha, final int nTimeBins) throws BinningException {
	Complex[] fourierComp = getFourierComponents(mean, duration, alpha, nTimeBins);
	return getPowers(fourierComp);
    }
	
    public static double[] getPowers(final Complex[] fourierComp) {
	return Complex.normSquared(fourierComp);
    }

    
    private static Complex[] scale(final Complex[] fourierComponents, final double scalingFactor) {
	Complex[] result = new Complex[fourierComponents.length];
	for (int i = 0; i < result.length; i++) {
	    result[i] = fourierComponents[i].times(scalingFactor);
	}
	return result;
    }
}
