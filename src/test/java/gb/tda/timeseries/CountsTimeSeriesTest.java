import org.apache.log4j.Logger;

public class CountsTimeSeriesTest {

    private static org.apache.log4j.Logger logger = Logger.getLogger(CountsTimeSeriesTest.class);

    public static void main(String[] args) throws BinningException {
        // Define dummy data
        double tstart = 5;
        double[] binCentres = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] binEdges = BinningUtils.getBinEdgesFromBinCentres(binCentres);
        double[] intensities = new double[] {5, 3, 4, 6, 7, 4, 6, 4, 4, 5};
        double[] uncertainties = new double[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        // Test constructors
        CountsTimeSeries[] tSeries = testConstructors(tstart, binEdges, intensities, uncertainties);

        // Test methods
        testMethods(tSeries);
    }

    private static CountsTimeSeries[] testConstructors(double tstart, double[] binEdges, double[] intensities, double[] uncertainties) {
        logger.info("TESTING -- CONSTRUCTORS --");
        logger.info("  Constructor CountsTimeSeries(binEdges, intensities)");
        CountsTimeSeries ts1 = new CountsTimeSeries(binEdges, intensities);
        CountsTimeSeries ts11 = new CountsTimeSeries(ts1);

        logger.info("  Constructor CountsTimeSeries(tstart, binEdges, intensities)");
        CountsTimeSeries ts2 = new CountsTimeSeries(tstart, binEdges, intensities);
        CountsTimeSeries ts22 = new CountsTimeSeries(ts2);

        logger.info("  Constructor CountsTimeSeries(binEdges, intensities, uncertainties)");
        CountsTimeSeries ts3 = new CountsTimeSeries(binEdges, intensities, uncertainties);
        CountsTimeSeries ts33 = new CountsTimeSeries(ts3);

        logger.info("  Constructor CountsTimeSeries(tstart, binEdges, intensities, uncertainties)");
        CountsTimeSeries ts4 = new CountsTimeSeries(tstart, binEdges, intensities, uncertainties);
        CountsTimeSeries ts44 = new CountsTimeSeries(ts4);

        return new CountsTimeSeries[] {ts1, ts2, ts3, ts4};
    }

    private static void testMethods(CountsTimeSeries[] tSeries) {
        int k = 0;
        for (CountsTimeSeries ts : tSeries) {
            logger.info("TESTING -- METHODS -- for CountsTimeSeries "+(k));

            // Basics
            logger.info("  --BASIC TIME-- ");
            int n = ts.nElements();
            logger.info("ts["+k+"] ts.nElements() = "+n);
            String string = ts.timeUnit();
            logger.info("(ts["+k+"]) ts.timeUnit() = "+string);
            double number = ts.tStart();
            logger.info("(ts["+k+"]) ts.tStart() = "+number);
            number = ts.tStop();
            logger.info("(ts["+k+"]) ts.tStop() = "+number);
            number = ts.tMid();
            logger.info("(ts["+k+"]) ts.tMid() = "+number);
            number = ts.duration();
            logger.info("(ts["+k+"]) ts.duration() = "+number);
            double[] array = ts.getTimes();
            logger.info("(ts["+k+"]) ts.getTimes()");
            for (int i = 0; i < array.length; i++) {
                logger.info("(ts["+k+"])   "+array[i]);
            }

            // Bins
            logger.info("  --BINS-- ");
            n = ts.nBins(); 
            logger.info("(ts["+k+"]) ts.nBins() = "+n);
            array = ts.getBinCentres();
            logger.info("(ts["+k+"]) ts.getBinCentres()");
            for (int i = 0; i < array.length; i++) {
                logger.info("(ts["+k+"])   "+array[i]);
            }
            array = ts.getBinWidths();
            logger.info("(ts["+k+"]) ts.getBinWidths()");
            for (int i = 0; i < array.length; i++) {
                logger.info("(ts["+k+"])   "+array[i]);
            }
            array = ts.getHalfBinWidths();
            logger.info("(ts["+k+"]) ts.getHalfBinWidths()");
            for (int i = 0; i < array.length; i++) {
                logger.info("(ts["+k+"])   "+array[i]);
            }
            array = ts.getBinEdges();
            logger.info("(ts["+k+"]) ts.getBinEdges()");
            for (int i = 0; i < array.length; i++) {
                logger.info("(ts["+k+"])   "+array[i]);
            }
            array = ts.getLeftBinEdges();
            logger.info("(ts["+k+"]) ts.getLeftBinEdges()");
            for (int i = 0; i < array.length; i++) {
                logger.info("(ts["+k+"])   "+array[i]);
            }
            array = ts.getRightBinEdges();
            logger.info("(ts["+k+"]) ts.getRightBinEdges()");
            for (int i = 0; i < array.length; i++) {
                logger.info("(ts["+k+"])   "+array[i]);
            }
            number = ts.binCentreAtMinIntensity();
            logger.info("(ts["+k+"]) ts.binCentreAtMinIntensity() = "+number);
            number = ts.binCentreAtMaxIntensity();
            logger.info("(ts["+k+"]) ts.binCentreAtMaxIntensity() = "+number);
            number = ts.minBinWidth();
            logger.info("(ts["+k+"]) ts.minBinWidth() = "+number);
            number = ts.maxBinWidth();
            logger.info("(ts["+k+"]) ts.maxBinWidth() = "+number);
            number = ts.avgBinWidth();
            logger.info("(ts["+k+"]) ts.avgBinWidth() = "+number);
            try {
                number = ts.binWidth();
            }
            catch (TimeSeriesException e) {
                logger.info("WARN: TimeSeriesException was thrown because the binWidth is not constant.");
                logger.info("WARN: the error is: "+e);
            }
            logger.info("(ts["+k+"]) ts.binWidth() = "+number);
            number = ts.sumOfBinWidths();
            logger.info("(ts["+k+"]) ts.sumOfBinWidths() = "+number);
            number = ts.ontime();
            logger.info("(ts["+k+"]) ts.ontime() = "+number);
        
            // Intensities
            logger.info("  --INTENSITIES-- ");
            array = ts.getIntensities();
            logger.info("(ts["+k+"]) ts.getIntensities()");
            for (int i = 0; i < array.length; i++) {
                logger.info("(ts["+k+"])   "+array[i]);
            }
            number = ts.sumOfIntensities();
            logger.info("(ts["+k+"]) ts.sumOfIntensities() = "+number);
            number = ts.meanIntensity();
            logger.info("(ts["+k+"]) ts.meanIntensity() = "+number);
            number = ts.minIntensity();
            logger.info("(ts["+k+"]) ts.minIntensity() = "+number);
            number = ts.maxIntensity();
            logger.info("(ts["+k+"]) ts.maxIntensity() = "+number);
            number = ts.varianceInIntensities();
            logger.info("(ts["+k+"]) ts.varianceInIntensities() = "+number);
            number = ts.meanDeviationInIntensities();
            logger.info("(ts["+k+"]) ts.meanDeviationInIntensities() = "+number);
            number = ts.kurtosisInIntensities();
            logger.info("(ts["+k+"]) ts.kurtosisInIntensities() = "+number);
            number = ts.kurtosisStandardError();
            logger.info("(ts["+k+"]) ts.kurtosisStandardError() = "+number);
            number = ts.skewnessInIntensities();
            logger.info("(ts["+k+"]) ts.skewnessInIntensities() = "+number);
            number = ts.skewnessStandardError();
            logger.info("(ts["+k+"]) ts.skewnessStandardError() = "+number);

            // Uncertainties
            logger.info("  --UNCERTAINTIES-- ");
            array = ts.getUncertainties();
            if (array != null) {
                logger.info("(ts["+k+"]) ts.getUncertainties()");
                for (int i = 0; i < array.length; i++) {
                    logger.info("(ts["+k+"])   "+array[i]);
                }
            }
            number = ts.sumOfUncertainties();
            logger.info("(ts["+k+"]) ts.sumOfUncertainties() = "+number);
            number = ts.meanUncertainty();
            logger.info("(ts["+k+"]) ts.meanUncertainty() = "+number);
            number = ts.minUncertainty();
            logger.info("(ts["+k+"]) ts.minUncertainty() = "+number);
            number = ts.maxUncertainty();
            logger.info("(ts["+k+"]) ts.maxUncertainty() = "+number);
            number = ts.varianceInUncertainties();
            logger.info("(ts["+k+"]) ts.varianceInUncertainties() = "+number);
            number = ts.errorOnMeanUncertainty();
            logger.info("(ts["+k+"]) ts.errorOnMeanUncertainty() = "+number);
            number = ts.meanDeviationInUncertainties();
            logger.info("(ts["+k+"]) ts.meanDeviationInUncertainties() = "+number);
            boolean bool = ts.uncertaintiesAreSet();
            logger.info("(ts["+k+"]) ts.uncertaintiesAreSet() = "+bool);

            //  Counts 
            logger.info("  --COUNTS-- ");
            //// Convenience methods that map counts to intensities defined in AbstractTimeSeries
            array = ts.getCounts();
            if (array != null) {
                logger.info("(ts["+k+"]) ts.counts()");
                for (int i = 0; i < array.length; i++) {
                    logger.info("(ts["+k+"])   "+array[i]);
                }
            }
            array = ts.getMeanSubtractedCounts();
            if (array != null) {
                logger.info("(ts["+k+"]) ts.getMeanSubtractedCounts()");
                for (int i = 0; i < array.length; i++) {
                    logger.info("(ts["+k+"])   "+array[i]);
                }
            }
            number = ts.meanCount();
            logger.info("(ts["+k+"]) ts.meanCount() = "+number);
            number = ts.minCount();
            logger.info("(ts["+k+"]) ts.minCount() = "+number);
            number = ts.maxCount();
            logger.info("(ts["+k+"]) ts.maxCount() = "+number);
            number = ts.errorOnMeanCount();
            logger.info("(ts["+k+"]) ts.errorOnMeanCount() = "+number);
            number = ts.varianceInCounts();
            logger.info("(ts["+k+"]) ts.varianceInCounts() = "+number);
            number = ts.meanDeviationInCounts();
            logger.info("(ts["+k+"]) ts.meanDeviationInCounts() = "+number);

            // Equivalent Rates
            logger.info("  --EQUIVALENT RATES-- ");
            array = ts.getEquivalentRates();
            if (array != null) {
                logger.info("(ts["+k+"]) ts.getEquivalentRates()");
                for (int i = 0; i < array.length; i++) {
                    logger.info("(ts["+k+"])   "+array[i]);
                }
            }
            array = ts.getUncertaintiesOnEquivalentRates();
            if (array != null) {
                logger.info("(ts["+k+"]) ts.getUncertaintiesOnEquivalentRates()");
                for (int i = 0; i < array.length; i++) {
                    logger.info("(ts["+k+"])   "+array[i]);
                }
            }
            array = ts.getMeanSubtractedEquivalentRates();
            if (array != null) {
                logger.info("(ts["+k+"]) ts.getMeanSubtractedEquivalentRates()");
                for (int i = 0; i < array.length; i++) {
                    logger.info("(ts["+k+"])   "+array[i]);
                }
            }
            number = ts.meanEquivalentRate();
            logger.info("(ts["+k+"]) ts.meanEquivalentRate() = "+number);
            number = ts.minEquivalentRate();
            logger.info("(ts["+k+"]) ts.minEquivalentRate() = "+number);
            number = ts.maxEquivalentRate();
            logger.info("(ts["+k+"]) ts.maxEquivalentRate() = "+number);
            number = ts.errorOnMeanEquivalentRate();
            logger.info("(ts["+k+"]) ts.errorOnMeanEquivalentRate() = "+number);
            number = ts.weightedMeanEquivalentRate();
            logger.info("(ts["+k+"]) ts.weightedMeanEquivalentRate() = "+number);
            number = ts.errorOnWeightedMeanEquivalentRate();
            logger.info("(ts["+k+"]) ts.errorOnWeightedMeanEquivalentRate() = "+number);
            number = ts.varianceInEquivalentRates();
            logger.info("(ts["+k+"]) ts.varianceInEquivalentRates() = "+number);
            number = ts.meanDeviationInEquivalentRates();
            logger.info("(ts["+k+"]) ts.meanDeviationInEquivalentRates() = "+number);

            k++;
        }
    }

}