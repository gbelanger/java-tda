package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class CountsTimeSeriesFactory implements ITimeSeriesFactory {

    private static final Logger logger  = Logger.getLogger(CountsTimeSeriesFactory.class);

    public static CountsTimeSeries create(String filename) throws TimeSeriesFileException {
        return (CountsTimeSeries) ITimeSeriesFactory.create(filename);
    }

    public static CountsTimeSeries create(ICountsTimeSeries ts) {
        return new CountsTimeSeries(ts);
    }

    public static CountsTimeSeries create(double[] binEdges, double[] intensities) {
        double tstart = binEdges[0];
        return create(tstart, binEdges, intensities);
    }

    public static CountsTimeSeries create(double tstart, double[] binEdges, double[] intensities) {
        return new CountsTimeSeries(tstart, binEdges, intensities);
    }

    public static CountsTimeSeries create(double[] binEdges, double[] intensities, double[] uncertainties) {
        return new CountsTimeSeries(binEdges, intensities, uncertainties);
    }

    public static CountsTimeSeries create(double tstart, double[] binEdges, double[] intensities, double[] uncertainties) {
        return new CountsTimeSeries(tstart, binEdges, intensities, uncertainties);
    }
}
