package gb.tda.timeseries;

import org.apache.log4j.Logger;
import gb.tda.eventlist.IEventList;
import gb.tda.eventlist.BasicEventList;
import gb.tda.eventlist.EventListSelector;
import gb.tda.eventlist.EventListException;
import gb.tda.binner.Binner;

public class BinnedTimeSeriesFactory {
    private static final Logger logger  = Logger.getLogger(BinnedTimeSeriesFactory.class);
    
    public static IBinnedTimeSeries create(IBinnedTimeSeries ts) {
        return new BinnedTimeSeries(ts);
    }
    
    public static IBinnedTimeSeries create(double[] binEdges, double[] intensities) {
        return new BinnedTimeSeries(binEdges, intensities);
    }

    public static IBinnedTimeSeries create(double tstart, double[] binEdges, double[] intensities) {
        return new BinnedTimeSeries(tstart, binEdges, intensities);
    }

    public static IBinnedTimeSeries create(double[] binEdges, double[] intensities, double[] uncertainties) {
        return new BinnedTimeSeries(binEdges, intensities, uncertainties);
    }

    public static IBinnedTimeSeries create(double tstart, double[] binEdges, double[] intensities, double[] uncertainties) {
        return new BinnedTimeSeries(tstart, binEdges, intensities, uncertainties);
    }

    public static IBinnedTimeSeries create(IEventList evlist) throws TimeSeriesException {
        logger.info("Using effective Nyquist (minimum) binWidth defined as 1/(2*meanCountRate)");
        double effectiveNyquistBinWidth = 0.5/evlist.meanRate();
        int nBins = (int) Math.floor(evlist.duration()/effectiveNyquistBinWidth);
        return create(evlist, nBins);
    }

    public static IBinnedTimeSeries create(IEventList evlist, int nBins) throws TimeSeriesException {
        logger.info("Making TimeSeries from BasicEventList using nBins = "+nBins);
        double binWidth = evlist.duration()/nBins;
        return create(evlist, binWidth);
    }

    public static IBinnedTimeSeries create(IEventList evlist, double binWidth) throws TimeSeriesException {
        logger.info("Making TimeSeries from BasicEventList using binWidth = "+binWidth);

        //  Define number of bins
        double n = evlist.duration()/binWidth;

        //  Use this to drop the last partial bin
        int nBins = (int) Math.floor(n);
        double diff = n - nBins;
        double lastPartialBinWidth = diff*binWidth;
        int nIgnoredEvents = (int) Math.round(lastPartialBinWidth*evlist.meanRate());
        if (nIgnoredEvents >= 1) {
            logger.warn("  Ignoring last partial bin: "+lastPartialBinWidth+" s");
            logger.warn("  This will result in ignoring approx "+nIgnoredEvents+" events");
            logger.warn("  To use all events, specify a number of bins instead of a binWidth");
        }
        double[] t = evlist.getArrivalTimes();
        double tStart = t[0];
        double tStop = t[0] + evlist.duration() + 0.5*Math.ulp(0);
        double[] binEdges;
        try {
            binEdges = BinningUtils.getBinEdges(tStart, tStop, binWidth);
        }
        catch (BinningException e) {
            throw new TimeSeriesException("Cannot construct bin edges");
        }

        //  EITHER: Bin the data with Binner
        double[] counts = Binner.binData(t, binEdges);

        //  OR: Bin using Histogram1D

        // //  Create Histogram1D
        // IAnalysisFactory af = IAnalysisFactory.create();
        // ITree tree = af.createTreeFactory().create();
        // IHistogramFactory hf = af.createHistogramFactory(tree);
        // double lowerEdge = evlist.tStart();
        // double upperEdge = lowerEdge + nBins*binWidth + 1e-6;
        // IHistogram1D histo = hf.createHistogram1D("Histo", nBins, lowerEdge, upperEdge);
        // int nOverflowEvents=0;
        // //  Fill with arrival times
        // double[] arrivalTimes = evlist.getArrivalTimes();
        // for (int i=0; i < evlist.nEvents(); i++) {
        //     histo.fill(arrivalTimes[i]);
        //     if (arrivalTimes[i] > upperEdge) {
        // 	nOverflowEvents++;
        //     }
        // }
        // if (nIgnoredEvents >= 1) {
        //     logger.info("Actual number of events that were dropped is: "+nOverflowEvents);
        // }
        // //  Get the counts in each bin and bin edges
        // IAxis histoAxis = histo.axis();
        // double[] counts = new double[nBins];
        // for (int i=0; i < nBins; i++) {
        //     counts[i] = histo.binHeight(i);
        //     binEdges[2*i] = histoAxis.binLowerEdge(i);
        //     binEdges[2*i+1] = histoAxis.binUpperEdge(i);
        // }

        //  Return the TimeSeries
        double[] zeroedBinEdges = Utils.resetToZero(binEdges);
        return new BinnedTimeSeries(new CountsTimeSeries(evlist.tStart(), zeroedBinEdges, counts));
    }

    public static IBinnedTimeSeries create(double[] arrivalTimes, int nBins) throws EventListException, TimeSeriesException {
        BasicEventList evlist = new BasicEventList(arrivalTimes);
        return create(evlist, nBins);
    }

    public static IBinnedTimeSeries create(double[] arrivalTimes, double binWidth) throws EventListException, TimeSeriesException {
        BasicEventList evlist = new BasicEventList(arrivalTimes);
        return create(evlist, binWidth);
    }

    public static IBinnedTimeSeries create(double[] arrivalTimes, double binWidth, double startTime) throws TimeSeriesException, EventListException {
        BasicEventList evlist = new BasicEventList(arrivalTimes);
        double[] selectedArrivalTimes = EventListSelector.getArrivalTimesFromTo(evlist, startTime, evlist.tStop());
        BasicEventList selectedEvlist = new BasicEventList(selectedArrivalTimes);
        return create(selectedEvlist, binWidth);
    }

    public static IBinnedTimeSeries create(double[] arrivalTimes, double binWidth, double startTime, double endTime) throws TimeSeriesException, EventListException {
        BasicEventList evlist = new BasicEventList(arrivalTimes);
        double[] selectedArrivalTimes = EventListSelector.getArrivalTimesFromTo(evlist, startTime, endTime);
        BasicEventList selectedEvlist = new BasicEventList(selectedArrivalTimes);
        return create(selectedEvlist, binWidth);
    }

}
