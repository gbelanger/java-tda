package gb.tda.timeseries;

import java.io.IOException;
import org.apache.log4j.Logger;

import gb.tda.eventlist.AstroEventList;
import gb.tda.eventlist.EventListSelector;
import gb.tda.eventlist.EventListException;
import gb.tda.binner.Binner;

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

//    public static ITimeSeries create(String filename) throws Exception, IOException, TimeSeriesFileException {
//        return TimeSeriesFileReader.read(filename);
//    }

    public static IBinnedTimeSeries create(IEventList evlist) throws TimeSeriesException {
        super.create(evlist);
    }

    public static IBinnedTimeSeries create(IEventList evlist, int nBins) throws TimeSeriesException {
        super.create(evlist, nBins);
    }

    public static IBinnedTimeSeries create(IEventList evlist, double binWidth) throws TimeSeriesException {
        super.create(evlist, binWidth);
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

    public static IBinnedTimeSeries create(double[] arrivalTimes, int nBins) throws EventListException, TimeSeriesException {
        super.create(arrivalTimes, nBins);
    }

    public static IBinnedTimeSeries create(double[] arrivalTimes, double binWidth) throws EventListException, TimeSeriesException {
        super.create(arrivalTimes, binWidth);
    }

    public static IBinnedTimeSeries create(double[] arrivalTimes, double binWidth, double startTime) throws TimeSeriesException, EventListException {
        super.create(arrivalTimes, binWidth, startTime);
    }

    public static IBinnedTimeSeries create(double[] arrivalTimes, double binWidth, double startTime, double endTime) throws TimeSeriesException, EventListException {
        super.create(selectedEvlist, binWidth, startTime, endTime);
    }

}
