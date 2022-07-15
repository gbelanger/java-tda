package gb.tda.timeseries;

import cern.colt.list.DoubleArrayList;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class RatesTimeSeriesFactory {
    private static final Logger logger  = Logger.getLogger(RatesTimeSeriesFactory.class);

    public static RatesTimeSeries create(IRatesTimeSeries ts) {
        return new RatesTimeSeries(ts);
    }

    /**
     * Construct a  <code>RatesTimeSeries</code> from bin centres, rates and errors on rates. 
     * We assume that the bins are adjacent and that the first two bins are of equal width.
     */
    public static RatesTimeSeries create(double[] binCentres, double[] rates, double[] errorsOnRates) throws TimeSeriesException, BinningException {
        logger.info("Making RatesTimeSeries from binCentres, rates and errors");
        logger.warn("Assuming adjacent bins");
        double[] halfBinWidths = BinningUtils.getHalfBinWidthsFromBinCentres(binCentres);
        return create(binCentres, halfBinWidths, rates, errorsOnRates);
    }

    /**
     * Construct a  <code>RatesTimeSeries</code> from bin centres, halfBinWidths, rates and errors on rates. 
     * We assume that the bins are adjacent and that the first two bins are of equal width.
     */
    public static RatesTimeSeries create(double[] binCentres, double[] halfBinWidths, double[] rates, double[] errorsOnRates) throws TimeSeriesException {
        logger.info("Making RatesTimeSeries from binCentres, halfBinWidths, rates and errors");
        double firstHalfBinWidth = halfBinWidths[0];
        double tStart = binCentres[0] - firstHalfBinWidth;
        double[] centres = Arrays.copyOf(binCentres, binCentres.length);
        if (tStart < 0) {
            for (int i=0; i < centres.length; i++) {
                centres[i] += tStart;
            }
            tStart = 0.0;
        }
        double positiveOffset = firstHalfBinWidth;
        double[] zeroedBinCentres = Utils.resetToZero(centres, positiveOffset);
        double[] binEdges;
        try {
            binEdges = BinningUtils.getBinEdgesFromBinCentresAndHalfWidths(zeroedBinCentres, halfBinWidths);
        }
        catch (BinningException e) {
            throw new TimeSeriesException("Cannot construct bin edges", e);
        }
        // Remove all bins with rate=0.0 and error=0.0
        DoubleArrayList goodBinEdges = new DoubleArrayList();
        DoubleArrayList goodRates = new DoubleArrayList();
        DoubleArrayList goodErrors = new DoubleArrayList();
        for (int i=0; i < rates.length; i++) {
            if (rates[i] != 0.0 && errorsOnRates[i] != 0.0) {
                goodBinEdges.add(binEdges[2*i]);
                goodBinEdges.add(binEdges[2*i+1]);
                goodRates.add(rates[i]);
                goodErrors.add(errorsOnRates[i]);
            }
        }
        goodBinEdges.trimToSize();
        goodRates.trimToSize();
        goodErrors.trimToSize();
        if (goodBinEdges.size() == 0 || goodRates.size() == 0 || goodErrors.size() == 0) {
            throw new TimeSeriesException("All bins are zeros: No TimeSeries can be made");
        }
        RatesTimeSeries ts = new RatesTimeSeries(tStart, goodBinEdges.elements(), goodRates.elements(), goodErrors.elements());
        if (ts.thereAreNaNs()) {
            ts = (RatesTimeSeries) TimeSeriesUtils.dropLeadingAndTrailingNaNs(ts);
        }
        return ts;
    }

    public static RatesTimeSeries create(double[] binCentres, double halfBinWidth, double[] rates, double[] errorsOnRates) throws TimeSeriesException {
        double[] halfBinWidths = new double[binCentres.length];
        for (int i=0; i < binCentres.length; i++) {
            halfBinWidths[i] = halfBinWidth;
        }
        return create(binCentres, halfBinWidths, rates, errorsOnRates);
    }

    public static RatesTimeSeries create(double[] binCentres, double halfBinWidth, double[] rates, double errorOnRates) throws TimeSeriesException {
        double[] halfBinWidths = new double[binCentres.length];
        double[] errorsOnRates = new double[binCentres.length];
        for (int i=0; i < binCentres.length; i++) {
            halfBinWidths[i] = halfBinWidth;
            errorsOnRates[i] = errorOnRates;
        }
        return create(binCentres, halfBinWidths, rates, errorsOnRates);
    }

}