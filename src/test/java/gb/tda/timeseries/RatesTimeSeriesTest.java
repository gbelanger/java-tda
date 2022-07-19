import org.apache.log4j.Logger;

public class RatesTimeSeriesTest {

    private static org.apache.log4j.Logger logger = Logger.getLogger(RatesTimeSeriesTest.class);

    public static void main(String[] args) throws IllegalArgumentException {
        // Define dummy data
        double tstart = 5;
        double[] binCentres = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] binEdges = BinningUtils.getBinEdgesFromBinCentres(binCentres);
        double[] intensities = new double[] {5, 3, 4, 6, 7, 4, 6, 4, 4, 5};
        double[] uncertainties = new double[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        // Test constructors
        RatesTimeSeries[] tSeries = testConstructors(tstart, binEdges, intensities, uncertainties);

        // Test methods
        testMethods(tSeries);
    }

    private static RatesTimeSeries[] testConstructors(double tstart, double[] binEdges, double[] intensities, double[] uncertainties) {
        logger.info("TESTING -- CONSTRUCTORS --");
        logger.info("  Constructor RatesTimeSeries(binEdges, intensities)");
        RatesTimeSeries ts1 = new RatesTimeSeries(binEdges, intensities);
        RatesTimeSeries ts11 = new RatesTimeSeries(ts1);

        logger.info("  Constructor RatesTimeSeries(tstart, binEdges, intensities)");
        RatesTimeSeries ts2 = new RatesTimeSeries(tstart, binEdges, intensities);
        RatesTimeSeries ts22 = new RatesTimeSeries(ts2);

        logger.info("  Constructor RatesTimeSeries(binEdges, intensities, uncertainties)");
        RatesTimeSeries ts3 = new RatesTimeSeries(binEdges, intensities, uncertainties);
        RatesTimeSeries ts33 = new RatesTimeSeries(ts3);

        logger.info("  Constructor RatesTimeSeries(tstart, binEdges, intensities, uncertainties)");
        RatesTimeSeries ts4 = new RatesTimeSeries(tstart, binEdges, intensities, uncertainties);
        RatesTimeSeries ts44 = new RatesTimeSeries(ts4);

        return new RatesTimeSeries[] {ts1, ts2, ts3, ts4};
    }

    private static void testMethods(RatesTimeSeries[] tSeries) {
        int k = 0;
        for (RatesTimeSeries ts : tSeries) {
            logger.info("TESTING -- METHODS -- for RatesTimeSeries "+(k));

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
            number = ts.skewnessInIntensities();
            logger.info("(ts["+k+"]) ts.skewnessInIntensities() = "+number);
            number = ts.skewnessStandardError();
            logger.info("(ts["+k+"]) ts.skewnessStandardError() = "+number);
            number = ts.kurtosisInIntensities();
            logger.info("(ts["+k+"]) ts.kurtosisInIntensities() = "+number);
            number = ts.kurtosisStandardError();
            logger.info("(ts["+k+"]) ts.kurtosisStandardError() = "+number);

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

            //  Rates 
            logger.info("  --RATES-- ");
            //// Convenience methods that map counts to intensities defined in AbstractTimeSeries
            array = ts.getRates();
            if (array != null) {
                logger.info("(ts["+k+"]) ts.getRates()");
                for (int i = 0; i < array.length; i++) {
                    logger.info("(ts["+k+"])   "+array[i]);
                }
            }
            array = ts.getMeanSubtractedRates();
            if (array != null) {
                logger.info("(ts["+k+"]) ts.getMeanSubtractedRates()");
                for (int i = 0; i < array.length; i++) {
                    logger.info("(ts["+k+"])   "+array[i]);
                }
            }
            number = ts.meanRate();
            logger.info("(ts["+k+"]) ts.meanRate() = "+number);
            number = ts.minRate();
            logger.info("(ts["+k+"]) ts.minRate() = "+number);
            number = ts.maxRate();
            logger.info("(ts["+k+"]) ts.maxRate() = "+number);
            number = ts.errorOnMeanRate();
            logger.info("(ts["+k+"]) ts.errorOnMeanRate() = "+number);
            number = ts.varianceInRates();
            logger.info("(ts["+k+"]) ts.varianceInRates() = "+number);
            number = ts.meanDeviationInRates();
            logger.info("(ts["+k+"]) ts.meanDeviationInRates() = "+number);
            number = ts.weightedMeanRate();
            logger.info("(ts["+k+"]) ts.weightedMeanRate() = "+number);
            number = ts.errorOnWeightedMeanRate();
            logger.info("(ts["+k+"]) ts.errorOnWeightedMeanRate() = "+number);

            // Equivalent Counts
            logger.info("  --EQUIVALENT COUNTS-- ");
            array = ts.getEquivalentCounts();
            if (array != null) {
                logger.info("(ts["+k+"]) ts.getEquivalentCounts()");
                for (int i = 0; i < array.length; i++) {
                    logger.info("(ts["+k+"])   "+array[i]);
                }
            }
            array = ts.getUncertaintiesOnEquivalentCounts();
            if (array != null) {
                logger.info("(ts["+k+"]) ts.getUncertaintiesOnEquivalentCounts()");
                for (int i = 0; i < array.length; i++) {
                    logger.info("(ts["+k+"])   "+array[i]);
                }
            }
            array = ts.getMeanSubtractedEquivalentCounts();
            if (array != null) {
                logger.info("(ts["+k+"]) ts.getMeanSubtractedEquivalentCounts()");
                for (int i = 0; i < array.length; i++) {
                    logger.info("(ts["+k+"])   "+array[i]);
                }
            }
            number = ts.minEquivalentCount();
            logger.info("(ts["+k+"]) ts.minEquivalentCount() = "+number);
            number = ts.maxEquivalentCount();
            logger.info("(ts["+k+"]) ts.maxEquivalentCount() = "+number);
            number = ts.meanEquivalentCount();
            logger.info("(ts["+k+"]) ts.meanEquivalentCount() = "+number);
            number = ts.errorOnMeanEquivalentCount();
            logger.info("(ts["+k+"]) ts.errorOnMeanEquivalentCount() = "+number);
            number = ts.weightedMeanEquivalentCount();
            logger.info("(ts["+k+"]) ts.weightedMeanEquivalentCount() = "+number);
            number = ts.errorOnWeightedMeanEquivalentCount();
            logger.info("(ts["+k+"]) ts.errorOnWeightedMeanEquivalentCount() = "+number);
            number = ts.varianceInEquivalentCounts();
            logger.info("(ts["+k+"]) ts.varianceInEquivalentCounts() = "+number);
            number = ts.meanDeviationInEquivalentCounts();
            logger.info("(ts["+k+"]) ts.meanDeviationInEquivalentCounts() = "+number);

            k++;
        }
    }

}