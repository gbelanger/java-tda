package gb.tda.periodogram;

import cern.colt.list.DoubleArrayList;
import org.apache.log4j.Logger;
import gb.tda.binner.BinningException;
import gb.tda.eventlist.IEventList;
import gb.tda.timeseries.BinnedTimeSeries;
import gb.tda.timeseries.IBinnedTimeSeries;
import gb.tda.timeseries.BinnedTimeSeriesFactory;
import gb.tda.timeseries.TimeSeriesResampler;
import gb.tda.timeseries.TimeSeriesUtils;
import gb.tda.timeseries.TimeSeriesException;
import gb.tda.utils.BasicStats;
import gb.tda.utils.Complex;
import gb.tda.utils.ComplexNumbers;
import gb.tda.utils.DataUtils;
import gb.tda.utils.MinMax;
import gb.tda.utils.FFT;
//import gb.tda.tools.MyFFT;

public class FFTPeriodogramFactory {

    private static Logger logger  = Logger.getLogger(FFTPeriodogramFactory.class);

    //  Core factory method for FFTPeriodogram
    public static FFTPeriodogram makeUnnormalizedWindowedFFTPeriodogram(double[] intensities, double duration, String windowName, int samplingFactor) throws PeriodogramException {
        logger.info("Making UnnormalizedWindowedFFTPeriodogram");
        logger.info("  Window function: "+windowName);
        logger.info("  Sampling factor: "+samplingFactor);
        logger.warn("  Treatment assumes uniform sampling with equal bins");
        if (samplingFactor > 1) {
            double powerOfTwo = Math.log(samplingFactor)/Math.log(2);
            double integerPart = Math.floor(powerOfTwo);
            double diff = powerOfTwo - integerPart;
            if (diff != 0) {
                throw new PeriodogramException("Sampling factor for FFTPeriodogram must be a power of 2: "+samplingFactor+" is not.");
            }
        }
        //  Subtract mean
        int nDataBins = intensities.length;
        double[] binHeights = new double[nDataBins];
        double avgOfInputData = BasicStats.getMean(intensities);
        for (int i=0; i < nDataBins; i++) {
            binHeights[i] = intensities[i] - avgOfInputData;
            if (Double.isNaN(binHeights[i])) binHeights[i] = 0.0;
        }
        logger.info("  Sum of intensities BEFORE mean-subtraction = "+BasicStats.getSum(intensities)+" (after = "+BasicStats.getSum(binHeights)+")");
        logger.info("  Sum of squared intensities AFTER mean-subtraction = "+BasicStats.getSumOfSquares(binHeights));
        //  Apply the smoothing window
        WindowFunction windowFunction = null;
        try {
            windowFunction = new WindowFunction(windowName);
        }
        catch (WindowFunctionException e) {
            throw new PeriodogramException("Cannot construct window function ("+windowName+")", e);
        }
        double[] windowedData = windowFunction.apply(binHeights);
        // NO WINDOW
        windowedData = binHeights;

        //  Define number of bins as a power-of-two
        double n = Math.log(nDataBins)/Math.log(2);
        double exp = Math.ceil(n);
        int nPowerOfTwoBins = (int) Math.pow(2, exp);
        //  Keep the original number of bins
        //int nPowerOfTwoBins = nDataBins;

        //  Pad with zeros from the original number of bins up to the closest power of 2
        int nBins = samplingFactor*nPowerOfTwoBins;
        double[] paddedData = padWithZeros(windowedData, nBins);
        // NO PADDING
        //paddedData = windowedData;

        //  Define test frequencies
        double timeBinWidth = duration/nPowerOfTwoBins;
        double nuMin = 1d/duration;
        double nuMax = 1d/(2*timeBinWidth);
        double[] testFreqs = PeriodogramUtils.getFourierFrequencies(nuMin, nuMax, duration, samplingFactor);
        //  Do the FFT

        ////  Using FFT.java
        logger.info("Calculating the FFT of input data");
        Complex[] binHeightsForFFT = Complex.realDoubleArrayToComplexArray(paddedData);
        Complex[] resultOfFFT = FFT.fft(binHeightsForFFT);
        double[] power = Complex.normSquared(resultOfFFT);

        ////  Using  MyFFT.java
        // int nn = paddedData.length;
        // double[] resultOfFFT = MyFFT.fft(ComplexNumbers.myComplex(paddedData), nn, +1);
        // //double[] resultOfFFT = MyFFT.fft(ComplexNumbers.myComplex(smoothedData), nn, +1);
        // double[] power = ComplexNumbers.getPower(resultOfFFT);
        // //  Correct for different normalization compared to FFT.java
        // for (int i=0; i < power.length; i++) {
        //     power[i] *= nn*nn;
        // }

        ////  Using  jTransform
        // DoubleFFT_1D jtransformFFT = new DoubleFFT_1D(paddedData.length);
        // jtransformFFT.realForward(paddedData);
        // double[] power = new double[paddedData.length];
        // for (int i=0; i < paddedData.length/2; i++) {
        //     power[i] = paddedData[2*i]*paddedData[2*i] + paddedData[2*i+1]*paddedData[2*i+1];
        // }

        //  Drop first terms and second half of power spectrum corresponding to negative frequencies
        int size = power.length/2;
        double[] pow = new double[size];
        for (int i=0; i < size; i++) {
            pow[i] = power[i+samplingFactor];
        }
        // int i=0;
        // int j=0;
        // while (i < size) {
        //     while (i <= samplingFactor) {
        // 	pow[j] = power[i+samplingFactor+1];
        // 	i++;
        // 	j++;
        //     }
        //     j++;
        // }

        // Keep only the physically meaningful frequencies
        DoubleArrayList goodFreqs = new DoubleArrayList();
        DoubleArrayList goodPowers = new DoubleArrayList();
        int i=0;
        double min = nuMin;
        double max = nuMax + testFreqs.length*Math.ulp(nuMax);
        while (testFreqs[i] < min) i++;
        while (i < testFreqs.length && testFreqs[i] <= max) {
            if (!Double.isNaN(pow[i])) {
                goodPowers.add(pow[i]);
                goodFreqs.add(testFreqs[i]);
            }
            i++;
        }
        goodPowers.trimToSize();
        goodFreqs.trimToSize();
        if (goodPowers.size() == 0) {
            throw new PeriodogramException("All power values are NaN: Cannot construct Periodogram");
        }
        return new FFTPeriodogram(goodFreqs.elements(), goodPowers.elements(), samplingFactor);
    }


    /**
     * <code>makeUnnormalizedWindowedRateFFTPeriodogram</code>
     *
     * @param timeSeries a <code>IBinnedTimeSeries</code> value
     * @param windowName a <code>String</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static FFTPeriodogram makeUnnormalizedWindowedRateFFTPeriodogram(IBinnedTimeSeries timeSeries, String windowName, int samplingFactor) throws PeriodogramException, BinningException {
        //  We must ensure uniform sampling and equal sized bins
        BinnedTimeSeries ts = (BinnedTimeSeries) BinnedTimeSeriesFactory.create(timeSeries);
        BinnedTimeSeries ts_resamp = (BinnedTimeSeries) BinnedTimeSeriesFactory.create(ts);
        if (! ts.binWidthIsConstant()) {
            double minBinWidth = timeSeries.minBinWidth();
            ts_resamp = TimeSeriesResampler.resample(ts, minBinWidth);
            //  This is not a good strategy because it degrades frequency resolution
            // int k=0;
            // while (ts_resamp.thereAreGaps()) {
            // 	k++;
            // 	logger.warn("There are still gaps: resampling with larger bin width");
            // 	double binWidth = minBinWidth + (k*0.1)*minBinWidth;
            // 	ts_resamp = TimeSeriesResampler.resample(ts, binWidth);
            // }
        }
        if (ts_resamp.thereAreGaps()) {
            ts_resamp = TimeSeriesUtils.fillGaps(ts_resamp);
        }
        return makeUnnormalizedWindowedFFTPeriodogram(ts_resamp.getIntensities(), ts_resamp.duration(), windowName, samplingFactor);
    }

    /**
     * <code>makeUnnormalizedWindowedFFTPeriodogram</code>
     *
     * @param timeSeries a <code>IBinnedTimeSeries</code> value
     * @param windowName a <code>String</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static FFTPeriodogram makeUnnormalizedWindowedFFTPeriodogram(IBinnedTimeSeries timeSeries, String windowName, int samplingFactor) throws PeriodogramException, BinningException {
        //  We must ensure uniform sampling and equal sized bins
        BinnedTimeSeries ts = (BinnedTimeSeries) BinnedTimeSeriesFactory.create(timeSeries);
        if (! ts.binWidthIsConstant()) {
            double minBinWidth = timeSeries.minBinWidth();
            ts = TimeSeriesResampler.resample(ts, minBinWidth);
        }
        return makeUnnormalizedWindowedFFTPeriodogram(ts.getIntensities(), ts.duration(), windowName, samplingFactor);
    }

    //  Oversampled Windowed FFTPeriodogram

    /**
     * <code>makeOversampledWindowedFFTPeriodogram</code>
     *
     * @param timeSeries a <code>IBinnedTimeSeries</code> value
     * @param windowName a <code>String</code> value
     * @param normName a <code>String</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makeOversampledWindowedFFTPeriodogram(IBinnedTimeSeries timeSeries, String windowName, String normName, int samplingFactor) throws PeriodogramException, BinningException  {
        FFTPeriodogram basicPeriodogram = makeUnnormalizedWindowedFFTPeriodogram(timeSeries, windowName, samplingFactor);
        return applyNormalization(basicPeriodogram, timeSeries, windowName, normName, "counts");
    }

    /**
     * <code>makeOversampledWindowedFFTPeriodogram</code>
     *
     * @param evlist an <code>IEventList</code> value
     * @param windowName a <code>String</code> value
     * @param normName a <code>String</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception TimeSeriesFileException if an error occurs
     * @exception PeriodogramException if an error occurs
     */
    public static FFTPeriodogram makeOversampledWindowedFFTPeriodogram(IEventList evlist, String windowName, String normName, int samplingFactor) throws TimeSeriesException, PeriodogramException, BinningException {
        return makeOversampledWindowedFFTPeriodogram(BinnedTimeSeriesFactory.create(evlist), windowName, normName, samplingFactor);
    }

    //  Windowed FFTPeriodogram

    /**
     * <code>makeWindowedFFTPeriodogram</code>
     *
     * @param timeSeries a <code>TimeSeries</code> value
     * @param windowName a <code>String</code> value
     * @param normName a <code>String</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makeWindowedFFTPeriodogram(IBinnedTimeSeries timeSeries, String windowName, String normName) throws PeriodogramException, BinningException {
        int sampling = 1;
        return makeOversampledWindowedFFTPeriodogram(timeSeries, windowName, normName, sampling);
    }

    /**
     * <code>makeWindowedFFTPeriodogram</code>
     *
     * @param evlist an <code>IEventList</code> value
     * @param windowName a <code>String</code> value
     * @param normName a <code>String</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception TimeSeriesFileException if an error occurs
     * @exception PeriodogramException if an error occurs
     */
    public static FFTPeriodogram makeWindowedFFTPeriodogram(IEventList evlist, String windowName, String normName) throws TimeSeriesException, PeriodogramException, BinningException {
        return makeWindowedFFTPeriodogram(BinnedTimeSeriesFactory.create(evlist), windowName, normName);
    }

    /**
     * <code>makeWindowedFFTPeriodogram</code>
     *
     * @param timeSeries a <code>TimeSeries</code> value
     * @param windowName a <code>String</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makeWindowedFFTPeriodogram(IBinnedTimeSeries timeSeries, String windowName) throws PeriodogramException, BinningException {
        String normName = "leahy";
        return makeWindowedFFTPeriodogram(timeSeries, windowName, normName);
    }

    /**
     * <code>makeWindowedFFTPeriodogram</code>
     *
     * @param evlist an <code>IEventList</code> value
     * @param windowName a <code>String</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception TimeSeriesFileException if an error occurs
     * @exception PeriodogramException if an error occurs
     */
    public static FFTPeriodogram makeWindowedFFTPeriodogram(IEventList evlist, String windowName) throws TimeSeriesException, PeriodogramException {
        String normName = "leahy";
        return makeWindowedFFTPeriodogram(evlist, windowName);
    }

    //   Oversampled Plain FFTPeriodogram (using a Rectangular window)

    /**
     * <code>makeOversampledPlainFFTPeriodogram</code>
     *
     * @param timeSeries a <code>TimeSeries</code> value
     * @param normName a <code>String</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makeOversampledPlainFFTPeriodogram(IBinnedTimeSeries timeSeries, String normName, int samplingFactor) throws PeriodogramException, BinningException {
        String windowName = "rectangular";
        return makeOversampledWindowedFFTPeriodogram(timeSeries, windowName, normName, samplingFactor);
    }

    /**
     * <code>makeOversampledPlainFFTPeriodogram</code>
     *
     * @param evlist an <code>IEventList</code> value
     * @param normName a <code>String</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception TimeSeriesFileException if an error occurs
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makeOversampledPlainFFTPeriodogram(IEventList evlist, String normName, int samplingFactor) throws TimeSeriesException, PeriodogramException, BinningException {
        String windowName = "rectangular";
        return makeOversampledPlainFFTPeriodogram(BinnedTimeSeriesFactory.create(evlist), normName, samplingFactor);
    }

    /**
     * <code>makeOversampledPlainFFTPeriodogram</code>
     *
     * @param timeSeries a <code>TimeSeries</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makeOversampledPlainFFTPeriodogram(IBinnedTimeSeries timeSeries, int samplingFactor) throws PeriodogramException, BinningException {
        String normName = "leahy";
        return makeOversampledPlainFFTPeriodogram(timeSeries, normName, samplingFactor);
    }

    /**
     * <code>makeOversampledPlainFFTPeriodogram</code>
     *
     * @param evlist an <code>IEventList</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception TimeSeriesFileException if an error occurs
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makeOversampledPlainFFTPeriodogram(IEventList evlist, int samplingFactor) throws TimeSeriesException, PeriodogramException, BinningException {
        return makeOversampledPlainFFTPeriodogram(BinnedTimeSeriesFactory.create(evlist), samplingFactor);
    }

    //  Plain FFT Periodogram

    /**
     * <code>makePlainFFTPeriodogram</code>
     *
     * @param timeSeries a <code>TimeSeries</code> value
     * @param normName a <code>String</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makePlainFFTPeriodogram(IBinnedTimeSeries timeSeries, String normName) throws PeriodogramException, BinningException {
        int sampling = 1;
        return makeOversampledPlainFFTPeriodogram(timeSeries, normName, sampling);
    }

    /**
     * <code>makePlainFFTPeriodogram</code>
     *
     * @param evlist an <code>IEventList</code> value
     * @param normName a <code>String</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception TimeSeriesFileException if an error occurs
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makePlainFFTPeriodogram(IEventList evlist, String normName) throws TimeSeriesException, PeriodogramException, BinningException {
        return makePlainFFTPeriodogram(BinnedTimeSeriesFactory.create(evlist), normName);
    }

    /**
     * <code>makePlainFFTPeriodogram</code>
     *
     * @param timeSeries a <code>TimeSeries</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makePlainFFTPeriodogram(IBinnedTimeSeries timeSeries) throws PeriodogramException, BinningException {
        String normName = "leahy";
        return makePlainFFTPeriodogram(timeSeries, normName);
    }

    /**
     * <code>makePlainFFTPeriodogram</code>
     *
     * @param evlist an <code>IEventList</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception TimeSeriesException if an error occurs
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makePlainFFTPeriodogram(IEventList evlist) throws TimeSeriesException, PeriodogramException, BinningException {
        return makePlainFFTPeriodogram(BinnedTimeSeriesFactory.create(evlist));
    }

    //  Make windowed Rate FFTPeriodogram

    /**
     * <code>makeOversampledWindowedRateFFTPeriodogram</code>
     *
     * @param timeSeries a <code>TimeSeries</code> value
     * @param windowName a <code>String</code> value
     * @param normName a <code>String</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makeOversampledWindowedRateFFTPeriodogram(IBinnedTimeSeries timeSeries, String windowName, String normName, int samplingFactor) throws PeriodogramException, BinningException {
        FFTPeriodogram basic = makeUnnormalizedWindowedRateFFTPeriodogram(timeSeries, windowName, samplingFactor);
        return applyNormalization(basic, timeSeries, windowName, normName, "rates");
    }

    /**
     * <code>makeOversampledWindowedRateFFTPeriodogram</code>
     *
     * @param evlist an <code>IEventList</code> value
     * @param windowName a <code>String</code> value
     * @param normName a <code>String</code> value
     * @param samplingFactor an <code>int</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception TimeSeriesException if an error occurs
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makeOversampledWindowedRateFFTPeriodogram(IEventList evlist, String windowName, String normName, int samplingFactor) throws TimeSeriesException, PeriodogramException, BinningException {
        return makeOversampledWindowedRateFFTPeriodogram(BinnedTimeSeriesFactory.create(evlist), windowName, normName, samplingFactor);
    }

    /**
     * <code>makeWindowedRateFFTPeriodogram</code>
     *
     * @param timeSeries a <code>TimeSeries</code> value
     * @param windowName a <code>String</code> value
     * @param normName a <code>String</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makeWindowedRateFFTPeriodogram(IBinnedTimeSeries timeSeries, String windowName, String normName) throws PeriodogramException, BinningException {
        int sampling = 1;
        return makeOversampledWindowedRateFFTPeriodogram(timeSeries, windowName, normName, sampling);
    }

    /**
     * <code>makeWindowedRateFFTPeriodogram</code>
     *
     * @param evlist an <code>IEventList</code> value
     * @param windowName a <code>String</code> value
     * @param normName a <code>String</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception TimeSeriesException if an error occurs
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makeWindowedRateFFTPeriodogram(IEventList evlist, String windowName, String normName) throws TimeSeriesException, PeriodogramException, BinningException {
        int sampling = 1;
        return makeWindowedRateFFTPeriodogram(BinnedTimeSeriesFactory.create(evlist), windowName, normName);
    }

    /**
     * <code>makePlainRateFFTPeriodogram</code>
     *
     * @param timeSeries a <code>TimeSeries</code> value
     * @param normName a <code>String</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makePlainRateFFTPeriodogram(IBinnedTimeSeries timeSeries, String normName) throws PeriodogramException, BinningException {
        String windowName = "rectangular";
        return makeWindowedRateFFTPeriodogram(timeSeries, windowName, normName);
    }

    /**
     * <code>makePlainRateFFTPeriodogram</code>
     *
     * @param evlist an <code>IEventList</code> value
     * @param normName a <code>String</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception TimeSeriesException if an error occurs
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makePlainRateFFTPeriodogram(IEventList evlist, String normName) throws TimeSeriesException, PeriodogramException, BinningException {
        String windowName = "rectangular";
        return makeWindowedRateFFTPeriodogram(BinnedTimeSeriesFactory.create(evlist), windowName, normName);
    }

    /**
     * <code>makePlainRateFFTPeriodogram</code>
     *
     * @param timeSeries a <code>TimeSeries</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makePlainRateFFTPeriodogram(IBinnedTimeSeries timeSeries) throws PeriodogramException, BinningException {
        String normName = "leahy";
        return makePlainRateFFTPeriodogram(timeSeries, normName);
    }

    /**
     * <code>makePlainRateFFTPeriodogram</code>
     *
     * @param evlist an <code>IEventList</code> value
     * @return a <code>FFTPeriodogram</code> value
     * @exception TimeSeriesException if an error occurs
     * @exception PeriodogramException if an error occurs
     */
    public static  FFTPeriodogram makePlainRateFFTPeriodogram(IEventList evlist) throws TimeSeriesException, PeriodogramException, BinningException {
        String normName = "leahy";
        return makePlainRateFFTPeriodogram(BinnedTimeSeriesFactory.create(evlist), normName);
    }


    private static FFTPeriodogram applyNormalization(FFTPeriodogram basicPer, IBinnedTimeSeries ts, String windowName, String normName, String inputDataType) throws PeriodogramException {
        if (!inputDataType.equals("counts") && !inputDataType.equals("rates")) {
            throw new PeriodogramException("Input data types can only be 'counts' or 'rates'");
        }
        logger.info("Applying '"+normName+"' normalization (for '"+inputDataType+"' data type)");
        //  Calculate normalization factors
        double duration = ts.duration();
        double freqBinWidth = basicPer.binWidth();
        double[] rawPowers = basicPer.getPowers();
        double sumOfRawPowers = BasicStats.getSum(rawPowers);
        double avgRawPower = sumOfRawPowers/rawPowers.length;
        int n = ts.nBins();
        double varNorm = 2d/(n*n*freqBinWidth);
        double leahyLikeNorm = 2d/avgRawPower;
        double sumOfSquaredIntensities = 0;
        sumOfSquaredIntensities = BasicStats.getSumOfSquares(ts.getMeanSubtractedIntensities());
        if (inputDataType.equals("rates")) {
            sumOfSquaredIntensities = BasicStats.getSumOfSquares(ts.getMeanSubtractedIntensities());
        }
        //double leahyNorm = 2d/sumOfSquaredIntensities;
        double leahyNorm = 2d/ts.sumOfIntensities();
        double rmsNorm = 2d/sumOfSquaredIntensities/ts.meanIntensity();

        //  Define the normalization
        double norm = 0;
        if (normName.equalsIgnoreCase("leahy")) {
            norm = leahyNorm;
        }
        else if (normName.equalsIgnoreCase("miyamoto")) {
            norm = rmsNorm;
        }
        else if (normName.equalsIgnoreCase("variance")) {
            norm = varNorm;
        }
        else if (normName.equalsIgnoreCase("leahy-like")) {
            norm = leahyLikeNorm;
        }
        else {
            printAvailableNormalizations();
            throw new PeriodogramException("Unknown normalization ("+normName+")");
        }

        //  Apply the normalization correction for the window function
        WindowFunction windowFunction = null;
        try {
            windowFunction = new WindowFunction(windowName);
        }
        catch (WindowFunctionException e) {
            throw new PeriodogramException("Cannot construct window function", e);
        }
        double[] function = windowFunction.getFunction(ts.nBins());
        double sumOfSquaredWeights = BasicStats.getSumOfSquares(function);
        double windowNorm = function.length/sumOfSquaredWeights;
        norm *= windowNorm;
        //  Return the normalized FFTPeriodogram
        return (FFTPeriodogram) basicPer.scale(norm);
    }

    private static String[] normNames = new String[] {"Leahy", "Miyamoto (rms^2)", "Variance", "Leahy-like"};
    public static void printAvailableNormalizations() {
        logger.info("Available normalizations are:");
        for (int i=0; i < normNames.length; i++) {
            logger.info("  "+normNames[i]);
        }
    }

    private static double[] padWithZeros(double[] data, int nBins) {
        double[] paddedData = new double[nBins];
        int nZeros = nBins - data.length;
        int nZerosBefore = (int) Math.floor(nZeros/2d);
        int nZerosAfter = nZeros - nZerosBefore;
        for (int i=0; i < nZerosBefore; i++) {
            paddedData[i] = 0;
        }
        for (int i=0; i < data.length; i++) {
            paddedData[i+nZerosBefore] = data[i];
        }
        for (int i=0; i < nZerosAfter; i++) {
            paddedData[i+nZerosBefore+data.length] = 0;
        }
        return paddedData;
    }

}