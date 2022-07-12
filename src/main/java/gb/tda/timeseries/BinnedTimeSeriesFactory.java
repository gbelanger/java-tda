package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class BinnedTimeSeriesFactory {
    private static final Logger logger  = Logger.getLogger(BinnedTimeSeriesFactory.class);

    public static BinnedTimeSeries create(BinnedTimeSeries ts) {
        return new BinnedTimeSeries(ts);
    }
}
