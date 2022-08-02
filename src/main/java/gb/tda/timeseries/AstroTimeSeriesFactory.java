package gb.tda.timeseries;

import java.io.IOException;
import org.apache.log4j.Logger;
import gb.tda.eventlist.AstroEventList;
import gb.tda.eventlist.EventListSelector;
import gb.tda.eventlist.EventListException;

public class AstroTimeSeriesFactory extends BinnedTimeSeriesFactory {

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

    public static IBinnedTimeSeries create(AstroEventList evlist, int nBins, double emin, double emax) throws EventListException, TimeSeriesException {
        double[] selectedArrivalTimes = EventListSelector.getArrivalTimesInEnergyRange(evlist, emin, emax);
        logger.info("Making TimeSeries from AstroEventList using nBins = "+nBins+" and [emin, emax] = ["+emin+", "+emax+"]");
        AstroEventList selectedEvlist = new AstroEventList(selectedArrivalTimes);
        return create(selectedEvlist, nBins);
    }

    public static IBinnedTimeSeries create(AstroEventList evlist, int nBins, double emin, double emax, double xmin, double xmax, double ymin, double ymax) throws EventListException, TimeSeriesException {
        //  Select according to X and Y coordinate range
        double[] coordSelectedArrivalTimes = EventListSelector.getArrivalTimesInCoordinateRange(evlist, xmin, xmax, ymin, ymax);
        AstroEventList coordSelectedEvlist = new AstroEventList(coordSelectedArrivalTimes);
        //  Select according to energy range
        double[] coordAndEnergySelectedArrivalTimes = EventListSelector.getArrivalTimesInEnergyRange(coordSelectedEvlist, emin, emax);
        AstroEventList selectedEvlist = new AstroEventList(coordAndEnergySelectedArrivalTimes);
        logger.info("Making TimeSeries from AstroEventList using nBins = "+nBins+
                " and [emin, emax] = ["+emin+", "+emax+"]"+
                " and [xmin, xmax] = ["+xmin+", "+xmax+"]"+
                " and [ymin, ymax] = ["+ymin+", "+ymax+"]"
        );
        return create(selectedEvlist, nBins);
    }

}
