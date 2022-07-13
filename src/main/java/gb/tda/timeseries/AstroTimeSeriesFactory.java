package gb.tda.timeseries;

import java.io.IOException;
import org.apache.log4j.Logger;

import gb.tda.eventlist.EventList;
import gb.tda.eventlist.EventListSelector;
import gb.tda.eventlist.EventListException;
import gb.tda.tools.DataUtils;
import gb.tda.binner.Binner;

public class AstroTimeSeriesFactory {
    private static final Logger logger  = Logger.getLogger(AstroTimeSeriesFactory.class);

    public static AstroTimeSeries create(IAstroTimeSeries ts) {
        return new AstroTimeSeries(ts);
    }

    public static AstroTimeSeries create(ICountsTimeSeries ts) {
        return new AstroTimeSeries(ts);
    }

    public static AstroTimeSeries create(IRatesTimeSeries ts) {
        return new AstroTimeSeries(ts);
    }

//    public static ITimeSeries create(String filename) throws Exception, IOException, TimeSeriesFileException {
//        return TimeSeriesFileReader.readTimeSeriesFile(filename);
//    }

    /**
     * Create <code>AstroTimeSeries</code>
     * from an <code>EventList</code>
     * with the Nyquist (minimum) binwidth = 1/(2*minEventSpacing).
     *
     * @param evlist an <code>EventList</code> value
     * @return a <code>AstroTimeSeries</code> value
     */
    public static AstroTimeSeries create(EventList evlist) throws TimeSeriesException {
        logger.info("Using effective Nyquist (minimum) binWidth defined as 1/(2*meanCountRate)");
        double effectiveNyquistBinWidth = 0.5/evlist.meanRate();
        int nBins = (int) Math.floor(evlist.duration()/effectiveNyquistBinWidth);
        return create(evlist, nBins);
    }

    /**
     * Create <code>AstroTimeSeries</code>
     * from an <code>EventList</code>
     * with the specified number of bins.
     *
     * @param evlist an <code>EventList</code> value
     * @param nBins an <code>int</code> value
     * @return a <code>TimeSeries</code> value
     */
    public static AstroTimeSeries create(EventList evlist, int nBins) throws TimeSeriesException {
        logger.info("Making TimeSeries from EventList using nBins = "+nBins);
        double binWidth = evlist.duration()/nBins;
        return create(evlist, binWidth);
    }

    /**
     * Create <code>AstroTimeSeries</code>
     * from an <code>EventList</code>
     * with the specified bin width.
     */
    public static AstroTimeSeries create(EventList evlist, double binWidth) throws TimeSeriesException {
        logger.info("Making TimeSeries from EventList using binWidth = "+binWidth);

        //  Define number of bins
        double n = evlist.duration()/binWidth;

        //  Use this to drop the last partial bin
        int nBins = (int) Math.floor(n);
        double diff = n - nBins;
        double lastPartialBinWidth = diff*binWidth;
        int nIgnoredEvents = (int) Math.round(lastPartialBinWidth*evlist.meanRate());
        if ( nIgnoredEvents >= 1 ) {
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
        catch ( BinningException e ) {
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
        // for ( int i=0; i < evlist.nEvents(); i++ ) {
        //     histo.fill(arrivalTimes[i]);
        //     if ( arrivalTimes[i] > upperEdge ) {
        // 	nOverflowEvents++;
        //     }
        // }
        // if ( nIgnoredEvents >= 1 ) {
        //     logger.info("Actual number of events that were dropped is: "+nOverflowEvents);
        // }
        // //  Get the counts in each bin and bin edges
        // IAxis histoAxis = histo.axis();
        // double[] counts = new double[nBins];
        // for ( int i=0; i < nBins; i++ ) {
        //     counts[i] = histo.binHeight(i);
        //     binEdges[2*i] = histoAxis.binLowerEdge(i);
        //     binEdges[2*i+1] = histoAxis.binUpperEdge(i);
        // }

        //  Return the TimeSeries
        double[] zeroedBinEdges = DataUtils.resetToZero(binEdges);
        return new AstroTimeSeries(new CountsTimeSeries(evlist.tStart(), zeroedBinEdges, counts));
    }

    /**
     * Create <code>AstroTimeSeries</code>
     * from an <code>EventList</code>
     * with the specified number of bins, minimum and maximum energies.
     */
    public static ITimeSeries create(EventList evlist, int nBins, double emin, double emax) throws EventListException, TimeSeriesException {
        double[] selectedArrivalTimes = EventListSelector.getArrivalTimesInEnergyRange(evlist, emin, emax);
        logger.info("Making TimeSeries from EventList using nBins = "+nBins+" and [emin, emax] = ["+emin+", "+emax+"]");
        EventList selectedEvlist = new EventList(selectedArrivalTimes);
        return create(selectedEvlist, nBins);
    }

    /**
     * Create <code>AstroTimeSeries</code>
     * from an <code>EventList</code>
     * with the specified number of bins, minimum and maximum energies, as well as bounding detector coordinates.
     */
    public static ITimeSeries create(EventList evlist, int nBins, double emin, double emax, double xmin, double xmax, double ymin, double ymax) throws EventListException, TimeSeriesException {
        //  Select according to X and Y coordinate range
        double[] coordSelectedArrivalTimes = EventListSelector.getArrivalTimesInCoordinateRange(evlist, xmin, xmax, ymin, ymax);
        EventList coordSelectedEvlist = new EventList(coordSelectedArrivalTimes);
        //  Select according to energy range
        double[] coordAndEnergySelectedArrivalTimes = EventListSelector.getArrivalTimesInEnergyRange(coordSelectedEvlist, emin, emax);
        EventList selectedEvlist = new EventList(coordAndEnergySelectedArrivalTimes);
        logger.info("Making TimeSeries from EventList using nBins = "+nBins+
                " and [emin, emax] = ["+emin+", "+emax+"]"+
                " and [xmin, xmax] = ["+xmin+", "+xmax+"]"+
                " and [ymin, ymax] = ["+ymin+", "+ymax+"]"
        );
        return create(selectedEvlist, nBins);
    }

    /**
     * Create <code>AstroTimeSeries</code>
     * from arrival times
     * with the specified number of bins.
     */
    public static AstroTimeSeries create(double[] arrivalTimes, int nBins) throws EventListException, TimeSeriesException {
        EventList evlist = new EventList(arrivalTimes);
        return create(evlist, nBins);
    }

    /**
     * Create <code>AstroTimeSeries</code>
     * from arrival times
     * with the specified bin width.
     */
    public static AstroTimeSeries create(double[] arrivalTimes, double binWidth) throws EventListException, TimeSeriesException {
        EventList evlist = new EventList(arrivalTimes);
        return create(evlist, binWidth);
    }

    /**
     * Construct a <code>AstroTimeSeries</code>
     * from arrival times,
     * with the specified bin width and start time.
     */
    public static AstroTimeSeries create(double[] arrivalTimes, double binWidth, double startTime) throws TimeSeriesException, EventListException {
        EventList evlist = new EventList(arrivalTimes);
        double[] selectedArrivalTimes = EventListSelector.getArrivalTimesFromTo(evlist, startTime, evlist.tStop());
        EventList selectedEvlist = new EventList(selectedArrivalTimes);
        return create(selectedEvlist, binWidth);
    }

    /**
     * Construct a <code>TimeSeries</code>
     * from arrival times,
     * with the specified bin width and start time.
     */
    public static AstroTimeSeries create(double[] arrivalTimes, double binWidth, double startTime, double endTime) throws TimeSeriesException, EventListException {
        EventList evlist = new EventList(arrivalTimes);
        double[] selectedArrivalTimes = EventListSelector.getArrivalTimesFromTo(evlist, startTime, endTime);
        EventList selectedEvlist = new EventList(selectedArrivalTimes);
        return create(selectedEvlist, binWidth);
    }

}
