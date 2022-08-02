package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class BasicTimeSeriesFactory implements ITimeSeriesFactory {

    private static final Logger logger  = Logger.getLogger(BasicTimeSeriesFactory.class);

    public static BasicTimeSeries create(String filename) throws TimeSeriesFileException {
        return (BasicTimeSeries) ITimeSeriesFactory.create(filename);
    }

    public static BasicTimeSeries create(BasicTimeSeries ts) {
        return new BasicTimeSeries(ts);
    }

    public static BasicTimeSeries create(double[] times, double[] intensities) {
        return new BasicTimeSeries(times, intensities);
    }

    public static BasicTimeSeries create(double tstart, double[] times, double[] intensities) {
        return new BasicTimeSeries(tstart, times, intensities);
    }

    public static BasicTimeSeries create(double[] times, double[] intensities, double[] uncertainties) {
        return new BasicTimeSeries(times, intensities, uncertainties);
    }

    public static BasicTimeSeries create(double tstart, double[] times, double[] intensities, double[] uncertainties) {
        return new BasicTimeSeries(tstart, times, intensities, uncertainties);
    }
}
