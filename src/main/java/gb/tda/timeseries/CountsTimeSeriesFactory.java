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

    public static CountsTimeSeries create(double[] binCentres, double[] counts) throws TimeSeriesException, BinningException {
        logger.info("Making CountsTimeSeries from binCentres and counts");
        logger.warn("Assuming adjacent bins");
        double[] halfBinWidths = BinningUtils.getHalfBinWidthsFromBinCentres(binCentres);
        return create(binCentres, halfBinWidths, counts);
    }

    public static CountsTimeSeries create(double[] binCentres, double halfBinWidth, double[] counts) throws TimeSeriesException, BinningException {
        double[] halfBinWidths = new double[binCentres.length];
        for (int i=0; i < binCentres.length; i++) {
            halfBinWidths[i] = halfBinWidth;
        }
        return create(binCentres, halfBinWidths, counts);
    }

    public static CountsTimeSeries create(double[] binCentres, double[] halfBinWidths, double[] counts) throws TimeSeriesException, BinningException {
        double[] binEdges = BinningUtils.getBinEdgesFromBinCentresAndHalfWidths(binCentres, halfBinWidths);
        return new CountsTimeSeries(binEdges, counts);
    }

}
