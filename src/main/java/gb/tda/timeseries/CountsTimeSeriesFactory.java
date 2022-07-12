package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class CountsTimeSeriesFactory {
    private static final Logger logger  = Logger.getLogger(CountsTimeSeriesFactory.class);

    public static CountsTimeSeries create(CountsTimeSeries ts) {
        return new CountsTimeSeries(ts);
    }

    public static CountsTimeSeries create(double[] binEdges, double[] counts) {
        double tStart = binEdges[0];
        double[] zeroedBinEdges = Utils.resetToZero(binEdges);
        CountsTimeSeries ts = new CountsTimeSeries(tStart, zeroedBinEdges, counts);
        if ( ts.thereAreNaNs() ) {
            ts = (CountsTimeSeries) TimeSeriesUtils.dropLeadingAndTrailingNaNs(ts);
        }
        return ts;
    }

}
