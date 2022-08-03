package gb.tda.timeseries;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

import gb.tda.eventlist.AsciiEventFileException;
import gb.tda.eventlist.AsciiEventFileReader;
//import gb.tda.eventlist.AstroEventList;
import gb.tda.eventlist.EventListException;
import gb.tda.io.AsciiDataFileFormatException;
import gb.tda.io.AsciiDataFileReader;

public class BinnedTimeSeriesFileReader implements ITimeSeriesFileReader {

    private static Logger logger  = Logger.getLogger(BinnedTimeSeriesFileReader.class);

    public ITimeSeries read(String filename) throws TimeSeriesFileException, TimeSeriesException, BinningException, IOException  {
        AsciiDataFileReader dataFile;
        try {
            dataFile = new AsciiDataFileReader(filename);
        }
        catch (AsciiDataFileFormatException e) {
            throw new AsciiTimeSeriesFileException("Problem reading file", e);
        }

        // Define possible data columns
        double[] binCentres = null;
        double[] intensities = null;
        double[] uncertainties = null;

        // Read the data
        int ncols = dataFile.getNDataCols();
        if (ncols == 1) {
            //  If there is only 1 column, assume event list
            try {
                return BinnedTimeSeriesFactory.create(new AsciiEventFileReader().read(filename));
            }
            catch (AsciiEventFileException e) {
                throw new AsciiTimeSeriesFileException("Problem reading data file", e);
            }
            catch (EventListException e) {
                throw new AsciiTimeSeriesFileException("Problem reading event file", e);
            }
        }
        else {
            // Two or more columns
            times = dataFile.getDblCol(0);
            intensities = dataFile.getDblCol(1);
            double[] binEdges;
            if (ncols == 2) {
                return BasicTimeSeriesFactory.create(times, intensities);
            }
            else if (ncols == 3) {
                double[] uncertainties = dataFile.getDblCol(2);
                return BasicTimeSeriesFactory.create(times, intensities, uncertainties);
            }
            else if (ncols == 4) {
                double[] binCentres = dataFile.getDblCol(0);
                double[] dtOver2 = dataFile.getDblCol(1);
                try {
                    binEdges = BinningUtils.getBinEdgesFromBinCentresAndHalfWidths(binCentres, dtOver2);
                } catch (BinningException e) {
                    throw new TimeSeriesFileException("Cannot construct bin edges", e);
                }
                intensities = dataFile.getDblCol(2);
                double[] uncertainties = dataFile.getDblCol(3);
                return BinnedTimeSeriesFactory.create(binEdges, intensities, uncertainties);
            }
            else {
                throw new AsciiTimeSeriesFileException("Not an ASCII time series file. " +
                        "Format can be:  1 col = arrival times;  " +
                        "2 cols = binCentres and intensities;   3 cols = binCentres, intensities and errors; " +
                        "4 cols (or more) = binCentres, halfBinWidths, intensities, errors");
            }
        }
    }

}
