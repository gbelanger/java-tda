package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class BasicTimeSeriesTest {

    private static final Logger logger = Logger.getLogger(BasicTimeSeriesTest.class);

    public static void main(String[] args) {
        // Define dummy data
        double tstart = 5;
        double[] times = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] intensities = new double[] {5, 3, 4, 6, 7, 4, 6, 4, 4, 5};
        double[] uncertainties = new double[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        // Test constructors
        BasicTimeSeries[] tSeries = testConstructors(tstart, times, intensities, uncertainties);

        // Test methods
        testMethods(tSeries);
    }

    private static BasicTimeSeries[] testConstructors(double tstart, double[] times, double[] intensities, double[] uncertainties) {
        logger.info("Testing constructor BasicTimeSeries(times, intensities)");
        BasicTimeSeries ts1 = new BasicTimeSeries(times, intensities);
        BasicTimeSeries ts11 = new BasicTimeSeries(ts1);

        logger.info("Testing constructor BasicTimeSeries(tstart, times, intensities)");
        BasicTimeSeries ts2 = new BasicTimeSeries(tstart, times, intensities);
        BasicTimeSeries ts22 = new BasicTimeSeries(ts2);

        logger.info("Testing constructor BasicTimeSeries(times, intensities, uncertainties)");
        BasicTimeSeries ts3 = new BasicTimeSeries(times, intensities, uncertainties);
        BasicTimeSeries ts33 = new BasicTimeSeries(ts3);

        logger.info("Testing constructor BasicTimeSeries(tstart, times, intensities, uncertainties)");
        BasicTimeSeries ts4 = new BasicTimeSeries(tstart, times, intensities, uncertainties);
        BasicTimeSeries ts44 = new BasicTimeSeries(ts4);

        return new BasicTimeSeries[] {ts1, ts2, ts3, ts4};
    }

    private static void testMethods(BasicTimeSeries[] tSeries) {
        int k = 0;
        for (BasicTimeSeries ts : tSeries) {
            logger.info("Testing BasicTimeSeries number "+(k));
            String var1 = ts.timeUnit();
            logger.info("(tSeries["+k+"]) ts.timeUnit() = "+var1);
            double var2 = ts.tStart();
            logger.info("(tSeries["+k+"]) ts.tStart() = "+var2);
            double var3 = ts.tStop();
            logger.info("(tSeries["+k+"]) ts.tStop() = "+var3);
            double[] var4 = ts.getTimes();
            logger.info("(tSeries["+k+"]) ts.getTimes()");
            for (int i = 0; i < var4.length; i++) {
                logger.info("(tSeries["+k+"])   "+var4[i]);
            }
            double[] var5 = ts.getIntensities();
            logger.info("(tSeries["+k+"]) ts.getIntensities()");
            for (int i = 0; i < var5.length; i++) {
                logger.info("(tSeries["+k+"])   "+var5[i]);
            }
            double var6 = ts.sumOfIntensities();
            logger.info("(tSeries["+k+"]) ts.sumOfIntensities() = "+var6);
            double var7 = ts.meanIntensity();
            logger.info("(tSeries["+k+"]) ts.meanIntensity() = "+var7);
            double var8 = ts.minIntensity();
            logger.info("(tSeries["+k+"]) ts.minIntensity() = "+var8);
            double var9 = ts.maxIntensity();
            logger.info("(tSeries["+k+"]) ts.maxIntensity() = "+var9);
            double var10 = ts.varianceInIntensities();
            logger.info("(tSeries["+k+"]) ts.varianceInIntensities() = "+var10);
            double var11 = ts.meanDeviationInIntensities();
            logger.info("(tSeries["+k+"]) ts.meanDeviationInIntensities() = "+var11);
            double var12 = ts.kurtosisInIntensities();
            logger.info("(tSeries["+k+"]) ts.kurtosisInIntensities() = "+var12);
            double var13 = ts.kurtosisStandardError();
            logger.info("(tSeries["+k+"]) ts.kurtosisStandardError() = "+var13);
            double var14 = ts.skewnessInIntensities();
            logger.info("(tSeries["+k+"]) ts.skewnessInIntensities() = "+var14);
            double var15 = ts.skewnessStandardError();
            logger.info("(tSeries["+k+"]) ts.skewnessStandardError() = "+var15);
            double[] var16 = ts.getUncertainties();
            if (var16 != null) {
                logger.info("(tSeries["+k+"]) ts.getUncertainties()");
                for (int i = 0; i < var16.length; i++) {
                    logger.info("(tSeries["+k+"])   "+var16[i]);
                }
            }
            double var17 = ts.sumOfUncertainties();
            logger.info("(tSeries["+k+"]) ts.sumOfUncertainties() = "+var17);
            double var18 = ts.meanUncertainty();
            logger.info("(tSeries["+k+"]) ts.meanUncertainty() = "+var18);
            double var19 = ts.minUncertainty();
            logger.info("(tSeries["+k+"]) ts.minUncertainty() = "+var19);
            double var20 = ts.maxUncertainty();
            logger.info("(tSeries["+k+"]) ts.maxUncertainty() = "+var20);
            double var21 = ts.varianceInUncertainties();
            logger.info("(tSeries["+k+"]) ts.varianceInUncertainties() = "+var21);
            double var22 = ts.errorOnMeanUncertainty();
            logger.info("(tSeries["+k+"]) ts.errorOnMeanUncertainty() = "+var22);
            double var23 = ts.meanDeviationInUncertainties();
            logger.info("(tSeries["+k+"]) ts.meanDeviationInUncertainties() = "+var23);
            boolean var24 = ts.uncertaintiesAreSet();
            logger.info("(tSeries["+k+"]) ts.uncertaintiesAreSet() = "+var24);
            k++;
        }
    }

}
