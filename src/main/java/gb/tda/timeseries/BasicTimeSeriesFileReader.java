package gb.tda.timeseries;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

import gb.tda.io.AsciiDataFileFormatException;
import gb.tda.io.AsciiDataFileReader;

public class BasicTimeSeriesFileReader implements ITimeSeriesFileReader {

    private static Logger logger  = Logger.getLogger(BasicTimeSeriesFileReader.class);

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
            throw new TimeSeriesFileFormatException(
                    "\n\tNot a BasicTimeSeries. There is only 1 column (probably an event list). Use BinnedTimeSeriesFileReader.");
        }
        else if (ncols == 2) {
            double[] times = dataFile.getDblCol(0);
            double[] intensities = dataFile.getDblCol(1);
            return BasicTimeSeriesFactory.create(times, intensities);
        }
        else if (ncols == 3) {
            double[] times = dataFile.getDblCol(0);
            double[] intensities = dataFile.getDblCol(1);
            double[] uncertainties = dataFile.getDblCol(2);
            return BasicTimeSeriesFactory.create(times, intensities, uncertainties);
        }
        else {
            throw new TimeSeriesFileFormatException(
                    "\n\tNot a BasicTimeSeries. BasicTimeSeriesFileReader accepts 2 formats:"+
                    "\n\t - 2 cols = times, intensities"+
                    "\n\t - 3 cols = times, intensities, uncertainties");
        }
    }

}
