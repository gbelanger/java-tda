package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class BasicTimeSeries extends AbstractTimeSeries {

    private static Logger logger = Logger.getLogger(BasicTimeSeries.class);

    BasicTimeSeries(ITimeSeries ts) {
        super(ts);
    }

    BasicTimeSeries(double[] times, double[] intensities) {
        super(times, intensities);
    }

    BasicTimeSeries(double tstart, double[] times, double[] intensities) {
        super(tstart, times, intensities);
    }

    BasicTimeSeries(double[] times, double[] intensities, double[] uncertainties) {
        super(times, intensities, uncertainties);
    }

    BasicTimeSeries(double tstart, double[] times, double[] intensities, double[] uncertainties) {
        super(tstart, times, intensities, uncertainties);
    }

}
