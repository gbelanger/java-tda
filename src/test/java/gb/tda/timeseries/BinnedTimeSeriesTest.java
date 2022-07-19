import org.apache.log4j.Logger;

public class BinnedTimeSeriesTest {

    private static org.apache.log4j.Logger logger = Logger.getLogger(BinnedTimeSeriesTest.class);

    public static void main(String[] args) throws IllegalArgumentException {
        // Define dummy data
        double tstart = 5;
        double[] binCentres = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] binEdges = BinningUtils.getBinEdgesFromBinCentres(binCentres);
        double[] intensities = new double[] {5, 3, 4, 6, 7, 4, 6, 4, 4, 5};
        double[] uncertainties = new double[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        // Test constructors
        BinnedTimeSeries[] tSeries = testConstructors(tstart, binEdges, intensities, uncertainties);

        // Test methods
        testMethods(tSeries);
    }

    private static BinnedTimeSeries[] testConstructors(double tstart, double[] binEdges, double[] intensities, double[] uncertainties) {
        logger.info("TESTING -- CONSTRUCTORS --");
        logger.info("  Constructor BinnedTimeSeries(binEdges, intensities)");
        BinnedTimeSeries ts1 = new BinnedTimeSeries(binEdges, intensities);
        BinnedTimeSeries ts11 = new BinnedTimeSeries(ts1);

        logger.info("  Constructor BinnedTimeSeries(tstart, binEdges, intensities)");
        BinnedTimeSeries ts2 = new BinnedTimeSeries(tstart, binEdges, intensities);
        BinnedTimeSeries ts22 = new BinnedTimeSeries(ts2);

        logger.info("  Constructor BinnedTimeSeries(binEdges, intensities, uncertainties)");
        BinnedTimeSeries ts3 = new BinnedTimeSeries(binEdges, intensities, uncertainties);
        BinnedTimeSeries ts33 = new BinnedTimeSeries(ts3);

        logger.info("  Constructor BinnedTimeSeries(tstart, binEdges, intensities, uncertainties)");
        BinnedTimeSeries ts4 = new BinnedTimeSeries(tstart, binEdges, intensities, uncertainties);
        BinnedTimeSeries ts44 = new BinnedTimeSeries(ts4);

        return new BinnedTimeSeries[] {ts1, ts2, ts3, ts4};
    }

    private static void testMethods(BinnedTimeSeries[] tSeries) {
        int k = 0;
        for (BinnedTimeSeries ts : tSeries) {
            logger.info("TESTING -- METHODS -- for BinnedTimeSeries "+(k));

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

            k++;
        }
    }

}