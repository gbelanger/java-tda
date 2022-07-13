package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class BinnedTimeSeriesFactory {
    private static final Logger logger  = Logger.getLogger(BinnedTimeSeriesFactory.class);
    
    public static BinnedTimeSeries create(BinnedTimeSeries ts) {
        return new BinnedTimeSeries(ts);
    }
    
    public static BinnedTimeSeries create(double[] binEdges, double[] intensities) {
        return new BinnedTimeSeries(binEdges, intensities);
    }

    public static BinnedTimeSeries create(double tstart, double[] binEdges, double[] intensities) {
        return new BinnedTimeSeries(tstart, binEdges, intensities);
    }

    public static BinnedTimeSeries create(double[] binEdges, double[] intensities, double[] uncertainties) {
        return new BinnedTimeSeries(binEdges, intensities, uncertainties);
    }

    public static BinnedTimeSeries create(double tstart, double[] binEdges, double[] intensities, double[] uncertainties) {
        return new BinnedTimeSeries(tstart, binEdges, intensities, uncertainties);
    }

}
