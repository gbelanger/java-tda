package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class BasicTimeSeriesFactoryTest {
    
    private static final Logger logger  = Logger.getLogger(BasicTimeSeriesFactoryTest.class);

    public static void main(String[] args) throws Exception {

        // Create from file
        String filename = "basic-time-series1.qdp";
        BasicTimeSeries bts = BasicTimeSeriesFactory.create(filename);

        // Create from other BasicTimeSeries
        BasicTimeSeries bts2 = BasicTimeSeriesFactory.create(bts);

        // Define dummy data
        double tstart = 5;
        double[] times = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] intensities = new double[] {5, 3, 4, 6, 7, 4, 6, 4, 4, 5};
        double[] uncertainties = new double[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        BasicTimeSeries bts3 = BasicTimeSeriesFactory.create(times, intensities);
        BasicTimeSeries bts4 = BasicTimeSeriesFactory.create(tstart, times, intensities);
        BasicTimeSeries bts5 = BasicTimeSeriesFactory.create(times, intensities, uncertainties);
        BasicTimeSeries bts6 = BasicTimeSeriesFactory.create(tstart, times, intensities, uncertainties);
    }

}