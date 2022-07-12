package gb.tda.timeseries;

import org.apache.log4j.Logger;

public class BinnedTimeSeries extends AbstractBinnedTimeSeries implements IBinnedTimeSeries {

    private static Logger logger = Logger.getLogger(BasicTimeSeries.class);

    BinnedTimeSeries(IBinnedTimeSeries ts) {
        super(ts);
    }

    BinnedTimeSeries(double[] binEdges, double[] intensities) {
        super(binEdges, intensities);
    }

    BinnedTimeSeries(double tstart, double[] binEdges, double[] intensities) {
        super(tstart, binEdges, intensities);
    }

    BinnedTimeSeries(double[] binEdges, double[] intensities, double[] uncertainties) {
        super(binEdges, intensities, uncertainties);
    }

    BinnedTimeSeries(double tstart, double[] binEdges, double[] intensities, double[] uncertainties) {
        super(tstart, binEdges, intensities, uncertainties);
    }
     
}
