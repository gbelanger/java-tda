import org.apache.log4j.Logger;

import java.awt.geom.Point2D;

public class AstroTimeSeriesTest {

    private static org.apache.log4j.Logger logger = Logger.getLogger(AstroTimeSeriesTest.class);

    public static void main(String[] args) throws Exception {
        // Define dummy data
        double tstart = 5;
        double[] binCentres = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] binEdges = BinningUtils.getBinEdgesFromBinCentres(binCentres);
        double[] intensities = new double[] {5, 3, 4, 6, 7, 4, 6, 4, 4, 5};
        double[] uncertainties = new double[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        // Test Counts
        AstroTimeSeries[] tsCounts = testCountsConstructors(tstart, binEdges, intensities, uncertainties);
        testMethods(tsCounts);

        // Test Rates
        AstroTimeSeries[] tsRates = testRatesConstructors(tstart, binEdges, intensities, uncertainties);
        testMethods(tsRates);

        // Test IAstro cloning with Counts
        AstroTimeSeries[] tsAstroCounts = testAstroConstructors(tsCounts);
        testMethods(tsAstroCounts);

        // test IAstro cloning with Rates
        AstroTimeSeries[] tsAstroRates = testAstroConstructors(tsRates);
        testMethods(tsAstroRates);
    }

    private static AstroTimeSeries[] testCountsConstructors(double tstart, double[] binEdges, double[] intensities, double[] uncertainties) {
        logger.info("TESTING -- (Counts) CONSTRUCTORS --");
        logger.info("  Constructor AstroTimeSeries(CountsTimeSeries(binEdges, intensities))");
        CountsTimeSeries ts1 = new CountsTimeSeries(binEdges, intensities);
        CountsTimeSeries ts11 = new CountsTimeSeries(ts1);
        AstroTimeSeries ts111 = new AstroTimeSeries(ts11);

        logger.info("  Constructor AstroTimeSeries(CountsTimeSeries(tstart, binEdges, intensities))");
        CountsTimeSeries ts2 = new CountsTimeSeries(tstart, binEdges, intensities);
        CountsTimeSeries ts22 = new CountsTimeSeries(ts2);
        AstroTimeSeries ts222 = new AstroTimeSeries(ts22);

        logger.info("  Constructor AstroTimeSeries(CountsTimeSeries(binEdges, intensities, uncertainties))");
        CountsTimeSeries ts3 = new CountsTimeSeries(binEdges, intensities, uncertainties);
        CountsTimeSeries ts33 = new CountsTimeSeries(ts3);
        AstroTimeSeries ts333 = new AstroTimeSeries(ts33);

        logger.info("  Constructor AstroTimeSeries(CountsTimeSeries(tstart, binEdges, intensities, uncertainties))");
        CountsTimeSeries ts4 = new CountsTimeSeries(tstart, binEdges, intensities, uncertainties);
        CountsTimeSeries ts44 = new CountsTimeSeries(ts4);
        AstroTimeSeries ts444 = new AstroTimeSeries(ts44);

        return new AstroTimeSeries[] {ts111, ts222, ts333, ts444};
    }

    private static AstroTimeSeries[] testRatesConstructors(double tstart, double[] binEdges, double[] intensities, double[] uncertainties) {
        logger.info("TESTING -- (Rates) CONSTRUCTORS --");
        logger.info("  Constructor AstroTimeSeries(RatesTimeSeries(binEdges, intensities))");
        RatesTimeSeries ts1 = new RatesTimeSeries(binEdges, intensities);
        RatesTimeSeries ts11 = new RatesTimeSeries(ts1);
        AstroTimeSeries ts111 = new AstroTimeSeries(ts11);

        logger.info("  Constructor AstroTimeSeries(RatesTimeSeries(tstart, binEdges, intensities))");
        RatesTimeSeries ts2 = new RatesTimeSeries(tstart, binEdges, intensities);
        RatesTimeSeries ts22 = new RatesTimeSeries(ts2);
        AstroTimeSeries ts222 = new AstroTimeSeries(ts22);

        logger.info("  Constructor AstroTimeSeries(RatesTimeSeries(binEdges, intensities, uncertainties))");
        RatesTimeSeries ts3 = new RatesTimeSeries(binEdges, intensities, uncertainties);
        RatesTimeSeries ts33 = new RatesTimeSeries(ts3);
        AstroTimeSeries ts333 = new AstroTimeSeries(ts33);

        logger.info("  Constructor AstroTimeSeries(RatesTimeSeries(tstart, binEdges, intensities, uncertainties))");
        RatesTimeSeries ts4 = new RatesTimeSeries(tstart, binEdges, intensities, uncertainties);
        RatesTimeSeries ts44 = new RatesTimeSeries(ts4);
        AstroTimeSeries ts444 = new AstroTimeSeries(ts44);

        return new AstroTimeSeries[] {ts111, ts222, ts333, ts444};
    }

    private static AstroTimeSeries[] testAstroConstructors(AstroTimeSeries[] tSeries) {
        AstroTimeSeries[] tsClones = new AstroTimeSeries[tSeries.length];
        int k = 0;
        for (AstroTimeSeries ts : tSeries) {
            logger.info("TESTING -- IAstroTimeSeries CONSTRUCTORS --");
            logger.info("  Constructor AstroTimeSeries(IAstroTimeSeries)");
            tsClones[k++] = new AstroTimeSeries(ts);
        }
        return tsClones;
    }
    private static void testMethods(AstroTimeSeries[] tSeries) {
        int k = 0;
        for (AstroTimeSeries ts : tSeries) {
            logger.info("TESTING -- METHODS -- for AstroTimeSeries "+(k));

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
            for (double v5 : array) {
                logger.info("(ts[" + k + "])   " + v5);
            }

            // Bins
            logger.info("  --BINS-- ");
            n = ts.nBins(); 
            logger.info("(ts["+k+"]) ts.nBins() = "+n);
            array = ts.getBinCentres();
            logger.info("(ts["+k+"]) ts.getBinCentres()");
            for (double v4 : array) {
                logger.info("(ts[" + k + "])   " + v4);
            }
            array = ts.getBinWidths();
            logger.info("(ts["+k+"]) ts.getBinWidths()");
            for (double v3 : array) {
                logger.info("(ts[" + k + "])   " + v3);
            }
            array = ts.getHalfBinWidths();
            logger.info("(ts["+k+"]) ts.getHalfBinWidths()");
            for (double v2 : array) {
                logger.info("(ts[" + k + "])   " + v2);
            }
            array = ts.getBinEdges();
            logger.info("(ts["+k+"]) ts.getBinEdges()");
            for (double v1 : array) {
                logger.info("(ts[" + k + "])   " + v1);
            }
            array = ts.getLeftBinEdges();
            logger.info("(ts["+k+"]) ts.getLeftBinEdges()");
            for (double element : array) {
                logger.info("(ts[" + k + "])   " + element);
            }
            array = ts.getRightBinEdges();
            logger.info("(ts["+k+"]) ts.getRightBinEdges()");
            for (double item : array) {
                logger.info("(ts[" + k + "])   " + item);
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
            for (double value : array) {
                logger.info("(ts[" + k + "])   " + value);
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
                for (double v : array) {
                    logger.info("(ts[" + k + "])   " + v);
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
            if (ts.isRatesTimeSeries()) {
                logger.info("  --RATES-- ");
                //// Convenience methods that map counts to intensities defined in AbstractTimeSeries
                array = ts.getRates();
                if (array != null) {
                    logger.info("(ts[" + k + "]) ts.getRates()");
                    for (double v : array) {
                        logger.info("(ts[" + k + "])   " + v);
                    }
                }
                array = ts.getMeanSubtractedRates();
                if (array != null) {
                    logger.info("(ts[" + k + "]) ts.getMeanSubtractedRates()");
                    for (double v : array) {
                        logger.info("(ts[" + k + "])   " + v);
                    }
                }
                number = ts.meanRate();
                logger.info("(ts[" + k + "]) ts.meanRate() = " + number);
                number = ts.minRate();
                logger.info("(ts[" + k + "]) ts.minRate() = " + number);
                number = ts.maxRate();
                logger.info("(ts[" + k + "]) ts.maxRate() = " + number);
                number = ts.errorOnMeanRate();
                logger.info("(ts[" + k + "]) ts.errorOnMeanRate() = " + number);
                number = ts.varianceInRates();
                logger.info("(ts[" + k + "]) ts.varianceInRates() = " + number);
                number = ts.meanDeviationInRates();
                logger.info("(ts[" + k + "]) ts.meanDeviationInRates() = " + number);
                number = ts.weightedMeanRate();
                logger.info("(ts[" + k + "]) ts.weightedMeanRate() = " + number);
                number = ts.errorOnWeightedMeanRate();
                logger.info("(ts[" + k + "]) ts.errorOnWeightedMeanRate() = " + number);

                // Equivalent Counts
                logger.info("  --EQUIVALENT COUNTS-- ");
                array = ts.getEquivalentCounts();
                if (array != null) {
                    logger.info("(ts[" + k + "]) ts.getEquivalentCounts()");
                    for (double v : array) {
                        logger.info("(ts[" + k + "])   " + v);
                    }
                }
                array = ts.getUncertaintiesOnEquivalentCounts();
                if (array != null) {
                    logger.info("(ts[" + k + "]) ts.getUncertaintiesOnEquivalentCounts()");
                    for (double v : array) {
                        logger.info("(ts[" + k + "])   " + v);
                    }
                }
                array = ts.getMeanSubtractedEquivalentCounts();
                if (array != null) {
                    logger.info("(ts[" + k + "]) ts.getMeanSubtractedEquivalentCounts()");
                    for (double v : array) {
                        logger.info("(ts[" + k + "])   " + v);
                    }
                }
                number = ts.minEquivalentCount();
                logger.info("(ts[" + k + "]) ts.minEquivalentCount() = " + number);
                number = ts.maxEquivalentCount();
                logger.info("(ts[" + k + "]) ts.maxEquivalentCount() = " + number);
                number = ts.meanEquivalentCount();
                logger.info("(ts[" + k + "]) ts.meanEquivalentCount() = " + number);
                number = ts.errorOnMeanEquivalentCount();
                logger.info("(ts[" + k + "]) ts.errorOnMeanEquivalentCount() = " + number);
                number = ts.weightedMeanEquivalentCount();
                logger.info("(ts[" + k + "]) ts.weightedMeanEquivalentCount() = " + number);
                number = ts.errorOnWeightedMeanEquivalentCount();
                logger.info("(ts[" + k + "]) ts.errorOnWeightedMeanEquivalentCount() = " + number);
                number = ts.varianceInEquivalentCounts();
                logger.info("(ts[" + k + "]) ts.varianceInEquivalentCounts() = " + number);
                number = ts.meanDeviationInEquivalentCounts();
                logger.info("(ts[" + k + "]) ts.meanDeviationInEquivalentCounts() = " + number);
            }
            else {
                //  Counts
                logger.info("  --COUNTS-- ");
                //// Convenience methods that map counts to intensities defined in AbstractTimeSeries
                array = ts.getCounts();
                if (array != null) {
                    logger.info("(ts[" + k + "]) ts.counts()");
                    for (double v : array) {
                        logger.info("(ts[" + k + "])   " + v);
                    }
                }
                array = ts.getMeanSubtractedCounts();
                if (array != null) {
                    logger.info("(ts[" + k + "]) ts.getMeanSubtractedCounts()");
                    for (double v : array) {
                        logger.info("(ts[" + k + "])   " + v);
                    }
                }
                number = ts.meanCount();
                logger.info("(ts[" + k + "]) ts.meanCount() = " + number);
                number = ts.minCount();
                logger.info("(ts[" + k + "]) ts.minCount() = " + number);
                number = ts.maxCount();
                logger.info("(ts[" + k + "]) ts.maxCount() = " + number);
                number = ts.errorOnMeanCount();
                logger.info("(ts[" + k + "]) ts.errorOnMeanCount() = " + number);
                number = ts.varianceInCounts();
                logger.info("(ts[" + k + "]) ts.varianceInCounts() = " + number);
                number = ts.meanDeviationInCounts();
                logger.info("(ts[" + k + "]) ts.meanDeviationInCounts() = " + number);

                // Equivalent Rates
                logger.info("  --EQUIVALENT RATES-- ");
                array = ts.getEquivalentRates();
                if (array != null) {
                    logger.info("(ts[" + k + "]) ts.getEquivalentRates()");
                    for (double v : array) {
                        logger.info("(ts[" + k + "])   " + v);
                    }
                }
                array = ts.getUncertaintiesOnEquivalentRates();
                if (array != null) {
                    logger.info("(ts[" + k + "]) ts.getUncertaintiesOnEquivalentRates()");
                    for (double v : array) {
                        logger.info("(ts[" + k + "])   " + v);
                    }
                }
                array = ts.getMeanSubtractedEquivalentRates();
                if (array != null) {
                    logger.info("(ts[" + k + "]) ts.getMeanSubtractedEquivalentRates()");
                    for (double v : array) {
                        logger.info("(ts[" + k + "])   " + v);
                    }
                }
                number = ts.meanEquivalentRate();
                logger.info("(ts[" + k + "]) ts.meanEquivalentRate() = " + number);
                number = ts.minEquivalentRate();
                logger.info("(ts[" + k + "]) ts.minEquivalentRate() = " + number);
                number = ts.maxEquivalentRate();
                logger.info("(ts[" + k + "]) ts.maxEquivalentRate() = " + number);
                number = ts.errorOnMeanEquivalentRate();
                logger.info("(ts[" + k + "]) ts.errorOnMeanEquivalentRate() = " + number);
                number = ts.weightedMeanEquivalentRate();
                logger.info("(ts[" + k + "]) ts.weightedMeanEquivalentRate() = " + number);
                number = ts.errorOnWeightedMeanEquivalentRate();
                logger.info("(ts[" + k + "]) ts.errorOnWeightedMeanEquivalentRate() = " + number);
                number = ts.varianceInEquivalentRates();
                logger.info("(ts[" + k + "]) ts.varianceInEquivalentRates() = " + number);
                number = ts.meanDeviationInEquivalentRates();
                logger.info("(ts[" + k + "]) ts.meanDeviationInEquivalentRates() = " + number);
            }

            // Spectifc to Astro
            String telescope = "TELESCOPE";
            ts.setTelescope(telescope);
            string = ts.telescope();
            logger.info("(ts["+k+"]) ts.telescope() = "+string);

            String instrument = "INSTRUMENT";
            ts.setInstrument(instrument);
            string = ts.instrument();
            logger.info("(ts["+k+"]) ts.instrument() = "+string);

            double mjdref = 52000;
            ts.setMJDREF(mjdref);
            number = ts.mjdref();
            logger.info("(ts["+k+"]) ts.mjdref() = "+number);

            String targetName = "TARGET_NAME";
            ts.setTargetName(targetName);
            string = ts.targetName();
            logger.info("(ts["+k+"]) ts.targetName() = "+string);

            double ra = 266.40;
            double dec = -29.01;
            ts.setTargetRaDec(ra, dec);
            number = ts.targetRA();
            logger.info("(ts["+k+"]) ts.targetRA() = "+number);
            number = ts.targetDec();
            logger.info("(ts["+k+"]) ts.targetDec() = "+number);
            Point2D.Double point = ts.targetRaDec();
            logger.info("(ts["+k+"]) ts.targetRaDec() = "+point.getX()+" "+point.getY());

            double emin = 20;
            double emax = 40;
            ts.setEnergyRange(emin, emax);
            number = ts.energyRangeMin();
            logger.info("(ts["+k+"]) ts.energyRangeMin() = "+number);
            number = ts.energyRangeMax();
            logger.info("(ts["+k+"]) ts.energyRangeMax() = "+number);
            point = ts.energyRange();
            logger.info("(ts["+k+"]) ts.energyRange() = "+point.getX()+" "+point.getY());

            double relTimeError = 0.006;
            ts.setRelTimeError(relTimeError);
            number = ts.relTimeError();
            logger.info("(ts["+k+"]) ts.relTimeError() = "+number);

            double absTimeError = 0.012;
            ts.setAbsTimeError(absTimeError);
            number = ts.absTimeError();
            logger.info("(ts["+k+"]) ts.absTimeError() = "+number);

            number = ts.ontime(); // integration time during the observation
            logger.info("(ts["+k+"]) ts.ontime() as sum of bins = "+number);
            double ontime = 54321;
            ts.setOntime(ontime);
            number = ts.ontime(); // integration time during the observation
            logger.info("(ts["+k+"]) ts.ontime() after setting = "+number);

            double livetime = 12345;
            ts.setLivetime(livetime);
            number = ts.livetime(); // exposure time after deadtime correction
            logger.info("(ts["+k+"]) ts.livetime() = "+number);

            double exposureOnTarget = 1234;
            ts.setExposureOnTarget(exposureOnTarget);
            number = ts.exposureOnTarget();
            logger.info("(ts["+k+"]) ts.exposureOnTarget() = "+number);

            //  Boolean checkers
            bool = ts.isCountsTimeSeries();
            logger.info("(ts["+k+"]) ts.isCountsTimeSeries() = "+bool);
            bool = ts.isRatesTimeSeries();
            logger.info("(ts["+k+"]) ts.isRatesTimeSeries() = "+bool);
            bool = ts.telescopeIsSet();
            logger.info("(ts["+k+"]) ts.telescopeIsSet() = "+bool);
            bool = ts.instrumentIsSet();
            logger.info("(ts["+k+"]) ts.instrumentIsSet() = "+bool);
            bool = ts.mjdrefIsSet();
            logger.info("(ts["+k+"]) ts.mjdrefIsSet() = "+bool);
            bool = ts.targetNameIsSet();
            logger.info("(ts["+k+"]) ts.targetNameIsSet() = "+bool);
            bool = ts.targetRaDecAreSet();
            logger.info("(ts["+k+"]) ts.targetRaDecAreSet() = "+bool);
            bool = ts.energyRangeIsSet();
            logger.info("(ts["+k+"]) ts.energyRangeIsSet() = "+bool);

            k++;
        }
    }

}