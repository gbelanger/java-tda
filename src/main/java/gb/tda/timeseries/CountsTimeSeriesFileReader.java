package gb.tda.timeseries;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

import gb.tda.eventlist.AsciiEventFileReader;
import gb.tda.eventlist.AsciiEventFileException;
import gb.tda.eventlist.EventListException;
import gb.tda.io.AsciiDataFileReader;
import gb.tda.io.AsciiDataFileFormatException;


public class CountsTimeSeriesFileReader implements ITimeSeriesFileReader {

    private static Logger logger  = Logger.getLogger(CountsTimeSeriesFileReader.class);

    public ITimeSeries read(String filename) throws TimeSeriesFileException, TimeSeriesException, BinningException, IOException  {

        AsciiDataFileReader dataFile;
        try {
            dataFile = new AsciiDataFileReader(filename);
        }
        catch (AsciiDataFileFormatException e) {
            throw new AsciiTimeSeriesFileException("Problem reading file", e);
        }

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
        else if (ncols == 2) {
            double[] times = dataFile.getDblCol(0);
            double[] intensities = dataFile.getDblCol(1);
            return CountsTimeSeriesFactory.create(times, intensities);
        }
        else if (ncols == 3) {
            double[] times = dataFile.getDblCol(0);
            double[] halfBinWidths = dataFile.getDblCol(1);
            double[] intensities = dataFile.getDblCol(2);
            return CountsTimeSeriesFactory.create(times, halfBinWidths, intensities);
        }
        else {
            throw new TimeSeriesFileFormatException(
                    "\n\tNot a CountsTimeSeries file. CountsTimeSeriesFileReader accepts 3 formats:"+
                            "\n\t - 1 col = event times"+
                            "\n\t - 2 cols = binCentres, counts"+
                            "\n\t - 3 cols = binCentres, halfBinWidths, counts");
        }
    }
}
