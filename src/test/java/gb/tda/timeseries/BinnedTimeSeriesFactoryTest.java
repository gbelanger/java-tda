package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class BinnedTimeSeriesFactoryTest {
    
    private static final Logger logger  = Logger.getLogger(BinnedTimeSeriesFactoryTest.class);

    public static void main(String[] args) throws Exception {

        // Create from file
        String filename = "binned-time-series3.qdp";
        BinnedTimeSeries bts = BinnedTimeSeriesFactory.create(filename);

        // Create from other BinnedTimeSeries
//        BinnedTimeSeries bts2 = BinnedTimeSeriesFactory.create(bts);

        // Define dummy data
//        double tstart = 5;
//        double[] binCentres = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
//        double[] binEdges = BinningUtils.getBinEdgesFromBinCentres(binCentres);
//        double[] intensities = new double[] {5, 3, 4, 6, 7, 4, 6, 4, 4, 5};
//        double[] uncertainties = new double[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
//
//        BinnedTimeSeries bts3 = BinnedTimeSeriesFactory.create(times, intensities);
//        BinnedTimeSeries bts4 = BinnedTimeSeriesFactory.create(tstart, times, intensities);
//        BinnedTimeSeries bts5 = BinnedTimeSeriesFactory.create(times, intensities, uncertainties);
//        BinnedTimeSeries bts6 = BinnedTimeSeriesFactory.create(tstart, times, intensities, uncertainties);
    }

}