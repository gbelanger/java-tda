package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class BasicTimeSeriesFactory {
    private static final Logger logger  = Logger.getLogger(BasicTimeSeriesFactory.class);

    public static BasicTimeSeries create(BasicTimeSeries ts) {
        return new BasicTimeSeries(ts);
    }
}
